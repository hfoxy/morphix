package uk.hfox.morphix.mongo.transform;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import uk.hfox.morphix.annotations.Entity;
import uk.hfox.morphix.annotations.Polymorphic;
import uk.hfox.morphix.annotations.field.Properties;
import uk.hfox.morphix.exception.mapper.MorphixEntityException;
import uk.hfox.morphix.mongo.TestMorphixMongoConnector;
import uk.hfox.morphix.transform.FieldFilter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class MongoTransformerTest {

    @Test
    void transformation() {
        TestMorphixMongoConnector connector = new TestMorphixMongoConnector();
        MongoTransformer transformer = connector.getTransformer();

        Base base = new Base();
        Document document = transformer.toDB(base);
        int subHash = base.sub.hashCode();
        System.out.println(document);

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

        assertNull(transformer.fromGenericDB(new Document(), null, null));

        assertThrows(MorphixEntityException.class, () -> transformer
                .fromGenericDB(new Document("name", "entity"), null, Broken.class));

        assertNotNull(transformer.fromGenericDB(new Document("name", "test"), null, Private.class));

        Index kidsIndex = new Index();
        kidsIndex.name = "kids";
        kidsIndex.book = new KidsBook("rawr dinosaur", "test");
        Document kidsDoc = transformer.toDB(kidsIndex);

        connector.getEntityManager().getCache().getCache().clear();
        connector.getEntityManager().getCache().getIds().clear();

        kidsIndex = transformer.fromGenericDB(kidsDoc, null, Index.class);
        assertEquals(KidsBook.class, kidsIndex.book.getClass());

        Index adultIndex = new Index();
        adultIndex.name = "adult";
        adultIndex.book = new AdultBook("test", "test");
        Document adultDoc = transformer.toDB(adultIndex);

        connector.getEntityManager().getCache().getCache().clear();
        connector.getEntityManager().getCache().getIds().clear();

        adultIndex = transformer.fromGenericDB(adultDoc, null, Index.class);
        assertEquals(AdultBook.class, adultIndex.book.getClass());
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

        @Properties(name = "my_string")
        private String myString = "test";

        private String[] simpleArray = new String[]{"test", "simple", "array"};
        private String[][] doubleDimensionArray;
        private String[][][] tripleDimensionArray;

        public Base() {
            final int SIZE = 3;

            int val = 1;
            this.doubleDimensionArray = new String[SIZE][];
            for (int j = 0; j < this.doubleDimensionArray.length; j++) {
                this.doubleDimensionArray[j] = new String[SIZE];
                for (int k = 0; k < this.doubleDimensionArray[j].length; k++) {
                    this.doubleDimensionArray[j][k] = val++ + "a";
                }
            }

            val = 1;
            this.tripleDimensionArray = new String[SIZE][][];
            for (int j = 0; j < this.tripleDimensionArray.length; j++) {
                this.tripleDimensionArray[j] = new String[SIZE][];
                for (int k = 0; k < this.tripleDimensionArray[j].length; k++) {
                    this.tripleDimensionArray[j][k] = new String[SIZE];
                    for (int l = 0; l < this.tripleDimensionArray[j][k].length; l++) {
                        this.tripleDimensionArray[j][k][l] = val++ + "a";
                    }
                }
            }
        }

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
                    ", myString='" + myString + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Base base = (Base) o;
            return a == base.a &&
                    b == base.b &&
                    c == base.c &&
                    d == base.d &&
                    Float.compare(base.e, e) == 0 &&
                    Double.compare(base.f, f) == 0 &&
                    g == base.g &&
                    h == base.h &&
                    Objects.equals(i, base.i) &&
                    Objects.equals(time, base.time) &&
                    Objects.equals(sub, base.sub) &&
                    Objects.equals(myString, base.myString) &&
                    Arrays.equals(simpleArray, base.simpleArray) &&
                    Arrays.equals(doubleDimensionArray, base.doubleDimensionArray) &&
                    Arrays.equals(tripleDimensionArray, base.tripleDimensionArray);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(a, b, c, d, e, f, g, h, i, time, sub, myString);
            result = 31 * result + Arrays.hashCode(simpleArray);
            result = 31 * result + Arrays.hashCode(doubleDimensionArray);
            result = 31 * result + Arrays.hashCode(tripleDimensionArray);
            return result;
        }

    }

    @Entity
    public static class Sub {

        private String sub = "test";

        @Override
        public String toString() {
            return "Sub[" + hashCode() + "]{" +
                    "sub='" + sub + '\'' +
                    '}';
        }

    }

    @Entity
    public static class Broken {

        private String name;

        public Broken(String name) {
            this.name = name;
        }

    }

    @Entity
    public static class Private {

        private String name;

        private Private() {
            // db only
        }

    }

    @Entity
    private static class Index {

        private String name;
        private Book book;

    }

    @Entity
    @Polymorphic
    private static abstract class Book {

        private String name;

        private Book() {
            // db only
        }

        public Book(String name) {
            this.name = name;
        }

    }

    @Entity
    private static class KidsBook extends Book {

        private String data;

        private KidsBook() {
            // db only
        }

        public KidsBook(String name, String data) {
            super(name);
            this.data = data;
        }

    }

    @Entity
    private static class AdultBook extends Book {

        private String blurb;

        private AdultBook() {
            // db only
        }

        public AdultBook(String name, String blurb) {
            super(name);
            this.blurb = blurb;
        }

    }

}