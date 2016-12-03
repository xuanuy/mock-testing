package com.aem.training.mocktesting.core.inject;

import org.apache.sling.api.resource.ValueMap;

@FunctionalInterface
public interface ValueMappable {

    ValueMap getValueMap();
}
