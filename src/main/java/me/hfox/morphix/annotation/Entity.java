package me.hfox.morphix.annotation;

import me.hfox.morphix.MorphixDefaults;

public @interface Entity {

    public String value() default MorphixDefaults.DEFAULT_COLLECTION_NAME;

}
