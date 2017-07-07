package com.example.myapplication.Sample2;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.R;

/**
 * Created by 旅行家的梦想 on 2017/7/7.
 */

public class loginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void login(View v){
       setResult(RESULT_OK);
        finish();
    }
}
