package eu.dgs_development.code.ejg.controllers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation-class to mark fields, which receive object-instances through dependency-injection.
 * Dependency-injection instances are provided by functions using the {@link InstanceProvider}-annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InstanceConsumer {

}