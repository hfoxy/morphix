package me.hfox.morphix.helper.polymorphism;

import com.mongodb.DBObject;

public interface PolymorhpismHelper {

    public Class<?> generate(DBObject dbObject);

    public Class<?> generate(String string);

}
