package uk.hfox.morphix.mongo.transform.converter;

import org.bson.Document;
import uk.hfox.morphix.transform.Converter;

public class IntegerConverter implements Converter<Document> {

    @Override
    public Integer pull(String key, Document entry) {
        return entry.getInteger(key);
    }

    @Override
    public void push(String key, Document entry, Object value) {
        entry.put(key, value);
    }

}
