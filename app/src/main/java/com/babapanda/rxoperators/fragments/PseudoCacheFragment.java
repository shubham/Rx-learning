package com.babapanda.rxoperators.fragments;

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
import com.babapanda.rxoperators.databinding.FragmentPseudoCacheBinding;
import com.babapanda.rxoperators.utility.network.Contributor;
import com.babapanda.rxoperators.utility.network.GithubApi;
import com.babapanda.rxoperators.utility.network.GithubService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class PseudoCacheFragment extends BaseFragment {

    private ArrayAdapter<String> adapterDetail, adapterSubscriptionInfo;
    private HashMap<String, Long> contributionMap = null;
    private FragmentPseudoCacheBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPseudoCacheBinding.inflate(inflater, container, false);
        setUpOnClickListeners();
        return binding.getRoot();
    }

    private void setUpOnClickListeners() {
        binding.btnPseudoCacheConcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConcatBtnClicked();
            }
        });

        binding.btnPseudoCacheConcatEager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConcatEagerBtnClicked();
            }
        });

        binding.btnPseudoCacheMerge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMergeBtnClicked();
            }
        });

        binding.btnPseudoCacheMergeSlowDisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMergeSlowBtnClicked();
            }
        });

        binding.btnPseudoCacheMergeOptimized.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMergeOptimizedBtnClicked();
            }
        });

        binding.btnPseudoCacheMergeOptimizedSlowDisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMergeOptimizedWithSlowDiskBtnClicked();
            }
        });
    }

    public void onConcatBtnClicked() {
        binding.infoPseudoCacheDemo.setText(R.string.msg_pseudoCache_demoInfo_concat);
        wireupDemo();

        Observable.concat(getSlowCachedDiskData(), getFreshNetworkData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new DisposableObserver<Contributor>() {
                            @Override
                            public void onComplete() {
                                Timber.d("done loading all data");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "arr something went wrong");
                            }

                            @Override
                            public void onNext(Contributor contributor) {
                                contributionMap.put(contributor.login, contributor.contributions);
                                adapterDetail.clear();
                                adapterDetail.addAll(mapAsList(contributionMap));
                            }
                        });
    }

    public void onConcatEagerBtnClicked() {
        binding.infoPseudoCacheDemo.setText(R.string.msg_pseudoCache_demoInfo_concatEager);
        wireupDemo();

        List<Observable<Contributor>> observables = new ArrayList<>(2);
        observables.add(getSlowCachedDiskData());
        observables.add(getFreshNetworkData());

        Observable.concatEager(observables)
                .subscribeOn(Schedulers.io()) // we want to add a list item at time of subscription
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new DisposableObserver<Contributor>() {
                            @Override
                            public void onComplete() {
                                Timber.d("done loading all data");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "arr something went wrong");
                            }

                            @Override
                            public void onNext(Contributor contributor) {
                                contributionMap.put(contributor.login, contributor.contributions);
                                adapterDetail.clear();
                                adapterDetail.addAll(mapAsList(contributionMap));
                            }
                        });
    }

    public void onMergeBtnClicked() {
        binding.infoPseudoCacheDemo.setText(R.string.msg_pseudoCache_demoInfo_merge);
        wireupDemo();

        Observable.merge(getCachedDiskData(), getFreshNetworkData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new DisposableObserver<Contributor>() {
                            @Override
                            public void onComplete() {
                                Timber.d("done loading all data");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "arr something went wrong");
                            }

                            @Override
                            public void onNext(Contributor contributor) {
                                contributionMap.put(contributor.login, contributor.contributions);
                                adapterDetail.clear();
                                adapterDetail.addAll(mapAsList(contributionMap));
                            }
                        });
    }

    public void onMergeSlowBtnClicked() {
        binding.infoPseudoCacheDemo.setText(R.string.msg_pseudoCache_demoInfo_mergeSlowDisk);
        wireupDemo();

        Observable.merge(getSlowCachedDiskData(), getFreshNetworkData())
                .subscribeOn(Schedulers.io()) // we want to add a list item at time of subscription
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new DisposableObserver<Contributor>() {
                            @Override
                            public void onComplete() {
                                Timber.d("done loading all data");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "arr something went wrong");
                            }

                            @Override
                            public void onNext(Contributor contributor) {
                                contributionMap.put(contributor.login, contributor.contributions);
                                adapterDetail.clear();
                                adapterDetail.addAll(mapAsList(contributionMap));
                            }
                        });
    }

    public void onMergeOptimizedBtnClicked() {
        binding.infoPseudoCacheDemo.setText(R.string.msg_pseudoCache_demoInfo_mergeOptimized);
        wireupDemo();

        getFreshNetworkData()
                .publish(
                        network ->
                                Observable.merge(
                                        network,
                                        getCachedDiskData().takeUntil(network)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new DisposableObserver<Contributor>() {
                            @Override
                            public void onComplete() {
                                Timber.d("done loading all data");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "arr something went wrong");
                            }

                            @Override
                            public void onNext(Contributor contributor) {
                                contributionMap.put(contributor.login, contributor.contributions);
                                adapterDetail.clear();
                                adapterDetail.addAll(mapAsList(contributionMap));
                            }
                        });
    }

    public void onMergeOptimizedWithSlowDiskBtnClicked() {
        binding.infoPseudoCacheDemo.setText(R.string.msg_pseudoCache_demoInfo_mergeOptimizedSlowDisk);
        wireupDemo();

        getFreshNetworkData() //
                .publish(
                        network -> //
                                Observable.merge(
                                        network, //
                                        getSlowCachedDiskData().takeUntil(network)))
                .subscribeOn(Schedulers.io()) // we want to add a list item at time of subscription
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new DisposableObserver<Contributor>() {
                            @Override
                            public void onComplete() {
                                Timber.d("done loading all data");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "arr something went wrong");
                            }

                            @Override
                            public void onNext(Contributor contributor) {
                                contributionMap.put(contributor.login, contributor.contributions);
                                adapterDetail.clear();
                                adapterDetail.addAll(mapAsList(contributionMap));
                            }
                        });
    }

    private void wireupDemo() {
        contributionMap = new HashMap<>();

        adapterDetail =
                new ArrayAdapter<>(
                        getActivity(), R.layout.item_log_white, R.id.item_log, new ArrayList<>());
        binding.infoPseudoCacheListDtl.setAdapter(adapterDetail);

        adapterSubscriptionInfo =
                new ArrayAdapter<>(
                        getActivity(), R.layout.item_log_white, R.id.item_log, new ArrayList<>());
        binding.infoPseudoCacheListSubscription.setAdapter(adapterSubscriptionInfo);
    }

    private Observable<Contributor> getSlowCachedDiskData() {
        return Observable.timer(1, TimeUnit.SECONDS).flatMap(dummy -> getCachedDiskData());
    }

    private Observable<Contributor> getCachedDiskData() {
        List<Contributor> list = new ArrayList<>();
        Map<String, Long> map = dummyDiskData();

        for (String username : map.keySet()) {
            Contributor c = new Contributor();
            c.login = username;
            c.contributions = map.get(username);
            list.add(c);
        }

        return Observable.fromIterable(list) //
                .doOnSubscribe(
                        (data) ->
                                new Handler(Looper.getMainLooper()) //
                                        .post(() -> adapterSubscriptionInfo.add("(disk) cache subscribed"))) //
                .doOnComplete(
                        () ->
                                new Handler(Looper.getMainLooper()) //
                                        .post(() -> adapterSubscriptionInfo.add("(disk) cache completed")));
    }

    private Observable<Contributor> getFreshNetworkData() {
        String githubToken = getResources().getString(R.string.github_oauth_token);
        GithubApi githubService = GithubService.createGithubService(githubToken);

        return githubService
                .contributors("square", "retrofit")
                .flatMap(Observable::fromIterable)
                .doOnSubscribe(
                        (data) ->
                                new Handler(Looper.getMainLooper()) //
                                        .post(() -> adapterSubscriptionInfo.add("(network) subscribed"))) //
                .doOnComplete(
                        () ->
                                new Handler(Looper.getMainLooper()) //
                                        .post(() -> adapterSubscriptionInfo.add("(network) completed")));
    }

    private List<String> mapAsList(HashMap<String, Long> map) {
        List<String> list = new ArrayList<>();

        for (String username : map.keySet()) {
            String rowLog = String.format( Locale.getDefault(),"%s [%d]", username, contributionMap.get(username));
            list.add(rowLog);
        }
        return list;
    }

    private Map<String, Long> dummyDiskData() {
        Map<String, Long> map = new HashMap<>();
        map.put("JakeWharton", 0L);
        map.put("pforhan", 0L);
        map.put("edenman", 0L);
        map.put("swankjesse", 0L);
        map.put("bruceLee", 0L);
        return map;
    }
}

