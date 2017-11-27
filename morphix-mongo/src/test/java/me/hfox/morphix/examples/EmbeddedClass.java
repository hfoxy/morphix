package me.hfox.morphix.examples;

import me.hfox.morphix.annotation.Property;
import me.hfox.morphix.annotation.entity.Entity;

@Entity
public class EmbeddedClass {

    @Property("some_string")
    private String embedded;

    public EmbeddedClass(String embedded) {
        this.embedded = embedded;
    }

    public String getEmbedded() {
        return embedded;
    }

    public void setEmbedded(String embedded) {
        this.embedded = embedded;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{hash=[" + hashCode() + "], embedded=\"" + embedded + "\"}";
    }

}
