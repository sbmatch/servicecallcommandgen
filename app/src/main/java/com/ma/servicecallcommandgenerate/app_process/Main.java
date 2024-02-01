package com.ma.servicecallcommandgenerate.app_process;

import android.os.Binder;
import android.os.Looper;

import com.ma.servicecallcommandgenerate.utils.PackageManager;
import com.ma.servicecallcommandgenerate.utils.ServiceManager;
import com.ma.servicecallcommandgenerate.utils.WifiManager;

public class Main {

    public static void main(String[] args) {

        if (Looper.getMainLooper() == null) {
            Looper.prepareMainLooper();
        }

        if (Binder.getCallingUid() == 0 || Binder.getCallingUid() == 2000) {

            // 判断参数长度
            switch (args.length) {
                case 2:
                    // 有两个参数 根据提供的参数设置对应key的值
                    String name = args[0];
                    String newValue = args[1];
                    if (name.equals("softap")){
                        WifiManager wifiManager = ServiceManager.getWifiManager();

                        try {
                            if (newValue.equals("enable")) {
                                System.out.println(wifiManager.getWifiApConfiguration() != null ? "√ 已获取系统Wifi热点配置" : "× 配置为空");
                                wifiManager.startSoftAp(wifiManager.getWifiApConfiguration(),  "android");
                                System.out.println("√ 已开启热点");
                            }
                            if (newValue.equals("disable")) {
                                wifiManager.stopSoftAp();
                                System.out.println("√ 已关闭热点");
                            }
                        }catch (Throwable e){
                            e.printStackTrace();
                        }

                        System.exit(0);
                    }
                    break;
            }
        }

        Looper.loop();
    }
}
