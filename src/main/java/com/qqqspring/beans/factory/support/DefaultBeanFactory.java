package com.qqqspring.beans.factory.support;

import com.qqqspring.beans.factory.config.BeanDefinition;
import com.qqqspring.tools.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Johnson
 * 2021/5/24
 */
public class DefaultBeanFactory extends AbstractBeanFactory {

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap(256);

    public <T> T getBean(Class<T> requiredType) {
        return this.getBean(requiredType, (Object[])null);
    }

    public <T> T getBean(Class<T> requiredType,Object... args)  {
        Assert.notNull(requiredType, "Required type must not be null");
        try {
            return requiredType.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void registerAlias(String var1, String var2) {

    }

    @Override
    public void removeAlias(String var1) {

    }

    @Override
    public boolean isAlias(String var1) {
        return false;
    }
}
