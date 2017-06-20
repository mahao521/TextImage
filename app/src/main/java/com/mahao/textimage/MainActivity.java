package com.mahao.textimage;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private TextImage mCurrentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextImage txt = (TextImage) findViewById(R.id.txt_txtImage);
        mCurrentImage = txt.getCurrentImage();
        mCurrentImage.setContent("打赏了21元");

    }

    public void btnClick(View view) {


        switch (view.getId()){

            case R.id.btn_one:

                mCurrentImage.setContent("打赏了20元");
                break;

            case R.id.btn_two:

                checkPermission();
                break;
        }
    }

  /*  // 记录血糖血药--读取数据库---读写权限
    public void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS_STORAGE, PERMISSION_STORAGE);
            }
        } else {
            Uri uri = mCurrentImage.saveImageFile();
            Button btnShow = (Button) findViewById(R.id.btn_two);
            btnShow.setText(uri+"..............");
        }
    }

    private static final int PERMISSION_STORAGE = 700;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
*/


    int REQUEST_EXTERNAL_STORAGE = 1;
    String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void checkPermission() {

        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );

        }else {

/*            File sd = Environment.getExternalStorageDirectory();
            boolean can_write = sd.canWrite();

            Uri uri = mCurrentImage.saveImageFile();
            mCurrentImage.setContent("打赏20元");
            Button btnShow = (Button) findViewById(R.id.btn_two);
            btnShow.setText(uri+".............." + can_write);*/

            Bitmap bitmap = createDrawable("打赏了110元");
            ImageView imgOne  = (ImageView) findViewById(R.id.img_one);
            imgOne.setImageBitmap(bitmap);
            BitmapFactory.de

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
        Paint paint = new Paint(); // 建立画笔
        paint.setDither(true);
        paint.setFilterBitmap(true);
        Rect src = new Rect(0, 0,resource.getWidth(), resource.getHeight());
        Rect dst = new Rect(0, 0,resource.getWidth(), resource.getHeight());
        canvas.drawBitmap(resource, src, dst, paint);

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.DEV_KERN_TEXT_FLAG);
        textPaint.setTextSize(dip2px(this,14));
        textPaint.setTypeface(Typeface.DEFAULT_BOLD); // 采用默认的宽度
        textPaint.setColor(Color.BLUE);
        float textWidth = textPaint.measureText(text);
        canvas.drawText(text, resource.getWidth()/2 - textWidth/2, resource.getHeight() - dip2px(this,15),
                textPaint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
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
