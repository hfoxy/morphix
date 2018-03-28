package uk.hfox.morphix.mongo.transform.converter;

import org.junit.jupiter.api.Test;
import uk.hfox.morphix.annotations.Entity;
import uk.hfox.morphix.mongo.TestMorphixMongoConnector;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;
import uk.hfox.morphix.transform.ConvertedType;

class ArrayConverterTest {

    @Test
    void testBooleanArray() throws Exception {
        System.out.println(BooleanArrayEntity.class.getDeclaredField("value").getType());
        System.out.println(BooleanArrayEntity.class.getDeclaredField("array").getType().getComponentType());
        System.out.println(BooleanArrayEntity.class.getDeclaredField("multidimensional").getType().getComponentType());

        MorphixMongoConnector connector = new TestMorphixMongoConnector();
        ArrayConverter converter = (ArrayConverter) connector.getTransformer().getConverter(ConvertedType.ARRAY);

        // con.
    }

    @Entity
    private static class BooleanArrayEntity {

        private boolean value;
        private boolean[] array;
        private boolean[][] multidimensional;

    }

}