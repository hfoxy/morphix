package uk.hfox.morphix.transform;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ConverterTest {

    @Test
    void pullBasic() {
        BasicConverter converter = new BasicConverter();
        converter.pull("key", new Object());
        converter.pull("key", new Object(), null, null);
    }

    @Test
    void pullValue() {
        ValueConverter converter = new ValueConverter();
        converter.pull("key", new Object());
        converter.pull("key", new Object(), null, null);
    }

    private static class BasicConverter implements Converter<Object> {

        @Override
        public Object pull(String key, Object entry) {
            assertNotNull(key);
            assertNotNull(entry);
            return null;
        }

        @Override
        public void push(String key, Object entry, Object value) {
            // not interested
        }
    }

    private static class ValueConverter implements Converter<Object> {

        @Override
        public Object pull(String key, Object entry, Object value, Class<?> type) {
            assertNotNull(key);
            assertNotNull(entry);
            assertNull(value);
            assertNull(type);
            return null;
        }

        @Override
        public void push(String key, Object entry, Object value) {
            // not interested
        }
    }

}