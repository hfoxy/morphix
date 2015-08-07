package me.hfox.morphix;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoTimeoutException;
import com.mongodb.ServerAddress;
import me.hfox.morphix.entities.Address;
import me.hfox.morphix.entities.User;
import me.hfox.morphix.query.Query;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class MorphixConnectionTest {

    private static Morphix morphix;

    @BeforeClass
    public static void setUp() throws Exception {
        MongoClient client = new MongoClient(new ServerAddress("127.0.0.1", 27017), MongoClientOptions.builder().connectTimeout(2500).build());
        morphix = new Morphix(client, "morphix_testing");

        try {
            List<String> names = client.getDatabaseNames();
            System.out.println("Connected to DB: " + names);
        } catch (MongoTimeoutException ex) {
            assumeTrue("Could not connect to MongoDB server - ignoring tests which require DB connection", false);
        }
    }

    @Test
    public void testUser() throws Exception {
        Address address = new Address(Arrays.asList("some", "cool", "address"), "post code");
        morphix.store(address);
        assumeTrue(true);

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
    }

}
