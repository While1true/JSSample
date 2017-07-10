package com.example.myapplication.Sample3.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.myapplication.R;
import com.zhy.autolayout.utils.AutoUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by S0005 on 2017/5/26.
 */

public class WindowHolder extends RecyclerView.ViewHolder {

    @InjectView(R.id.img_logo)
    public ImageView imgLogo;
    @InjectView(R.id.state_qlj)
    public TextView stateQlj;
    @InjectView(R.id.state_new)
    public TextView stateNew;
    @InjectView(R.id.state_from)
    public TextView stateFrom;
    @InjectView(R.id.title_name)
    public TextView titleName;
    @InjectView(R.id.newprice)
    public TextView newprice;
    @InjectView(R.id.oldprice)
    public TextView oldprice;
    @InjectView(R.id.soldnum)
    public TextView soldnum;
    @InjectView(R.id.more)
    public TextView more;
    @InjectView(R.id.quan_flag)
    public ImageView quanFlag;
//    @BindView(R.id.similar)
//    public ImageView similar;
//    @BindView(R.id.collect)
//    public ImageView collect;
//    @BindView(R.id.ll_more)
//    public LinearLayout llMore;

    public WindowHolder(View itemView) {
        super(itemView);
        AutoUtils.auto(itemView);
        ButterKnife.inject(this, itemView);

    }
}
