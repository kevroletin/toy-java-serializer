package io.github.kevroletin.json.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TypeUtils {
    static public boolean isScalar(Object x) {
        return isSupportedScalar(x) || isUnsupportedScalar(x);
    }

    static public boolean isScalarClass(Class<?> cls) {
        return isSupportedScalarClass(cls) || isUnsupportedScalarClass(cls);
    }
    
    static public boolean isSupportedScalar(Object x) {
        return isNull(x)
            || isDouble(x)
            || isInteger(x)
            || isString(x)
            || isBoolean(x);
    }

    static public boolean isSupportedScalarClass(Class<?> cls) {
        return cls == Double.class
            || cls == Integer.class
            || cls == String.class
            || cls == Boolean.class;
    }
    
    static public boolean isUnsupportedScalar(Object x) {
        return x instanceof Float
            || x instanceof Short
            || x instanceof Long
            || x instanceof Byte
            || x instanceof Character;
    }
    
    static public boolean isUnsupportedScalarClass(Class<?> cls) {
        // We don't support fields which are not nullable for simplicity
        if (cls.isPrimitive()) {
            return true;
        }
        return cls == Float.class
            || cls == Short.class
            || cls == Long.class
            || cls == Byte.class
            || cls == Character.class;
    }

    static public boolean isNull(Object x) {
        return x == null;
    }

    static public boolean isDouble(Object x) {
        return x instanceof Double;
    }

    static public boolean isInteger(Object x) {
        return x instanceof Integer;
    }

    static public boolean isString(Object x) {
        return x instanceof String;
    }

    static public boolean isBoolean(Object x) {
        return x instanceof Boolean;
    }

    static public boolean isArray(Object x) {
        return x.getClass().isArray();
    }

    static public boolean isArrayClass(Class<?> cls) {
        return cls.isArray();
    }
    
    /** Due to inheritance we need to recursively scan super classes to find all
     * fields
     */
    public static List<Field> getAllFields(Class<?> cls) {
        Field[] currentFields = cls.getDeclaredFields();
        if (currentFields == null || currentFields.length == 0) {
            return new ArrayList<>(0);
        }

        List<Field> allFields = new ArrayList<>(currentFields.length);
        Collections.addAll(allFields, currentFields);

        if (cls.getSuperclass() != null && cls.getSuperclass() == Object.class) {
            List<Field> next = getAllFields(cls.getSuperclass());
            allFields.addAll(next);
        }

        return allFields;
    }
    
    /** This approach works better that class.newInstance because here we can
     * call ctor.setAccessible(true) which prevents some access violation errors.
     */
    public static Constructor<?> getDefaultConstructor(Class<?> cls) {
        Optional<Constructor<?>> ctor =
            Arrays.stream(cls.getDeclaredConstructors())
            .filter((x) -> {
                    return x.getParameterTypes() == null
                    || x.getParameterTypes().length == 0;
                })
            .findFirst();
        if (!ctor.isPresent()) {
            throw new IllegalStateException(cls.getName() + " have no default constructor");
        }
        Constructor<?> res = ctor.get();
        res.setAccessible(true);
        return ctor.get();
    }
}
