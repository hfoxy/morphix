package me.hfox.morphix.mongo.helper.polymorphism;

import com.mongodb.DBObject;

public interface PolymorhpismHelper {

    public void register(Class<?> type, PolymorphSelector<?> selector);

    public void register(Class<?> type, PolymorphSelector<?> selector, boolean highest);

    public Class<?> generate(DBObject dbObject);

    public Class<?> generate(String string);

    public void store(DBObject dbObject, Class<?> cls);

}
