package com.ma.servicecallcommandgenerate.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ChangedPackages;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.IInterface;
import android.util.ArraySet;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class PackageManager {

    private final IInterface manager;
    private Method getInstalledApplicationsMethod;

    public PackageManager(IInterface manager){
        this.manager = manager;
    }

    private Method getInstalledApplicationsMethod() throws NoSuchMethodException {
        if (getInstalledApplicationsMethod == null){
            getInstalledApplicationsMethod = manager.getClass().getMethod("getInstalledApplications", (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? long.class : int.class , int.class);
        }
        return getInstalledApplicationsMethod;
    }


    public PackageInfo getPackageInfo(String packageName){
        try {
            return (PackageInfo) ReflectUtil.callObjectMethod(manager, "getPackageInfo", manager.getClass(), new Class[]{String.class, (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? long.class : int.class , int.class}, packageName, 0, UserManager.myUserId());
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }

    public ApplicationInfo getApplicationInfo(String packageName){
        try {
            return (ApplicationInfo) ReflectUtil.callObjectMethod(manager, "getApplicationInfo", manager.getClass(), new Class[]{String.class, (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? long.class : int.class , int.class}, packageName , 0 , UserManager.myUserId());
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }

    public String getApkPath(String packageName) {
        return getApplicationInfo(packageName).sourceDir;
    }

    public ArraySet<String> getInstalledApplications() {

        ArraySet<String> a = new ArraySet<>();
        try {
            Object parceledListSlice = getInstalledApplicationsMethod().invoke(manager, 0, UserManager.myUserId());
            // 通过反射调用 getList 方法
            Method getListMethod = parceledListSlice.getClass().getDeclaredMethod("getList");
            getListMethod.setAccessible(true);
            for (ApplicationInfo applicationInfo : (List<ApplicationInfo>) getListMethod.invoke(parceledListSlice)){
                a.add(applicationInfo.packageName);
            }
            return a;
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }


    public String getAppNameForPackageName(Context context ,String packageName) {
        return (String) getApplicationInfo(packageName).loadLabel(context.getPackageManager());
    }

    public int getUidForPackageName(String packageName){
        return (int) getApplicationInfo(packageName).uid;
    }

    public String getNameForUid(int uid){
        return (String) ReflectUtil.callObjectMethod(manager,  "getNameForUid", manager.getClass(), new Class[]{int.class}, new Object[]{uid});
    }

    public int checkUidPermission(String permName, int uid){
        return (int) ReflectUtil.callObjectMethod(manager, "checkUidPermission", manager.getClass(), new Class[]{String.class, int.class}, new Object[]{permName, uid});
    }

}
