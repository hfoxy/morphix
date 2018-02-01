package uk.hfox.morphix.exception.connection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvalidConfigurationExceptionTest {

    @Test
    void getQueryBuilder() {
        String msg = "Test Message";
        InvalidConfigurationException ex = new InvalidConfigurationException(msg);
        assertEquals(msg, ex.getMessage());

        Exception cause = new Exception();
        ex = new InvalidConfigurationException(msg, cause);
        assertEquals(msg, ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

}