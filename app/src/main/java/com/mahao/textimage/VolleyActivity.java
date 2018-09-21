package com.mahao.textimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *    1 ： 创建请求 ，vollery默认有StringRequest, JsonArrayRequest,
 *            JsonOBjectRequest,ImageRequest请求；
 *
 *    2 :  设置是否缓存等配置，默认缓存。 缓存大小5m；是内存缓存，/data/data/包名/cache目录下；
 *
 *    3 :  创建请求队列。   Volley.newRequestQueue(this);
 *
 *         -)默认创建的是HttpClient的请求方式。
 *         二)如果要使用httpurlConnetion，需要 newRequestQueue(Context context, HttpStack stack)
 *             第二个参数需要传递HurlStack对象。
 *          1 ： CacheDispatcher 使用阻塞队列，死循环，不断从 mCacheQueue.take()取出数据。
 *               使用mCacheQueue和netWorkQueue; netWork唯一，缓存中的request也需要请求。
 *               初始化缓存，取出缓存中的数据，
 *                       ---是否有数据，是否过期，如果过期就加入netwrok队列。
 *                                    没有过期就返回response给dilver;
 *                       ---没有数据，加入到network队列中.
 *
 *          2 ：NetworkDispatcher 默认创建4个网络请求线程，
 *                   -----请求网络mNetwork.performRequest(request);
 *                   ---- 将返回数据封装成String,JsonObject,JsonArray 使用request.parseNetworkResponse(networkResponse);
 *                   ----- 是否需要设置内存缓存，----设置缓存。
 *                             mCache.put(request.getCacheKey(), response.cacheEntry);
 *          3 ： 传递返回数据给用户
 *                   --------mDelivery.postResponse(request, response);
 *
 *    4 ： 将请求加入请求队列。
 *    请求方式 ： BaseHttpStack 网络公共抽象类。 吊起网络请求= mBaseHttpStack.executeRequest(request, additionalRequestHeaders);
 *
 *              ----具体实现类1 AdaptedHttpStack； 使用httpclient；
 *              ----具体实现类2 HurlStack；使用的是HttpurlConnection；
 *
 *    5 ： 数据缓存。（内存缓存）
 *         默认开启数据缓存。jsObjRequest.setShouldCache(true);
 *         是否真正缓存---依据 ： 1 ： response返回的header中Cache-Control判断。服务器设置。
 *         也就是通常volley是没有文件缓存的。
 *
 *    6 : 获取图片的两种方式
 *         1 ： 常用的请求方式ImageRequest(); 重写parseNetworkResponse返回想要的数据类型；
 *         2 :  Volley里面的 ImageLoader，暴露了图片缓存和获取接口。
 *
 *    7 ： StringRequst,JsonObjectRequest,JsonArray 三个post调用注意：
 *         1 ：josnObject和JsonArray是Request的间接子类，复写了getBody()方法，
 *             但是getBody没有调用getParams方法，因此复写传递post请求时候，复写getParams方法没用
 *         2 ： StringRequest是直接子类，可以复写getParams方法；
 *
 *     8 :vollery弊端 ： 没有使用线程池，默认开启4个线程去请求网络，使用了阻塞队列，当有大量任务时候，阻塞队列过长会导致oom或者over stack flow；
 */
public class VolleyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vollery);

        Log.i("mahao", Environment.getExternalStorageDirectory().getAbsolutePath());
        final TextView txtVolley = (TextView) findViewById(R.id.txt_volley);
        ImageView iVVolley = (ImageView) findViewById(R.id.img_volley);
        final String url = "https://suggest.taobao.com/sug?code=utf-8&q=phone";
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        txtVolley.setText("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        //是否设置内存缓存  最大的缓存5m;
         jsObjRequest.setShouldCache(false);
        RequestQueue mQueue = Volley.newRequestQueue(this);
        mQueue.add(jsObjRequest);

        //默认执行过程：
        jsObjRequest.setShouldCache(true);
        final BasicNetwork network = new BasicNetwork(new HurlStack());
            new Thread(new Runnable() {
                @Override
                public void run() {

                    NetworkResponse networkResponse = null;
                    try {
                        networkResponse = network.performRequest(jsObjRequest);
                        if(networkResponse.data != null){
                            String str = new String(networkResponse.data);
                            Log.i("mahao",str);
                        }
                    } catch (VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                    Map<String, String> headers = networkResponse.headers;
                    Set<String> set  = headers.keySet();
                    Iterator<String> iterator = set.iterator();
                    while (iterator.hasNext()){
                        String key = iterator.next();
                        String value = headers.get(key);
                        Log.i("mahao",key + "...:"+value);
                    }
                }
            }).start();

        //设置图片
        ImageLoader imageLoader =
                new ImageLoader(mQueue, new ImageLoader.ImageCache() {

                    //暴露图片缓存---获取的方法
                    @Override
                    public Bitmap getBitmap(String url) {

                        Random random = new Random();
                        int i = random.nextInt(100);
                        if(i %2 ==  0){
                            return BitmapFactory.decodeResource(getResources(),R.mipmap.rion);
                        }else{
                            return null;
                        }
                    }
                    //暴露图片缓存---保存的方法
                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {

                        Toast.makeText(VolleyActivity.this,"网络获取了，现在图片保存吧",Toast.LENGTH_SHORT).show();
                    }
                });
        //默认图---错误图
        ImageLoader.ImageListener imageListener = ImageLoader.getImageListener(iVVolley,R.mipmap.rion,R.mipmap.rion);
        ImageLoader.ImageContainer container =
                imageLoader.get("https://gss1.bdstatic.com/9vo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike92%2C5%2C5%2C92%2C30/sign=fb7717c44dfbfbedc8543e2d19999c53/c8ea15ce36d3d53917d889383c87e950342ab060.jpg",imageListener);
        Bitmap bitmap = container.getBitmap();
        iVVolley.setImageBitmap(bitmap);
    }
}
