package com.qqqspring.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class相关操作，这里使用
 *
 * @author qqq
 * 2020/10/24
 */
public class ClassParseUtil {

    /**
     * 扫描路径下所有的class并返回
     * @param packageName
     * @return
     */
    public static List<Class<?>> getClasses(String packageName) {
        String packOpperPath = packageName.replace(".", "/");
        //线程上下文类加载器得到当前的classpath的绝对路径.（动态加载资源）
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        List<Class<?>> classes = new ArrayList<Class<?>>();
        try {
            //(来得到当前的classpath的绝对路径的URI表示法。)
            Enumeration<URL> resources = classloader.getResources(packOpperPath);
            while (resources.hasMoreElements()) {
                //先获得本类的所在位置
                URL url = resources.nextElement();
                //url.getProtocol()是获取URL的HTTP协议。
                if (url.getProtocol().equals("jar")) { //判断是不是jar包
                    scanPackage(url, classes);
                } else {
                    //此方法不会自动将链接中的非法字符转义。而在File转化成URI的时候，会将链接中的特殊字符如#或!等字符进行编码。
                    File file = new File(url.toURI());
                    if (!file.exists()) {
                        continue;
                    }
                    scanPackage(packageName, file, classes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return classes;
    }

    //扫描一般的包。
    private static void scanPackage(String packageName, File currentFile, List<Class<?>> classes) {
        File[] fileList = currentFile.listFiles(new FileFilter() {
            //FileFilter是文件过滤器,源代码只写了一个accapt的抽象方法。
            public boolean accept(File pathName) {
                if (pathName.isDirectory()) {    //判断是否是目录
                    return true;
                }
                return pathName.getName().endsWith(".class");
            }
        });

        for (File file : fileList) {
            if (file.isDirectory()) {
                scanPackage(packageName + "." + file.getName(), file, classes);
            } else {
                String fileName = file.getName().replace(".class", "");
                String className = packageName + "." + fileName;
                try {
                    Class<?> klass = Class.forName(className);//取出所有的类
                    //Class<?> klass = Thread.currentThread().getContextClassLoader().loadClass(className);
                    //不扫描注解类、枚举类、接口和八大基本类型。
                    if (klass.isAnnotation()
                            || klass.isEnum()
                            || klass.isInterface()
                            || klass.isPrimitive()) {
                        continue;
                    }
                    classes.add(klass);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //扫描jar包方法。
    private static void scanPackage(URL url, List<Class<?>> classes) throws IOException {
        JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
        JarFile jarfile = urlConnection.getJarFile();
        Enumeration<JarEntry> jarEntries = jarfile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarName = jarEntry.getName();
            if (!jarName.endsWith(".class")) {
                continue;
            }
            String className = jarName.replace(".class", "").replaceAll("/", ".");
            try {
                Class<?> klass = Class.forName(className);
                if (klass.isAnnotation() || klass.isInterface() || klass.isEnum() || klass.isPrimitive()) {
                    classes.add(klass);
                    continue;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
