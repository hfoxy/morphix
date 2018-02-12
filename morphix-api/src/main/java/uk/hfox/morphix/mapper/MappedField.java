package uk.hfox.morphix.mapper;

import uk.hfox.morphix.transform.Converter;

import java.lang.reflect.Field;

/**
 * Represents a field to be mapped
 *
 * @param <T> The DB type
 */
public class MappedField<T> {

    private final Field field;
    private final Converter<T> converter;

    public MappedField(Field field, Converter<T> converter) {
        this.field = field;
        this.converter = converter;
    }

    /**
     * Gets the field to be mapped
     *
     * @return The field to be mapped
     */
    public Field getField() {
        return field;
    }

    /**
     * Gets the converter used for this field
     *
     * @return The converter to be used
     */
    public Converter<T> getConverter() {
        return converter;
    }

}
