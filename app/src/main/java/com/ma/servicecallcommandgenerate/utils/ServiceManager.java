package com.ma.servicecallcommandgenerate.utils;

import android.annotation.SuppressLint;
import android.os.IBinder;
import android.os.IInterface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ServiceManager {

    private ServiceManager() {
        /* not instantiable */
    }
    private static IInterface getService(String service, String type){
        try {
            @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
            Method getServiceMethod = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
            IBinder binder = (IBinder) getServiceMethod.invoke(null, service);
            Method asInterfaceMethod = Class.forName(type + "$Stub").getMethod("asInterface", IBinder.class);
            return (IInterface) asInterfaceMethod.invoke(null, binder);
        } catch (NullPointerException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static IBinder getService(String service){
        try {
            @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
            Method getServiceMethod = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            return  (IBinder) getServiceMethod.invoke(null, service);
        } catch (NullPointerException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException e) {
            e.getStackTrace();
            return null;
        }
    }

    public static IBinder checkService(String name){
        try {
            @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
            Method getServiceMethod = Class.forName("android.os.ServiceManager").getMethod("checkService", String.class);
            return  (IBinder) getServiceMethod.invoke(null, name);
        } catch (NullPointerException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException e) {
            e.getStackTrace();
            return null;
        }
    }

    @SuppressLint("PrivateApi")
    public static String[] listServices() {
        try {
            return (String[]) Class.forName("android.os.ServiceManager").getMethod("listServices").invoke(null);
        } catch (NullPointerException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static ClipboardManager getClipboardManager(){
        return new ClipboardManager(getService("clipboard","android.content.IClipboard"));
    }

    public static UserManager getUserManager(){
        return new UserManager(getService("user","android.os.IUserManager"));
    }

    public static PackageManager getPackageManager(){
        return new PackageManager(getService("package", "android.content.pm.IPackageManager"));
    }

    public static WifiManager getWifiManager(){
        return new WifiManager(getService("wifi","android.net.wifi.IWifiManager"));
    }

    public static Object getAppRunningControlManager(){
        try {
            return ReflectUtil.callStaticObjectMethod(Class.forName("miui.security.AppRunningControlManager"), "getInstance");
        }catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
