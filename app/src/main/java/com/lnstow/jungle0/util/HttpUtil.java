package com.lnstow.jungle0.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lnstow.jungle0.BaseJungle;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {
    //request--->appInterceptor--->okhttpCore--->networkInterceptor--->server
    //response--->networkInterceptor--->okhttpCore--->appInterceptor--->client
    public static OkHttpClient client;
    private static CacheControl cacheControl_home;
    private static CacheControl cacheControl_list;
    private static CacheControl cacheControl_detail;
    private static List<Cookie> cookieList;
    private static File cookieFile;
    private static Gson gson;

    public static void initOkHttpClient(File cacheDir, File cookieDir) {
        if (client != null) return;
        client = new OkHttpClient.Builder()
//                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .cache(new Cache(new File(cacheDir, "http_cache"), 1024 * 1024 * 10))
                .addInterceptor(new RequestInterceptor())
                .addNetworkInterceptor(new ResponseInterceptor())
                .cookieJar(new CustomCookieJar())
                .build();
        cacheControl_list = new CacheControl.Builder().maxAge(1, TimeUnit.DAYS).build();
        cacheControl_detail = new CacheControl.Builder().maxAge(3, TimeUnit.DAYS).build();
        cacheControl_home = new CacheControl.Builder().maxAge(60, TimeUnit.SECONDS).build();
        cookieFile = new File(cookieDir, "cookie");
        gson = new Gson();
        loadCookieFromFile();
    }

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback, byte requestType) {
        Request.Builder builder = new Request.Builder().url(address);
        switch (requestType) {
            case BaseJungle.MOVIE_LIST:
                builder.cacheControl(cacheControl_list);
                break;
            case BaseJungle.MOVIE_DETAIL:
                builder.cacheControl(cacheControl_detail);
                break;
            case BaseJungle.MOVIE_HOME:
                builder.cacheControl(cacheControl_home);
                break;
            default:
                break;
        }
        client.newCall(builder.build()).enqueue(callback);
    }

    static class RequestInterceptor implements Interceptor {

        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.currentTimeMillis();
            Log.d("RequestInterceptor", "request url: " + request.url());
            Log.d("RequestInterceptor", "request connection: " + chain.connection());
            Log.d("RequestInterceptor", "request cacheControl: " + request.cacheControl());
            Log.d("RequestInterceptor", "request headers: " + request.headers());
            Response response = chain.proceed(request);
            long t2 = System.currentTimeMillis();
            Log.d("RequestInterceptor", "response url: " + response.request().url());
            Log.d("RequestInterceptor", "response time: " + (t2 - t1));
            Log.d("RequestInterceptor", "response cacheControl: " + response.cacheControl());
            Log.d("RequestInterceptor", "response headers: " + response.headers());
            return response;
        }
    }

    static class ResponseInterceptor implements Interceptor {

        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.currentTimeMillis();
            Log.d("ResponseInterceptor", "request url: " + request.url());
            Log.d("ResponseInterceptor", "request connection: " + chain.connection());
            Log.d("ResponseInterceptor", "request cacheControl: " + request.cacheControl());
            Log.d("ResponseInterceptor", "request headers: " + request.headers());
            Response response = chain.proceed(request);
            long t2 = System.currentTimeMillis();
            Log.d("ResponseInterceptor", "response url: " + response.request().url());
            Log.d("ResponseInterceptor", "response time: " + (t2 - t1));
            Log.d("ResponseInterceptor", "response cacheControl: " + response.cacheControl());
            Log.d("ResponseInterceptor", "response headers: " + response.headers());
            String value = request.header("Cache-Control");
            if (value == null) return response;
            return response.newBuilder().header("Cache-Control", value).build();
        }
    }

    static class CustomCookieJar implements CookieJar {

        @NotNull
        @Override
        public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
//            Log.d("CookieJar", "loadForRequest: " + httpUrl.query());
            return cookieList;
        }

        @Override
        public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
//            Log.d("CookieJar", "saveFromResponse: " + httpUrl.query());
            int i = 0;
            Cookie newCookie = null;
            for (Cookie cookie : list) {
                if (!cookie.value().equals("deleted")) {
                    if (newCookie != null && cookie.name().equals(newCookie.name())) continue;
                    if (!cookie.name().equals("search_type") || cookie.value().equals("id")) {
                        newCookie = cookie;
                    } else {
                        Cookie.Builder builder = new Cookie.Builder()
                                .name(cookie.name())
                                .value("id")
                                .expiresAt(cookie.expiresAt())
                                .domain(cookie.domain())
                                .path(cookie.path());
                        if (cookie.secure()) builder.secure();
                        if (cookie.httpOnly()) builder.httpOnly();
                        if (cookie.hostOnly()) builder.hostOnlyDomain(cookie.domain());
                        newCookie = builder.build();
                    }
                    if (cookieList.size() > i) cookieList.set(i, newCookie);
                    else cookieList.add(newCookie);
                    i++;
                }
            }
        }
    }

    public static void saveCookieToFile() {
        BufferedWriter bufferedWriter = null;
        try {
            if (!cookieFile.exists())
                cookieFile.createNewFile();
            bufferedWriter = new BufferedWriter(new FileWriter(cookieFile));
            bufferedWriter.write(gson.toJson(cookieList));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadCookieFromFile() {
        BufferedReader bufferedReader = null;
        try {
            if (!cookieFile.exists())
                cookieFile.createNewFile();
            else {
                bufferedReader = new BufferedReader(new FileReader(cookieFile));
                cookieList = gson.fromJson(bufferedReader.readLine(),
                        new TypeToken<ArrayList<Cookie>>() {
                        }.getType());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (cookieList == null) cookieList = new ArrayList<>();
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
