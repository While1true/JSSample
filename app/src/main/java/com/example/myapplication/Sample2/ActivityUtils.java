package com.example.myapplication.Sample2;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import static android.app.Activity.RESULT_OK;

/**
 * Created by 旅行家的梦想 on 2017/7/7.
 */

public class ActivityUtils {

    /***
     * 跳转到登录
     * requestCode=1111
     */
    public static void startLogin(FragmentActivity context, ActivityResultListner listener) {
        //先看activity是否添加过该fragment， 添加根据Tag找出 ，没有就添加
        FragmentManager manager = context.getSupportFragmentManager();
        MyFragment myFragment = null;
        Fragment loginf = manager.findFragmentByTag(MyFragment.LOGIN + "");
        if (loginf == null) {
            myFragment = new MyFragment();
            manager.beginTransaction().add(myFragment, MyFragment.LOGIN + "").commit();
            //这句是让commit立即生效，不然运行会报错，fragment还没有被attach
            manager.executePendingTransactions();
        } else {
            myFragment = (MyFragment) loginf;
        }
        //设置监听
        myFragment.setListener(listener);
        Intent intent = new Intent(context, loginActivity.class);
        myFragment.startActivityForResult(intent, MyFragment.LOGIN);
    }


    public abstract static class ActivityResultListner {
        public void loginsuccess() {
        }

        public void logincancel() {
        }
    }


    public static class MyFragment extends Fragment {

        public static final int LOGIN = 123;

        ActivityResultListner listener;

        public void setListener(ActivityResultListner listener) {
            this.listener = listener;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == LOGIN) {
                if (resultCode == RESULT_OK) {
                    if (listener != null)
                        listener.loginsuccess();
                } else {
                    if (listener != null)
                        listener.logincancel();
                }
            }

            if (GlobalDatas.DEBUG)
                Log.i(GlobalDatas.TAG, "onActivityResult: requestCode" + requestCode + "resultCode:" + resultCode);

        }
    }

}
