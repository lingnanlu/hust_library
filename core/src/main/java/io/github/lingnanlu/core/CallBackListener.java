package io.github.lingnanlu.core;

/**
 * Created by Administrator on 2015/12/28.
 */
public interface CallBackListener<T> {

    void onSuccess(T data);

    void onError();

}
