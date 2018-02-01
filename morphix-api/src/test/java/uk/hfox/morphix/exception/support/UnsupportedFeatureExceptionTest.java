package uk.hfox.morphix.exception.support;

import org.junit.jupiter.api.Test;
import uk.hfox.morphix.connector.MorphixConnector;
import uk.hfox.morphix.connector.TestConnector;
import uk.hfox.morphix.exception.query.InvalidQueryException;
import uk.hfox.morphix.query.QueryBuilder;
import uk.hfox.morphix.query.TestQueryBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnsupportedFeatureExceptionTest {

    @Test
    void getQueryBuilder() {
        MorphixConnector connector = new TestConnector();
        UnsupportedFeatureException ex = new UnsupportedFeatureException(connector, "Test Exception");
        assertEquals(connector, ex.getConnector());
    }
    
}