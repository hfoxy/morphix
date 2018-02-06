package uk.hfox.morphix.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Polymorphic {

    /**
     * Defines whether or not polymorphism is enabled on this class
     * The implementation of polymorphism is defined by the set polymorphism helper
     *
     * @return true if it is enabled, false otherwise.
     */
    boolean value() default true;

}
