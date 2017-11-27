package me.hfox.morphix.mongo.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * From http://stackoverflow.com/a/19363555/3150575
 */
public class ReflectionUtils {

    public static Type determineType(Field field, Object object) {
        return determineType(field, object.getClass());
    }

    public static Type determineType(Field field, Class<?> type) {
        return getType(type, field).type;
    }

    protected static class TypeInfo {

        private Type type;
        private Type name;

        public TypeInfo(Type type, Type name) {
            this.type = type;
            this.name = name;
        }

    }

    public static TypeInfo getType(Class<?> clazz, Field field) {
        TypeInfo info = new TypeInfo(null, null);
        if (field.getGenericType() instanceof TypeVariable) {
            TypeVariable type = (TypeVariable) field.getGenericType();
            
        }

        if (field.getGenericType() instanceof TypeVariable<?>) {
            TypeVariable<?> genericTyp = (TypeVariable<?>) field.getGenericType();
            Class<?> superClazz = clazz.getSuperclass();

            if (clazz.getGenericSuperclass() instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) clazz.getGenericSuperclass();
                TypeVariable<?>[] superTypeParameters = superClazz.getTypeParameters();
                if (!Object.class.equals(paramType)) {
                    if (field.getDeclaringClass().equals(superClazz)) {
                        // this is the root class an starting point for this search
                        info.name = genericTyp;
                        info.type = null;
                    } else {
                        info = getType(superClazz, field);
                    }
                }
                if (info.type == null || info.type instanceof TypeVariable<?>) {
                    // lookup if type is not found or type needs a lookup in current concrete class
                    for (int j = 0; j < superClazz.getTypeParameters().length; ++j) {
                        TypeVariable<?> superTypeParam = superTypeParameters[j];
                        if (info.name.equals(superTypeParam)) {
                            info.type = paramType.getActualTypeArguments()[j];
                            Type[] typeParameters = clazz.getTypeParameters();
                            if (typeParameters.length > 0) {
                                for (Type typeParam : typeParameters) {
                                    TypeVariable<?> objectOfComparison = superTypeParam;
                                    if(info.type instanceof TypeVariable<?>) {
                                        objectOfComparison = (TypeVariable<?>)info.type;
                                    }
                                    if (objectOfComparison.getName().equals(((TypeVariable<?>) typeParam).getName())) {
                                        info.name = typeParam;
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } else {
            info.type = field.getGenericType();
        }

        return info;
    }

}
