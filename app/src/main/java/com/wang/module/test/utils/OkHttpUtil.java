package com.wang.module.test.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;


/**
 * 封装的okhttp
 */
public class OkHttpUtil {

    private static final String TAG = OkHttpUtil.class.getSimpleName();

    private static volatile OkHttpUtil singleton; //单例引用
    private OkHttpClient mOkHttpClient;          //okHttpClient 实例
    private Handler okHttpHandler;               //全局处理子线程和M主线程通信

    //发送数据的类型MIME类型(常用Json,xml,png,jpg,gif,text,doc,xls,*);
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType XML = MediaType.parse("application/xml; charset=utf-8");
    public static final MediaType PNG = MediaType.parse("image/png; charset=utf-8");
    public static final MediaType JPG = MediaType.parse("image/jpeg; charset=utf-8");
    public static final MediaType GIF = MediaType.parse("image/gif; charset=utf-8");
    public static final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");
    public static final MediaType WORD = MediaType.parse("application/msword; charset=utf-8");
    public static final MediaType EXCEL = MediaType.parse("application/vnd.ms-excel; charset=utf-8");
    //任何类型
    public static final MediaType BASE_TYPE = MediaType.parse("application/octet-stream; charset=utf-8");

    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();

    private OkHttpUtil(Context context) {
        //初始化OkHttpClient
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS) //设置连接超时时间
                .readTimeout(30, TimeUnit.SECONDS)    //设置读取超时时间
                .writeTimeout(30, TimeUnit.SECONDS)   //设置写入超时时间
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> cookies) {
                        cookieStore.put(httpUrl.host(), cookies);
                        for (Cookie ccokie:cookies){
                            Log.d(TAG,"saveFromResponse"+ccokie.toString());
                        }
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                }).build();
        //初始化Handler
        okHttpHandler = new Handler(context.getMainLooper());
    }

    /**
     * 单例
     *
     * @param context
     * @return
     */
    public static OkHttpUtil getInstance(Context context) {
        if (singleton == null) {
            synchronized (OkHttpUtil.class) {
                if (singleton == null) {
                    singleton = new OkHttpUtil(context.getApplicationContext());
                }
            }
        }
        return singleton;
    }


    /**
     * get同步请求服务器(需在子线运行)
     *
     * @param url 服务器地址
     * @return body/null
     */
    public String getSyncFromServer(String url) {
        Request request = new Request.Builder().url(url).get().build();
        Response response = null;
        try {
            response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseUrl = response.body().string();
                Log.d(TAG, "response ----->" + response.body().string());
                return responseUrl;
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
        return null;
    }


    /**
     * get异步请求服务器
     *
     * @param url      请求地址
     * @param callBack 回调接口
     */
    public <T> void getAsynFromServer(String url, final ReqCallBack<T> callBack) {
        Request request = new Request.Builder().url(url).get().build();
        Call call = mOkHttpClient.newCall(request);
        enqueue(call, callBack);
    }


    /**
     * get异步请求服务器(添加请求头参数)
     *
     * @param url        请求地址
     * @param headerList header集合
     * @param callBack   异步回调
     */
    public <T> void getAsynFromServer(String url, HashMap<String, String> headerList, final ReqCallBack<T> callBack) {
        Request.Builder builder = new Request.Builder().get();
        for (String key : headerList.keySet()) {
            builder.addHeader(key, headerList.get(key));
        }
        Request request = builder.url(url).build();
        Call call = mOkHttpClient.newCall(request);
        enqueue(call, callBack);
    }

    /**
     * post异步请求服务器(带String键值对)
     *
     * @param url      请求地址
     * @param maplist  body(键值对集合)
     * @param callBack 异步回调
     */
    public <T> void postAsynFromServer(String url, HashMap<String, String> maplist, ReqCallBack<T> callBack) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : maplist.keySet()) {
            builder.add(key, maplist.get(key));
        }
        FormBody requestBody = builder.build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Call call = mOkHttpClient.newCall(request);
        enqueue(call, callBack);
    }

    /**
     * post异步请求服务器(带json数据)
     *
     * @param url        请求地址
     * @param bodyJson   bodyjson字符串
     * @param callBack   异步回调
     */
    public <T> void postAsynFromServer(String url, String bodyJson, ReqCallBack<T> callBack) {
        RequestBody body = RequestBody.create(JSON, bodyJson);
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = mOkHttpClient.newCall(request);
        enqueue(call, callBack);
    }

    /**
     * post不带参数上传文件
     *
     * @param url            上传地址
     * @param upLoadFilePath 本地上传文件路径(需要带后缀.xxx)
     * @param callBack       异步回调
     */
    public <T> void upLoadFile(String url, String upLoadFilePath, ReqCallBack<T> callBack) {
        File file = new File(upLoadFilePath);
        RequestBody body = RequestBody.create(BASE_TYPE, file);
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = mOkHttpClient.newBuilder()
                .writeTimeout(100, TimeUnit.SECONDS)
                .build().newCall(request);
        enqueue(call, callBack);
    }

    /**
     * post 带参数上传文件
     *
     * @param url       上传地址
     * @param paramsMap 参数<String,object>
     * @param callBack  异步回调
     */
    public <T> void upLoadFile(String url, HashMap<String, Object> paramsMap, ReqCallBack<T> callBack) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (String key : paramsMap.keySet()) {
            Object object = paramsMap.get(key);
            if (!(object instanceof File)) {
                builder.addFormDataPart(key, object.toString());
            } else {
                File file = (File) object;
                builder.addFormDataPart(key, file.getName(), RequestBody.create(BASE_TYPE, file));
            }
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = mOkHttpClient.newBuilder()
                .writeTimeout(100, TimeUnit.SECONDS).build()
                .newCall(request);
        enqueue(call, callBack);
    }

    /**
     * post 带参数带进度上传文件
     *
     * @param url       上传地址
     * @param paramsMap 参数<String,object>
     * @param callBack  进度的条回调
     * @param <T>
     */
    public <T> void upLoadFile(String url, HashMap<String, Object> paramsMap, final ReqProgressCallBack<T> callBack) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (String key : paramsMap.keySet()) {
            Object object = paramsMap.get(key);
            if (!(object instanceof File)) {
                builder.addFormDataPart(key, object.toString());
            } else {
                File file = (File) object;
                builder.addFormDataPart(key, file.getName(), createProgressRequestBody(BASE_TYPE, file, callBack));
            }
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = mOkHttpClient.newBuilder()
                .writeTimeout(100, TimeUnit.SECONDS).build()
                .newCall(request);
        enqueue(call, callBack);
    }


    /**
     * 创建带进度的RequestBody
     *
     * @param contentType MediaType
     * @param file        准备上传的文件
     * @param callBack    回调
     * @param <T>
     * @return
     */
    public <T> RequestBody createProgressRequestBody(final MediaType contentType, final File file, final ReqProgressCallBack<T> callBack) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    Buffer buf = new Buffer();
                    long remaining = contentLength();
                    long current = 0;
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        current += readCount;
                        Log.d(TAG, "current------>" + current);
                        progressCallBack(remaining, current, callBack);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        };
    }

    /**
     * 不带进度文件下载 (url还不好确认是不是加密格式或其他格式)
     *
     * @param url         下载地址
     * @param destFileDir 存放地址
     * @param callBack    回调
     * @param <T>
     */

    public <T> void downLoadFile(String url, String destFileDir, final ReqCallBack<T> callBack) {
        File fileUrl = new File(url);
        String fileName = fileUrl.getName();  //这两部还要看url是什么进行调整;
        final File file = new File(destFileDir, fileName);
        if (file.exists()) {
            successCallBack((T) file, callBack);
            return;
        }
        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newBuilder()
                .readTimeout(100, TimeUnit.SECONDS)
                .build().newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        failedCallBack("访问失败", callBack);
                        Log.e(TAG, e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            InputStream is = null;
                            byte[] buf = new byte[2048];
                            int len = 0;
                            FileOutputStream fos = null;
                            try {
                                long total = response.body().contentLength();  //总大小
                                Log.e(TAG, "total------>" + total);
                                long current = 0;
                                is = response.body().byteStream();     //拿到的输入流
                                fos = new FileOutputStream(file);      //获取一个文件输出流
                                while ((len = is.read(buf)) != -1) {
                                    current += len;
                                    fos.write(buf, 0, len);
                                    Log.d(TAG, "current------>" + current);
                                }
                                fos.flush();
                                successCallBack((T) file, callBack);
                            } catch (IOException e) {
                                Log.e(TAG, e.toString());
                                failedCallBack("下载失败", callBack);
                            } finally {
                                try {
                                    if (is != null) {
                                        is.close();
                                    }
                                    if (fos != null) {
                                        fos.close();
                                    }
                                } catch (IOException e) {
                                    Log.e(TAG, e.toString());
                                }
                            }
                        } else {
                            failedCallBack("服务器错误", callBack);
                        }
                    }
                });


    }

    /**
     * 带进度文件下载
     * @param url          下载地址
     * @param destFileDir  存放地址
     * @param callBack     进度条回调
     * @param <T>
     */
    public <T> void downLoadFile(String url, String destFileDir, final ReqProgressCallBack<T> callBack) {
        File fileUrl = new File(url);
        String fileName = fileUrl.getName();  //这两部还要看url是什么进行调整;
        final File file = new File(destFileDir, fileName);
        if (file.exists()) {
            successCallBack((T) file, callBack);
            return;
        }
        final Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newBuilder()
                .readTimeout(100, TimeUnit.SECONDS)
                .build().newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        failedCallBack("访问失败", callBack);
                        Log.e(TAG, e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            responseStartCallBack(response,callBack);
                            InputStream is = null;
                            byte[] buf = new byte[2048];
                            int len = 0;
                            FileOutputStream fos = null;
                            try {
                                long total = response.body().contentLength();  //总大小
                                Log.d(TAG, "total------>" + total);
                                long current = 0;
                                is = response.body().byteStream();     //拿到的输入流
                                fos = new FileOutputStream(file);      //获取一个文件输出流
                                while ((len = is.read(buf)) != -1) {
                                    current += len;
                                    fos.write(buf, 0, len);
                                    Log.d(TAG, "current------>" + current);
                                    progressCallBack(total, current, callBack);
                                }
                                fos.flush();
                                successCallBack((T) file, callBack);
                            } catch (IOException e) {
                                Log.e(TAG, e.toString());
                                failedCallBack("下载失败", callBack);
                            } finally {
                                try {
                                    if (is != null) {
                                        is.close();
                                    }
                                    if (fos != null) {
                                        fos.close();
                                    }
                                } catch (IOException e) {
                                    Log.e(TAG, e.toString());
                                }
                            }
                        } else {
                            failedCallBack("服务器错误", callBack);
                        }
                    }
                });


    }


    /**
     * 异步请求方法
     *
     * @param call     http请求对象
     * @param callBack 回调
     * @param <T>
     */
    private <T> void enqueue(Call call, final ReqCallBack<T> callBack) {
        call.enqueue(new Callback() {
            //失败
            @Override
            public void onFailure(Call call, IOException e) {
                failedCallBack("访问失败", callBack);
                Log.e(TAG, e.toString());
            }

            //成功
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    Log.d(TAG, "response ----->" + string);
                    successCallBack((T) string, callBack);
                } else {
                    failedCallBack("服务器错误", callBack);
                    Log.e(TAG,response.code()+"");
                }
            }
        });
    }

    /**
     * 统一同意处理成功信息
     *
     * @param result
     * @param callBack
     * @param <T>
     */
    private <T> void successCallBack(final T result, final ReqCallBack<T> callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqSuccess(result);
                }
            }
        });
    }

    /**
     * 统一处理失败信息
     *
     * @param errorMsg
     * @param callBack
     * @param <T>
     */
    private <T> void failedCallBack(final String errorMsg, final ReqCallBack<T> callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqFailed(errorMsg);
                }
            }
        });
    }


    /**
     * 统一处理进度信息
     *
     * @param total    总计大小
     * @param current  当前进度
     * @param callBack
     * @param <T>
     */
    private <T> void progressCallBack(final long total, final long current, final ReqProgressCallBack<T> callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onProgress(total, current);
                }
            }
        });
    }

    /**
     * 相应成功后开始
     * @param response 相应数据
     * @param callBack
     * @param <T>
     */
    private <T> void responseStartCallBack(final Response response, final ReqProgressCallBack<T> callBack){
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onResponseStart(response);
                }
            }
        });
    }

    /**
     * 自定义接口
     *
     * @param <T>
     */
    public interface ReqCallBack<T> {

        void onReqSuccess(T result);//响应成功

        void onReqFailed(String errorMsg);//响应失败
    }

    /**
     * 自定义进度接口
     *
     * @param <T>
     */
    public interface ReqProgressCallBack<T> extends ReqCallBack<T> {

        void onResponseStart(Response response);    //相应开始

        void onProgress(long total, long current);  //响应进度更新
    }


}