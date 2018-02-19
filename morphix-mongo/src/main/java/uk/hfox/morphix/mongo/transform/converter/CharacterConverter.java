package uk.hfox.morphix.mongo.transform.converter;

import org.bson.Document;
import uk.hfox.morphix.transform.Converter;

public class CharacterConverter implements Converter<Document> {

    @Override
    public Character pull(String key, Document entry) {
        return entry.getString(key).charAt(0);
    }

    @Override
    public void push(String key, Document entry, Object value) {
        if (!(value instanceof Character)) {
            throw new IllegalArgumentException("value must be character");
        }

        entry.put(key, value.toString());
    }

}
