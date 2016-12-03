package com.aem.training.mocktesting.core.inject;

import com.day.cq.wcm.api.PageManager;
import com.lucienbarriere.core.api.constants.Constant;
import com.lucienbarriere.core.api.models.BaseModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Service
public class ModelPathInjector extends AbstractInjector {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelPathInjector.class);

    protected static final String NAME = "model-path";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object getValue(final Object adaptable, final String name, final Type declaredType,
                           final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry) {
        ModelPath annotation = element.getAnnotation(ModelPath.class);
        if (annotation == null) {
            return null;
        }

        List<BaseModel> models = getResources(adaptable, name, declaredType, annotation.hasIsActive());
        if (CollectionUtils.isNotEmpty(models)) {
            if (isDeclaredTypeCollection(declaredType)) {
                return models;
            } else if (models.size() == 1) {
                return models.get(0);
            } else {
                LOGGER.warn("Cannot inject multiple resources into field {} since it is not declared as a list", name);
                return null;
            }
        } else if (isDeclaredTypeCollection(declaredType)) {
            return Collections.emptyList();
        } else {
            return null;
        }
    }

    @Override
    public InjectAnnotationProcessor createAnnotationProcessor(final Object o, final AnnotatedElement element) {
        return Optional.ofNullable(element.getAnnotation(ModelPath.class))
                .map(annotation -> new ModelPathAnnotationProcessor(annotation.fieldName()))
                .orElse(null);
    }

    private List<BaseModel> getResources(final Object adaptable, final String name, final Type declaredType, final String hasIsActive) {
        ResourceResolver resolver = getResourceResolver(adaptable);
        Class<? extends BaseModel> clazz = getModelClass(declaredType);

        return Optional.ofNullable(adaptable)
                .map(this::getValueMap)
                .map(map -> map.get(name, String[].class))
                .map(paths -> Arrays.stream(paths).filter(StringUtils::isNotBlank).collect(Collectors.toList()))
                .orElse(Collections.emptyList())
                .stream()
                .map(resolver::getResource)
                .filter(resource -> this.checkIsActive(resource, hasIsActive))
                .map(resource -> this.toModel(resolver, resource, clazz))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private BaseModel toModel(final ResourceResolver resolver, final Resource resource, final Class<? extends BaseModel> clazz) {
        return Optional.ofNullable(resource.adaptTo(clazz))
                .map(model -> {
                    model.setPath(resource.getPath());
                    model.setPage(resolver.adaptTo(PageManager.class).getContainingPage(resource.getPath()));
                    return model;
                })
                .orElse(null);
    }

    private boolean checkIsActive(final Resource resource, final String hasIsActive) {
        return Objects.nonNull(resource)
                && (!Boolean.parseBoolean(hasIsActive)
                || Boolean.logicalAnd(Boolean.parseBoolean(hasIsActive), Boolean.parseBoolean(resource.getValueMap().get(Constant.IS_ACTIVE_PROPERTY, StringUtils.EMPTY))));
    }

    private static class ModelPathAnnotationProcessor implements InjectAnnotationProcessor {

        private final String fieldName;

        ModelPathAnnotationProcessor(final String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public String getName() {
            return fieldName;
        }

        @Override
        @SuppressWarnings("deprecation")
        public Boolean isOptional() {
            return true;
        }

        @Override
        public String getVia() {
            return null;
        }

        @Override
        public boolean hasDefault() {
            return false;
        }

        @Override
        public Object getDefault() {
            return null;
        }
    }

}