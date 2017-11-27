package me.hfox.morphix.annotation.entity;

import me.hfox.morphix.MorphixDefaults;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

    public String value() default MorphixDefaults.DEFAULT_CACHE_NAME;

    public boolean enabled() default true;

}