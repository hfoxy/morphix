package me.hfox.morphix.entities;

import me.hfox.morphix.annotation.Id;
import me.hfox.morphix.annotation.Property;
import me.hfox.morphix.annotation.entity.Entity;
import org.bson.types.ObjectId;

import java.util.List;

@Entity("addresses")
public class Address {

    @Id
    private ObjectId id;
    private List<String> address;

    @Property("post_code")
    private String postCode;

    public Address(List<String> address, String postCode) {
        this.address = address;
        this.postCode = postCode;
    }

    public ObjectId getId() {
        return id;
    }

    public List<String> getAddress() {
        return address;
    }

    public String getPostCode() {
        return postCode;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{hash=[" + hashCode() + "], id=[" + id + "], address=[" + address + "], postCode=" + postCode + "}";
    }

}
