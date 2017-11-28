package me.hfox.morphix.exception.query;

import me.hfox.morphix.exception.MorphixException;
import me.hfox.morphix.query.Query;

/**
 * Created by Harry on 28/11/2017.
 *
 * Thrown when a query has valid parameters
 */
public class InvalidQueryException extends MorphixException {

    private Query<?> query;

    public InvalidQueryException(Query<?> query, String reason) {
        super(reason);
        this.query = query;
    }

    /**
     * Get the query which caused this exception
     * @return The faulting query
     */
    public Query<?> getQuery() {
        return query;
    }

}
