package com.aem.training.mocktesting.core.inject;

import com.aem.training.mocktesting.core.models.BaseModel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.spi.AcceptsNullName;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.stream.Stream;

@Component(componentAbstract = true)
public abstract class AbstractInjector implements Injector, AcceptsNullName, StaticInjectAnnotationProcessorFactory {

    protected ResourceResolver getResourceResolver(final Object adaptable) {
        ResourceResolver resolver = null;
        if (adaptable instanceof Resource) {
            resolver = ((Resource) adaptable).getResourceResolver();
        } else if (adaptable instanceof SlingHttpServletRequest) {
            resolver = ((SlingHttpServletRequest) adaptable).getResourceResolver();
        } else if (adaptable instanceof ResourceResolvable) {
            resolver = ((ResourceResolvable) adaptable).getResourceResolver();
        }
        return resolver;
    }

    protected ValueMap getValueMap(final Object adaptable) {
        if (adaptable instanceof ValueMappable) {
            return ((ValueMappable) adaptable).getValueMap();
        } else if (adaptable instanceof ValueMap) {
            return (ValueMap) adaptable;
        } else if (adaptable instanceof Adaptable) {
            return ((Adaptable) adaptable).adaptTo(ValueMap.class);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    protected Class<? extends BaseModel> getModelClass(final Type declaredType) {
        Class<? extends BaseModel> clazz = null;

        // unwrap if necessary
        if (isDeclaredTypeCollection(declaredType)) {
            Type[] actualTypeArguments = ((ParameterizedType) declaredType).getActualTypeArguments();
            if (ArrayUtils.isNotEmpty(actualTypeArguments)) {
                clazz = (Class<? extends BaseModel>) actualTypeArguments[0];
            }
        } else {
            clazz = (Class<? extends BaseModel>) declaredType;
        }

        return clazz;
    }

    protected boolean isDeclaredTypeCollection(final Type declaredType) {
        return Stream.of(declaredType)
                .filter(type -> type instanceof ParameterizedType)
                .map(type -> (ParameterizedType) type)
                .map(type -> (Class<?>) type.getRawType())
                .anyMatch(Collection.class::isAssignableFrom);
    }
}