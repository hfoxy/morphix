package uk.hfox.morphix.mongo.transform.converter;

import org.bson.Document;
import uk.hfox.morphix.transform.Converter;

public class BooleanConverter implements Converter<Document> {

    @Override
    public Boolean pull(String key, Document entry) {
        return entry.getBoolean(key);
    }

    @Override
    public void push(String key, Document entry, Object value) {
        entry.put(key, value);
    }

}
