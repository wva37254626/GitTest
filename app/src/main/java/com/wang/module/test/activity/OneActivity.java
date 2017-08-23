package com.wang.module.test.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wang.module.test.R;
import com.wang.module.test.bean.Book;
import com.wang.module.test.utils.HttpUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OneActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mInfo;

    private static final String TAG = "OneActivity";

    //主线程定义Handler对象,实现handleMessage()方法;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String data = (String) msg.obj;
            mInfo.setText(data);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);

        Button firstNext = (Button) findViewById(R.id.bt_next1);  //跳转下一主页
        firstNext.setOnClickListener(this);

        Button refreshUI = (Button) findViewById(R.id.bt_refreshUI);  //刷新UI
        mInfo = (TextView) findViewById(R.id.tv_info);
        refreshUI.setOnClickListener(this);

        Button netGet = (Button) findViewById(R.id.bt_get);
        Button netPost = (Button) findViewById(R.id.bt_post);
        netGet.setOnClickListener(this);
        netPost.setOnClickListener(this);

        Button xmlPull = (Button) findViewById(R.id.bt_pull);
        xmlPull.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_next1:
                startActivity(new Intent(OneActivity.this, TwoActivity.class));
                break;
            case R.id.bt_refreshUI:
                getDataFromNet();
                break;
            case R.id.bt_get:
                getNetGet();
                break;
            case R.id.bt_post:
                getNetPost();
                break;
            case R.id.bt_pull:
                parseXml();
                break;
        }
    }




    /**
     * 测试Hanlder请求网络
     */
    private void getDataFromNet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //耗时操作,让线程睡1秒
                SystemClock.sleep(1000);
                //消息
                Message mes = new Message();
                mes.obj = "看你毛线";
                //handler发送消息到消息队列中
                handler.sendMessage(mes);
            }
        }).start();
    }

    /**
     * 测试网络请求get
     */
    public void getNetGet() {
        HttpUtils.doGetAsyn("http://www.baidu.com", new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                //result就是请求回调的结果
                //....在用handler刷新界面等逻辑
            }
        });
    }

    /**
     * 测试网络请求post
     */
    private void getNetPost() {
        HttpUtils.doPostAsyn("http://www.baidu.com", "name1=value1&name2=value2", new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                //result就是请求回调的结果
                //....在用handler刷新界面等逻辑
            }
        });

    }

    /**
     * 解析xml
     */
    private void parseXml() {
        List<Book> mList=null;
        Book book=null;
        //第一步,创建pull解析器
        XmlPullParser xmlPullParser = Xml.newPullParser();
        try {
            //获取assets中文件
            InputStream xml = getAssets().open("books.xml");
            //第二步,设置解析对象及编码
            xmlPullParser.setInput(xml,"UTF-8");
            //第三步,获取解析到的事件类型
            int eventType = xmlPullParser.getEventType();
            //第四步,未解析到文档结尾,则循环解析
            while(eventType!=XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:  //文档开始事件
                        Log.d(TAG,"读取文档开始!");
                        break;
                    case XmlPullParser.START_TAG:   //开始标签事件
                        String tagName=xmlPullParser.getName().trim(); //获取标签名称
                        if ("books".equals(tagName)){
                            mList=new ArrayList<Book>();
                        }else if ("book".equals(xmlPullParser.getName())){
                            book = new Book();
                            String id = xmlPullParser.getAttributeValue(null, "id"); //获取标签的id属性值
                            book.setId(id);
                        }else if ("name".equals(tagName)){
                            book.setName(xmlPullParser.nextText().trim());   //获取标签中的文本内容
                        }else if ("author".equals(tagName)){
                            book.setAuthor(xmlPullParser.nextText().trim());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("book".equals(xmlPullParser.getName())){
                            mList.add(book);
                        }
                        break;

                }
                eventType=xmlPullParser.next();   //将指针调到下一事件状态
            }

            for (Book book1: mList){
                Log.d(TAG,"编号:"+book1.getId()+",书名:"+book1.getName()+",作者:"+book1.getAuthor());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }


}
