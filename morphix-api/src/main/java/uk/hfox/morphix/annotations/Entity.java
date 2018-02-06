package uk.hfox.morphix.annotations;

import uk.hfox.morphix.connector.MorphixConnector;


public @interface Entity {

    /**
     * Defines the list of Morphix connector types that this entity can be used on.
     * Example: {MorphixMongoConnector.class, MorphixPostgreConnector.class}
     * <p>
     * An empty array represents "no filter", allowing all connectors.
     *
     * @return The list of Morphix connector types
     */
    Class<? extends MorphixConnector>[] value() default {};

}
