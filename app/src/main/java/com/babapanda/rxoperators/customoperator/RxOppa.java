package com.babapanda.rxoperators.customoperator;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class RxOppa {
    public static void main(String[] args) {
     Observable<Long> observableWeather = Observable.interval(150, TimeUnit.MILLISECONDS);
         Observable<Long>  observableTime= Observable.interval(100, TimeUnit.MILLISECONDS);
//        Observable.combineLatest(observableWeather,observableTime,
//                (time, ObservableWeather) ->
//                        "Refreshed news " + time + " times and weather " + ObservableWeather)
//                .subscribe( item -> System.out.println(item));

        observableTime.zipWith(observableWeather,(first,last) -> "Yeello "+first + " "+ last)
                .subscribe(item -> System.out.println(item));
    }

}
