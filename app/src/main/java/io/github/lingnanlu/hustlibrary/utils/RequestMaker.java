package io.github.lingnanlu.hustlibrary.utils;

import com.squareup.okhttp.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2015/12/9.
 */
public class RequestMaker {

    public static final String prefix = "http://ftp.lib.hust.edu.cn/search*chx?/X";
    private static final String TAG = "RequestMaker";

    public static Request make(String path) {

        Request request = new Request.Builder().url(prefix + path).build();

        //Log.d(TAG, request.urlString());

        return request;
    }

    public static Request make(String keyWord, int begin, int
            totalCount) {

        StringBuilder sb = new StringBuilder();

        sb.append(prefix);

        StringBuilder codePoints = new StringBuilder();
        int length = keyWord.length();
        for (int i = 0; i < length; i++) {

            codePoints.append("{u");
            String codepoint = Integer.toHexString(keyWord.codePointAt
                    (i));

            codePoints.append(codepoint);
            codePoints.append("}");
        }

        sb.append(codePoints.toString());

        sb.append("&SORT=D/X");
        sb.append(codePoints.toString());

        sb.append("&SORT=D&SUBKEY=");


        try {
            sb.append(URLEncoder.encode(keyWord, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sb.append("/");

        StringBuilder left = new StringBuilder();
        left.append(begin);
        left.append(",");
        left.append(totalCount);
        left.append(",");
        left.append(totalCount);
        left.append(",");

        try {

            String urlEncoded = URLEncoder.encode(left.toString(),
                    "UTF-8");
            //System.out.println(urlEncoded);
            sb.append(urlEncoded);
            sb.append("B/browse");

            //System.out.println(sb.toString());

            Request request = new Request.Builder().url(sb.toString()).build();
            return request;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }


}
