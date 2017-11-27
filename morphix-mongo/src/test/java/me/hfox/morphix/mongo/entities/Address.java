package me.hfox.morphix.mongo.entities;

import me.hfox.morphix.mongo.annotation.Id;
import me.hfox.morphix.mongo.annotation.Property;
import me.hfox.morphix.mongo.annotation.entity.Entity;
import me.hfox.morphix.mongo.annotation.lifecycle.AccessedAt;
import me.hfox.morphix.mongo.annotation.lifecycle.CreatedAt;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

@Entity("addresses")
public class Address {

    @Id
    private ObjectId id;
    private List<String> address;

    @Property("post_code")
    private String postCode;

    @CreatedAt
    @Property("created_at")
    private Date createdAt;

    @AccessedAt
    @Property("accessed_at")
    private Date accessedAt;

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getAccessedAt() {
        return accessedAt;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{hash=[" + hashCode() + "], id=[" + id + "], address=[" + address + "], postCode=\"" + postCode + "\"}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Address) {
            Address address = (Address) obj;

            boolean equals = address.id.equals(id) && address.postCode.equals(postCode);
            for (int i = 0; i < address.address.size(); i++) {
                equals = equals && address.address.get(i).equals(this.address.get(i));
            }

            return equals;
        }

        return super.equals(obj);
    }

}
