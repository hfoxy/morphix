package me.hfox.morphix.entities;

import me.hfox.morphix.annotation.Id;
import me.hfox.morphix.annotation.Reference;
import me.hfox.morphix.annotation.entity.Entity;
import me.hfox.morphix.annotation.lifecycle.PostDelete;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity("users")
public class User {

    @Id
    private ObjectId id;
    private String email;
    private String username;
    private String password;
    private List<String> additional;

    @Reference
    private List<Address> addresses;

    private Settings settings;

    public User(String email, String username, String password, Address... addresses) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.additional = new ArrayList<>();
        this.addresses = Arrays.asList(addresses);
        this.settings = new Settings(false);
    }

    public ObjectId getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getAdditional() {
        return additional;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public Settings getSettings() {
        return settings;
    }

    @PostDelete
    public void postDelete() {
        this.password = null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{hash=[" + hashCode() + "], id=[" + id + "], email=\"[" + email + "\", username=\"[" + username + "\", password=\""
                + password + "], additional=" + additional + ", addresses=" + addresses + ", settings=" + settings + "}";
    }

}
