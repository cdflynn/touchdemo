package com.github.cdflynn.touch.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.cdflynn.touch.R;
import com.github.cdflynn.touch.view.view.BaseViews;
import com.github.cdflynn.touch.view.view.TensionAnimatedBezierView;

import butterknife.Bind;

public class TensionFragment extends BaseFragment {

    static class Views extends BaseViews {

        @Bind(R.id.tension_animated_bezier_view)
        TensionAnimatedBezierView tensionView;

        Views(View root) {
            super(root);
        }
    }

    public static TensionFragment newInstance() {
        return new TensionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tension, container, false);
        return root;
    }
}
