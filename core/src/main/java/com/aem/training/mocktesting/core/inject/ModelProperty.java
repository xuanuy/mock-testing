package com.aem.training.mocktesting.core.inject;

import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Embedded -
 *
 * @author Sylvain Colucci
 * @version $Id$
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@InjectAnnotation
@Source(ModelPropertyInjector.NAME)
public @interface ModelProperty {

    String property();

}