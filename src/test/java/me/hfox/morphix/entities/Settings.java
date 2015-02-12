package me.hfox.morphix.entities;

import me.hfox.morphix.annotation.Property;
import me.hfox.morphix.annotation.entity.Entity;
import me.hfox.morphix.annotation.entity.Polymorph;

@Entity
@Polymorph(false)
public class Settings {

    @Property("display_address")
    private boolean displayAddress;

    public Settings(boolean displayAddress) {
        this.displayAddress = displayAddress;
    }

    public boolean displayAddress() {
        return displayAddress;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{hash=[" + hashCode() + "], displayAddress=" + displayAddress + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Settings) {
            Settings settings = (Settings) obj;

            return settings.displayAddress == displayAddress;
        }

        return super.equals(obj);
    }

}
