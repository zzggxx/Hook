package com.maniu.hookams;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HookAms {
    Context context;

    public void hookAms(Context context) throws Exception {
        this.context = context;
//         小于26
        Field gDefault = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Class<?> ActivityManagerNativecls = Class.forName("android.app.ActivityManagerNative");
            gDefault = ActivityManagerNativecls.getDeclaredField("gDefault");
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            Class<?> activityManager = Class.forName("android.app.ActivityManager");
            gDefault = activityManager.getDeclaredField("IActivityManagerSingleton");
        } else {
//            更高的版本
        }

        gDefault.setAccessible(true);
        Object defaultValue = gDefault.get(null);
        Class<?> SingletonClass = Class.forName("android.util.Singleton");
        Field mInstance = SingletonClass.getDeclaredField("mInstance");
        mInstance.setAccessible(true);

//        IACtiviityManager  app  仅此一份   全班 60  1个女生        随心所欲
        Object iActivityManagerObject = mInstance.get(defaultValue);
//hook点     处理   ------》  处理器       动态代理
        iActivityManagerObject.hashCode();

        Class<?> IActivityManagerIntercept = Class.forName("android.app.IActivityManager");
//        动态代理
//        参数:类加载器,,处理器
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{IActivityManagerIntercept}, new AmsInvocationHandler(iActivityManagerObject));
        mInstance.set(defaultValue, proxy);
    }

    //钩子         破解  系统破解 不能hook   敌军   弱点   前提是需要有hook
    class AmsInvocationHandler implements InvocationHandler {

        private Object iActivityManagerObject;

        public AmsInvocationHandler(Object iActivityManagerObject) {
            this.iActivityManagerObject = iActivityManagerObject;
        }

        /**Hook技术利用binder技术,性能高,但是非常的不安全*/
        @Override
        public Object invoke(Object o, Method method, Object[] args) throws Throwable {
            Log.i("david", "----------------ams------------   " + method.getName());
            if ("startActivity".equals(method.getName())) {

                Intent intent = null;
                int index = 0;
                for (int i = 0; i < args.length; i++) {

                    Object arg = args[i];
                    if (arg instanceof Intent) {
                        intent = (Intent) args[i]; // 原意图，过不了安检
                        index = i;
                    }
                }

                SharedPreferences share = context.getSharedPreferences("david", Context.MODE_PRIVATE);
                ComponentName componentName = null;
                if (!share.getBoolean("login", false)) {
                    String jumpActivity = intent.getComponent().getClassName();

//                    需要登录的界面写一个xml来被读取,方便管理.就不像之前的那么繁琐的判断,每一次跳转都要判断,这是集中式的判断
                    if (!"com.maniu.hookams.SceondActivity".equals(jumpActivity)) {
                        intent.putExtra("extraIntent", jumpActivity);

                        //                        当前状态没有登录  怎么   跳转到登录?

//                        不能直接跳转因为Hook不能改变人家本来的目的,改了的话人家就发现了,但是你可以改变达到目的的参数
//                        context.startActivity(new Intent(context, LoginActivity.class));
                        componentName = new ComponentName(context, LoginActivity.class);
                        intent.setComponent(componentName);
                    }

                }
            }
            return method.invoke(iActivityManagerObject, args);
        }
    }

    /*-------------------------------------PMS的Hook,一般用的不多----------------------------------------------*/
    class HookHandler implements InvocationHandler {


        private Object iActivityManagerObject;

        public HookHandler(Object iActivityManagerObject) {
            this.iActivityManagerObject = iActivityManagerObject;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] args) throws Throwable {
            Log.i("david", "----------------pms------------   " + method.getName());
            return method.invoke(iActivityManagerObject, args);
        }
    }

    public void hookPMS(Context context) {
        try {
            // 获取全局的ActivityThread对象
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);//得到ActivityThread对象

            // 获取ActivityThread里面原始的 sPackageManager
            Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");

            sPackageManagerField.setAccessible(true);
            Object sPackageManager = sPackageManagerField.get(currentActivityThread);

            // 准备好代理对象, 用来替换原始的对象
            Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
            Object proxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(),
                    new Class<?>[]{iPackageManagerInterface},
                    new HookHandler(sPackageManager));

            // 1. 替换掉ActivityThread里面的 sPackageManager 字段
            sPackageManagerField.set(currentActivityThread, proxy);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
