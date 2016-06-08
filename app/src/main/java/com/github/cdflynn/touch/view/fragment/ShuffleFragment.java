package com.github.cdflynn.touch.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.cdflynn.touch.R;
import com.github.cdflynn.touch.view.view.BaseViews;
import com.github.cdflynn.touch.view.view.ShuffleView;

import butterknife.Bind;

public class ShuffleFragment extends BaseFragment {

    static class Views extends BaseViews {

        @Bind(R.id.shuffle_view)
        ShuffleView shuffle;

        Views(View root) {
            super(root);
        }
    }

    private Views mViews;

    public static ShuffleFragment newInstance() {
        return new ShuffleFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shuffle, container, false);
        mViews = new Views(root);
        return root;
    }
}
