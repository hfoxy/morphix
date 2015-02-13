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
            boolean type = (this.type == null && data.type == null) || (this.type != null && data.type != null && this.type.equals(data.type));
            boolean parent = (this.parent == null && data.parent == null) || (this.parent != null && data.parent != null && this.parent.equals(data.parent));
            boolean field = (this.field == null && data.field == null) || (this.field != null && data.field != null && this.field.equals(data.field));
            boolean morphix = (this.morphix == null && data.morphix == null) || (this.morphix != null && data.morphix != null && this.morphix.equals(data.morphix));

            return type && parent && field && morphix;
        }

        return super.equals(object);
    }

}
