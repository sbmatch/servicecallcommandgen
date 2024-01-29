package com.ma.servicecallcommandgenerate.utils;

import android.annotation.SuppressLint;

public class OsUtils {
    @SuppressLint("PrivateApi")
    public static boolean isMIUI(){
        try {
            return (boolean) ReflectUtil.callStaticObjectMethod(Class.forName("android.telephony.TelephonyBaseUtilsStub"),"isMiuiRom");
        }catch (Throwable e){
            e.printStackTrace();
            return false;
        }
    }
}
