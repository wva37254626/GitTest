package com.wang.module.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //拿到短信的信息,短信内容封装在intent中
        Bundle bundle = intent.getExtras();
        //以pdus为键，取出一个object数组，数组中的每一个元素，都是一条短信
        Object[] objects = (Object[]) bundle.get("pdus");

        //拿到广播中的所有短信
        for (Object object : objects) {
            //通过pdu来构造短信
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
            if (sms.getOriginatingAddress().equals("138438")) {
                //阻止其他广播接收者收到这条广播
                abortBroadcast();
//				SmsManager.getDefault().sendTextMessage(sms.getOriginatingAddress(), null, "你是个好人", null, null);
            }
//			System.out.println(sms.getOriginatingAddress()); //短信来自于那个号码
//			System.out.println(sms.getMessageBody());  //短信内容
        }
    }
}