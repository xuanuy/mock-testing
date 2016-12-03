package com.aem.training.mocktesting.core.services.impl;

import com.aem.training.mocktesting.core.constants.Constants;
import com.aem.training.mocktesting.core.models.BaseModel;
import com.aem.training.mocktesting.core.services.GenericServiceInterface;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * The abstract service that provides base methods for other services.
 */
@Component(componentAbstract = true)
public abstract class AbstractService<T extends BaseModel> implements GenericServiceInterface<T> {

    @Reference
    private QueryBuilder queryBuilder;

    private final Class<T> parameterizedType;

    @SuppressWarnings("unchecked")
    public AbstractService() {
        this.parameterizedType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    protected abstract String getAdminPath();

    protected abstract String getContentPath();

    @Override
    public List<T> getList(final SlingHttpServletRequest request) {
        return getList(request.getResourceResolver(), Locale.ENGLISH);
    }

    @Override
    public List<T> getList(final ResourceResolver resourceResolver, final Locale locale) {
        return getList(resourceResolver, getPath(locale));
    }

    @Override
    public List<Resource> getListAsResources(final ResourceResolver resourceResolver, final Locale locale) {
        Map<String, String> parameters = getParameters(getPath(locale), Collections.emptyList(), -1);
        Query query = queryBuilder.createQuery(PredicateGroup.create(parameters), resourceResolver.adaptTo(Session.class));

        final Iterable<Resource> iterable = () -> query.getResult().getResources();
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    private String getPath(final Locale locale) {
        if (isAdmin()) {
            return getAdminPath();
        } else {
            return String.format("%s/%s", getContentPath(), locale.getLanguage());
        }
    }

    @Override
    public List<T> getList(final ResourceResolver resourceResolver, final String path) {
        return getModels(resourceResolver, getParameters(path, Collections.emptyList(), -1));
    }

    @Override
    public List<T> filterList(final SlingHttpServletRequest request, final List<String> filterParam) {
        return filterList(request.getResourceResolver(), Locale.ENGLISH, filterParam);
    }

    @Override
    public List<T> filterList(final ResourceResolver resourceResolver, final Locale locale, final List<String> filterParam) {
        return filterList(resourceResolver, getPath(locale), filterParam);
    }

    @Override
    public List<T> filterList(final ResourceResolver resourceResolver, final String path, final List<String> filterParam) {
        return getModels(resourceResolver, getParameters(path, filterParam, -1));
    }

    @Override
    public T getByIdentifier(final ResourceResolver resourceResolver, final String identifier) {
        if (StringUtils.isBlank(identifier)) {
            return null;
        }
        if (isId(identifier)) {
            return getById(resourceResolver, identifier);
        }
        return getByPath(resourceResolver, identifier);
    }

    private boolean isId(final String identifier) {
        return !StringUtils.startsWith(identifier, "/");
    }

    private T getById(final ResourceResolver resourceResolver, final String id) {
        String path = getAdminPath();
        final Map<String, String> parameters = getParameters(path, Collections.emptyList(), 1);
        parameters.put("3_property", "id");
        parameters.put("3_property.value", id);

        return getModels(resourceResolver, parameters).stream()
                .findFirst()
                .orElse(null);
    }

    private T getByPath(final ResourceResolver resourceResolver, final String path) {
        return Optional.ofNullable(resourceResolver.getResource(path))
                .filter(resource -> !hasIsActiveProperty() || Boolean.parseBoolean(resource.getValueMap().get(Constants.IS_ACTIVE_PROPERTY, StringUtils.EMPTY)))
                .map(resource -> this.toModel(resourceResolver, resource))
                .orElse(null);
    }

    private List<T> getModels(final ResourceResolver resourceResolver, final Map<String, String> parameters) {
        final Query query = queryBuilder.createQuery(PredicateGroup.create(parameters), resourceResolver.adaptTo(Session.class));
        return query.getResult().getHits().stream()
                .map(this::getResource)
                .filter(Objects::nonNull)
                .map(resource -> this.toModel(resourceResolver, resource))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private T toModel(final ResourceResolver resourceResolver, final Resource resource) {
        return Optional.ofNullable(resource.adaptTo(parameterizedType))
                .map(model -> {
                    model.setPath(resource.getPath());
                    model.setPage(resourceResolver.adaptTo(PageManager.class).getContainingPage(resource.getPath()));
                    return model;
                })
                .orElse(null);
    }

    private Resource getResource(final Hit hit) {
        try {
            return hit.getResource();
        } catch (RepositoryException e) {
            return null;
        }
    }

    private Map<String, String> getParameters(final String path, final List<String> filterParam, final int limit) {
        final Map<String, String> map = new HashMap<>();

        int idx = 1;

        map.put("path", path);
        map.put("type", Constants.CQ_PAGECONTENT);

        map.put(idx + "_property", Constants.SLING_RESOURCE_TYPE);
        List<String> resourceTypes = getResourceTypes();
        for (int i = 0; i < resourceTypes.size(); i++) {
            map.put(idx + "_property." + i + "_value", resourceTypes.get(i));
        }

        idx++;
        if (hasIsActiveProperty()) {
            map.put(idx + "_property", Constants.IS_ACTIVE_PROPERTY);
            map.put(idx + "_property.value", "true");
            idx++;
        }

        if (CollectionUtils.isNotEmpty(filterParam)) {
            for (String param : filterParam) {
                if (appendParam(idx, map, param)) {
                    idx++;
                }
            }
        }

        map.put("p.offset", "0");
        map.put("p.limit", String.valueOf(limit));

        return map;
    }

    private boolean appendParam(final int index, final Map<String, String> map, final String param) {
        if (StringUtils.isNotBlank(param)) {
            String[] paramArr = param.split(":");
            if (paramArr.length == 2) {
                map.put(index + "_property", paramArr[0]);
                map.put(index + "_property.value", paramArr[1]);
                return true;
            }
        }
        return false;
    }
}