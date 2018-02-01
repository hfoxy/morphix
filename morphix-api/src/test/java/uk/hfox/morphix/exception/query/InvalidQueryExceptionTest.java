package uk.hfox.morphix.exception.query;

import org.junit.jupiter.api.Test;
import uk.hfox.morphix.query.QueryBuilder;
import uk.hfox.morphix.query.TestQueryBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvalidQueryExceptionTest {

    @Test
    void getQueryBuilder() {
        QueryBuilder<Object> query = new TestQueryBuilder<>();
        InvalidQueryException ex = new InvalidQueryException(query, "Test Exception");
        assertEquals(query, ex.getQueryBuilder());
    }

}