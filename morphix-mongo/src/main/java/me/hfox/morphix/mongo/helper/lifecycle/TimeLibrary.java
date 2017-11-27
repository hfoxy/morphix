package me.hfox.morphix.mongo.helper.lifecycle;

import me.hfox.morphix.mongo.exception.MorphixException;

public enum TimeLibrary {

    DEFAULT("java.util.Date"),
    JODA("org.joda.time.DateTime");

    private boolean tested;
    private String testClass;

    TimeLibrary(String testClass) {
        this.testClass = testClass;
    }

    public void test() {
        if (tested) {
            return;
        }

        try {
            Class<?> cls = Class.forName(testClass);
            // System.out.println("Loaded " + name());
            tested = true;
        } catch (ClassNotFoundException ex) {
            throw new MorphixException("Could not load the '" + name() + "' time library", ex);
        }
    }

}
