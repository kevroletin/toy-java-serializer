package io.github.kevroletin.json.utils;

import io.github.kevroletin.json.Location;
import io.github.kevroletin.json.exceptions.DeserializationException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
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
        return isSupportedScalarClass(x.getClass());
    }

    static public boolean isSupportedScalarClass(Class<?> cls) {
        return cls == Double.class
            || cls == Integer.class
            || cls == String.class
            || cls == Boolean.class;
    }

    static public boolean isUnsupportedScalar(Object x) {
        return x != null && isUnsupportedScalarClass(x.getClass());
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

        List<Field> allFields;

        if (currentFields == null || currentFields.length == 0) {
            allFields = new ArrayList<>();
        } else {
            allFields = new ArrayList<>(currentFields.length);
            Collections.addAll(allFields, currentFields);
        }

        if (cls.getSuperclass() != null && cls.getSuperclass() != Object.class) {
            List<Field> next = getAllFields(cls.getSuperclass());
            allFields.addAll(next);
        }

        return allFields;
    }

    /** This approach works better than class.newInstance because here we can
     * call ctor.setAccessible(true) which prevents some access forbidden errors.
     */
    public static Constructor<?> getDefaultConstructor(Class<?> cls) throws DeserializationException {
        Optional<Constructor<?>> ctor =
            Arrays.stream(cls.getDeclaredConstructors())
            .filter((x) -> {
                    return x.getParameterTypes() == null
                        || x.getParameterTypes().length == 0;
                })
            .findFirst();
        if (!ctor.isPresent()) {
            throw new DeserializationException(cls.getName() + " have no default constructor");
        }
        Constructor<?> res = ctor.get();
        res.setAccessible(true);
        return ctor.get();
    }

    public static Class getClassFromTypeNoThrow(List<String> err, Location loc, Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parType = (ParameterizedType)type;
            try {
                return Class.forName(parType.getRawType().getTypeName());
            } catch (ClassNotFoundException ex) {
                err.add(loc.toStringWith("Failed to convert %s type into class: %s",
                                         type.toString(), ex.getMessage()));
                return null;
            }
        }
        if (type instanceof GenericArrayType) {
            assert(false);
        }
        if (type instanceof TypeVariable) {
            assert(false);
        }
        if (type instanceof WildcardType) {
            err.add("Can't turn ? into class");
            return null;
        }
        return (Class) type;
    }

    public static Type getGenericParameterTypeNoThrow(List<String> err, Location loc, int idx, Type type) {
        if (!(type instanceof ParameterizedType)) {
            err.add(loc.toStringWith("No generic parameters is attached to a type %s", type.toString()));
            return null;
        }
        ParameterizedType par = (ParameterizedType) type;
        Type[] args = par.getActualTypeArguments();
        if (args.length <= idx) {
            err.add(loc.toStringWith("Type has less than %d type parameters", idx));
            return null;
        }
        return args[idx];
    }
}
