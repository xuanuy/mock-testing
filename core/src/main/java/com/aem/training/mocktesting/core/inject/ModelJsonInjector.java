package com.aem.training.mocktesting.core.inject;

import com.aem.training.mocktesting.core.constants.Constants;
import com.aem.training.mocktesting.core.models.BaseModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.adapter.SlingAdaptable;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
public class ModelJsonInjector extends AbstractInjector {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelJsonInjector.class);

    protected static final String NAME = "model-json";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object getValue(final Object adaptable, final String name, final Type declaredType,
                           final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry) {
        InjectJson annotation = element.getAnnotation(InjectJson.class);
        if (annotation == null) {
            return null;
        }

        // We get the json string of the wanted model
        String[] jsons = getValueMap(adaptable).get(name, String[].class);
        if (jsons != null) {
            List<BaseModel> models = Arrays.stream(jsons)
                    // map the json into the wanted bean
                    .map(json -> this.getFromJson(json, annotation.className()))
                    // filter the null item
                    .filter(Objects::nonNull)
                    // filter the non SlingAdaptble beans
                    .filter(obj -> SlingAdaptable.class.isAssignableFrom(obj.getClass()))
                    // filter the non ResourceResolvable beans
                    .filter(obj -> ResourceResolvable.class.isAssignableFrom(obj.getClass()))
                    // set the resource resolver for each bean
                    .peek(obj -> ((ResourceResolvable) obj).setResourceResolver(getResourceResolver(adaptable)))
                    // cast it into SlingAdaptable (so we can call the adaptTo(...) method
                    .map(SlingAdaptable.class::cast)
                    // adapt the bean into the wanted Model class
                    .map(slingAdaptable -> slingAdaptable.adaptTo(getModelClass(declaredType)))
                    // collect the models objects
                    .collect(Collectors.toList());

            if (isDeclaredTypeCollection(declaredType)) {
                return models;
            } else if (models.size() == 1) {
                return models.get(0);
            } else {
                return null;
            }
        } else if (isDeclaredTypeCollection(declaredType)) {
            return Collections.emptyList();
        } else {
            return null;
        }
    }

    private Object getFromJson(final String json, final Class<?> clazz) {
        try {
            return Constants.MAPPER.readValue(unescapeSpecialChars(json), clazz);
        } catch (IOException e) {
            LOGGER.error("can not read value from json", e);
            return null;
        }
    }

    public static String unescapeSpecialChars(String input) {
        return StringUtils.isBlank(input) ? "" : input.replaceAll("&#039;", "\'");
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(AnnotatedElement element) {
        return Optional.ofNullable(element.getAnnotation(InjectJson.class))
                .map(annotation -> new ModelJsonAnnotationProcessor(annotation.fieldName()))
                .orElse(null);
    }

    private static class ModelJsonAnnotationProcessor implements InjectAnnotationProcessor2 {

        private final String fieldName;

        ModelJsonAnnotationProcessor(final String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public String getName() {
            return fieldName;
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