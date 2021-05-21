package com.qqqspring.beans.factory.support;

/**
 * @author Johnson
 * 2021/5/21
 */
public interface AliasRegistry {
    void registerAlias(String var1, String var2);

    void removeAlias(String var1);

    boolean isAlias(String var1);

    String[] getAliases(String var1);
}
