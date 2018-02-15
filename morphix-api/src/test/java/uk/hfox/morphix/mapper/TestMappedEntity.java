package uk.hfox.morphix.mapper;

import uk.hfox.morphix.annotations.field.Properties;
import uk.hfox.morphix.transform.ConvertedType;
import uk.hfox.morphix.transform.Converter;
import uk.hfox.morphix.transform.TestTransformer;

import java.lang.reflect.Field;

public class TestMappedEntity extends MappedEntity<TestMappedField, TestTransformer> {

    public TestMappedEntity(TestTransformer transformer, Class<?> clazz) {
        super(transformer, clazz);
    }

    @Override
    protected TestMappedField getField(Properties properties, Field field) {
        String name = properties != null ? properties.name() : field.getName();
        ConvertedType type = ConvertedType.findByField(field);
        Converter<Object> converter = getTransformer().getConverter(type);

        TestMappedField mappedField = new TestMappedField(name, type, field, converter);
        return mappedField;
    }

}
