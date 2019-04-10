package com.demotmdb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.demotmdb.Setting.Config;
import com.demotmdb.interfaces.RetrofitInterface;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;


@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    public Activity mContext;
    public RetrofitInterface api;
    public Call<ResponseBody> call;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initAll();
    }

    private void initAll() {
        mContext = BaseActivity.this;
        api = retrofitCall(Config.BaseUrl).create(RetrofitInterface.class);
    }


    public Retrofit retrofitCall(String url) {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(40, TimeUnit.SECONDS)
                .connectTimeout(40, TimeUnit.SECONDS)
                .build();
        return new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .build();
    }


}