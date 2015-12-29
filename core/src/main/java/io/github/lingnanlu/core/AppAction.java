package io.github.lingnanlu.core;

import java.util.ArrayList;

import io.github.lingnanlu.model.Book;
import io.github.lingnanlu.model.BookAbstract;

/**
 * Created by Administrator on 2015/12/28.
 */
public interface AppAction {

    void loadBookList(String keyword, int page, CallBack<ArrayList<BookAbstract>> listener);

    void loadBook(String bookTitle, CallBack<Book> listener);
}
