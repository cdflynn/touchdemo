package com.github.cdflynn.touch.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.cdflynn.touch.R;

public class TouchSlopFragment extends BaseFragment {

    private static class Views {

        Views(View root) {

        }
    }

    private Views mViews;

    public static TouchSlopFragment newInstance() {
        return new TouchSlopFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_touch_slop, container, false);
        mViews = new Views(root);
        return root;
    }
}
