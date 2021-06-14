package com.babapanda.rxoperators.utility;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

public class RxBus {
    private final Relay<Object> bus= PublishRelay.create().toSerialized();

    public void send(Object object){
        bus.accept(object);
    }

    public Flowable<Object> asFlowable(){
        return bus.toFlowable(BackpressureStrategy.LATEST);
    }

    public boolean hasObservers(){
        return bus.hasObservers();
    }
}
