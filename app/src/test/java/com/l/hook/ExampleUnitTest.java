package com.l.hook;

import org.junit.Test;

import java.lang.reflect.Field;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        getActivityManager();
    }

    /*通过反射获得已经存在的对象*/
    private void getActivityManager() throws Exception {
//        由此可见这里的也是可以拿到真的系统层面的ActivityManager的,就是通过包名
        Field mIActivityManager = Class.forName("com.l.hook.ActivityManager").getField("mIActivityManager");
//        Field mIActivityManager = ActivityManager.class.getField("mIActivityManager");
//        获取到Field的那个对象
        IActivityManager iActivityManager = (IActivityManager) mIActivityManager.get(null);
        String name = iActivityManager.getName();
        System.out.println(name);
    }
}