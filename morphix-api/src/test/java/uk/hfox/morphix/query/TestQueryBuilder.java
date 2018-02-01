package uk.hfox.morphix.query;

import uk.hfox.morphix.connector.MorphixConnector;
import uk.hfox.morphix.query.result.QueryResult;
import uk.hfox.morphix.utils.Testing;

public class TestQueryBuilder<T> implements QueryBuilder<T> {

    @Override
    public MorphixConnector getConnector() {
        throw Testing.unsupported();
    }

    @Override
    public Class<T> getQueryType() {
        throw Testing.unsupported();
    }

    @Override
    public FieldQueryBuilder<T> where(String field) {
        throw Testing.unsupported();
    }

    @Override
    public QuerySortBuilder<T> sort() {
        throw Testing.unsupported();
    }

    @Override
    public void delete() {
        throw Testing.unsupported();
    }

    @Override
    public void delete(boolean justOne) {
        throw Testing.unsupported();
    }

    @Override
    public QueryResult<T> result() {
        throw Testing.unsupported();
    }

}
