package com.aem.training.mocktesting.core.inject;

import org.apache.commons.lang3.EnumUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Optional;

@Component
@Service
public class ModelEnumInjector extends AbstractInjector {

    protected static final String NAME = "model-enum";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object getValue(final Object adaptable, final String name, final Type declaredType,
                           final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry) {
        ModelEnum annotation = element.getAnnotation(ModelEnum.class);
        if (annotation == null) {
            return null;
        }

        String enumValue = getValueMap(adaptable).get(name, String.class);

        return EnumUtils.getEnum((Class) declaredType, enumValue);
    }

    @Override
    public InjectAnnotationProcessor createAnnotationProcessor(final Object o, final AnnotatedElement element) {
        return Optional.ofNullable(element.getAnnotation(ModelEnum.class))
                .map(annotation -> new ModelEnumAnnotationProcessor(((Field) element).getName()))
                .orElse(null);
    }

    private static class ModelEnumAnnotationProcessor implements InjectAnnotationProcessor {

        private final String fieldName;

        ModelEnumAnnotationProcessor(final String fieldName) {
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