package uk.hfox.morphix.mongo.transform.converter;

import org.bson.Document;
import uk.hfox.morphix.transform.Converter;

public class DoubleConverter implements Converter<Document> {

    @Override
    public Double pull(String key, Document entry) {
        return entry.getDouble(key);
    }

    @Override
    public void push(String key, Document entry, Object value) {
        entry.put(key, value);
    }

}
