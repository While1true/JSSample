package com.example.myapplication.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.HttpVersion;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.security.KeyStore;

/**
 * 网络请求
 */
public class HttpUtils {
    public static SchemeRegistry getSchemeRegistry() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier());
            SSLSocketFactory sf = new SSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 10000);
            HttpConnectionParams.setSoTimeout(params, 10000);
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            return registry;
        } catch (Exception e) {
            return null;
        }
    }
//    private static AsyncHttpClient client = new AsyncHttpClient(getSchemeRegistry());
    private static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

    static {
        client.setTimeout(10000);
    }

    private HttpUtils() {
        Log.e("-----------","Httputils");
    }

    public static AsyncHttpClient getClient() {
        synchronized (HttpUtils.class) {
            return client;
        }
    }

    public static void get(Context context, String urlString, AsyncHttpResponseHandler res)    //用一个完整url获取一个string对象
    {
        if (isNetworkConnected(context)) {
            client.cancelRequests(context, true);
            client.get(urlString, res);
        } else {
            makeText(context);
        }

    }

    public static void get(Context context, String urlString, RequestParams params, AsyncHttpResponseHandler res)   //url里面带参数
    {
        if (isNetworkConnected(context)) {
            client.cancelRequests(context, true);
            client.get(urlString, params, res);
        } else {
            makeText(context);
        }

    }

    public static void get(Context context, String urlString, JsonHttpResponseHandler res)   //不带参数，获取json对象或者数组
    {
        if (isNetworkConnected(context)) {
            client.cancelRequests(context, true);
            client.get(urlString, res);
        } else {
            makeText(context);
        }

    }
    public static void get(Context context, String urlString, TextHttpResponseHandler res)   //不带参数，获取json对象或者数组
    {
        if (isNetworkConnected(context)) {
            client.cancelRequests(context, true);
            client.get(urlString, res);
        } else {
            makeText(context);
        }

    }
    public static void post(Context context, String urlString, JsonHttpResponseHandler res)   //不带参数，获取json对象或者数组
    {
        if (isNetworkConnected(context)) {
            client.cancelRequests(context, true);
            client.post(urlString, res);
        } else {
            makeText(context);
        }

    }

    public static void get(Context context, String urlString, RequestParams params, JsonHttpResponseHandler res)   //带参数，获取json对象或者数组
    {
        if (isNetworkConnected(context)) {
            client.cancelRequests(context, true);
            client.get(urlString, params, res);
        } else {
            makeText(context);
        }

    }

    public static void post(Context context, String urlString, RequestParams params, JsonHttpResponseHandler res)   //带参数，获取json对象或者数组
    {
        if (isNetworkConnected(context)) {
            client.cancelRequests(context, true);
            client.post(urlString, params, res);
        } else {
            makeText(context);
        }

    }

    public static void get(Context context, String uString, BinaryHttpResponseHandler bHandler)   //下载数据使用，会返回byte数据
    {
        if (isNetworkConnected(context)) {
            client.cancelRequests(context, true);
            client.get(uString, bHandler);
        } else {
            makeText(context);
        }

    }

    public static void get(Context context, String uString, GsonHttpResponseHandler res) {
        if (isNetworkConnected(context)) {
            client.cancelRequests(context, true);
            client.get(uString, res);
        } else {
            makeText(context);
        }

    }

    public static void post(Context context, String uString, RequestParams params, GsonHttpResponseHandler res) {
        if (isNetworkConnected(context)) {
            client.cancelRequests(context, true);
            client.post(uString, params, res);
        } else {
            makeText(context);
        }

    }
    public static void post(Context context, String uString, RequestParams params, TextHttpResponseHandler res) {
        if (isNetworkConnected(context)) {
            client.cancelRequests(context, true);
            client.post(uString, params, res);
        } else {
            makeText(context);
        }

    }

    public static void post(Context context, String uString, BinaryHttpResponseHandler res) {
        if (isNetworkConnected(context)) {
            client.cancelRequests(context, true);
            client.post(uString, res);
        } else {
            makeText(context);
        }

    }

    private static void makeText(Context context) {
        Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
    }


    /**
     * 判断是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (mConnectivityManager == null) {
                return false;
            }
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

}
