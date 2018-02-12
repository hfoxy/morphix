package uk.hfox.morphix.mapper;

import org.junit.jupiter.api.Test;
import uk.hfox.morphix.transform.Converter;
import uk.hfox.morphix.utils.Conditions;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MappedFieldTest {

    @Test
    void getField() throws Exception {
        Field field = TestClass.class.getDeclaredField("field");
        BlankConverter converter = new BlankConverter();
        ObjectMappedField mapped = new ObjectMappedField(field, converter);
        assertEquals(field, mapped.getField());
    }

    @Test
    void getConverter() throws Exception {
        Field field = TestClass.class.getDeclaredField("field");
        BlankConverter converter = new BlankConverter();
        ObjectMappedField mapped = new ObjectMappedField(field, converter);
        assertEquals(converter, mapped.getConverter());
    }

    public static class TestClass {

        private String field;

    }

    public static class BlankConverter implements Converter<Object> {

        @Override
        public Object pull(String key, Object entry) {
            throw Conditions.unimplemented();
        }

        @Override
        public void push(String key, Object entry, Object value) {
            throw Conditions.unimplemented();
        }

    }

    public static class ObjectMappedField extends MappedField<Object> {

        public ObjectMappedField(Field field, Converter<Object> converter) {
            super(field, converter);
        }

    }

}