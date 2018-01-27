package uk.hfox.morphix;

import org.junit.jupiter.api.Test;
import uk.hfox.morphix.connector.TestConnector;

import static org.junit.jupiter.api.Assertions.*;

class MorphixTest {

    @Test
    void getConnector() {
        assertThrows(IllegalArgumentException.class, () -> new Morphix(null));

        TestConnector connector = new TestConnector();
        Morphix morphix = new Morphix(connector);

        assertEquals(connector, morphix.getConnector());
    }

}