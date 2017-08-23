package com.wang.module.test.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.wang.module.test.utils.ActivityManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/2.
 */

public class BaseActivity extends AppCompatActivity {

    //activity创建的时候调用
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("BaseActivity", getClass().getSimpleName()); //得当当前类的名称
        ActivityManager.addActivity(this);
    }

    //activity销毁的时候调用
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }

}
