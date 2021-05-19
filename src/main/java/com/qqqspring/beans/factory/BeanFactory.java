package com.qqqspring.beans.factory;

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

    boolean containsBean(String name); // 是否存在

    boolean isSingleton(String name);// 是否为单实例

    boolean isPrototype(String name);// 是否为原型（多实例）

    boolean isTypeMatch(String name, Class<?> targetType);// 名称、类型是否匹配

    Class<?> getType(String name); // 获取类型

    String[] getAliases(String name);// 根据实例的名字获取实例的别名
}
