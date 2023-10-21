package eu.dgs_development.code.ejg.controllers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation-class to mark static functions, which construct a global available object-instance for
 * dependency-injection. These instances get injected into fields, which use the {@link InstanceConsumer}-annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface InstanceProvider {

}