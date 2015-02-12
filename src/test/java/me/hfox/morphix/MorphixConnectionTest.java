package me.hfox.morphix;

import com.mongodb.MongoClient;
import junit.framework.TestCase;
import me.hfox.morphix.entities.Address;
import me.hfox.morphix.entities.User;

import java.util.Arrays;

public class MorphixConnectionTest extends TestCase {

    private Morphix morphix;

    @Override
    protected void setUp() throws Exception {
        MongoClient client = new MongoClient("127.0.0.1", 27017);
        morphix = new Morphix(client, "morphix_testing");
    }

    public void testUser() throws Exception {
        Address address = new Address(Arrays.asList("some", "cool", "address"), "post code");
        morphix.store(address);

        User user = new User("email", "username", "password", address);
        morphix.store(user);

        morphix.getCache(User.class).clear();
        User queried = morphix.createQuery(User.class).field("email").equal("email").get();
    }

}
