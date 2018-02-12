package uk.hfox.morphix.transform;

import org.junit.jupiter.api.Test;
import uk.hfox.morphix.annotations.Entity;

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
    void entityType() {
        assertFalse(ENTITY.isSatisfied(Object.class));
        assertTrue(ENTITY.isSatisfied(EntityTest.class));
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

        assertEquals(ENTITY, ConvertedType.findByField(cls.getDeclaredField("entityField")));
        assertThrows(IllegalArgumentException.class, () -> ConvertedType.findByField(cls.getDeclaredField("objectField")));
    }

    private void check(Class<?> cls) throws Exception {
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

        private EntityTest entityField;
        private Object objectField;

    }

}