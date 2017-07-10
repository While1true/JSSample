package com.example.myapplication.Sample3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.myapplication.R;
import com.example.myapplication.View.NestedScrollWebView;
import com.example.myapplication.View.SAllView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by 旅行家的梦想 on 2017/7/9.
 */

public class SAllActivity extends AppCompatActivity {
    @InjectView(R.id.webview)
    NestedScrollWebView webview;
    @InjectView(R.id.sallview)
    SAllView sallview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sall_activity);
        ButterKnife.inject(this);

        sallview.addDefaultHeaderFooter()
                .setRefreshMode(true,true,true,true)
                .setRefreshingListener(new SAllView.OnRefreshListener() {
                    @Override
                    public void Refreshing() {
                        sallview.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sallview.notifyRefreshComplete();
                            }
                        },1000) ;
                    }

                    @Override
                    public void Loading() {
                        super.Loading();
                        sallview.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sallview.notifyRefreshComplete();
                            }
                        },1000) ;
                    }
                });



        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webview.loadUrl(url);
                return true;
            }
        });
        webview.loadUrl("https://github.com/While1true");
    }
}
