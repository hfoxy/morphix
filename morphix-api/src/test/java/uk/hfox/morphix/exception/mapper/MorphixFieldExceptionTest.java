package uk.hfox.morphix.exception.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MorphixFieldExceptionTest {

    @Test
    void getQueryBuilder() {
        String msg = "Test Message";
        Exception cause = new Exception();
        MorphixFieldException ex = new MorphixFieldException(msg, cause);
        assertEquals(msg, ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

}