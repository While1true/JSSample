package com.example.myapplication.Sample3;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.example.myapplication.R;
import com.example.myapplication.Sample3.Adapter.Adapter;
import com.example.myapplication.Sample3.Adapter.HomeProductBean;
import com.example.myapplication.View.SRecyclerView;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by 旅行家的梦想 on 2017/7/9.
 */

public class SRActivity extends AutoLayoutActivity {
    public static int come = 1;
    @InjectView(R.id.srecyclerview)
    SRecyclerView srecyclerview;
    @InjectView(R.id.ic)
    ImageView ic;

    private int ic_origin_height;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.srec_layout);
        ButterKnife.inject(this);
        final Adapter adapter = new Adapter();
        adapter.add(creatProduct());
        ic.post(new Runnable() {
            @Override
            public void run() {
                ic_origin_height = ic.getLayoutParams().height;
            }
        });
        switch (come) {
            case 1:
                srecyclerview
                        .setPullRate(2)
                        .setAdapter(new GridLayoutManager(this, 2), adapter)
                        .setRefreshMode(true, true, false, false);
                break;
//            case 2:
//                srecyclerview
//                        .addHeader(new TextView(this), 100)
//                        .setAdapter(new GridLayoutManager(this, 2), adapter)
//                        .setRefreshMode(true, true, false, false)
//                break;
            case 3:
                srecyclerview
                        .addDefaultHeaderFooter()
                        .setAdapter(new GridLayoutManager(this, 2), adapter)
                        .setRefreshMode(true, true, true, true)
                        .setRefreshingListener(new SRecyclerView.OnRefreshListener() {
                            @Override
                            public void Refreshing() {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.set(creatProduct());
                                        srecyclerview.notifyRefreshComplete();
                                    }
                                }, 1000);
                            }

                            @Override
                            public void Loading() {
                                super.Loading();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
//                                        adapter.add(creatProduct());
                                        srecyclerview.notifyRefreshComplete();
                                    }
                                }, 1000);
                            }
                        });
                break;
//            case 4:
//                srecyclerview
//                        .addDefaultHeaderFooter()
//                        .setAdapter(new GridLayoutManager(this, 2), adapter)
//                        .setRefreshMode(true, true, true, true)
//                        .setRefreshingListener(new SRecyclerView.OnRefreshListener() {
//                            @Override
//                            public void Refreshing() {
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        adapter.set(creatProduct());
//                                        srecyclerview.notifyRefreshComplete();
//                                    }
//                                }, 1000);
//                            }
//
//                            @Override
//                            public void Loading() {
//                                super.Loading();
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//                                        srecyclerview.notifyRefreshComplete();
//                                    }
//                                }, 1000);
//                            }
//
//                        });
//                break;
            case 5:
                srecyclerview
                        .addDefaultHeaderFooter()
                        .setPullRate(3)
                        .setPreLoadingCount(3)
                        .setAdapter(new GridLayoutManager(this, 2), adapter)
                        .setRefreshMode(true, true, false, false)
                        .setActrullyScrollMode(false, false)
                        .setRefreshingListener(new SRecyclerView.OnRefreshListener() {

                            @Override
                            public void pullDown(int height) {
                                super.pullDown(height);
                                ic.getLayoutParams().height = ic_origin_height - height;
                                ic.requestLayout();
                            }

                            @Override
                            public void pullUp(int height) {
                                super.pullUp(height);
                            }

                            @Override
                            public void Loading() {
                                super.Loading();
                            }

                            @Override
                            public void PreLoading() {
                                super.PreLoading();
                            }

                            @Override
                            public void Refreshing() {

                            }
                        });
                break;
            case 6:

                srecyclerview
                        .addDefaultHeaderFooter()
                        .setAdapter(new GridLayoutManager(this, 2), adapter)
                        .setRefreshMode(true, false, true, false)
                        .setPreLoadingCount(2)
                        .setRefreshingListener(new SRecyclerView.OnRefreshListener() {
                            @Override
                            public void PreLoading() {
                                super.PreLoading();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.showLong("加在完成");
                                        adapter.add(creatProduct());
                                        srecyclerview.notifyRefreshComplete();
                                    }
                                }, 200);
                            }

                            @Override
                            public void Refreshing() {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.set(creatProduct());
                                        srecyclerview.notifyRefreshComplete();
                                    }
                                }, 1000);
                            }

                            @Override
                            public void Loading() {
                                super.Loading();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.add(creatProduct());
                                        srecyclerview.notifyRefreshComplete();
                                    }
                                }, 1000);
                            }
                        });
                break;
            case 7:
                final TextView header = new TextView(this);
                header.setText("这是自定义头部");

                final TextView footer = new TextView(this);
                footer.setText("这是自定义尾部");

                srecyclerview
                        .addHeader(header, 100)
                        .addFooter(footer, 50)
                        .setAdapter(new GridLayoutManager(this, 2), adapter)
                        .setRefreshMode(true, true, true, true)
                        .setRefreshingListener(new SRecyclerView.OnRefreshListener() {
                            @Override
                            public void pullDown(int height) {
                                super.pullDown(height);
                                header.setText("这是自定义头部" + height);
                            }

                            @Override
                            public void pullUp(int height) {
                                super.pullUp(height);
                                footer.setText("这是自定义尾部" + height);
                            }

                            @Override
                            public void Refreshing() {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.set(creatProduct());
                                        srecyclerview.notifyRefreshComplete();
                                    }
                                }, 1000);
                            }

                            @Override
                            public void Loading() {
                                super.Loading();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        srecyclerview.notifyRefreshComplete();
                                    }
                                }, 1000);
                            }
                        });
                break;

        }
    }

    private List<HomeProductBean> creatProduct() {
        List<HomeProductBean> pr = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            HomeProductBean temp = new HomeProductBean();
            pr.add(temp);
        }
        return pr;
    }
}
