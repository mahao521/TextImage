package com.mahao.textimage.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Penghy on 2017/9/27.
 *
 *  ReentrantLock和Synchronize相比
 *  1 .等待可中断：如果获得锁的线程，长时间不释放锁，正在等待的线程可以选择放弃等待，改为处理其他事情。
 *  2 .公平锁：当多个线程等待同一个锁时，必须按照申请锁的时间顺序来一次获得锁，可以通过boolean 类型的构造函数使用公平锁。
 *  3 .绑定多个条件：ReentrantLock对象可以同时绑定多个Condition对象，而synchronized中，锁对象的wait() 和 notify()
 *     或notifyAll() 方法可以实现一个隐含的条件，如果多余一个条件关联的时候，就不得不额外加个锁，而ReentantLock无需这么做，只需要多次new Condition方法即可。
 *
 *  线程池详细讲解： http://825635381.iteye.com/blog/2184680
 *
 *   参数名	作用
 *          corePoolSize	核心线程池大小
 *          maximumPoolSize	最大线程池大小
 *          keepAliveTime	线程池中超过corePoolSize数目的空闲线程最大存活时间；可以allowCoreThreadTimeOut(true)使得核心线程有效时间
 *          TimeUnit	keepAliveTime时间单位
 *          workQueue	阻塞任务队列
 *          threadFactory	新建线程工厂
 *          RejectedExecutionHandler	当提交任务数超过maxmumPoolSize+workQueue之和时，任务会交给RejectedExecutionHandler来处理
 *
 *         1、构造一个固定线程数目的线程池，配置的corePoolSize与maximumPoolSize大小相同，同时使用了一个无界LinkedBlockingQueue存放阻塞任务，因此多余的任务将存在再阻塞队列，不会由RejectedExecutionHandler处理

          public static ExecutorService newFixedThreadPool(int nThreads) {
          return new ThreadPoolExecutor(nThreads, nThreads,
          0L, TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<Runnable>());
         }
         2、构造一个缓冲功能的线程池
         ExecutorService newCachedThreadPool() {
         return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
         60L, TimeUnit.SECONDS,
         new SynchronousQueue<Runnable>());
         }

         3、构造一个只支持一个线程的线程池，配置corePoolSize=maximumPoolSize=1，无界阻塞队列LinkedBlockingQueue；保证任务由一个线程串行执行
         public static ExecutorService newSingleThreadExecutor() {
         return new FinalizableDelegatedExecutorService
         (new ThreadPoolExecutor(1, 1,
         0L, TimeUnit.MILLISECONDS,
         new LinkedBlockingQueue<Runnable>()));
         }

         4、构造有定时功能的线程池，配置corePoolSize，无界延迟阻塞队列DelayedWorkQueue；有意思的是：maximumPoolSize=Integer.MAX_VALUE，由于DelayedWorkQueue是无界队列，所以这个值是没有意义的
         public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
         return new ScheduledThreadPoolExecutor(corePoolSize);
         }

         public static ScheduledExecutorService newScheduledThreadPool(
         int corePoolSize, ThreadFactory threadFactory) {
         return new ScheduledThreadPoolExecutor(corePoolSize, threadFactory);
         }

         public ScheduledThreadPoolExecutor(int corePoolSize,
         ThreadFactory threadFactory) {
         super(corePoolSize, Integer.MAX_VALUE, 0, TimeUnit.NANOSECONDS,
         new DelayedWorkQueue(), threadFactory);
         }

         正如上面所说，这三者均是 Executor 框架中的一部分。
         Java 开发者很有必要学习和理解他们，以便更高效的使用 Java 提供的不同类型的线程池。
         总结一下这三者间的区别，以便大家更好的理解：
         •Executor 和 ExecutorService 这两个接口主要的区别是：ExecutorService 接口继承了 Executor 接口，是 Executor 的子接口
         •Executor 和 ExecutorService 第二个区别是：Executor 接口定义了 execute()方法用来接收一个Runnable接口的对象，而 ExecutorService 接口中的 submit()方法可以接受Runnable和Callable接口的对象。
         •Executor 和 ExecutorService 接口第三个区别是 Executor 中的 execute() 方法不返回任何结果，而 ExecutorService 中的 submit()方法可以通过一个 Future 对象返回运算结果。
         •Executor 和 ExecutorService 接口第四个区别是除了允许客户端提交一个任务，ExecutorService 还提供用来控制线程池的方法。比如：调用 shutDown() 方法终止线程池。可以通过 《Java Concurrency in Practice》 一书了解更多关于关闭线程池和如何处理 pending 的任务的知识。
         •Executors 类提供工厂方法用来创建不同类型的线程池。比如: newSingleThreadExecutor() 创建一个只有一个线程的线程池，newFixedThreadPool(int numOfThreads)来创建固定线程数的线程池，newCachedThreadPool()可以根据需要创建新的线程，但如果已有线程是空闲的会重用已有线程。

         总结
         下表列出了 Executor 和 ExecutorService 的区别：
         Executor
         ExecutorService
         Executor 是 Java 线程池的核心接口，用来并发执行提交的任务 ExecutorService 是 Executor 接口的扩展，提供了异步执行和关闭线程池的方法
         提供execute()方法用来提交任务 提供submit()方法用来提交任务
         execute()方法无返回值 submit()方法返回Future对象，可用来获取任务执行结果
         不能取消任务 可以通过Future.cancel()取消pending中的任务
         没有提供和关闭线程池有关的方法 提供了关闭线程池的方法
         Executor：是Java线程池的超级接口；提供一个execute(Runnable command)方法;我们一般用它的继承接口ExecutorService。
         Executors：是java.util.concurrent包下的一个类，提供了若干个静态方法，用于生成不同类型的线程池。
 * */
public class ExextorUtil extends ThreadPoolExecutor {

    private boolean isPaused;
    private ReentrantLock pauseLock = new ReentrantLock();
    private Condition unpaused = pauseLock.newCondition();

    public ExextorUtil(int corePoolSize,
                       int maximumPoolSize,
                       long keepAliveTime,
                       TimeUnit unit,
                       BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);

        pauseLock.lock();
        try {
            while (isPaused) unpaused.await();
        } catch (InterruptedException ie) {
            t.interrupt();
        } finally {
            pauseLock.unlock();
        }
    }

    public void pause() {
        pauseLock.lock();
        try {
            isPaused = true;
        } finally {
            pauseLock.unlock();
        }
    }

    public void resume() {
        pauseLock.lock();
        try {
            isPaused = false;
            unpaused.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }
}
