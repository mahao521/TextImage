package com.mahao.textimage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpActivity extends AppCompatActivity implements View.OnClickListener {

    private  final String okUrl = "https://suggest.taobao.com/sug?code=utf-8&q=phone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http);

        findViewById(R.id.btn_click_ok).setOnClickListener(this);
    }

    /**
     *   OkHttp请求
     * @param netUrl
     * @return
     */
    public String okRun(String netUrl) {

        Request request = new Request.Builder()
                .url(netUrl)
                .build();
        OkHttpClient httpClient = new OkHttpClient();
        try {
            Response response = httpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_click_ok: //请求网络数据

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String responStr = okRun("http://publicobject.com/helloworld.txt");
                        Log.i("mahao",responStr);
                    }
                }).start();
                break;
        }
    }
}
