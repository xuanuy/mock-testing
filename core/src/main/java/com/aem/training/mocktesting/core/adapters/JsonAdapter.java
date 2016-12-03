package com.aem.training.mocktesting.core.adapters;

import com.aem.training.mocktesting.core.models.BaseModel;
import com.aem.training.mocktesting.core.models.HelloWorldModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;

@Component(metatype = true, immediate = true)
@Service
public class JsonAdapter implements AdapterFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonAdapter.class);

    protected BaseModel adaptResourceToCustomClass(Resource resource) {
        BaseModel model = new HelloWorldModel();
        if (null != resource) {
            //Do your logic to get all info
        }
        return model;
    }

    protected BaseModel adaptNodeToCustomClass(Node node, ResourceResolverFactory resolverFactory) {
        ResourceResolver adminResourceResolver = null;
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, null);

        try {
            adminResourceResolver = resolverFactory.getServiceResourceResolver(param);
            return adaptResourceToCustomClass(adminResourceResolver.getResource(node.getPath()));
        } catch (LoginException e) {
            LOGGER.error(e.getMessage());
        } catch (RepositoryException e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (null != adminResourceResolver) {
                adminResourceResolver.close();
            }
        }
        return null;
    }

    @Override
    public <AdapterType> AdapterType getAdapter(Object o, Class<AdapterType> aClass) {
        return null;
    }
}
