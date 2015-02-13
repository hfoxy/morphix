package me.hfox.morphix.mapping.field;

import me.hfox.morphix.Morphix;

import java.lang.reflect.Field;

public class FieldData {

    protected Class<?> type;
    protected Class<?> parent;
    protected Field field;
    protected Morphix morphix;

    public FieldData(Class<?> type, Class<?> parent, Field field, Morphix morphix) {
        this.type = type;
        this.parent = parent;
        this.field = field;
        this.morphix = morphix;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof FieldData) {
            FieldData data = (FieldData) object;
            return type.equals(data.type) && parent.equals(data.parent) && field.equals(data.field) && morphix.equals(data.morphix);
        }

        return super.equals(object);
    }

}
