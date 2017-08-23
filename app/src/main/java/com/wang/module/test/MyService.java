package com.wang.module.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

public class MyService extends Service {

    private static final String TAG = "MyService";

    public MyService() {
    }

    @Override
    public void onCreate() {   //只是创建执行一次
        super.onCreate();
        Log.d(TAG, "service的onCreate()方法");

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {  //每次点击都会执行
        Log.d(TAG, "service的onStartCommand()方法");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {   //关闭时执行
        Log.d(TAG, "service的onDestroy()方法");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
