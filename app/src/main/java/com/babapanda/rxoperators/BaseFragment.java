package com.babapanda.rxoperators;

import androidx.fragment.app.Fragment;

import com.squareup.leakcanary.RefWatcher;

public class BaseFragment extends Fragment {

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher();
        refWatcher.watch(this);
    }
}
