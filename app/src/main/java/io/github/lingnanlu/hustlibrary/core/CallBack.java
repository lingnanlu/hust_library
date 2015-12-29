package io.github.lingnanlu.hustlibrary.core;

/**
 * Created by Administrator on 2015/12/28.
 */
public interface CallBack<T> {

    void onSuccess(T data);

    void onError();

}
