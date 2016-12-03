package com.aem.training.mocktesting.core.inject;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Optional;

@Component
@Service
public class ModelPropertyInjector extends AbstractInjector {
    protected static final String NAME = "model-property";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object getValue(final Object adaptable, final String name, final Type declaredType,
                           final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry) {
        ModelProperty annotation = element.getAnnotation(ModelProperty.class);
        if (annotation == null) {
            return null;
        }

        return getValueMap(adaptable).get(name);
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(AnnotatedElement element) {
        return Optional.ofNullable(element.getAnnotation(ModelProperty.class))
                .map(annotation -> new ModelEnumAnnotationProcessor(annotation.property()))
                .orElse(null);
    }

    private static class ModelEnumAnnotationProcessor implements InjectAnnotationProcessor2 {

        private final String property;

        ModelEnumAnnotationProcessor(final String property) {
            this.property = property;
        }

        @Override
        public String getName() {
            return property;
        }

        /**
         * @deprecated
         */
        @Override
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

        @Override
        public InjectionStrategy getInjectionStrategy() {
            return null;
        }
    }

}