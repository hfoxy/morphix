package uk.hfox.morphix.utils;

public final class Testing {

    private Testing() {
        // private constructor
    }

    public static UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("Not supported during test");
    }

}
