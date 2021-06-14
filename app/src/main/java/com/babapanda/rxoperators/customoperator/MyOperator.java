package com.babapanda.rxoperators.customoperator;

import org.jetbrains.annotations.NotNull;

import io.reactivex.SingleObserver;
import io.reactivex.SingleOperator;
import io.reactivex.disposables.Disposable;

public class MyOperator<T> implements SingleOperator<T,T> {


    @NotNull
    @Override
    public SingleObserver<? super T> apply(@NotNull SingleObserver<? super T> singleObserver) throws Exception {
        return new SingleObserver<T>() {
            @Override
            public void onSubscribe(@NotNull Disposable d) {
                singleObserver.onSubscribe(d);
            }

            @Override
            public void onSuccess(@NotNull T t) {
                singleObserver.onSuccess(t);
            }


            @Override
            public void onError(@NotNull Throwable e) {

            }
        };
    }
}