package uk.hfox.morphix.mongo.transform;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import uk.hfox.morphix.annotations.Entity;
import uk.hfox.morphix.mongo.TestMorphixMongoConnector;
import uk.hfox.morphix.transform.FieldFilter;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MongoTransformerTest {

    @Test
    void toDB() {
        TestMorphixMongoConnector connector = new TestMorphixMongoConnector();
        MongoTransformer transformer = connector.getTransformer();

        Base base = new Base();
        Document document = transformer.toDB(base);
        int subHash = base.sub.hashCode();

        ((Document) document.get("sub")).put("sub", "updated");
        transformer.fromGenericDB(document, base, null);

        assertEquals("updated", base.sub.sub);
        assertEquals(subHash, base.sub.hashCode());

        Base second = transformer.fromGenericDB(document, null, Base.class);
        assertNotEquals(base.hashCode(), second.hashCode());

        document.put("a", (byte) 9);
        document.put("b", (short) 8);
        document.put("c", 7);
        document.put("d", 6L);

        String original = base.i;
        document.put("i", "ignored");

        transformer.fromGenericDB(document, base, null, new FieldFilter(false, "a", "b", "c", "d"));
        assertEquals(document.get("a"), base.a);
        assertEquals(document.get("b"), base.b);
        assertEquals(document.get("c"), base.c);
        assertEquals(document.get("d"), base.d);
        assertEquals(original, base.i);
    }

    @Entity
    public static class Base {

        private byte a = 1;
        private short b = 2;
        private int c = 3;
        private long d = 4;
        private float e = 5;
        private double f = 6;
        private char g = '7';
        private boolean h = true;
        private String i = "9";
        private LocalDateTime time = LocalDateTime.now();
        private Sub sub = new Sub();

        @Override
        public String toString() {
            return "Base{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    ", d=" + d +
                    ", e=" + e +
                    ", f=" + f +
                    ", g=" + g +
                    ", h=" + h +
                    ", i='" + i + '\'' +
                    ", time=" + time +
                    ", sub=" + sub +
                    '}';
        }

    }

    @Entity
    public static class Sub {

        private String sub = "test";

        @Override
        public String toString() {
            return "Sub{" +
                    "sub='" + sub + '\'' +
                    '}';
        }

    }

}