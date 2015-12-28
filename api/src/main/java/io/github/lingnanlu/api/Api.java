package io.github.lingnanlu.api;

import java.util.ArrayList;

import io.github.lingnanlu.model.Book;
import io.github.lingnanlu.model.BookAbstract;

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
