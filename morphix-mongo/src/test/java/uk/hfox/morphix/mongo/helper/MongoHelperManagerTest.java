package uk.hfox.morphix.mongo.helper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.hfox.morphix.helper.Helper;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class MongoHelperManagerTest {

    private MongoHelperManager manager;
    private CollectionHelper helper;

    @BeforeAll
    void setUp() {
        this.manager = new MongoHelperManager(null);
        this.helper = new CollectionHelper(null);

        this.manager.setHelper("collection", this.helper);
    }

    @Test
    void getCollectionHelper() {
        assertEquals(this.helper, this.manager.getCollectionHelper());
    }

    @Test
    void setCollectionHelper() {
        CollectionHelper newHelper = new CollectionHelper(null);
        this.manager.setCollectionHelper(newHelper);

        assertEquals(newHelper, this.manager.getCollectionHelper());
        this.manager.setCollectionHelper(this.helper);

        assertEquals(this.helper, this.manager.getCollectionHelper());
    }

    @Test
    void setHelper() {
        this.manager.setHelper("test", new NotCollectionHelper());
        assertThrows(IllegalArgumentException.class, () -> this.manager.setHelper("collection", new NotCollectionHelper()));
    }

    public static class NotCollectionHelper extends Helper<MorphixMongoConnector> {

        public NotCollectionHelper() {
            super(null);
        }

    }

}