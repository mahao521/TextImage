package com.mahao.textimage.thread;

import android.telecom.Call;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Penghy on 2017/10/11.
 */

/**
 *  Callable接口类似于Runnable，两者都是为那些其实例可能被另一个线程执行的类设计的。
 *  但是 Runnable 不会返回结果，并且无法抛出经过检查的异常而Callable又返回结果，
 *  而且当获取返回结果时可能会抛出异常。Callable中的call()方法类似Runnable的run()方法，
 *  区别同样是有返回值，后者没有。
 */

public class CallableThread {

    public static void main(String[] args){

        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<String>> resultList = new ArrayList<>();

        for(int i = 0; i < 10; i++){
           //  Future<String> future = executorService.submit(new TaskResult(i));
            String result  = new String("我是返回值");
            Future<String> future = executorService.submit(new TaskRunnable(i),result);
            resultList.add(future);
        }

        for(int j = 0; j < resultList.size(); j++){

            while (!resultList.get(j).isDone());
            try {
                System.out.println(resultList.get(j).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }finally {
                executorService.shutdown();
            }
        }
    }

    //必须使用submit调用，而且有返回值
    static class TaskResult implements Callable<String>{

        private int id;
        public TaskResult(int id){
            this.id = id;
        }

        @Override
        public String call() throws Exception {

            System.out.println("call() 方法被自动调用！！！" + Thread.currentThread().getName());
            return "call()方法被自动调用，任务返回的结果是："+id+"  " + Thread.currentThread().getName();
        }
    }

    //一个参数没有返回值，可以使用submit和exector执行   2个参数的有返回值，就是传递的值。
    //executorService.submit(new TaskRunnable(i),result)
    static  class TaskRunnable implements Runnable{

        private int id;
        public TaskRunnable(int id){
            this.id = id;
        }

        @Override
        public void run() {
            System.out.println("call() 方法被自动调用！！！" + Thread.currentThread().getName());
        }
    }
}



















