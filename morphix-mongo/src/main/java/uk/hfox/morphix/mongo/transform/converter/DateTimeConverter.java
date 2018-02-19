package uk.hfox.morphix.mongo.transform.converter;

import org.bson.Document;
import uk.hfox.morphix.transform.Converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateTimeConverter implements Converter<Document> {

    @Override
    public LocalDateTime pull(String key, Document entry) {
        Date date = entry.getDate(key);
        Instant instant = date.toInstant();

        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    @Override
    public void push(String key, Document entry, Object value) {
        if (!(value instanceof LocalDateTime)) {
            throw new IllegalArgumentException("value must be LocalDateTime");
        }

        LocalDateTime local = (LocalDateTime) value;
        ZonedDateTime zoned = local.atZone(ZoneId.systemDefault());
        Date date = Date.from(zoned.toInstant());

        entry.put(key, date);
    }

}
