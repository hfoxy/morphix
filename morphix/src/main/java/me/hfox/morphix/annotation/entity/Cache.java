package me.hfox.morphix.annotation.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

    /*
     * String value() default MorphixDefaults.DEFAULT_CACHE_NAME;
     * TODO: Setup caching without conflicts
     */

    boolean enabled() default true;

}