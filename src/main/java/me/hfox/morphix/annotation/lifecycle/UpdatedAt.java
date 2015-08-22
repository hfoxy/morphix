package me.hfox.morphix.annotation.lifecycle;

import me.hfox.morphix.helper.lifecycle.TimeLibrary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UpdatedAt {

    public TimeLibrary value() default TimeLibrary.DEFAULT;

}
