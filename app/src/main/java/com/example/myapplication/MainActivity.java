package com.example.myapplication;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.View.SProgress;
import com.example.myapplication.View.SScrollview;

import java.util.Random;


public class MainActivity extends AppCompatActivity {
    SScrollview sScrollview;
    SProgress sProgress;
    int progress;
    int[][] colors = new int[][]{{0xff1565c0, 0xff004d40, 0xffff6f00, 0xff666666, 0xff263238, 0xff0091ea}, {0xff64b5f6, 0xffb39ddb, 0xff66bb6a, 0xffa1887f, 0xff9e9e9e, 0xff404cff}};

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    int i = (int) (sProgress.getProgress() + 2);
                    int a = new Random().nextInt(5);
                    int b = new Random().nextInt(5);
                    if (i > 100)
                        sProgress.setColor(colors[0][b], colors[1][a], 0xffff0000);

                    sProgress.setProgress(i % 101);
                    sendEmptyMessageDelayed(1, 100);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sScrollview = (SScrollview) findViewById(R.id.sscrollview);
        sScrollview.addHeader(new View(this), 100)
                .setActrullyScrollMode(true, true)
                .setOverScrollEnable(true, true, false, false)
                .setRefreshMode(true, true, false, false);

        initialProgress();
    }

    private void initialProgress() {
        sProgress = (SProgress) findViewById(R.id.sprogress);

        findViewById(R.id.restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                int a = new Random().nextInt(5);
                int b = new Random().nextInt(5);

                sProgress
                        .setProgress(0)
                        .setColor(colors[0][b], colors[1][a], 0xffff0000)
                        .setTextSize(12)
                        .animatorToProgress(98.9f);
            }
        });

        findViewById(R.id.frentallystart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                if (tv.getText().toString().equals("动态进度")) {
                    tv.setText("停止");
                    handler.sendEmptyMessage(1);
                } else {
                    tv.setText("动态进度");
                    handler.removeCallbacksAndMessages(null);
                }
            }
        });


    }
}
