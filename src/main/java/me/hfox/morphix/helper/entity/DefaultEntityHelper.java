package me.hfox.morphix.helper.entity;

import me.hfox.morphix.Morphix;
import me.hfox.morphix.MorphixDefaults;
import me.hfox.morphix.annotation.entity.Entity;
import me.hfox.morphix.exception.MorphixException;
import me.hfox.morphix.util.AnnotationUtils;

public class DefaultEntityHelper implements EntityHelper {

    private Morphix morphix;

    public DefaultEntityHelper(Morphix morphix) {
        this.morphix = morphix;
    }

    @Override
    public String getCollectionName(Class<?> clazz) {
        Entity entity = AnnotationUtils.getHierarchicalAnnotation(clazz, Entity.class);
        if (entity == null) {
            throw new MorphixException("No Entity annotation could be found for " + clazz.getSimpleName());
        }

        return getCollectionName(clazz, entity);
    }

    @Override
    public String getCollectionName(Class<?> clazz, Entity entity) {
        String name;
        if (entity.value() == null || entity.value().equals(MorphixDefaults.DEFAULT_COLLECTION_NAME)) {
            name = morphix.getNameHelper(entity.nameHelper()).generate(clazz);
        } else {
            name = entity.value();
        }

        return name;
    }

}
