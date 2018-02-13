package uk.hfox.morphix.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.hfox.morphix.transform.ConvertedType;
import uk.hfox.morphix.transform.Converter;
import uk.hfox.morphix.utils.Conditions;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MappedFieldTest {

    private ConvertedType type;
    private Field field;
    private BlankConverter converter;
    private ObjectMappedField mapped;

    @BeforeEach
    void setup() throws Exception {
        this.type = ConvertedType.ENTITY;
        this.field = TestClass.class.getDeclaredField("field");
        this.converter = new BlankConverter();
        this.mapped = new ObjectMappedField(type, field, converter);
    }

    @Test
    void getType() {
        assertEquals(type, mapped.getType());
    }

    @Test
    void getField() {
        assertEquals(field, mapped.getField());
    }

    @Test
    void getConverter() {
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

        public ObjectMappedField(ConvertedType type, Field field, Converter<Object> converter) {
            super(type, field, converter);
        }

    }

}