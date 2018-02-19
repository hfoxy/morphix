package uk.hfox.morphix.mongo.transform.converter;

import org.bson.Document;
import uk.hfox.morphix.transform.Converter;

public class FloatConverter implements Converter<Document> {

    @Override
    public Float pull(String key, Document entry) {
        double value = entry.getDouble(key);
        return (float) value;
    }

    @Override
    public void push(String key, Document entry, Object value) {
        entry.put(key, value);
    }

}
