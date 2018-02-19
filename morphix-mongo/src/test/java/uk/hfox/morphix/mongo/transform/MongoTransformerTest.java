package uk.hfox.morphix.mongo.transform;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import uk.hfox.morphix.annotations.Entity;
import uk.hfox.morphix.mongo.TestMorphixMongoConnector;

import java.time.LocalDateTime;

class MongoTransformerTest {

    @Test
    void toDB() {
        TestMorphixMongoConnector connector = new TestMorphixMongoConnector();
        MongoTransformer transformer = connector.getTransformer();

        Base base = new Base();
        Document document = transformer.toDB(base);

        System.out.println(document);
        System.out.println(base);
        System.out.println(base.sub.hashCode());

        ((Document) document.get("sub")).put("sub", "scrub");
        transformer.fromGenericDB(document, base, null);
        System.out.println(base);
        System.out.println(base.sub.hashCode());
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