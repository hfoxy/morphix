package me.hfox.morphix;

import me.hfox.morphix.exception.MorphixException;
import me.hfox.morphix.helper.entity.DefaultEntityHelper;
import me.hfox.morphix.helper.entity.EntityHelper;
import me.hfox.morphix.helper.name.NameHelper;
import me.hfox.morphix.mapping.ObjectMapper;
import me.hfox.morphix.mapping.ObjectMapperImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Morphix {

    private MorphixOptions options;
    private ObjectMapper mapper;

    private EntityHelper entityHelper;
    private Map<Class<? extends NameHelper>, NameHelper> nameHelpers;

    public Morphix() {
        this(null, null);
    }

    public Morphix(MorphixOptions options) {
        this(options, null);
    }

    public Morphix(ObjectMapper mapper) {
        this(null, mapper);
    }

    public Morphix(MorphixOptions options, ObjectMapper mapper) {
        this.options = (options == null ? MorphixOptions.builder().build() : options);
        this.mapper = (mapper == null ? new ObjectMapperImpl() : mapper);

        this.entityHelper = new DefaultEntityHelper(this);
        this.nameHelpers = new HashMap<>();
        getNameHelper(MorphixDefaults.DEFAULT_NAME_HELPER);
    }

    public MorphixOptions getOptions() {
        return options;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public EntityHelper getEntityHelper() {
        return entityHelper;
    }

    public void setEntityHelper(EntityHelper entityHelper) {
        this.entityHelper = entityHelper;
    }

    public Map<Class<? extends NameHelper>, NameHelper> getNameHelpers() {
        return nameHelpers;
    }

    public NameHelper getNameHelper(Class<? extends NameHelper> clazz) {
        NameHelper helper = nameHelpers.get(clazz);
        if (helper == null) {
            helper = createHelper(clazz);
            nameHelpers.put(clazz, helper);
        }

        return helper;
    }

    public NameHelper createHelper(Class<? extends NameHelper> clazz) {
        try {
            Constructor<? extends NameHelper> constructor = clazz.getConstructor(Morphix.class);
            return constructor.newInstance(this);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new MorphixException(ex);
        }
    }

}
