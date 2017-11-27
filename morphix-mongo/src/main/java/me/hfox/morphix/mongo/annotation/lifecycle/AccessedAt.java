package me.hfox.morphix.mongo.annotation.lifecycle;

import me.hfox.morphix.mongo.helper.lifecycle.TimeLibrary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessedAt {

    public TimeLibrary value() default TimeLibrary.DEFAULT;

    public boolean onlyUpdateOnLoad() default false;

}
