package com.ma.servicecallcommandgenerate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;
import androidx.preference.DialogPreference;
import androidx.preference.DropDownPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.EditTextPreferenceDialogFragmentCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;

import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.widget.EditText;
import android.widget.Toast;

import com.ma.servicecallcommandgenerate.utils.MultiJarClassLoader;
import com.ma.servicecallcommandgenerate.utils.ReflectUtil;
import com.ma.servicecallcommandgenerate.utils.ServiceManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import rikka.preference.SimpleMenuPreference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("service call 生成器");
        getSupportActionBar().setBackgroundDrawable(null);

        getSupportFragmentManager().beginTransaction().replace(R.id.inputDataPreference, new ServiceCallTool()).commit();
    }


    public static class ServiceCallTool extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener{
        PreferenceScreen screen;
        static MultiJarClassLoader loader = MultiJarClassLoader.getInstance();
        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

            screen = getPreferenceManager().createPreferenceScreen(requireContext());

            PreferenceCategory category = new PreferenceCategory(requireContext());
            category.setIconSpaceReserved(false);
            screen.addPreference(category);

            SimpleMenuPreference serviceNameList = new SimpleMenuPreference(requireContext());
            serviceNameList.setTitle("请选择系统服务类");
            serviceNameList.setKey("system_service");
            serviceNameList.setIconSpaceReserved(false);
            serviceNameList.setCopyingEnabled(true);
            serviceNameList.setEntries(getServiceNameByServiceManager.values().toArray(new CharSequence[0]));
            serviceNameList.setEntryValues(getServiceNameByServiceManager.keySet().toArray(new CharSequence[0]));
            serviceNameList.setSummaryProvider(preference -> ((SimpleMenuPreference)preference).getEntry());
            serviceNameList.setOnPreferenceChangeListener(this);
            category.addPreference(serviceNameList);

            SimpleMenuPreference methodLists = new SimpleMenuPreference(requireContext());
            methodLists.setTitle("请选择方法名");
            methodLists.setKey("method_list");
            methodLists.setIconSpaceReserved(false);
            methodLists.setCopyingEnabled(true);
            methodLists.setEntries(new CharSequence[0]);
            methodLists.setEntryValues(new CharSequence[0]);
            methodLists.setOnPreferenceClickListener(preference -> {
                if (((SimpleMenuPreference) preference).getEntries().length == 0)
                    Toast.makeText(requireContext(), "请先选择系统服务类", Toast.LENGTH_SHORT).show();
                return true;
            });
            category.addPreference(methodLists);

            setPreferenceScreen(screen);
        }

        public static HashMap<String, String> getServiceNameByServiceManager = new HashMap<String,String>(){{
            for (String s : ServiceManager.listServices()){
                try {
                    IBinder binder = ServiceManager.getService(s);
                    if (binder != null && isClassExists(binder.getInterfaceDescriptor())) {
                        put(s, binder.getInterfaceDescriptor());
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }};

        /**
        *
        * @param types 用于检查方法的参数类型
        * */
        public static boolean allParametersOfType(Method method, Class<?>... types) {
            // 获取方法的所有参数类型
            Class<?>[] parameterTypes = method.getParameterTypes();
            // 遍历每个参数类型，检查是否都在传入的类型中
            for (Class<?> paramType : parameterTypes) {
                boolean found = false;
                for (Class<?> targetType : types) {
                    if (targetType.isAssignableFrom(paramType)) {
                        found = true;
                    }
                }
                // 如果某个参数类型不在传入的类型中，则返回false
                if (!found) {
                    return false;
                }
            }
            // 所有参数类型都在传入的类型中，则返回true
            return true;
        }

        public static boolean isClassExists(String className) {
            try {
                loader.loadClass(className);
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }

        /**
        *
        * @param clz 获取所有符合条件的方法
        * */
        public static ArrayMap<String, String> getMethodFieldValueBySystemService(String clz){
            try {
                ArrayMap<String, String> arrayMap = new ArrayMap<>();

                for (Method method : loader.loadClass(clz).getDeclaredMethods()) {
                    method.setAccessible(true);

                    if (allParametersOfType(method, String.class, boolean.class, int.class, IBinder.class, IInterface.class)) {
                        for (Field field : loader.loadClass(clz + "$Stub").getDeclaredFields()) {
                            if (field.getName().equals("TRANSACTION_" + method.getName())) {
                                field.setAccessible(true);
                                arrayMap.put(field.get(null)+" "+field.getName().split("TRANSACTION_")[1], String.valueOf(field.get(null)));
                            }
                        }
                    }
                }
                return arrayMap;
            }catch (Throwable e){
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {

            String systemServiceName = getServiceNameByServiceManager.get(newValue);
            if (systemServiceName != null) {
                SimpleMenuPreference p2 = screen.findPreference("method_list");
                p2.setEntries(getMethodFieldValueBySystemService(systemServiceName).keySet().toArray(new CharSequence[0]));
                p2.setEntryValues(getMethodFieldValueBySystemService(systemServiceName).values().toArray(new CharSequence[0]));
                p2.setSummaryProvider(preference1 -> {
                    try {
                        for (Method m : loader.loadClass(systemServiceName + "$Default").getDeclaredMethods()) {

                            if (m.getName().equals(((SimpleMenuPreference) preference1).getEntry().toString().split(" ")[1])) {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (Class<?> clz : m.getParameterTypes()) {
                                    if (clz.equals(String.class)) stringBuilder.append(" s16 ");
                                    if (clz.equals(boolean.class)) stringBuilder.append(" i32 ");
                                    if (clz.equals(int.class)) stringBuilder.append(" i32 ");
                                    if (IBinder.class.isAssignableFrom(clz))
                                        stringBuilder.append(" null ");
                                    if (IInterface.class.isAssignableFrom(clz))
                                        stringBuilder.append(" null ");
                                }
                                String s = "service call " + newValue + " " + ((SimpleMenuPreference) preference1).getValue() + " " + stringBuilder;
                                System.out.println("generate command: " + s);
                                ClipData data = ClipData.newPlainText("", s);
                                ServiceManager.getClipboardManager().setPrimaryClip(data);
                                Toast.makeText(requireContext(), s + "", Toast.LENGTH_SHORT).show();
                                return m.getName() + " " + Arrays.asList(m.getParameterTypes());
                            }
                        }
                    } catch (NullPointerException | ClassNotFoundException ignored) {
                    }
                    return null;
                });
            }


            return true;
        }
    }


}