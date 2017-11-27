package me.hfox.morphix.helper.lifecycle;

import java.lang.annotation.Annotation;

public interface LifecycleHelper {

    public int callField(Class<? extends Annotation> cls, Object object);

    public int callField(Class<? extends Annotation> cls, Object object, boolean update);

    public int callMethod(Class<? extends Annotation> cls, Object object);

}
