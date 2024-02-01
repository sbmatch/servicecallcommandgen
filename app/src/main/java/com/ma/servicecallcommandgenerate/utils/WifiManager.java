package com.ma.servicecallcommandgenerate.utils;

import android.net.wifi.WifiConfiguration;
import android.os.IInterface;

public class WifiManager {
    private IInterface manager;

    public WifiManager(IInterface manager){
        this.manager = manager;
    }

    public WifiConfiguration getWifiApConfiguration(){
        return (WifiConfiguration) ReflectUtil.callObjectMethod(manager, "getWifiApConfiguration", manager.getClass());
    }

    public boolean startSoftAp(WifiConfiguration wifiConfig, String packageName){
        return (boolean) ReflectUtil.callObjectMethod(manager, "startSoftAp", manager.getClass(), new Class[]{WifiConfiguration.class, String.class}, new Object[]{wifiConfig, packageName});
    }

    public boolean stopSoftAp(){
        return (boolean) ReflectUtil.callObjectMethod(manager, "stopSoftAp", manager.getClass());
    }
    public int getWifiApEnabledState(){
        return (int) ReflectUtil.callObjectMethod(manager,"getWifiApEnabledState",manager.getClass());
    }
}
