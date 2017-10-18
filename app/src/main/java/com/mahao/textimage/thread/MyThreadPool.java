package com.mahao.textimage.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Penghy on 2017/10/11.
 */
public class MyThreadPool {

    public static void main(String[] args){

        //创建等待队列
        ArrayBlockingQueue<Runnable> bqueue =  new ArrayBlockingQueue<Runnable>(20);
        //创建线程池，翅中保存的线程数为3，允许最大线程数为5
        ThreadPoolExecutor pool = new ThreadPoolExecutor(3,5,50, TimeUnit.MINUTES,bqueue);
        //创建任务
        Runnable t1 = new MyThead();
        Runnable t2 = new MyThead();
        Runnable t3 = new MyThead();
        Runnable t4 = new MyThead();
        Runnable t5 = new MyThead();
        Runnable t6 = new MyThead();

        //线程池执行线程是随机的 。。。。。。。。异步的。
        String str1 = new String("111");
        Future<String> submit1 = pool.submit(t1, str1);

        String str2 = new String();
        Future<String> submit2 = pool.submit(t2, str2);

        String str3 = new String();
        Future<String> submit3 = pool.submit(t3, str3);

        String str4 = new String("999");
        Future<String> submit4 = pool.submit(t4, str4);

        String str5 = new String();
        Future<String> submit5 = pool.submit(t5, str5);

        String str6 = new String("8888");
        Future<String> submit6 = pool.submit(t6, str6);

        try {
            //主线程sleep20秒之后，取消任务6,就会返回false;
            Thread.sleep(20000);
            boolean cancel = submit6.cancel(true);
            System.out.println(cancel+".........................");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Future<String>> list = new ArrayList<>();
        list.add(submit1);
        list.add(submit2);
        list.add(submit3);
        list.add(submit4);
        list.add(submit5);
        list.add(submit6);

        for(int i = 0; i < list.size(); i++){

            //if this task completed
            while (!list.get(i).isDone());
            try {
                //the computed result     ...如果该任务已经取消，则抛出异常
                System.out.println(list.get(i).get()+"...............");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }finally {
                pool.shutdown();
            }
        }
    }

   static  class MyThead implements  Runnable{

        @Override
        public void run() {

            System.out.println(Thread.currentThread().getName() + "正在执行....");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}














