package com.mahao.textimage.utils;

/**
 * Created by Penghy on 2017/9/19.
 */


public class VolleyInstance {


    //饿汉式
    private VolleyInstance(){}
   /* private  static final VolleyInstance myVollery = new VolleyInstance();
    public static final VolleyInstance getVolleyInstance(){

        return myVollery;
    }*/

    //懒汉式
    private volatile static VolleyInstance myVollery;
    public static final VolleyInstance getVolleryInstacne(){

        if(myVollery == null){

            synchronized (VolleyInstance.class){

                if(myVollery == null){

                    myVollery = new VolleyInstance();
                }
            }
        }
        return myVollery;
    }
}
