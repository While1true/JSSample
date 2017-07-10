package com.example.myapplication;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

/**
 * Created by 旅行家的梦想 on 2017/7/9.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);

    }
}
