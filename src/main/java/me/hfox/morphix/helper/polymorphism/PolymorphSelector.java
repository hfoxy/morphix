package me.hfox.morphix.helper.polymorphism;

import com.mongodb.DBObject;
import me.hfox.morphix.exception.MorphixException;

public abstract class PolymorphSelector<T> {

    protected static final String defaultField = "class_name";
    protected static final String defaultAnnotatedField = "annotated_class_name";

    public abstract String getField();

    protected Class<?> fromString(String string) {
        try {
            return Class.forName(string);
        } catch (NullPointerException | ClassNotFoundException ex) {
            throw new MorphixException(ex);
        }
    }

    public abstract Class<? extends T> select(DBObject document);

    public abstract void store(DBObject document, Class<?> type);

}
