package com.aem.training.mocktesting.core.services;

import com.aem.training.mocktesting.core.models.BaseModel;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;
import java.util.Locale;

public interface GenericServiceInterface<T extends BaseModel> {

    List<T> getList(final SlingHttpServletRequest request);

    List<T> getList(final ResourceResolver resourceResolver, final Locale locale);

    List<Resource> getListAsResources(final ResourceResolver resourceResolver, final Locale locale);

    List<T> getList(final ResourceResolver resourceResolver, final String path);

    List<T> filterList(final SlingHttpServletRequest request, final List<String> filterParam);

    List<T> filterList(final ResourceResolver resourceResolver, final Locale locale, final List<String> filterParam);

    List<T> filterList(final ResourceResolver resourceResolver, final String path, final List<String> filterParam);

    T getByIdentifier(final ResourceResolver resourceResolver, final String identifier);

    List<String> getResourceTypes();

    boolean hasIsActiveProperty();

    boolean isAdmin();

}
