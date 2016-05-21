package com.github.cdflynn.touch.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.github.cdflynn.touch.R;
import com.github.cdflynn.touch.view.interfaces.MotionEventListener;
import com.github.cdflynn.touch.view.view.BezierView;
import com.github.cdflynn.touch.view.view.MotionEventLogView;
import com.github.cdflynn.touch.view.view.TouchSlopMotionEventView;

public class TouchSlopFragment extends BaseFragment {


    private class Views {
        BezierView touchTarget;
        MotionEventLogView log;
        AppCompatSeekBar seekBar;

        Views(View root) {
            touchTarget = (BezierView) root.findViewById(R.id.touch_slop_target);
            log = (MotionEventLogView) root.findViewById(R.id.touch_slop_log);
            seekBar = (AppCompatSeekBar) root.findViewById(R.id.touch_slop_seek_bar);
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
        mViews.touchTarget.setMotionEventListener(mTouchTargetListener);
        mViews.seekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        return root;
    }


    private final MotionEventListener mTouchTargetListener = new MotionEventListener() {
        @Override
        public void onMotionEvent(MotionEvent e) {
            mViews.log.log(e);
        }
    };

    private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mViews.touchTarget.setAdditionalTouchSlop(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // do nothing
        }
    };
}
