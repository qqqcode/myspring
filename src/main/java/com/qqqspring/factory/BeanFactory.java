package com.qqqspring.factory;

/**
 * @author Johnson
 * 2020/10/23
 */
public interface BeanFactory {
    Object getBean(String var1);

    <T> T getBean(String var1, Class<T> var2);

    Object getBean(String var1, Object... var2);

    <T> T getBean(Class<T> var1);

    <T> T getBean(Class<T> var1, Object... var2);
}
