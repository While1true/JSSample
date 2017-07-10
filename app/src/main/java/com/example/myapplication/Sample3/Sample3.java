package com.example.myapplication.Sample3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;
import com.example.myapplication.View.SScrollview;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 旅行家的梦想 on 2017/7/9.
 */

public class Sample3 extends AppCompatActivity {
    @InjectView(R.id.lesson1)
    Button lesson1;
    @InjectView(R.id.lesson2)
    Button lesson2;
    @InjectView(R.id.lesson3)
    Button lesson3;
    @InjectView(R.id.lesson4)
    Button lesson4;
    @InjectView(R.id.lesson5)
    Button lesson5;
    @InjectView(R.id.lesson6)
    Button lesson6;
    @InjectView(R.id.lesson7)
    Button lesson7;
    @InjectView(R.id.lesson8)
    Button lesson8;
    @InjectView(R.id.sscrollview)
    SScrollview sscrollview;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample3);
        ButterKnife.inject(this);
        sscrollview.setRefreshMode(true,true,false,false)
                //fling 头部是否出来       尾部是否出来            是否加载
                //boolean overscrollhead, boolean overscrollfoot, boolean overloadingheader, boolean overloadingfooter
        .setOverScrollEnable(true,true,true,true);
    }

    @OnClick({R.id.lesson1, R.id.lesson2, R.id.lesson3, R.id.lesson4, R.id.lesson5, R.id.lesson6, R.id.lesson7, R.id.lesson8})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lesson1:
                startActivity(1);
                break;
            case R.id.lesson2:
                startActivity(2);
                break;
            case R.id.lesson3:
                startActivity(3);
                break;
            case R.id.lesson4:
                startActivity(4);
                break;
            case R.id.lesson5:
                startActivity(5);
                break;
            case R.id.lesson6:
                startActivity(6);
                break;
            case R.id.lesson7:
                startActivity(7);
                break;
            case R.id.lesson8:
                startActivity(new Intent(this, SAllActivity.class));
                break;
        }
    }

    private void startActivity(int type) {
        SRActivity.come = type;
        startActivity(new Intent(this, SRActivity.class));

    }

}
