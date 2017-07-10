package com.example.myapplication.Sample3.Adapter;


import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by S0005 on 2017/5/26.
 * <p>
 * -----------------封装的路越走越远--------------
 * 封装所有橱窗Item 添加长按菜单
 */

public class ItemUtils {
    static SimpleDateFormat format;
    static int flags = 0;
    private static int padding;
    private static SimpleDateFormat format1;

    /**
     * 橱窗展示UI
     *
     * @param holder
     * @param position
     * @param homeProductBean
     * @R.layout.window_item
     */
    public static void setItemData(final WindowHolder holder, final int position, final HomeProductBean homeProductBean) {



        String quan_price = homeProductBean.getQuan_price();


        /**
         * @过期了
         */
        if (isOutOfDate(homeProductBean)) {
            holder.stateQlj.setVisibility(View.GONE);
            holder.oldprice.setVisibility(View.GONE);
            holder.quanFlag.setVisibility(View.GONE);
            ItemUtils.showPrice(holder.oldprice, homeProductBean.getTb_big_price());
        }
        /**
         * @没过期
         */
        else {
            holder.stateQlj.setVisibility(View.VISIBLE);
            holder.stateQlj.setText("券立减 " + quan_price);
            if (flags == 0) {
                flags = holder.oldprice.getPaint().getFlags();
            }
            holder.oldprice.getPaint().setFlags(flags | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.oldprice.setVisibility(View.VISIBLE);
            holder.quanFlag.setVisibility(View.VISIBLE);
            ItemUtils.showPrice(holder.oldprice, homeProductBean.getTb_big_price());
            ItemUtils.showPrice(holder.newprice, homeProductBean.getPrice());

        }

        /***
         * @商品名称
         */
        holder.titleName.setText(homeProductBean.getName());


        /***
         * @上新标签
         */
        if (isToday(homeProductBean.getTb_addtime())) {
            holder.stateNew.setVisibility(View.VISIBLE);
        } else {
            holder.stateNew.setVisibility(View.GONE);
        }

        /***
         * @来源标签
         */
        holder.stateFrom.setText(getFrom(homeProductBean.getTb_type(), homeProductBean.getTb_is_tmall()));

        /**
         * @已售标签
         */
        holder.soldnum.setText("已售 " + homeProductBean.getTb_sale() + " 件");


        /***
         * @设置padding
         */

        if (padding == 0) {
            padding = (int) (SizeUtils.dp2px(14));
        }
        if (position % 2 == 0)
            holder.itemView.setPadding(padding, padding, padding / 2, 0);
        else
            holder.itemView.setPadding(padding / 2, padding, padding, 0);

    }



    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        if (strDate == null) {
            return new Date();
        }
        Date strtodate = formatter.parse(strDate, pos);
        if (strtodate == null) {
            return new Date();
        } else {
            return strtodate;
        }
    }

    /**
     * 是否过期
     *
     * @param homeProductBean
     * @return
     */
    private static boolean isOutOfDate(HomeProductBean homeProductBean) {
        long time = ItemUtils.strToDateLong(homeProductBean.getQuan_time()).getTime();
        int surplus = homeProductBean.getQuan_surplus();
        if (System.currentTimeMillis() >= time || surplus <= 0)
            return true;
        return false;
    }

    private static boolean isToday(String time) {
        if (format == null) {
            format1 = format;
            format1 = new SimpleDateFormat("yyyy-MM-dd");
        }
        Date date = null;
        try {
            date = format.parse(time);
        } catch (Exception e) {
            date = null;
        }
        boolean isToday = date == null ? false : format.format(date).equals(format.format(new Date()));
        return isToday;
    }

    private static String getFrom(String type, String istaobao) {
        switch (type) {
            case "1":
                return "特卖";
            case "2":
                return "晒单";
            case "3":
                if (istaobao.equals("0"))
                    return "淘宝";
                else if (istaobao.equals("1"))
                    return "天猫";
        }

        return "";
    }

    private static String getgoods_type(String istaobao) {
        if (istaobao.equals("0") || istaobao.equals("1"))
            return "3";
        return "1";
    }
    /**
     * TODO  价格显示
     *
     * @param tv
     * @param money
     */
    public static void showPrice(TextView tv, String money) {
        tv.setText("¥" + money.replace(",", ""));
    }
}
