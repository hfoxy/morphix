package me.hfox.morphix.annotation.entity;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Polymorph {

    public boolean value() default true;

    public boolean storeAnnotatedClass() default true;

}
