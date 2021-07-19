package com.maniu.hookams;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/2/26 0026.
 */

public class LoginActivity extends Activity {
    EditText name;
    EditText password;
//    原来跳转的  acitivty 页面

//    不安全  系统    不安全  只是应用内部 ，，，没办法解决,
//    注意这里只是应用内部的,因为你得注入就是进程内的,若是想hook别人,请使用Native,使用xpose技术
    private String className;
    SharedPreferences share;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name = (EditText) findViewById(R.id.name);
        password = (EditText) findViewById(R.id.password);
        share = this.getSharedPreferences("david", MODE_PRIVATE);//实例化
        className = getIntent().getStringExtra("extraIntent");
        if (className != null) {
            ((TextView)findViewById(R.id.text)).setText(" 跳转界面："+className);
        }
    }

    public void login(View view) {
        if ((name.getText() == null || password.getText() == null)) {
            Toast.makeText(this, "请填写用户名 或密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("david".equals(name.getText().toString()) && "123456".equals(password.getText()
                .toString())) {
            SharedPreferences share = super.getSharedPreferences("david", MODE_PRIVATE);//实例化
            SharedPreferences.Editor editor = share.edit(); //使处于可编辑状态
            editor.putString("name", name.getText().toString());
            editor.putString("sex", password.getText().toString());
            editor.putBoolean("login",true);   //设置保存的数据
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            editor.commit();    //提交数据保存
            if (className != null) {
                ComponentName componentName = new ComponentName(this, className);
                Intent intent = new Intent();
                intent.setComponent(componentName);
                startActivity(intent);
                finish();
            }
        }else{
            SharedPreferences.Editor editor = share.edit(); //使处于可编辑状态
            editor.putBoolean("login",false);   //设置保存的数据
            Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
            editor.commit();    //提交数据保存
        }
    }
}
