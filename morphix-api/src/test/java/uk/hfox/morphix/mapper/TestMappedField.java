package uk.hfox.morphix.mapper;

import uk.hfox.morphix.transform.ConvertedType;
import uk.hfox.morphix.transform.Converter;

import java.lang.reflect.Field;

public class TestMappedField extends MappedField<Object> {

    public TestMappedField(String name, ConvertedType type, Field field, Converter<Object> converter) {
        super(name, type, field, converter);
    }

}
