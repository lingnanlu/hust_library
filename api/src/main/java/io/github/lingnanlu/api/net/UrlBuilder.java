package io.github.lingnanlu.api.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2015/12/28.
 */
public class UrlBuilder {

    public static final String PREFIX = "http://ftp.lib.hust.edu.cn/search*chx?/X";

    private static final String TAG = "UrlBuilder";
    private static final int COUNT_PER_PAGE = 50;

    public static String build(String keyword) {

        StringBuilder sb = new StringBuilder();

        sb.append("http://ftp.lib.hust.edu.cn/search*chx/X");
        sb.append("?SEARCH=");

        try {
            sb.append(URLEncoder.encode(keyword, "UTF-8"));
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;

    }

    //因为url必须有总数的值,所以得有totalCount这个参数
    public static String build(String keyWord, int page, int totalCount) {
        StringBuilder sb = new StringBuilder();

        sb.append(PREFIX);

        StringBuilder codePoints = new StringBuilder();
        int length = keyWord.length();
        for (int i = 0; i < length; i++) {

            char c = keyWord.charAt(i);

            //图书馆的URL编码太恶心了，暂时先这样
            if ( (c >= 'a' && c <= 'z')
                    || (c >= 'A' && c <= 'Z')
                    || (c >= '0' && c <= '9') ) {

                //如果是字母或数字，直接保留
                codePoints.append(c);
            } else if( c == '+') {

                //如果是标点符号，编码为UTF-8
                try {
                    codePoints.append(URLEncoder.encode("" + c, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {

                //其它编码为{u+codepoint}形式
                int codePoint = (int)c;

                codePoints.append("{u");
                codePoints.append(Integer.toHexString(codePoint).toUpperCase());
                codePoints.append("}");

            }

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

        // page = 1 -> begin = 1
        // page = 2 -> begin = 51;
        int begin = 1 + COUNT_PER_PAGE * (page - 1);
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

            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

}
