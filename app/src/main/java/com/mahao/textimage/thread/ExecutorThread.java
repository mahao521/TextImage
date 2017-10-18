package com.mahao.textimage.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Penghy on 2017/10/11.
 */
public class ExecutorThread {

   public static void main(String[] args){

      // ExecutorService executorService = Executors.newCachedThreadPool();
       //ExecutorService executorService = Executors.newFixedThreadPool(3);
       ExecutorService executorService = Executors.newSingleThreadExecutor();

       for(int i = 0; i < 5; i++){

           executorService.execute(new TaskRunnable());
           System.out.print(".........." + i+"...........\n");
       }
       executorService.shutdown();
   }

   static class TaskRunnable implements  Runnable{

       @Override
       public void run() {

           System.out.print(Thread.currentThread().getName()+"线程被调用了\n");
       }
   }

}














