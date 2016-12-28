package com.zhangruofan.requery.util;

import android.util.Log;

import rx.Subscriber;

/**
 * Created by zhangruofan on 16-12-26.
 */

public class CustomizeSubscriber<T> extends Subscriber<T> {

    private static final String TAG = CustomizeSubscriber.class.getSimpleName();

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "onError:", e);
    }

    @Override
    public void onNext(T t) {

    }
}
