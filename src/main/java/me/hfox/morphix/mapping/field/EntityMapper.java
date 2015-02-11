package me.hfox.morphix.mapping.field;

import me.hfox.morphix.Morphix;
import me.hfox.morphix.annotation.NotSaved;
import me.hfox.morphix.annotation.Reference;
import me.hfox.morphix.annotation.entity.Entity;
import me.hfox.morphix.annotation.entity.IgnoreEmpty;
import me.hfox.morphix.annotation.entity.IgnoreNull;
import me.hfox.morphix.annotation.entity.Polymorph;
import me.hfox.morphix.util.AnnotationUtils;

import java.lang.reflect.Field;

public class EntityMapper<T> extends FieldMapper<T> {

    private Entity entity;
    private IgnoreEmpty ignoreEmpty;
    private IgnoreNull ignoreNull;
    private Polymorph polymorph;
    private boolean polymorphEnabled;

    private Reference reference;
    private NotSaved notSaved;

    public EntityMapper(Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        super(type, parent, field, morphix);
    }

    @Override
    protected void discover() {
        super.discover();
        entity = AnnotationUtils.getHierarchicalAnnotation(type, Entity.class);
        ignoreEmpty = AnnotationUtils.getHierarchicalAnnotation(type, IgnoreEmpty.class);
        ignoreNull = AnnotationUtils.getHierarchicalAnnotation(type, IgnoreNull.class);
        polymorph = AnnotationUtils.getHierarchicalAnnotation(type, Polymorph.class);
        polymorphEnabled = polymorph == null || polymorph.value();

        reference = field.getAnnotation(Reference.class);
        notSaved = field.getAnnotation(NotSaved.class);
    }

    @Override
    public Object unmarshal(Object obj) {
        return null;
    }

    @Override
    public Object marshal(Object obj) {
        return null;
    }

}
