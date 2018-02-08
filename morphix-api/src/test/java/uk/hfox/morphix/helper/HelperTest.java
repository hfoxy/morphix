package uk.hfox.morphix.helper;

import org.junit.jupiter.api.Test;
import uk.hfox.morphix.connector.TestConnector;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelperTest {

    @Test
    void getConnector() {
        TestConnector connector = new TestConnector();
        Helper<TestConnector> helper = new HelperManagerTest.TestHelper(connector);
        assertEquals(connector, helper.getConnector());
    }

}