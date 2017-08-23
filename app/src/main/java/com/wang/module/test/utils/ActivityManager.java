package com.wang.module.test.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * activity管理类
 */

public class ActivityManager {

    private static List<Activity> list = new ArrayList<Activity>();

    /**
     * 添加activity方法
     * @param activity
     */
    public static void addActivity(Activity activity) {
        list.add(activity);
    }

    /**
     * 删除activity方法
     * @param activity
     */
    public static void removeActivity(Activity activity) {
        list.remove(activity);
    }

    /**
     * 退出所有的activity方法
     */
    public static void finishAll() {
        for (Activity activity : list) {
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
