package com.ma.servicecallcommandgenerate.utils;

import com.ma.servicecallcommandgenerate.utils.OsUtils;

import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;

public class MultiJarClassLoader extends ClassLoader {
    private static MultiJarClassLoader multiJarClassLoader;
    List<DexClassLoader> dexClassLoaders = new ArrayList<>();
    private MultiJarClassLoader(ClassLoader parentClassLoader) {
        super(parentClassLoader);
        if (OsUtils.isMIUI()) addMIUIFrameworkJar();
    }

    public static MultiJarClassLoader getInstance(){
        if (multiJarClassLoader == null){
            multiJarClassLoader = new MultiJarClassLoader(ClassLoader.getSystemClassLoader());
        }
        return multiJarClassLoader;
    }

    private void addMIUIFrameworkJar(){
        addJar("/system/framework/services.jar");
        addJar("/system/system_ext/framework/miui-framework.jar");
        addJar("/system/system_ext/framework/miui-services.jar");
    }

    public void addJar(String jarPath) {

        DexClassLoader dexClassLoader = new DexClassLoader(
                jarPath,
                null,
                null, // 额外的库路径，可以为 null
                getParent() // 父类加载器
        );
        dexClassLoaders.add(dexClassLoader);
    }
}