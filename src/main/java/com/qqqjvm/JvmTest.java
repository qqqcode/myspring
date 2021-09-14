package com.qqqjvm;

/**
 * @author Johnson
 * 2021/9/7
 */
public class JvmTest {
    static class OOMObject{

    }

    public static void fun() {
        while (true) {
            OOMObject oomObject = new OOMObject();
            oomObject = null;
        }
    }

    public static void main(String[] args) {
        JvmTest.fun();
    }
}
