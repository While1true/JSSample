package com.example.myapplication.Sample3.Adapter;

import android.text.TextUtils;


import java.io.Serializable;

/**
 * 首页商品 ---展示
 * Created by zqzx on 2015/12/2.
 */
public class HomeProductBean implements Serializable{

    //    "showid": "61",
//            "id": "36",
//            "oldprice": "80.00",
//            "nowprice": "9.90",
//            "name": "创意卡通大白小夜灯插电带开关床头节能灯",
//            "pic": "\/data\/upfiles\/201512\/2310441092.jpg"


    public boolean isshowdialog() {
        return isshowdialog;
    }

    public void setIsshowdialog(boolean isshowdialog) {
        this.isshowdialog = isshowdialog;
    }

    private boolean isshowdialog=false;

    private int repeat_num = 0;
    public int getRepeat_num() {
        return repeat_num;
    }

    public void setRepeat_num(int repeat_num) {
        this.repeat_num = repeat_num;
    }
    private String showid = "";
    private String oldprice = "30";
    private String nowprice = "60";//原价
    private String isCollected = "";
    private int tb_limit = 0;
    public int getTb_limit() {
        return tb_limit;
    }

    public void setTb_limit(int tb_limit) {
        this.tb_limit = tb_limit;
    }
    private String tb_addtime;

    public String getTb_addtime() {
        return tb_addtime;
    }

    public void setTb_addtime(String tb_addtime) {
        this.tb_addtime = tb_addtime;
    }



    public String getNineflag() {
        return tb_is_nine;
    }

    public void setNineflag(String tb_is_nine) {
        this.tb_is_nine = tb_is_nine;
    }

    // 收藏添加字段
    private String product_id = "";
    private String nickname = "haood dsd";

    //淘宝链接
    private String goods_link = "";


    private String quan_receive = "";


    public String getTb_discount() {
        return tb_discount;
    }

    public void setTb_discount(String tb_discount) {
        this.tb_discount = tb_discount;
    }

    public String getTb_discount_begin() {
        return tb_discount_begin;
    }

    public void setTb_discount_begin(String tb_discount_begin) {
        this.tb_discount_begin = tb_discount_begin;
    }

    public String getTb_discount_end() {
        return tb_discount_end;
    }

    public void setTb_discount_end(String tb_discount_end) {
        this.tb_discount_end = tb_discount_end;
    }

    private String tb_discount;//折扣
    private String tb_discount_begin;//折扣开始时间
    private String tb_discount_end;//折扣结束时间

    private String price = "";
    private String tb_id = "";
    private String tb_name = "dhjfjd";
    private String tb_pic = "";
    private String tb_type = "3";
    private String tb_sale ;
    private String tb_big_price="60";

    public String getTb_big_price() {
        return tb_big_price;
    }

    public void setTb_big_price(String tb_big_price) {
        this.tb_big_price = tb_big_price;
    }

    public String getTb_sale() {
        return tb_sale;
    }

    public void setTb_sale(String tb_sale) {
        this.tb_sale = tb_sale;
    }

    public String getTb_price() {
        return tb_price;
    }

    public void setTb_price(String tb_price) {
        this.tb_price = tb_price;
    }

    private String tb_price = "";

    public String getPrice() {
        if (!tb_price.isEmpty())
            return tb_price;
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    private String tb_quan_price = "2017-07-09 14:18";
    private String tb_goodsID = "";//淘宝商品ID
    private String tb_quan_time = "";
    private int tb_quan_surplus = 0;
    private String tb_is_nine = "";//是否为9.9商品，1为9.9商品
    private String tb_is_tmall = "";//是否为天猫，0淘宝 1天猫 2其他

    public String getTb_type() {
        return tb_type;
    }

    public void setTb_type(String tb_type) {
        this.tb_type = tb_type;
    }

    public String getQuan_time() {
        return tb_quan_time;
    }

    public void setQuan_time(String tb_quan_time) {
        this.tb_quan_time = tb_quan_time;
    }

    public int getQuan_surplus() {
        return tb_quan_surplus;
    }

    public void setQuan_surplus(int tb_quan_surplus) {
        this.tb_quan_surplus = tb_quan_surplus;
    }

    public String getQuan_receive() {
        return quan_receive;
    }

    public void setQuan_receive(String quan_receive) {
        this.quan_receive = quan_receive;
    }

    public String getQuan_price() {
        return tb_quan_price;
    }

    public void setQuan_price(String tb_quan_price) {
        this.tb_quan_price = tb_quan_price;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getShowid() {
        return showid;
    }

    public void setShowid(String showid) {
        this.showid = showid;
    }

    public String getId() {
        return tb_id;
    }

    public void setId(String tb_id) {
        this.tb_id = tb_id;
    }

    public String getName() {
        if (!TextUtils.equals(nickname, "")) {
            tb_name = "来自" + nickname + "收藏";
        }
        return tb_name;
    }

    public void setName(String tb_name) {
        this.tb_name = tb_name;
    }

    public String getPic() {
        return tb_pic;
    }

    public void setPic(String tb_pic) {
        this.tb_pic = tb_pic;
    }

    public String getOldprice() {
        return oldprice;
    }

    public void setOldprice(String oldprice) {
        this.oldprice = oldprice;
    }

    public String getNowprice() {
        return nowprice;
    }

    public void setNowprice(String nowprice) {
        this.nowprice = nowprice;
    }

    public String getIsCollected() {
        return isCollected;
    }

    public void setIsCollected(String isCollected) {
        this.isCollected = isCollected;
    }

    public String getGoods_link() {
        return goods_link;
    }

    public void setGoods_link(String goods_link) {
        this.goods_link = goods_link;
    }

    public String getGoodsID() {
        return tb_goodsID;
    }

    public void setGoodsID(String tb_goodsID) {
        this.tb_goodsID = tb_goodsID;
    }

    public String getTb_is_tmall() {
        return tb_is_tmall;
    }

    public void setTb_is_tmall(String tb_is_tmall) {
        this.tb_is_tmall = tb_is_tmall;
    }


}
