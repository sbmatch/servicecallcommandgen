package com.ma.servicecallcommandgenerate.utils;

import android.content.ClipData;
import android.os.Binder;
import android.os.IInterface;
import android.os.Process;

import java.lang.reflect.Method;

public class ClipboardManager {
    private IInterface manager;
    private Method setPrimaryClipMethod;
    public ClipboardManager(IInterface manager){
        this.manager = manager;
    }

    private Method getSetPrimaryClipMethod() throws NoSuchMethodException {
        if (setPrimaryClipMethod == null) setPrimaryClipMethod = manager.getClass().getMethod("setPrimaryClip", ClipData.class, String.class, int.class);
        return setPrimaryClipMethod;
    }

    public void setPrimaryClip(ClipData clip){
        try {
            getSetPrimaryClipMethod().invoke(manager, clip, ServiceManager.getPackageManager().getNameForUid(Binder.getCallingUid()), UserManager.myUserId());
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }

}
