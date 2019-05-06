package org.dp.facedetection;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static MyApplication instence;
    @Override
    public void onCreate() {
        super.onCreate();
        instence=this;
    }


    /**
     * 获取全局上下文
     */
    public static Context getContext() {
        return instence;
    }



}
