package com.mahao.textimage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Penghy on 2017/6/20.
 */


public class TextImage extends android.support.v7.widget.AppCompatImageView {

    private Paint mPaint;
    private int mResourceId;
    private String mText;
    private int mWidth;
    private int mHeigth;
    private Bitmap mBitmap;
    private Paint mTextPaint;

    private int ponitBottom;

    public TextImage(Context context) {
        this(context,null);
    }

    public TextImage(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TextImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initData(context,attrs);
    }

    /**
     *   初始化数据
     */
    private void initData(Context context,AttributeSet attributeSet) {

        mPaint = new Paint();
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);

        ponitBottom = dip2px(context,10);

        //获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.textImage);
        mResourceId = typedArray.getResourceId(R.styleable.textImage_txtSrc, R.mipmap.ic_launcher);
        mText = typedArray.getString(R.styleable.textImage_txtContent);

        typedArray.recycle();

        mBitmap = BitmapFactory.decodeResource(context.getResources(), mResourceId);
       // setImageResource(mResourceId);
        mWidth = mBitmap.getWidth();
        mHeigth = mBitmap.getHeight();

        mTextPaint = new Paint();
        mTextPaint.setTextSize(dip2px(context,14));
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.setColor(Color.BLUE);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int modeX = MeasureSpec.getMode(widthMeasureSpec);
        int sizeX = MeasureSpec.getSize(widthMeasureSpec);

        int modeY = MeasureSpec.getMode(heightMeasureSpec);
        int sizeY = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(mWidth,MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mHeigth,MeasureSpec.EXACTLY));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //将图片画在画布上
        canvas.drawBitmap(mBitmap,0,0,mPaint);

        float textWidth = mPaint.measureText(mText);
        //将文字画在画布上
        canvas.drawText(mText,mWidth/2-textWidth/2,mBitmap.getHeight()-ponitBottom,mTextPaint);



        canvas.save();
        canvas.restore();
    }

    /**
     *    获取当前的ImageView
     * @return
     */
    public TextImage getCurrentImage(){

        return this;
    }

    public Uri saveImageFile(){

        TextImage currentImage = getCurrentImage();
        BitmapDrawable drawable = (BitmapDrawable) currentImage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        //创建一个文件，将图片写入文件
  /*     File filePagth = new File(getFilePagth());
        if(!filePagth.exists()){
            filePagth.mkdirs();
        }*/
    //    Log.i("mahao",filePagth+"...." + filePagth.getAbsolutePath());
        File file = new File(getFilePagth(),"reward.png");
        try {
            if(!file.exists()){

                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            //BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file.getAbsolutePath()));
            //bitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream);
            byte[] bytes = Bitmap2Bytes(mBitmap);
            File pngFile = getFileFromBytes(bytes, file);
            return Uri.fromFile(pngFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *  创建一个文件
     */
    public String   getFilePagth(){

         String path;

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){

           path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }else {

            path = Environment.getDataDirectory().getAbsolutePath();
        }
        path = path + "/yzhl";
        return path;
    }


    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     *   设置显示文本
     * @param text
     */
    public void setContent(String text){

        this.mText = text;
        invalidate();
    }

    /**
     *    设置显示图片
     * @param resourceId
     */
    public void setImagSrc(int resourceId){

        this.mResourceId = resourceId;
        invalidate();
    }

    /**
     * 把Bitmap转Byte
     */
    public static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    /**
     * 把字节数组保存为一个文件
     */
    public static File getFileFromBytes(byte[] b, File file) {
        BufferedOutputStream stream = null;
        try {
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }


}
