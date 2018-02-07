package uk.hfox.morphix.mongo.query.raw.output;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import uk.hfox.morphix.mongo.query.raw.MongoOutputQuery;

/**
 * Performs a find query on the collection
 */
public class MongoFindQuery extends MongoOutputQuery<FindIterable<Document>> {

    private final Bson search;

    public MongoFindQuery(MongoCollection<Document> collection, Bson search) {
        super(collection);
        this.search = search;
    }

    @Override
    protected void runQuery() {
        super.output = super.collection.find(search);
    }

}
