package com.wang.module.test;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.wang.module.test.activity.BaseActivity;
import com.wang.module.test.activity.OneActivity;
import com.wang.module.test.db.MySQLiteOpenHelper;
import com.wang.module.test.fragment.Right2Fragment;
import com.wang.module.test.fragment.RightFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private Intent mIntent;
    private MySQLiteOpenHelper oh;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //实例化组件对象
        Button startSeriver = (Button) findViewById(R.id.bt_seriver);
        Button stopSeriver = (Button) findViewById(R.id.bt_unSeriver);
        Button dbInsert = (Button) findViewById(R.id.bt_insert);
        Button dbDelete = (Button) findViewById(R.id.bt_delete);
        Button dbUpdate = (Button) findViewById(R.id.bt_update);
        Button dbSelect = (Button) findViewById(R.id.bt_select);
        Button btFragment = (Button) findViewById(R.id.bt_fragment1);
        Button btFragment2 = (Button) findViewById(R.id.bt_fragment2);
        Button nextActivity = (Button) findViewById(R.id.bt_next);
        //设置组件的点击事件
        startSeriver.setOnClickListener(this);
        stopSeriver.setOnClickListener(this);
        dbInsert.setOnClickListener(this);
        dbDelete.setOnClickListener(this);
        dbUpdate.setOnClickListener(this);
        dbSelect.setOnClickListener(this);
        btFragment.setOnClickListener(this);
        btFragment2.setOnClickListener(this);
        nextActivity.setOnClickListener(this);

        //意图类,跳转界面或打开操作
        mIntent = new Intent(this, MyService.class);
        //参数(上下文,数据库名称,游标工厂,版本号)
        oh = new MySQLiteOpenHelper(this, "people.db", null, 1);
        //对数据库的操作对象
        db = oh.getWritableDatabase();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_seriver:
                startService(mIntent); //启动服务
                break;
            case R.id.bt_unSeriver:
                stopService(mIntent);  //关闭服务
                break;
            case R.id.bt_insert:
                insertApi();           //数据库_增
                break;
            case R.id.bt_delete:
                deleteApi();           //数据库_删
                break;
            case R.id.bt_update:
                updateApi();           //数据库_改
                break;
            case R.id.bt_select:
                selectApi();           //数据库_查
                break;
            case R.id.bt_fragment1:
                openRightFragment2();           //切换碎片1
                break;
            case R.id.bt_fragment2:
                openRightFragment();           //切换碎片2
                break;
            case R.id.bt_next:
                startActivity(new Intent(this, OneActivity.class)); //跳转下一页面
                break;

        }
    }

    /**
     * 打开fragment2
     */
    private void openRightFragment2() {
        Right2Fragment fragment2=new Right2Fragment();
        //获取fragment管理者
        FragmentManager fragmentManager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //把内容显示到帧布局
        transaction.replace(R.id.right_layout,fragment2);
        //提交
        transaction.commit();
    }
    /**
     * 打开fragment
     */
    private void openRightFragment() {
        RightFragment fragment=new RightFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.right_layout,fragment);
        transaction.commit();
    }

    public void insertApi(){
        //把要插入的数据全部封装至ContentValues对象
        ContentValues values = new ContentValues();
        values.put("name", "游天龙");
        values.put("salary", "16000");
        values.put("phone", "15999");
        long id = db.insert("person", null, values);   //返回的是插入行对应的id
        values.clear();
        Log.d(TAG, "什么鬼");
    }

    public void deleteApi(){
        int i = db.delete("person", "name = ? and _id = ?", new String[]{"游天龙", "1"});  //返回删除的行数
        Log.d(TAG,i+"");
    }

    public void updateApi(){
        ContentValues values = new ContentValues();
        values.put("salary", "26000");
        int i = db.update("person", values, "name = ?", new String[]{"游天龙"});  //受影响的行数
        Log.d(TAG,i+"");
    }

    public void selectApi(){
        Cursor cursor = db.query("person", null, null, null, null, null, null, null);
        while(cursor.moveToNext()){  //移动到下一条记录
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            String salary = cursor.getString(cursor.getColumnIndex("salary"));
            Log.d(TAG,name + ";" + phone + ";" + salary);
        }
        cursor.close();  //关闭游标
    }
}
