package me.hfox.morphix.mongo;

public final class MorphixOptions {

    private boolean collectionNameSnakeCase = true;
    private boolean collectionNameLowercase = true;

    private MorphixOptions() {
        // can't be created externally
    }

    public boolean isCollectionNameSnakeCase() {
        return collectionNameSnakeCase;
    }

    public boolean isCollectionNameLowercase() {
        return collectionNameLowercase;
    }

    public static MorphixOptionsBuilder builder() {
        return new MorphixOptionsBuilder();
    }

    public static class MorphixOptionsBuilder {

        private MorphixOptions options;

        private MorphixOptionsBuilder() {
            // can't be created externally
            options = new MorphixOptions();
        }

        public void setCollectionNameSnakeCase(boolean bool) {
            options.collectionNameSnakeCase = bool;
        }

        public void setCollectionNameLowercase(boolean bool) {
            options.collectionNameLowercase = bool;
        }

        public MorphixOptions build() {
            return options;
        }

    }

}
