package io.github.lingnanlu.hustlibrary.utils;

import android.util.Log;

import com.squareup.okhttp.Request;

/**
 * Created by Administrator on 2015/12/9.
 */
public class RequestMaker {

    public static final String prefix = "http://ftp.lib.hust.edu.cn/";
    private static final String TAG = "RequestMaker";

    public static Request make(String path) {

        Request request = new Request.Builder().url(prefix + path).build();

        Log.d(TAG, request.urlString());

        return request;
    }
}
