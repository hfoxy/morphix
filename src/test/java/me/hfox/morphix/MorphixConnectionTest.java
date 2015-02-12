package me.hfox.morphix;

import com.mongodb.MongoClient;
import junit.framework.TestCase;
import me.hfox.morphix.entities.Address;
import me.hfox.morphix.entities.User;

import java.util.Arrays;

public class MorphixConnectionTest extends TestCase {

    private Morphix morphix;
    private boolean cont;

    @Override
    protected void setUp() throws Exception {
        MongoClient client = new MongoClient("127.0.0.1", 27017);
        morphix = new Morphix(client, "morphix_testing");

        try {
            morphix.getDatabase().getCollection("users");
            cont = true;
        } catch (Exception ex) {
            System.out.println("Could not connect to MongoDB server - ignoring tests which require DB connection");
        }
    }

    public void testUser() throws Exception {
        if (!cont) return;

        Address address = new Address(Arrays.asList("some", "cool", "address"), "post code");
        morphix.store(address);

        User user = new User("email", "username", "password", address);
        System.out.println(user);
        morphix.store(user);

        morphix.getCache(User.class).clear();
        User queried = morphix.createQuery(User.class).field("email").equal("email").get();
        System.out.println(queried);
    }

}
