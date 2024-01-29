package com.ma.servicecallcommandgenerate.utils;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.IInterface;
import android.os.Process;
import android.os.UserHandle;
import android.util.ArraySet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class UserManager {

    private IInterface manager;
    public UserManager(IInterface manager){
        this.manager = manager;
    }

    public static int myUserId(){
        try {
            return (int) UserHandle.class.getMethod("myUserId").invoke(Process.myUserHandle());
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }

}
