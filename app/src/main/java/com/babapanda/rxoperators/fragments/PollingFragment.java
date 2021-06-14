package com.babapanda.rxoperators.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.babapanda.rxoperators.BaseFragment;
import com.babapanda.rxoperators.R;
import com.babapanda.rxoperators.databinding.FragmentPollingBinding;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import timber.log.Timber;

public class PollingFragment extends BaseFragment {

    private static final int INITIAL_DELAY = 0;
    private static final int POLLING_INTERVAL = 1000;
    private static final int POLL_COUNT = 8;
    private LogAdapter _adapter;
    private int _counter = 0;
    private CompositeDisposable _disposables;
    private List<String> _logs;
    private FragmentPollingBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _disposables = new CompositeDisposable();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPollingBinding.inflate(inflater, container, false);
        setupLogger();
        setUpOnClickListeners();
        return binding.getRoot();
    }

    private void setUpOnClickListeners() {

        binding.btnStartIncreasinglyDelayedPolling.setOnClickListener(v -> onStartIncreasinglyDelayedPolling());

        binding.btnStartSimplePolling.setOnClickListener(v -> onStartSimplePollingClicked());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _disposables.clear();
    }

    public void onStartSimplePollingClicked() {

        final int pollCount = POLL_COUNT;

        Disposable d =
                Flowable.interval(INITIAL_DELAY, POLLING_INTERVAL, TimeUnit.MILLISECONDS)
                        .map(this::_doNetworkCallAndGetStringResult)
                        .take(pollCount)
                        .doOnSubscribe(
                                subscription -> {
                                    _log(String.format("Start simple polling - %s", _counter));
                                })
                        .subscribe(
                                taskName -> {
                                    _log(
                                            String.format(
                                                    Locale.US,
                                                    "Executing polled task [%s] now time : [xx:%02d]",
                                                    taskName,
                                                    _getSecondHand()));
                                });

        _disposables.add(d);
    }

    public void onStartIncreasinglyDelayedPolling() {
        setupLogger();

        final int pollingInterval = POLLING_INTERVAL;
        final int pollCount = POLL_COUNT;

        _log(
                String.format(
                        Locale.US, "Start increasingly delayed polling now time: [xx:%02d]", _getSecondHand()));

        _disposables.add(
                Flowable.just(1L)
                        .repeatWhen(new RepeatWithDelay(pollCount, pollingInterval))
                        .subscribe(
                                o ->
                                        _log(
                                                String.format(
                                                        Locale.US,
                                                        "Executing polled task now time : [xx:%02d]",
                                                        _getSecondHand())),
                                e -> Timber.d(e, "arrrr. Error")));
    }

    private String _doNetworkCallAndGetStringResult(long attempt) {
        try {
            if (attempt == 4) {
                Thread.sleep(9000);
            } else {
                Thread.sleep(3000);
            }

        } catch (InterruptedException e) {
            Timber.d("Operation was interrupted");
        }
        _counter++;

        return String.valueOf(_counter);
    }

    private int _getSecondHand() {
        long millis = System.currentTimeMillis();
        return (int)
                (TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private void _log(String logMsg) {
        if (_isCurrentlyOnMainThread()) {
            _logs.add(0, logMsg + " (main thread) ");
            _adapter.clear();
            _adapter.addAll(_logs);
        } else {
            _logs.add(0, logMsg + " (NOT main thread) ");

            // You can only do below stuff on main thread.
            new Handler(Looper.getMainLooper())
                    .post(
                            () -> {
                                _adapter.clear();
                                _adapter.addAll(_logs);
                            });
        }
    }

    private void setupLogger() {
        _logs = new ArrayList<>();
        _adapter = new LogAdapter(getActivity(), new ArrayList<>());
        binding.listThreadingLog.setAdapter(_adapter);
        _counter = 0;
    }

    private boolean _isCurrentlyOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    //public static class RepeatWithDelay
    public class RepeatWithDelay implements Function<Flowable<Object>, Publisher<Long>> {

        private final int _repeatLimit;
        private final int _pollingInterval;
        private int _repeatCount = 1;

        RepeatWithDelay(int repeatLimit, int pollingInterval) {
            _pollingInterval = pollingInterval;
            _repeatLimit = repeatLimit;
        }

        @Override
        public Publisher<Long> apply(Flowable<Object> inputFlowable) throws Exception {
            // it is critical to use inputObservable in the chain for the result
            // ignoring it and doing your own thing will break the sequence

            return inputFlowable.flatMap(
                    new Function<Object, Publisher<Long>>() {
                        @Override
                        public Publisher<Long> apply(Object o) throws Exception {
                            if (_repeatCount >= _repeatLimit) {
                                // terminate the sequence cause we reached the limit
                                _log("Completing sequence");
                                return Flowable.empty();
                            }

                            // since we don't get an input
                            // we store state in this handler to tell us the point of time we're firing
                            _repeatCount++;

                            return Flowable.timer(_repeatCount * _pollingInterval, TimeUnit.MILLISECONDS);
                        }
                    });
        }
    }

    private class LogAdapter extends ArrayAdapter<String> {

        public LogAdapter(Context context, List<String> logs) {
            super(context, R.layout.item_log, R.id.item_log, logs);
        }
    }
}
