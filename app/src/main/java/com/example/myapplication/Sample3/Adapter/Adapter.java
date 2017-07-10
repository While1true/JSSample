package com.example.myapplication.Sample3.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 旅行家的梦想 on 2017/7/9.
 */

public class Adapter extends RecyclerView.Adapter {
    List<HomeProductBean> list = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WindowHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.windows_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WindowHolder) {
            ItemUtils.setItemData((WindowHolder) holder, position, list.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void add(List<HomeProductBean> list){
        this.list.addAll(list);
        notifyDataSetChanged();
    }
    public void set(List<HomeProductBean> list){
        this.list=list;
        notifyDataSetChanged();
    }
}
