package me.hfox.morphix.helper.lifecycle;

import java.lang.annotation.Annotation;

public interface LifecycleHelper {

    public int call(Class<? extends Annotation> cls, Object object);

}
