package com.l.hook;

/**
 * author: Lenovo
 * date: 2021/6/28,11:04
 * projectName:Hook
 * packageName:com.l.hook
 */

// 假定这是一个系统的层面的管理activity的类
public class ActivityManager {
    //具有唯一性质的对象,并且前边必须有static,不然是无法反射出来的,也就是说无法被hook的
    public static IActivityManager mIActivityManager = new IActivityManager("zgx");
}

class IActivityManager {
    private String name;

    public IActivityManager(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
