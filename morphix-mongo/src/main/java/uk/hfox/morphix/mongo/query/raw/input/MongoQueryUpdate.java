package uk.hfox.morphix.mongo.query.raw.input;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.conversions.Bson;
import uk.hfox.morphix.mongo.query.raw.MongoQueryInput;

/**
 * Performs an update query on the database with the supplied options.
 * Supports updating a single document and updating multiple documents.
 */
public class MongoQueryUpdate extends MongoQueryInput {

    private final Bson filters;
    private final Bson update;
    private final boolean many;
    private final UpdateOptions options;

    public MongoQueryUpdate(MongoCollection collection, Bson filters, Bson update, boolean many, UpdateOptions options) {
        super(collection);
        this.filters = filters;
        this.update = update;
        this.many = many;
        this.options = options;
    }

    @Override
    public void performQuery() {
        if (many) {
            if (options != null) {
                super.collection.updateMany(this.filters, this.update, this.options);
            } else {
                super.collection.updateMany(this.filters, this.update);
            }
        } else {
            if (options != null) {
                super.collection.updateOne(this.filters, this.update, this.options);
            } else {
                super.collection.updateOne(this.filters, this.update);
            }
        }
    }

}
