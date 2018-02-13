package uk.hfox.morphix.exception.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MorphixEntityExceptionTest {

    @Test
    void getQueryBuilder() {
        String msg = "Test Message";
        MorphixEntityException ex = new MorphixEntityException(msg);
        assertEquals(msg, ex.getMessage());

        Exception cause = new Exception();
        ex = new MorphixEntityException(msg, cause);
        assertEquals(msg, ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

}