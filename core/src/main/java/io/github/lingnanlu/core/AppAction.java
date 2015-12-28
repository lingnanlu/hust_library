package io.github.lingnanlu.core;

import java.util.ArrayList;

import io.github.lingnanlu.model.BookAbstract;

/**
 * Created by Administrator on 2015/12/28.
 */
public interface AppAction {

    void loadBooks(String keyWord, int page, CallBackListener<ArrayList<BookAbstract>> listener);


}
