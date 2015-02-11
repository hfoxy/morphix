package me.hfox.morphix.annotation.entity;

import me.hfox.morphix.MorphixDefaults;
import me.hfox.morphix.helper.name.DefaultNameHelper;
import me.hfox.morphix.helper.name.NameHelper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {

    public String value() default MorphixDefaults.DEFAULT_COLLECTION_NAME;

    public boolean createOnDelete() default true;

    public boolean inheritParentFields() default true;

    public Class<? extends NameHelper> nameHelper() default DefaultNameHelper.class;

}
