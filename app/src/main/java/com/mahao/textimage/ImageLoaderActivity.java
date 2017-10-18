package com.mahao.textimage;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
/**
 *  imageloader加载流程：
 *
 *   1 : 配置application 配置是否需要缓存
 *
 *   2 ： loadImage 或者 displayImage调用
 *
 *   3 ：url_size，以此为key请求内存缓存
 *
 *       1) 获取到了缓存   configuration.memoryCache.get(memoryCacheKey);
 *
 *         1  是否shouldPostProcess()是否缓存之前的缓存
 *              是--- 执行缓存---显示
 *              否---直接显示
 *
 *         2  是否需要异步加载
 *              是--- 新开线程；
 *              否---- 将任务加入线程池；
 *
 *       2）未获取到内存缓存
 *
 *          1 : 从文件中获取缓存  File imageFile = configuration.diskCache.get(uri);
 *
 *             1） 获取到，返回文件路径
 *
 *             2） 未获取到，从网络获取 tryCacheImageOnDisk()，并写入文件（依据初始化的options），返回文件路径
 *
 *          2 ： 通过路径生成bitmap，通过options，是否需要存储到内存缓存；
 *               configuration.memoryCache.put(memoryCacheKey, bmp);
 *
 *   4 : 获取图片，返回到DisplayBitmapTask，执行run()方法；执行display
 *
 *         区别： 1） displayImage 调用时候，在display方法中 子类setImageBitmap()设置图片
 *
 *                  loadImage  调用的时候，在display方法中 子类setImageBitmap() 不做任何处理；
 *
 *   5 暴露接口  1 ： BitmapDisplayer接口，可以设置图片显示效果，圆形，圆角等
 *             2 ： BitmapProcessor  接口， 对传入的原始的BItmap做处理，压缩，裁剪等，
 *             3 ：BaseImageDownloader 类 ，实现就可以使用任意请求框架，网络请求。
 *
 *   6 ：  文件缓存路径 ： String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
 *        内存缓存路径 : 一个线程所占用APP内存的大小; 由 dalvik.vm.heapsize 控制；
 *                      过多分配会内存溢出
 *
 *   7 ： 构造一个缓冲功能的线程池，配置corePoolSize=0，
 *        maximumPoolSize=Integer.MAX_VALUE，keepAliveTime=60s,
 *        以及一个无容量的阻塞队列 SynchronousQueue，因此任务提交之后，将会创建新的线程执行；
 *        线程空闲超过60s将会销毁
 *        ImageLoader内置一个缓冲线程池，因此可以处理多个图片异步加载。
 *
 *
 *
 */
public class ImageLoaderActivity extends AppCompatActivity {

    private DisplayImageOptions mOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_loader);

        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();

        //加载第一幅图
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true);
               //.displayer(new RoundedVignetteBitmapDisplayer(5,10)) //图片眩晕效果
               //.displayer(new FadeInBitmapDisplayer(300))
               // .displayer(new CircleBitmapDisplayer(Color.RED,2))

        final ImageView img = (ImageView) findViewById(R.id.img_loader_1);
        final  ProgressBar bar = (ProgressBar) findViewById(R.id.bar_progress);
        String path = "https://gss2.bdstatic.com/9fo3dSag_xI4khGkpoWK1HF6hhy/baike/crop%3D15%2C0%2C552%2C364%3Bc0%3Dbaike80%2C5%2C5%2C80%2C26/sign=ed4f4900753e6709aa4f1fbf06f6aa11/a5c27d1ed21b0ef4511c2d79d7c451da81cb3e24.jpg";
        WindowManager win  = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display defaultDisplay = win.getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        ImageSize size = new ImageSize(point.x,200);
        builder.showImageOnLoading(R.mipmap.ic_launcher);
        mOptions = builder.displayer(new FadeInBitmapDisplayer(300)).build();
        ImageLoader.getInstance().loadImage(path,size, mOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                bar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                bar.setVisibility(View.VISIBLE);
                Toast.makeText(ImageLoaderActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                img.setImageBitmap(loadedImage);
                bar.setVisibility(View.GONE);
            }
        });
        //加载第二图
        String path2 = "https://gss1.bdstatic.com/9vo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike92%2C5%2C5%2C92%2C30/sign=fb7717c44dfbfbedc8543e2d19999c53/c8ea15ce36d3d53917d889383c87e950342ab060.jpg";
        ImageView imgLoder2 = (ImageView) findViewById(R.id.img_loader_2);
        mOptions = builder.displayer(new CircleBitmapDisplayer(Color.RED,2)).build();
        ImageLoader.getInstance().displayImage(path2,imgLoder2,mOptions,new SimpleImageLoadingListener(){

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
                bar.setProgress(0);
                bar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                bar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                bar.setVisibility(View.GONE);
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {

                Log.i("mahao",100.0f*current / total + "..." + current +" toatal--" + total);
                bar.setProgress(Math.round(100.0f * current / total));
            }
        });
    }
}
