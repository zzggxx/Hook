package com.maniu.hook;
public class ActivityManager {
    public static IActivityManager activityManager = new IActivityManager("david");

}
class IActivityManager{
    private String name;

    public IActivityManager(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}