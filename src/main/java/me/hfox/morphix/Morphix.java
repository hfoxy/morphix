package me.hfox.morphix;

import me.hfox.morphix.mapping.ObjectMapper;

public class Morphix {

    private ObjectMapper mapper;

    public Morphix(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

}
