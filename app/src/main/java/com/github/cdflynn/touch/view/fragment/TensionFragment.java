package com.github.cdflynn.touch.view.fragment;

import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.cdflynn.touch.R;
import com.github.cdflynn.touch.view.view.BaseViews;
import com.github.cdflynn.touch.view.view.TensionAnimatedBezierView;

import butterknife.Bind;

public class TensionFragment extends BaseFragment {

    private static final float MAX_TENSION = 1f;
    private static final float MIN_TENSION = .01f;
    private static final float DEFAULT_TENSION = (MAX_TENSION - MIN_TENSION)/2 + MIN_TENSION;

    static class Views extends BaseViews {

        @Bind(R.id.tension_animated_bezier_view)
        TensionAnimatedBezierView tensionView;
        @Bind(R.id.tension_seek_bar)
        SeekBar seekBar;
        @Bind(R.id.tension_text)
        TextView tensionText;

        Views(View root) {
            super(root);
        }
    }

    private Views mViews;

    public static TensionFragment newInstance() {
        return new TensionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tension, container, false);
        mViews = new Views(root);
        mViews.seekBar.setProgress(50);
        mViews.seekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mViews.tensionView.setTension(DEFAULT_TENSION);
        mViews.tensionText.setText(getString(R.string.tension, DEFAULT_TENSION));
        return root;
    }

    private float progressToTension(@IntRange(from = 0, to = 100) int progress) {
        return ((float)progress/100 * (MAX_TENSION - MIN_TENSION)) + MIN_TENSION;
    }

    private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float tension = progressToTension(progress);
            mViews.tensionText.setText(getString(R.string.tension, tension));
            mViews.tensionView.setTension(progressToTension(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
