package uk.hfox.morphix.mongo.transform.converter;

import org.bson.Document;
import uk.hfox.morphix.transform.Converter;

public class ByteConverter implements Converter<Document> {

    @Override
    public Byte pull(String key, Document entry) {
        int value = entry.getInteger(key);
        return (byte) value;
    }

    @Override
    public void push(String key, Document entry, Object value) {
        entry.put(key, value);
    }

}
