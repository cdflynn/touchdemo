package com.github.cdflynn.touch.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.cdflynn.touch.R;
import com.github.cdflynn.touch.view.interfaces.MotionEventListener;
import com.github.cdflynn.touch.view.view.BaseViews;
import com.github.cdflynn.touch.view.view.AnimatedBezierView;
import com.github.cdflynn.touch.view.view.MotionEventLogView;

import butterknife.Bind;

public class BezierFragment extends BaseFragment {

    static class Views extends BaseViews {

        @Bind(R.id.bezier_touch_target)
        AnimatedBezierView bezierView;
        @Bind(R.id.bezier_log)
        MotionEventLogView log;

        Views(View root) {
            super(root);
        }
    }

    private Views mViews;

    public static BezierFragment newInstance() {
        return new BezierFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bezier, container, false);
        mViews = new Views(root);
        mViews.bezierView.setMotionEventListener(mMotionEventListener);
        return root;
    }

    private final MotionEventListener mMotionEventListener = new MotionEventListener() {
        @Override
        public void onMotionEvent(MotionEvent e) {
            mViews.log.log(e);
        }
    };
}
