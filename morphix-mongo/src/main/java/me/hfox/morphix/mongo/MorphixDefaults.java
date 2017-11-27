package me.hfox.morphix.mongo;

import me.hfox.morphix.mongo.helper.name.DefaultNameHelper;
import me.hfox.morphix.mongo.helper.name.NameHelper;

public final class MorphixDefaults {

    public static final String DEFAULT_COLLECTION_NAME = ".";
    public static final String DEFAULT_FIELD_NAME = ".";
    public static final Class<? extends NameHelper> DEFAULT_NAME_HELPER = DefaultNameHelper.class;
    public static final String DEFAULT_CACHE_NAME = "default";
    public static final boolean DEFAULT_LIFECYCLE = true;

}
