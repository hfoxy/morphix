package me.hfox.morphix.mongo.examples;

import me.hfox.morphix.mongo.annotation.Id;
import me.hfox.morphix.mongo.annotation.entity.Entity;
import org.bson.types.ObjectId;

import java.util.List;

@Entity
public class TestClass {

    @Id
    private ObjectId id;
    private String test;
    private List<String> strings;
    private EmbeddedClass embedded;

    public TestClass(String test, List<String> strings, EmbeddedClass embedded) {
        this.test = test;
        this.strings = strings;
        this.embedded = embedded;
    }

    public ObjectId getId() {
        return id;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    public EmbeddedClass getEmbedded() {
        return embedded;
    }

    public void setEmbedded(EmbeddedClass embedded) {
        this.embedded = embedded;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{hash=[" + hashCode() + "], id=[" + id + "], test=\"" + test + "\", strings=" + strings + ", embedded=[" + embedded + "]}";
    }

}
