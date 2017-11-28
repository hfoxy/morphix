package me.hfox.morphix.exception.support;

import me.hfox.morphix.connector.MorphixConnector;
import me.hfox.morphix.exception.MorphixException;

/**
 * Created by Harry on 28/11/2017.
 *
 * Thrown by implementing libraries when a database does not support a feature
 */
public class UnsupportedFeatureException extends MorphixException {

    private final MorphixConnector connector;

    public UnsupportedFeatureException(MorphixConnector connector, String message) {
        super(message);
        this.connector = connector;
    }

    /**
     * Get the connector which does not support the feature
     * @return The connector which lacks feature support
     */
    public MorphixConnector getConnector() {
        return connector;
    }

}
