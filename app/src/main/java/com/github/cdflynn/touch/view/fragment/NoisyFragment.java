package com.github.cdflynn.touch.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.cdflynn.touch.R;
import com.github.cdflynn.touch.view.interfaces.MotionEventListener;
import com.github.cdflynn.touch.view.view.MotionEventLogView;
import com.github.cdflynn.touch.view.view.NoisyMotionEventView;

public class NoisyFragment extends BaseFragment {

    private class Views {
        NoisyMotionEventView touchTarget;
        MotionEventLogView log;

        Views(View root) {
            touchTarget = (NoisyMotionEventView) root.findViewById(R.id.touch_target);
            log = (MotionEventLogView) root.findViewById(R.id.log);
        }
    }

    private Views mViews;

    public static NoisyFragment newInstance() {
        return new NoisyFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_noisy, container, false);
        mViews = new Views(root);
        mViews.touchTarget.setMotionEventListener(mTouchTargetListener);
        return root;
    }

    private final MotionEventListener mTouchTargetListener = new MotionEventListener() {
        @Override
        public void onMotionEvent(MotionEvent e) {
            mViews.log.log(e);
        }
    };
}
