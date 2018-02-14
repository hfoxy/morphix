package uk.hfox.morphix.exception.transformer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MorphixTransformationExceptionTest {

    @Test
    void getQueryBuilder() {
        String msg = "Test Message";
        MorphixTransformationException ex = new MorphixTransformationException(msg);
        assertEquals(msg, ex.getMessage());

        Exception cause = new Exception();
        ex = new MorphixTransformationException(msg, cause);
        assertEquals(msg, ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

}