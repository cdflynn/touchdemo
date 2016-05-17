package com.github.cdflynn.touch.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {

    private boolean mHasOnSaveInstanceStateBeenCalled = false;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mHasOnSaveInstanceStateBeenCalled = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHasOnSaveInstanceStateBeenCalled = false;
    }

    public boolean hasOnSaveInstanceStateBeenCalled() {
        return mHasOnSaveInstanceStateBeenCalled;
    }
}
