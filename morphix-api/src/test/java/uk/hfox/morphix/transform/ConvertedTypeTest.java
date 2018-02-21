package uk.hfox.morphix.transform;

import org.junit.jupiter.api.Test;
import uk.hfox.morphix.annotations.Entity;
import uk.hfox.morphix.annotations.field.Id;
import uk.hfox.morphix.annotations.field.Reference;
import uk.hfox.morphix.exception.mapper.MorphixEntityException;
import uk.hfox.morphix.exception.mapper.MorphixFieldException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static uk.hfox.morphix.transform.ConvertedType.*;

class ConvertedTypeTest {

    @Test
    void byteType() {
        assertFalse(BYTE.isSatisfied(Object.class));
        assertTrue(BYTE.isSatisfied(byte.class));
        assertTrue(BYTE.isSatisfied(Byte.class));
    }

    @Test
    void shortType() {
        assertFalse(SHORT.isSatisfied(Object.class));
        assertTrue(SHORT.isSatisfied(short.class));
        assertTrue(SHORT.isSatisfied(Short.class));
    }

    @Test
    void intType() {
        assertFalse(INTEGER.isSatisfied(Object.class));
        assertTrue(INTEGER.isSatisfied(int.class));
        assertTrue(INTEGER.isSatisfied(Integer.class));
    }

    @Test
    void longType() {
        assertFalse(LONG.isSatisfied(Object.class));
        assertTrue(LONG.isSatisfied(long.class));
        assertTrue(LONG.isSatisfied(Long.class));
    }

    @Test
    void floatType() {
        assertFalse(FLOAT.isSatisfied(Object.class));
        assertTrue(FLOAT.isSatisfied(float.class));
        assertTrue(FLOAT.isSatisfied(Float.class));
    }

    @Test
    void doubleType() {
        assertFalse(DOUBLE.isSatisfied(Object.class));
        assertTrue(DOUBLE.isSatisfied(double.class));
        assertTrue(DOUBLE.isSatisfied(Double.class));
    }

    @Test
    void charType() {
        assertFalse(CHARACTER.isSatisfied(Object.class));
        assertTrue(CHARACTER.isSatisfied(char.class));
        assertTrue(CHARACTER.isSatisfied(Character.class));
    }

    @Test
    void booleanType() {
        assertFalse(BOOLEAN.isSatisfied(Object.class));
        assertTrue(BOOLEAN.isSatisfied(boolean.class));
        assertTrue(BOOLEAN.isSatisfied(Boolean.class));
    }

    @Test
    void stringType() {
        assertFalse(STRING.isSatisfied(Object.class));
        assertTrue(STRING.isSatisfied(String.class));
    }

    @Test
    void dateTimeType() {
        assertFalse(DATETIME.isSatisfied(Object.class));
        assertTrue(DATETIME.isSatisfied(LocalDateTime.class));
    }

    @Test
    void entityType() {
        assertFalse(ENTITY.isSatisfied(Object.class));
        assertTrue(ENTITY.isSatisfied(EntityTest.class));
    }

    @Test
    void referenceType() {
        assertFalse(REFERENCE.isSatisfied(Object.class));
        assertTrue(REFERENCE.isSatisfied(EntityTest.class));
    }

    @Test
    void idType() {
        assertFalse(ID.isSatisfied(Object.class));
        assertFalse(ID.isSatisfied(EntityTest.class));
    }

    @Test
    void findByPrimitiveField() throws Exception {
        check(PrimitiveTypes.class);
    }

    @Test
    void findByClassField() throws Exception {
        Class<ObjectTypes> cls = ObjectTypes.class;
        check(cls);

        assertEquals(STRING, ConvertedType.findByField(cls.getDeclaredField("stringField")));

        assertEquals(DATETIME, ConvertedType.findByField(cls.getDeclaredField("dateTimeField")));

        assertEquals(ENTITY, ConvertedType.findByField(cls.getDeclaredField("entityField")));
        assertThrows(MorphixFieldException.class, () -> ConvertedType.findByField(cls.getDeclaredField("objectField")));

        assertEquals(REFERENCE, ConvertedType.findByField(cls.getDeclaredField("referenceField")));
        assertThrows(MorphixEntityException.class, () -> ConvertedType.findByField(cls.getDeclaredField("objectReferenceField")));

        assertEquals(ID, ConvertedType.findByField(cls.getDeclaredField("id")));
    }

    private void check(Class<?> cls) throws Exception {
        assertThrows(IllegalArgumentException.class, () -> ConvertedType.findByField(null));

        assertEquals(BYTE, ConvertedType.findByField(cls.getDeclaredField("byteField")));
        assertEquals(SHORT, ConvertedType.findByField(cls.getDeclaredField("shortField")));
        assertEquals(INTEGER, ConvertedType.findByField(cls.getDeclaredField("intField")));
        assertEquals(LONG, ConvertedType.findByField(cls.getDeclaredField("longField")));
        assertEquals(FLOAT, ConvertedType.findByField(cls.getDeclaredField("floatField")));
        assertEquals(DOUBLE, ConvertedType.findByField(cls.getDeclaredField("doubleField")));
        assertEquals(CHARACTER, ConvertedType.findByField(cls.getDeclaredField("characterField")));
        assertEquals(BOOLEAN, ConvertedType.findByField(cls.getDeclaredField("booleanField")));
    }

    @Entity
    public static class EntityTest {
    }

    public static class PrimitiveTypes {

        private byte byteField;
        private short shortField;
        private int intField;
        private long longField;
        private float floatField;
        private double doubleField;
        private char characterField;
        private boolean booleanField;

    }

    public static class ObjectTypes {

        private Byte byteField;
        private Short shortField;
        private Integer intField;
        private Long longField;
        private Float floatField;
        private Double doubleField;
        private Character characterField;
        private Boolean booleanField;

        private String stringField;

        private LocalDateTime dateTimeField;

        private EntityTest entityField;
        private Object objectField;

        @Reference
        private EntityTest referenceField;

        @Reference
        private Object objectReferenceField;

        @Id
        private Object id;

    }

}