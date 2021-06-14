package com.babapanda.rxoperators.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.babapanda.rxoperators.databinding.FragmentNetworkDetectorBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;

public class NetworkDetectorFragment extends BaseFragment {

    private LogAdapter adapter;
    private BroadcastReceiver broadcastReceiver;
    private List<String> logs;
    private Disposable disposable;
    private PublishProcessor<Boolean> publishProcessor;
    private FragmentNetworkDetectorBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNetworkDetectorBinding.inflate(inflater, container, false);
        setOnClickListeners();
        setupLogger();
        return binding.getRoot();
    }

    private void setOnClickListeners() {

    }

    @Override
    public void onStart() {
        super.onStart();

        publishProcessor = PublishProcessor.create();

        disposable =
                publishProcessor
                        .startWith(getConnectivityStatus(requireActivity()))
                        .distinctUntilChanged()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                online -> {
                                    if (online) {
                                        log("You are online");
                                    } else {
                                        log("You are offline");
                                    }
                                });

        listenToNetworkConnectivity();
    }

    @Override
    public void onStop() {
        super.onStop();
        disposable.dispose();
        requireActivity().unregisterReceiver(broadcastReceiver);
    }

    private void listenToNetworkConnectivity() {

        broadcastReceiver =
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        publishProcessor.onNext(getConnectivityStatus(context));
                    }
                };

        final IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        requireActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    private boolean getConnectivityStatus(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void log(String logMsg) {

        if (isCurrentlyOnMainThread()) {
            logs.add(0, logMsg + " (main thread) ");
            adapter.clear();
            adapter.addAll(logs);
        } else {
            logs.add(0, logMsg + " (NOT main thread) ");
            new Handler(Looper.getMainLooper())
                    .post(
                            () -> {
                                adapter.clear();
                                adapter.addAll(logs);
                            });
        }
    }

    private void setupLogger() {
        logs = new ArrayList<>();
        adapter = new LogAdapter(getActivity(), new ArrayList<>());
        binding.listThreadingLog.setAdapter(adapter);
    }

    private boolean isCurrentlyOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private class LogAdapter extends ArrayAdapter<String> {

        public LogAdapter(Context context, List<String> logs) {
            super(context, R.layout.item_log, R.id.item_log, logs);
        }
    }
}
