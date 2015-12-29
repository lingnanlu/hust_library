package io.github.lingnanlu.hustlibrary.api;


import java.util.ArrayList;

import io.github.lingnanlu.hustlibrary.bean.Book;
import io.github.lingnanlu.hustlibrary.bean.BookAbstract;

/**
 * Created by Administrator on 2015/12/28.
 *
 * Api接口,将RESTFUL接口转换成Java接口,并不负责通信
 *
 */
public interface Api {

    public ArrayList<BookAbstract> bookAbstractList(String keyWord, int page);

    public Book bookDetail(String bookTitle);

}
