package com.designPattern.single;

/**
 * @author Johnson
 * 2021/9/10
 */
public class QqqSingleton {

    private static QqqSingleton uniqueInstance;

    private QqqSingleton() {
    }

    public static synchronized QqqSingleton getUniqueInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new QqqSingleton();
        }
        return uniqueInstance;
    }
}
