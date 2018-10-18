# TextImage
volley + okhttp + imageloader 源码分析；

Volley:
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
 
 
 ImageLoader:
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
 */
 
 
 
 
 
 
 
 
 
