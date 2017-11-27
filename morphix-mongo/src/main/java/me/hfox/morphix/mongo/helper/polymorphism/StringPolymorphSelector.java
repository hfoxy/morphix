package me.hfox.morphix.mongo.helper.polymorphism;

import com.mongodb.DBObject;

public class StringPolymorphSelector<T> extends PolymorphSelector<T> {

    private String[] fields;

    public StringPolymorphSelector(String... fields) {
        if (fields.length < 1) {
            throw new IllegalArgumentException("String Polymorph Selectors require at least 1 field");
        }

        this.fields = fields;
    }

    @Override
    public String getField() {
        return fields[0];
    }

    @SuppressWarnings("unchecked")
    public Class<? extends T> fromObject(DBObject document) {
        String string = (String) document.get(defaultField);
        if (string != null) {
            return (Class<? extends T>) fromString(string);
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends T> select(DBObject document) {
        String string = null;
        for (String field : fields) {
            if (document.get(field) != null) {
                string = (String) document.get(field);
                break;
            }
        }

        if (string == null) {
            return fromObject(document);
        }

        return (Class<? extends T>) fromString(string);
    }

    @Override
    public void store(DBObject document, Class<?> type) {
        document.put(fields[0], type.getName());
    }

}
