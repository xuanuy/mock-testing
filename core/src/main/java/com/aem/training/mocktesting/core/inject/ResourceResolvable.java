package com.aem.training.mocktesting.core.inject;

import org.apache.sling.api.resource.ResourceResolver;

public interface ResourceResolvable {

    ResourceResolver getResourceResolver();

    void setResourceResolver(ResourceResolver resourceResolver);
}
