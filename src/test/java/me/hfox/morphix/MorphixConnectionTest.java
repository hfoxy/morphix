package me.hfox.morphix;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoTimeoutException;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import me.hfox.morphix.entities.Address;
import me.hfox.morphix.entities.User;
import me.hfox.morphix.query.Query;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class MorphixConnectionTest {

    private static Morphix morphix;

    @BeforeClass
    public static void setUp() throws Exception {
        MongoClient client = new MongoClient(new ServerAddress("127.0.0.1", 27017), MongoClientOptions.builder().connectTimeout(2500).writeConcern(WriteConcern.ACKNOWLEDGED).build());
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
        // System.out.println("Starting User test");
        Address address = new Address(Arrays.asList("some", "cool", "address"), "post code");
        WriteResult result = morphix.store(address);
        // System.out.println("Inserted Address");
        assertNotNull("Created at was not updated", address.getCreatedAt());
        // System.out.println("Address CreatedAt: " + address.getCreatedAt());

        User user = new User("email", "username", "password", address);
        morphix.store(user); // create
        // System.out.println(user.getId());
        // System.out.println("User CreatedAt: " + user.getCreatedAt());
        // System.out.println("Inserted User");
        assertNotNull("Created at was not updated", user.getCreatedAt());

        Date time = new Date();
        user.setUsername("testing");
        morphix.store(user); // save
        // System.out.println(user.getId());
        // System.out.println("User UpdatedAt: " + user.getUpdatedAt());
        // System.out.println("Updated User");
        // System.out.println("Checking to see if " + user.getUpdatedAt() + " is after or equal to " + time + " (" + user.getUpdatedAt().getTime() + " >= " + time.getTime() + ")");
        assertTrue("Updated at was not updated", user.getUpdatedAt().getTime() >= time.getTime());

        Query<User> query = morphix.createQuery(User.class).field("_id").equal(user.getId());
        User queried = query.get();
        assertSame(user, queried);

        morphix.getCache(User.class).clear();

        query = morphix.createQuery(User.class).field("_id").equal(user.getId());
        // System.out.println(query.toQueryObject());
        queried = query.get();
        // System.out.println(queried.getId());

        assertEquals(user, queried);

        time = new Date();
        morphix.getCache().remove(user);
        morphix.getCache().remove(address);

        queried = query.get();
        // System.out.println(queried.getId());

        for (Address addr : queried.getAddresses()) {
            assertNotNull("Created at was not updated", addr.getCreatedAt());
            // System.out.println("Address CreatedAt: " + addr.getCreatedAt());
            // System.out.println("Address AccessedAt: " + addr.getAccessedAt());
            assertNotNull("Accessed at was not updated", addr.getAccessedAt());
            assertTrue("Accessed at was not updated", addr.getAccessedAt().getTime() >= time.getTime());
            morphix.store(addr);
        }

        query = morphix.createQuery(User.class).field("_id").equal(user.getId());
        query.delete();

        assertNull(queried.getPassword());
        queried = query.get();
        assertNull(queried);
    }

}
