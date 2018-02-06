package uk.hfox.morphix.mongo.query.raw.input;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.InsertOneOptions;
import org.bson.Document;
import uk.hfox.morphix.mongo.query.raw.MongoQueryInput;
import uk.hfox.morphix.utils.Conditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs an insert query on the database with the supplied options.
 * Supports inserting a single document and inserting multiple documents.
 */
public class MongoQueryInsert extends MongoQueryInput {

    private final List<Document> documents;

    private final boolean many;
    private final InsertOneOptions oneOptions;
    private final InsertManyOptions manyOptions;

    public MongoQueryInsert(MongoCollection collection, Document document, InsertOneOptions options) {
        super(collection);
        Conditions.notNull(document, "document");

        this.documents = new ArrayList<>();
        this.documents.add(document);

        this.many = false;
        this.oneOptions = options;
        this.manyOptions = null;
    }

    public MongoQueryInsert(MongoCollection collection, List<Document> documents, InsertManyOptions options) {
        super(collection);
        Conditions.notEmptyOrNullFilled(documents, "documents");

        this.documents = documents;

        this.many = true;
        this.oneOptions = null;
        this.manyOptions = options;
    }

    @Override
    public void performQuery() {
        if (many) {
            if (manyOptions != null) {
                super.collection.insertMany(this.documents, manyOptions);
            } else {
                super.collection.insertMany(this.documents);
            }
        } else {
            if (oneOptions != null) {
                super.collection.insertOne(this.documents.get(0), oneOptions);
            } else {
                super.collection.insertOne(this.documents.get(0));
            }
        }
    }

}
