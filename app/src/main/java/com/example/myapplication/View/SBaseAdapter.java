///*
// * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
// * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
// * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
// * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
// * Vestibulum commodo. Ut rhoncus gravida arcu.
// */
//
//package com.example.myapplication.View;
//
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//
//
///**
// * Created by S0005 on 2017/4/17.
// * SHOW_EMPTY:为空时  SHOW_LOADING：加载  SHOW_ERROR：网络错误 SHOW_NOMORE：无更多
// */
//
//public abstract class SBaseAdapter<T> extends RecyclerView.Adapter {
//    private RecyclerView.ViewHolder emptyholder = null, neterrorholder = null, loadingholder = null;
//    public static final int SHOW_EMPTY = -100, SHOW_LOADING = -200, SHOW_ERROR = -300, SHOW_NOMORE = -400;
//    protected int showstate;
//    private String text = "";
//    protected T t;
//
//    protected HandlerListener listener;
//
//    public static final int TYPE_ITEM = 30000000;
//    private int height = 0;
//
//    //各种状态的资源id
//    int emptyres = R.layout.empty_textview, loadingres = R.layout.sbase_laoding, errorres = R.layout.network_error;
//
//    //是否全屏
//    private boolean full = true;
//    private SLoading sLoading;
//
//    public void setStateLayout(int emptyres, int loadingres, int errorres, boolean full) {
//        this.emptyres = emptyres;
//        this.loadingres = loadingres;
//        this.errorres = errorres;
//        this.full = full;
//    }
//
//    public static class HandlerListener {
//        public void netError() {
//        }
//    }
//
//    public void setHolder(RecyclerView.ViewHolder emptyholder, RecyclerView.ViewHolder neterrorholder, RecyclerView.ViewHolder loadingholder) {
//        this.emptyholder = emptyholder;
//        this.neterrorholder = neterrorholder;
//        this.loadingholder = loadingholder;
//    }
//
//    public int getShowstate() {
//        return showstate;
//    }
//
//    public void setBean(T t) {
//        this.t = t;
//        showstate = TYPE_ITEM;
//    }
//
//    public T getBean() {
//        return t;
//    }
//
//    @Override
//    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//        if (full) {
//            recyclerView.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (height == 0)
//                        height = recyclerView.getMeasuredHeight();
////                    notifyDataSetChanged();
//                }
//            });
//
//        }
//    }
//
//    public void showState(int showstate, String text) {
//        this.showstate = showstate;
//        this.text = text;
//    }
//
//    public void showState(int showstate, String text, int height) {
//        this.height=height;
//        this.showstate = showstate;
//        this.text = text;
//        if (showstate != SHOW_LOADING && sLoading != null)
//            sLoading.stopAnimator();
//    }
//
//    @Override
//    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
//        super.onDetachedFromRecyclerView(recyclerView);
//        if(sLoading!=null){
//            sLoading.stopAnimator();
//            sLoading=null;
//        }
//    }
//
//    private RecyclerView.ViewHolder creatHolder(int layout, ViewGroup viewGroup) {
//
//        return new RecyclerView.ViewHolder(InflateView(layout, viewGroup)) {
//            @Override
//            public String toString() {
//                return super.toString();
//            }
//        };
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        switch (viewType) {
//            case SHOW_EMPTY:
//                if (emptyholder == null && emptyres != 0)
//                    return creatHolder(emptyres, parent);
//                return emptyholder;
//            case SHOW_LOADING:
//                if (loadingholder == null && loadingres != 0)
//                    return creatHolder(loadingres, parent);
//                return loadingholder;
//            case SHOW_ERROR:
//                if (neterrorholder == null && errorres != 0)
//                    return creatHolder(errorres, parent);
//                return neterrorholder;
//            case SHOW_NOMORE:
//                return creatHolder(R.layout.view_home_nomore_normal, parent);
//        }
//        return onCreate(parent, viewType);
//    }
//
//    public View InflateView(int layout, ViewGroup parent) {
//
//        return LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        switch (showstate) {
//            case SHOW_EMPTY:
//                TextView tv = (TextView) holder.itemView.findViewById(R.id.tv);
//                if (tv != null)
//                    tv.setText(text);
//                if (height != 0)
//                    holder.itemView.getLayoutParams().height = height;
//                break;
//            case SHOW_LOADING:
//                if (height != 0)
//                    holder.itemView.getLayoutParams().height = height;
//
//
//                if (sLoading == null) {
//                    sLoading = (SLoading) holder.itemView.findViewById(R.id.sloading);
//                }
//                if (sLoading != null)
//                    sLoading.startAnimator();
//
//                break;
//            case SHOW_ERROR:
//                if (height != 0)
//                    holder.itemView.getLayoutParams().height = height;
//
//                View reloadbt = holder.itemView.findViewById(R.id.reload);
//                if (reloadbt != null && listener != null)
//                    reloadbt.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            listener.netError();
//                        }
//                    });
//
//                break;
//            case SHOW_NOMORE:
//                if (position == getItemCount() - 1)
//                    ((TextView) holder.itemView.findViewById(R.id.tv_nomore)).setText(text);
//                break;
//        }
//        onBind(holder, position);
//    }
//
//    @Override
//    public int getItemCount() {
//        if (showstate == SHOW_EMPTY || showstate == SHOW_LOADING || showstate == SHOW_ERROR) {
//            return 1;
//        }
//        if (showstate == SHOW_NOMORE)
//            return getCount() + 1;
//        return getCount();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        switch (showstate) {
//            case SHOW_EMPTY:
//                return SHOW_EMPTY;
//            case SHOW_LOADING:
//                return SHOW_LOADING;
//            case SHOW_ERROR:
//                return SHOW_ERROR;
//            case SHOW_NOMORE:
//                if (position < getItemCount() - 1)
//                    return getType(position);
//                else
//                    return SHOW_NOMORE;
//
//        }
//        return getType(position);
//    }
//
//    protected abstract RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType);
//
//    protected abstract int getCount();
//
//    protected abstract void onBind(RecyclerView.ViewHolder holder, int position);
//
//    protected abstract int getType(int position);
//
//}
