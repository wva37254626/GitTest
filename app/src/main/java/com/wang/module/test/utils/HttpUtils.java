package com.wang.module.test.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 封装的HttpURLConnection
 */

public class HttpUtils {

    private static final int TIMEOUT_IN_MILLIONS = 5000;


    public interface CallBack {
        void onRequestComplete(String result);
    }


    /**
     *   异步的Get请求
     *   @param urlStr  发送请求的 URL
     *   @param callBack 接口回调
     */
    public static void doGetAsyn(final String urlStr, final CallBack callBack) {
        new Thread() {
            public void run() {
                try {
                    String result = doGet(urlStr);
                    if (callBack != null) {
                        callBack.onRequestComplete(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }.start();

    }


    /**
     * 异步的Post请求
     *
     * @param urlStr
     *
     * @param params
     *
     * @param callBack
     *
     */
    public static void doPostAsyn(final String urlStr, final String params,final CallBack callBack){
        new Thread() {
            public void run() {
                try {
                    String result = doPost(urlStr, params);
                    if (callBack != null) {
                        callBack.onRequestComplete(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }


    /**
     * Get请求，获得返回数据
     * @param urlStr
     * @return
     */
    public static String doGet(String urlStr) {
        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            url = new URL(urlStr);  //新建一个URL对象
            conn = (HttpURLConnection) url.openConnection(); // 打开一个HttpURLConnection连接
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);     // 设置连接主机超时时间
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);        //设置从主机读取数据超时
            conn.setRequestMethod("GET");                    // 设置请求方式
            conn.setRequestProperty("accept", "*/*");        //Conn设置请求头信息
            conn.setRequestProperty("connection", "Keep-Alive");   //设置客户端与服务连接类型
            conn.connect();  // 开始连接
            if (conn.getResponseCode() == 200) {    // 判断请求是否成功
                is = conn.getInputStream();         // 获取返回的数据
                baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] buf = new byte[1024];
                while ((len = is.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                }
                baos.flush();
                return baos.toString();
            } else {
                throw new RuntimeException(" responseCode is  not 200 ... ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {


            }
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {


            }
            conn.disconnect();   // 关闭连接
        }
        return null;
    }


    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式
     * @return 代表响应结果
     * @throws Exception
     */
    public static String doPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
//        String result="";
        StringBuilder result=new StringBuilder();
        try {
            URL realUrl = new URL(url);  // 新建一个URL对象
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection(); // 打开一个HttpURLConnection连接
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS); // 设置连接超时时间
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);    // 设置从主机读取数据超时
            conn.setRequestProperty("accept", "*/*");  //Conn设置请求头信息
            conn.setRequestProperty("connection", "Keep-Alive");   //Conn设置请求头信息
            conn.setRequestMethod("POST");   // 设置请求方式
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); // 配置请求Content-Type
            conn.setRequestProperty("charset", "utf-8");  //设置编码语言
            conn.setUseCaches(false); // Post请求不能使用缓存,发送POST请求必须设置如下两行
            conn.setDoOutput(true);   //Post请求必须设置允许输出 默认false
            conn.setDoInput(true);    //设置请求允许输入 默认是true
            if (param != null && !param.trim().equals("")) { // 获取URLConnection对象对应的输出流
                out = new PrintWriter(conn.getOutputStream()); // 发送请求参数
                out.print(param);
                // flush输出流的缓冲
                out.flush();
                out.close();
            }
            if (conn.getResponseCode()==200){

            }
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
//                result += line;
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流和输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }
}
