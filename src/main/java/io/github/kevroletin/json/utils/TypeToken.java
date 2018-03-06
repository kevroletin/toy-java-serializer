package io.github.kevroletin.json.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeToken<T> {

    public Type getType() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        return type.getActualTypeArguments()[0];
    }

}
