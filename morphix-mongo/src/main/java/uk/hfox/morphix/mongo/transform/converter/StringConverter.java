package uk.hfox.morphix.mongo.transform.converter;

import org.bson.Document;
import uk.hfox.morphix.transform.Converter;

public class StringConverter implements Converter<Document> {

    @Override
    public String pull(String key, Document entry) {
        return entry.getString(key);
    }

    @Override
    public void push(String key, Document entry, Object value) {
        entry.put(key, value);
    }

}
