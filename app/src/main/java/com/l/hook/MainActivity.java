package com.l.hook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.lang.reflect.Field;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
//            一下需要在对应的源码中进行,gDefault就是在<26版本中才有的
            Class<?> mActivityManagerNative = Class.forName("android.app.ActivityManagerNative");
            Field gDefault = mActivityManagerNative.getDeclaredField("gDefault");
            gDefault.setAccessible(true);
            Object defaultValue = gDefault.get(null);
            System.out.println("---:" + defaultValue.hashCode());

            Class<?> mSingletonClass = Class.forName("android.util.Singleton");
            Field mInstance = mSingletonClass.getDeclaredField("mInstance");
            mInstance.setAccessible(true);
            Object iAtivityManagerPbject = mInstance.get(defaultValue);

        } catch (Exception e) {
            System.out.println("error: " + e);
        }

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                startActivity(intent);
            }
        });
    }
}