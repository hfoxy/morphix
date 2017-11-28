package me.hfox.morphix.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Reference {

    /*
     * TODO: Decide on implementation of annotation-handled features
     * boolean dbRef() default false;
     */

}
