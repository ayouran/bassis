package com.bassis.tools.reflex;


/**
 * @author liucheng
 * @version 1.0
 * @description: TODO
 * @date 2022/1/20 16:56
 */
public class BassisClassLoader extends java.lang.ClassLoader {
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {

        return loadClass(name, false);
    }


}
