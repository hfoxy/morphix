package me.hfox.morphix.annotation.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {

    /*
     * TODO: Setup default collection/table name
     * String value() default MorphixDefaults.DEFAULT_COLLECTION_NAME;
     */

    boolean createOnDelete() default false;

    boolean inheritParentFields() default true;

    /*
     * TODO: Setup helpers for entity annotation
     * Class<? extends NameHelper> nameHelper() default DefaultNameHelper.class;
     */

}
