package uk.hfox.morphix.mongo.query.sort;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MongoQuerySortElementTest {

    @Test
    void append() {
        String field = "my_field";
        int value = 1;

        Document document = new Document();
        MongoQuerySortElement element = new MongoQuerySortElement(field, value);
        element.append(document);
        assertEquals(field, document.keySet().iterator().next());
        assertEquals(value, (int) document.getInteger(field));
    }

}