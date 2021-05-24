package com.qqqspring.boot;


import com.qqqspring.context.support.QqqAbstractApplicationContext;
import com.qqqspring.tools.BeanUtils;
import com.qqqspring.tools.ClassParseUtil;


public class QqqSpringApplication {

    public QqqSpringApplication() {

    }

    public static QqqAbstractApplicationContext run(Class<?> source, String[] args) {
        return null;
    }

    ClassLoader getClassLoader() {
        return ClassParseUtil.getDefaultClassLoader();
    }


    QqqAbstractApplicationContext run() {
        QqqAbstractApplicationContext context = createApplicationContext();
        return context;
    }

    protected QqqAbstractApplicationContext createApplicationContext() {
        Class<?> aClass = null;
        try {
            aClass = Class.forName("com.qqqspring.context.annotation.QqqAnnotationConfigApplicationContext");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (QqqAbstractApplicationContext) BeanUtils.instantiateClass(aClass);
    }

    protected void refresh(QqqAbstractApplicationContext abstractApplicationContext) {
        abstractApplicationContext.refresh();
    }
}
