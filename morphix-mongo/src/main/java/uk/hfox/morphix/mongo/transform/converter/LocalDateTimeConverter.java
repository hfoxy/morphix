/*-
 * ========================LICENSE_START========================
 * Morphix MongoDB
 * %%
 * Copyright (C) 2017 - 2018 Harry Fox
 * %%
 * This file is part of Morphix, licensed under the MIT License (MIT).
 *
 * Copyright 2018 Harry Fox <https://hfox.uk/>
 * Copyright 2018 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * ========================LICENSE_END========================
 */
package uk.hfox.morphix.mongo.transform.converter;

import org.bson.Document;
import uk.hfox.morphix.transform.Converter;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class LocalDateTimeConverter implements Converter<Document> {

    @Override
    public LocalDateTime pull(Object value, Object current, Class<?> type) {
        Date date = (Date) value;
        if (date == null) {
            return null;
        }

        Instant instant = date.toInstant();

        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    @Override
    public LocalDateTime pull(String key, Document entry) {
        return pull(entry.getDate(key), null, null);
    }

    @Override
    public Object push(Object value) {
        Date date = null;
        if (value != null) {
            LocalDateTime local = (LocalDateTime) value;
            ZonedDateTime zoned = local.atZone(ZoneId.systemDefault());
            date = Date.from(zoned.toInstant());
        }

        return date;
    }

    @Override
    public void push(String key, Document entry, Object value, Field field) {
        entry.put(key, push(value));
    }

}
