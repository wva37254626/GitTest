package com.wang.module.test;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class MyContentProvider extends ContentProvider {
    private static final String TAG = "MyContentProvider";

    public MyContentProvider() {

    }

    //删除
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    //返回类型
    @Override
    public String getType(Uri uri) {
        return null;
    }

    //插入
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    //创建时候调用
    @Override
    public boolean onCreate() {
        return false;
    }

    //查询
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return null;
    }
    //修改
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d(TAG,"update");
       return 4;
    }
}
