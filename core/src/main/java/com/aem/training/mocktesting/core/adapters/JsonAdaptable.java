package com.aem.training.mocktesting.core.adapters;

import org.apache.sling.api.adapter.Adaptable;

public class JsonAdaptable implements Adaptable {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        if (type == String.class) {
            return (AdapterType) null;
        }
        return null;
    }
}
