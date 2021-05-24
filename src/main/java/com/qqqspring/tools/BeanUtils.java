package com.qqqspring.tools;

import com.qqqspring.beans.factory.BeanInstantiationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Johnson
 * 2021/5/24
 */
public class BeanUtils {

    public static <T> T instantiateClass(Class<T> clazz) throws BeanInstantiationException {
        Assert.notNull(clazz, "Class must not be null");
        try {
            return instantiateClass(clazz.getDeclaredConstructor());
        } catch (NoSuchMethodException e) {
            throw new BeanInstantiationException("instantiateClass : NoSuchMethodException");
        }
    }

    public static <T> T instantiateClass(Constructor<T> ctor, Object... args) {
        Assert.notNull(ctor, "Constructor must not be null");
        try {
            return ctor.newInstance();
        } catch (InstantiationException e) {
            throw new BeanInstantiationException("instantiateClass : InstantiationException");
        } catch (IllegalAccessException e) {
            throw new BeanInstantiationException("instantiateClass : IllegalAccessException");
        } catch (InvocationTargetException e) {
            throw new BeanInstantiationException("instantiateClass : InvocationTargetException");
        }
    }
}
