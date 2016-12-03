package com.aem.training.mocktesting.core.adapters;

import com.aem.training.mocktesting.core.constants.Constants;
import com.aem.training.mocktesting.core.models.BaseModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.io.IOException;

@Component(metatype = true, immediate = true)
@Service
public class JsonAdapterFactory implements AdapterFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonAdapterFactory.class);
    private static final Class<BaseModel> MODEL_CLASS = BaseModel.class;
    private static final Class<String> JSON_CLASS = String.class;

    @Property(name = "adapters")
    protected static final String[] ADAPTER_CLASSES = {MODEL_CLASS.getName()};

    @Property(name = "adaptables")
    protected static final String[] ADAPTABLE_CLASSES = {JSON_CLASS.getName()};

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Override
    public <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type) {
        if (adaptable instanceof String) {
            return this.adaptFromJsonString((String) adaptable, type);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <AdapterType> AdapterType getAdapter(Resource resource, Class<AdapterType> type) {
        if (null == resource) {
            return null;
        }
        try {
            if (type == MODEL_CLASS) {
                //Logic to adapt your resource to resource
                return (AdapterType) new JsonAdapter().adaptResourceToCustomClass(resource);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to adapt resource {}", resource.getPath());
        }
        LOGGER.error("Unable to adapt resource {}", resource.getPath());
        return null;
    }

    @SuppressWarnings("unchecked")
    private <AdapterType> AdapterType getAdapter(Node node, Class<AdapterType> type) {
        if (null == node) {
            return null;
        }
        try {
            if (type == MODEL_CLASS) {
                return (AdapterType) new JsonAdapter().adaptNodeToCustomClass(node, this.resourceResolverFactory);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to adapt node to Your custom class");
        }
        LOGGER.error("Unable to adapt node");
        return null;
    }

    private <AdapterType> AdapterType adaptFromJsonString(String json, Class<AdapterType> type) {
        if (StringUtils.isNotBlank(json)) {
            try {
                return Constants.MAPPER.readValue(unescapeSpecialChars(json), type);
            } catch (IOException var4) {
                LOGGER.error("cannot adapt json to model", var4);
                return null;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <AdapterType> AdapterType adaptFromResource(Resource resource, Class<AdapterType> type) {
        return null;
    }

    public static String unescapeSpecialChars(String input) {
        return StringUtils.isBlank(input) ? "" : input.replaceAll("&#039;", "\'");
    }
}
