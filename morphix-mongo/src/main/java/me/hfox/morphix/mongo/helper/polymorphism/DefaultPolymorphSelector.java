package me.hfox.morphix.mongo.helper.polymorphism;

import com.mongodb.DBObject;

public class DefaultPolymorphSelector extends StringPolymorphSelector<Object> {

    public DefaultPolymorphSelector() {
        super(defaultField, "className");
    }

    @Override
    public Class<?> fromObject(DBObject document) {
        Class<?> cls = super.fromObject(document);
        // if (cls == null) {
        //     throw new MorphixException("The document supplied could not be polymorphed because it does not have a '" + defaultField + "' field");
        // }

        return cls;
    }

}
