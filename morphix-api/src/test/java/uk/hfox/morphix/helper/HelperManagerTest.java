package uk.hfox.morphix.helper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.hfox.morphix.connector.TestConnector;

import static org.junit.jupiter.api.Assertions.*;

class HelperManagerTest {

    private TestConnector connector;
    private HelperManager<TestConnector> manager;
    private TestHelper testHelper;

    @BeforeEach
    void setUp() {
        this.connector = new TestConnector();
        this.manager = new HelperManager<>(connector);

        this.testHelper = new TestHelper(connector);
        this.manager.setHelper("test", testHelper);
    }

    @Test
    void getConnector() {
        assertEquals(this.connector, this.manager.getConnector());
    }

    @Test
    void hasHelper() {
        assertTrue(this.manager.hasHelper("test"));
        assertFalse(this.manager.hasHelper("no-test"));
    }

    @Test
    void getHelper() {
        Helper<TestConnector> helper = this.manager.getHelper("test");
        assertNotNull(helper);
        assertEquals(this.testHelper, helper);

        assertNull(this.manager.getHelper("no-test"));

        TestHelper test = this.manager.getHelper("test", TestHelper.class);
        assertNotNull(test);
        assertEquals(this.testHelper, test);

        assertThrows(IllegalArgumentException.class, () -> this.manager.getHelper("test", BlankHelper.class));

        assertNull(this.manager.getHelper("no-test", TestHelper.class));
    }

    @AfterEach
    void tearDown() {
    }

    public static class TestHelper extends Helper<TestConnector> {

        public TestHelper(TestConnector connector) {
            super(connector);
        }

    }

    public static class BlankHelper extends Helper<TestConnector> {

        public BlankHelper(TestConnector connector) {
            super(connector);
        }

    }

}