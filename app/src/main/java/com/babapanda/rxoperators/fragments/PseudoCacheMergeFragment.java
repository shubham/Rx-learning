package com.babapanda.rxoperators.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.babapanda.rxoperators.BaseFragment;
import com.babapanda.rxoperators.R;
import com.babapanda.rxoperators.databinding.FragmentCacheConcatBinding;
import com.babapanda.rxoperators.utility.network.Contributor;
import com.babapanda.rxoperators.utility.network.GithubApi;
import com.babapanda.rxoperators.utility.network.GithubService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class PseudoCacheMergeFragment extends BaseFragment {

    private ArrayAdapter<String> adapter;
    private HashMap<String, Long> contributionMap = null;
    private final HashMap<Contributor, Long> resultAgeMap = new HashMap<>();
    private FragmentCacheConcatBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCacheConcatBinding.inflate(inflater, container, false);
        initializeCache();
        setUpOnClickListeners();
        return binding.getRoot();
    }

    private void setUpOnClickListeners() {
        binding.btnStartPseudoCache.setOnClickListener(v -> onDemoPseudoCacheClicked());
    }

    public void onDemoPseudoCacheClicked() {
        adapter = new ArrayAdapter<>(getActivity(), R.layout.item_log, R.id.item_log, new ArrayList<>());

        binding.logList.setAdapter(adapter);
        initializeCache();

        Observable.merge(getCachedData(), getFreshData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new DisposableObserver<Pair<Contributor, Long>>() {
                            @Override
                            public void onComplete() {
                                Timber.d("done loading all data");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "arr something went wrong");
                            }

                            @Override
                            public void onNext(Pair<Contributor, Long> contributorAgePair) {
                                Contributor contributor = contributorAgePair.first;

                                if (resultAgeMap.containsKey(contributor)
                                        && resultAgeMap.get(contributor) > contributorAgePair.second) {
                                    return;
                                }

                                contributionMap.put(contributor.login, contributor.contributions);
                                resultAgeMap.put(contributor, contributorAgePair.second);

                                adapter.clear();
                                adapter.addAll(getListStringFromMap());
                            }
                        });
    }

    private List<String> getListStringFromMap() {
        List<String> list = new ArrayList<>();

        for (String username : contributionMap.keySet()) {
            String rowLog = String.format("%s [%d]", username, contributionMap.get(username));
            list.add(rowLog);
        }

        return list;
    }

    private Observable<Pair<Contributor, Long>> getCachedData() {

        List<Pair<Contributor, Long>> list = new ArrayList<>();

        Pair<Contributor, Long> dataWithAgePair;

        for (String username : contributionMap.keySet()) {
            Contributor c = new Contributor();
            c.login = username;
            c.contributions = contributionMap.get(username);

            dataWithAgePair = new Pair<>(c, System.currentTimeMillis());
            list.add(dataWithAgePair);
        }

        return Observable.fromIterable(list);
    }

    private Observable<Pair<Contributor, Long>> getFreshData() {
        String githubToken = getResources().getString(R.string.github_oauth_token);
        GithubApi githubService = GithubService.createGithubService(githubToken);

        return githubService
                .contributors("square", "retrofit")
                .flatMap(Observable::fromIterable)
                .map(contributor -> new Pair<>(contributor, System.currentTimeMillis()));
    }

    private void initializeCache() {
        contributionMap = new HashMap<>();
        contributionMap.put("JakeWharton", 0l);
        contributionMap.put("pforhan", 0l);
        contributionMap.put("edenman", 0l);
        contributionMap.put("swankjesse", 0l);
        contributionMap.put("bruceLee", 0l);
    }
}

