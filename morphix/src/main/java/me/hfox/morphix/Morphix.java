package me.hfox.morphix;

import me.hfox.morphix.connector.MorphixConnector;

/**
 * Created by Harry on 27/11/2017.
 */
public class Morphix {

    private MorphixConnector connector;

    public Morphix(MorphixConnector connector) {
        this.connector = connector;
    }

    /**
     * Gets the connector used by this Morphix instance
     * @return The connector used by this Morphix instance
     */
    public MorphixConnector getConnector() {
        return connector;
    }

}
