package me.hfox.morphix;

import com.mongodb.MongoClient;
import com.mongodb.MongoTimeoutException;
import junit.framework.TestCase;
import me.hfox.morphix.entities.Address;
import me.hfox.morphix.entities.User;
import me.hfox.morphix.query.Query;

import java.util.Arrays;

public class MorphixConnectionTest extends TestCase {

    private Morphix morphix;

    @Override
    protected void setUp() throws Exception {
        MongoClient client = new MongoClient("127.0.0.1", 27017);
        morphix = new Morphix(client, "morphix_testing");
    }

    public void testUser() throws Exception {
        try {
            Address address = new Address(Arrays.asList("some", "cool", "address"), "post code");
            morphix.store(address);

            User user = new User("email", "username", "password", address);
            morphix.store(user); // create

            user.setUsername("testing");
            morphix.store(user); // save
            // System.out.println(user.getId());

            Query<User> query = morphix.createQuery(User.class).field("_id").equal(user.getId());
            User queried = query.get();
            assertSame(user, queried);

            morphix.getCache(User.class).clear();

            query = morphix.createQuery(User.class).field("_id").equal(user.getId());
            // System.out.println(query.toQueryObject());
            queried = query.get();
            // System.out.println(queried.getId());

            assertEquals(user, queried);

            query = morphix.createQuery(User.class).field("_id").equal(user.getId());
            query.delete();

            assertNull(queried.getPassword());
            queried = query.get();
            assertNull(queried);
        } catch (MongoTimeoutException ex) {
            System.out.println("Could not connect to MongoDB server - ignoring tests which require DB connection");
        }
    }

}
