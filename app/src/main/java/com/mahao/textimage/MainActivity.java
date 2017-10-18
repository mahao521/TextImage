package com.mahao.textimage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mahao.textimage.utils.TextImage;

public class MainActivity extends AppCompatActivity {

    private TextImage mTxtImg;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTxtImg = (TextImage) findViewById(R.id.txt_txtImage);
        mTxtImg.setContent("打赏了21元");
    }

    public void btnClick(View view) {

        switch (view.getId()){

            case R.id.btn_one: //绘制图片--保存
                mTxtImg.setContent("打赏了20元");
                Uri uri = mTxtImg.saveImageFile();
                Toast.makeText(this,"存储到了"+uri.toString(),Toast.LENGTH_SHORT).show();
                checkPermission();
                break;
            case R.id.btn_two: //ImageLoader---加载图片解析
                Intent intent = new Intent(this,ImageLoaderActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_three: //volley
                Intent intent3 = new Intent(this,VolleyActivity.class);
                startActivity(intent3);
                break;
            case R.id.btn_four: //okhttp
                Intent intent4 = new Intent(this,OkHttpActivity.class);
                startActivity(intent4);
                break;
        }
    }

    /**
     *  检查权限
     */
    public void checkPermission() {

        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }else {
            Bitmap bitmap = createDrawable("打赏了110元");
            ImageView imgOne  = (ImageView) findViewById(R.id.img_one);
            imgOne.setImageBitmap(bitmap);
        }
    }

    /**
     *   获取assert 和 raw 文件
     *
     */
    public void getAssertFile(){
/*
        获得取InputStream对象：
        InputStream is = getResources().openRawResource(R.id.beep);
        有时候需要获得Uri对象：
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.beep);
        2.读取assets下的文件资源
        获得取InputStream对象：
        InputStream is = getAssets().open("beep.ogg");
        有时候需要获得Uri对象：
        Uri uri = Uri.parse("file:///android_asset/beep.ogg");*/
    }

    /**
     *
     */
    private Bitmap createDrawable(String text) {

        //Drawable drawable = getResources().getDrawable(R.mipmap.dashang);
        Bitmap resource = BitmapFactory.decodeResource(getResources(), R.mipmap.dashang);
        Bitmap  bitmap = Bitmap.createBitmap(resource.getWidth(), resource.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        Paint paint = new Paint(); // 建立画笔
        paint.setDither(true);
        paint.setFilterBitmap(true);
        Rect src = new Rect(0, 0,resource.getWidth(), resource.getHeight());
        Rect dst = new Rect(0, 0,resource.getWidth(), resource.getHeight());
        canvas.drawBitmap(resource, src, dst, paint);

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.DEV_KERN_TEXT_FLAG);
        textPaint.setTextSize(dip2px(this,8));
        textPaint.setTypeface(Typeface.DEFAULT_BOLD); // 采用默认的宽度
        textPaint.setColor(Color.BLUE);
        float textWidth = textPaint.measureText(text);
        canvas.drawText(text, resource.getWidth()/2 - textWidth/2, resource.getHeight() - dip2px(this,15),
                textPaint);
        canvas.restore();
        return bitmap;
   }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
