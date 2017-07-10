package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Sample1_2.MainActivity;
import com.example.myapplication.Sample3.Sample3;
import com.example.myapplication.View.SScrollview;
import com.example.myapplication.net.HttpUtils;
import com.loopj.android.http.TextHttpResponseHandler;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by 旅行家的梦想 on 2017/7/9.
 */

public class RKAc extends AppCompatActivity {
    @InjectView(R.id.lesson1)
    Button lesson1;
    @InjectView(R.id.lesson2)
    Button lesson2;
    @InjectView(R.id.lesson3)
    Button lesson3;
    @InjectView(R.id.sscrollview)
    SScrollview sscrollview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rk_activity);
        ButterKnife.inject(this);
        sscrollview.setRefreshMode(true, true, false, false)
        .setOverScrollEnable(true,true,true,true);

    }

    @OnClick({R.id.lesson1, R.id.lesson2, R.id.lesson3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lesson1:
                startActivity(MainActivity.class);
                break;
            case R.id.lesson2:
                startActivity(MainActivity.class);
                break;
            case R.id.lesson3:

                startActivity(Sample3.class);
                break;
        }
    }

    private void startActivity(Class clazz) {
        startActivity(new Intent(this, clazz));
    }
}
