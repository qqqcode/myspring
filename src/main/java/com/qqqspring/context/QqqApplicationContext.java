package com.qqqspring.context;

import com.qqqspring.beans.factory.BeanFactory;

/**
 * @author Johnson
 * 2021/5/19
 */
public interface QqqApplicationContext extends BeanFactory {

    String getId();

    String getApplicationName();

    String getDisplayName();

    long getStartupDate();

}
