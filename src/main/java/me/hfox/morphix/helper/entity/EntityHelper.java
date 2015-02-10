package me.hfox.morphix.helper.entity;

import me.hfox.morphix.annotation.Entity;

public interface EntityHelper {

    public String getCollectionName(Class<?> clazz);

    public String getCollectionName(Class<?> clazz, Entity entity);

}
