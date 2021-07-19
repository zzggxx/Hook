package com.maniu.hook;

import java.lang.reflect.Field;

public class Main {


    public static void main(String[] args) throws Exception  {
        Field field =Class.forName("com.maniu.hook.ActivityManager").getField("activityManager");
        IActivityManager object= (IActivityManager) field.get(null);
        System.out.println(object.getName());

    }
}
