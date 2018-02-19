package uk.hfox.morphix.mongo.transform.converter;

import org.bson.Document;
import uk.hfox.morphix.transform.Converter;

public class LongConverter implements Converter<Document> {

    @Override
    public Long pull(String key, Document entry) {
        return entry.getLong(key);
    }

    @Override
    public void push(String key, Document entry, Object value) {
        entry.put(key, value);
    }

}
