package io.github.kevroletin.json;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TypeUtils {
    static public boolean isPrimitive(Object x) {
        return isNull(x) || isDouble(x) || isInteger(x) || isString(x)
            || isBoolean(x);
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
    
    static public boolean isList(Object x) {
	return x instanceof List;
    }

    /** Due to inheritance we need to recursively scan super classes to find all
     * fields
     */
    static List<Field> getAllFields(Class<?> cls) {
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
}
