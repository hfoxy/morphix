package me.hfox.morphix.mongo.annotation;

import me.hfox.morphix.mongo.MorphixDefaults;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {

    public String value() default MorphixDefaults.DEFAULT_FIELD_NAME;

}
