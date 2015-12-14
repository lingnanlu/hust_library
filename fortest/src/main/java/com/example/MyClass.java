package com.example;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MyClass {

   public static void main(String[] args) {

       String prefix="http://ftp.lib.hust.edu.cn/search*chx?/X";
       String keyWord="叔本华";
       int begin = 1;
       int totalCount = 87;
       StringBuilder sb = new StringBuilder();

       sb.append(prefix);

       StringBuilder codePoints = new StringBuilder();
       int length = keyWord.length();
       for(int i = 0; i < length; i++) {

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
           System.out.println(urlEncoded);
           sb.append(urlEncoded);
           sb.append("B/browse");

           System.out.println(sb.toString());
       } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
       }


   }
}
