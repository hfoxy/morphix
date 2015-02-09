package me.hfox.morphix;

import me.hfox.morphix.mapping.ObjectMapper;
import me.hfox.morphix.mapping.ObjectMapperImpl;

public class Morphix {

    private MorphixOptions options;
    private ObjectMapper mapper;

    public Morphix() {
        this(null, null);
    }

    public Morphix(MorphixOptions options) {
        this(options, null);
    }

    public Morphix(ObjectMapper mapper) {
        this(null, mapper);
    }

    public Morphix(MorphixOptions options, ObjectMapper mapper) {
        this.options = (options == null ? MorphixOptions.builder().build() : options);
        this.mapper = (mapper == null ? new ObjectMapperImpl() : mapper);
    }

    public MorphixOptions getOptions() {
        return options;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

}
