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
import com.github.cdflynn.touch.view.view.InterpolatedTensionView;

import butterknife.Bind;
import io.apptik.widget.MultiSlider;

public class InterpolatedTensionFragment extends BaseFragment {

    private static final float MAX_TENSION = 2f;
    private static final float MIN_TENSION = 0f;
    private static final float DEFAULT_TENSION = (MAX_TENSION - MIN_TENSION) / 2 + MIN_TENSION;
    private static final int RADIUS_MIN = 0;
    private static final int RADIUS_MAX/*IMUS*/ = 1300;
    private static final int DEFAULT_RADIUS_MIN = 300;
    private static final int DEFAULT_RADIUS_MAX = 500;

    static class Views extends BaseViews {

        @Bind(R.id.interpolated_tension_view)
        InterpolatedTensionView tensionView;
        @Bind(R.id.tension_seek_bar)
        SeekBar seekBar;
        @Bind(R.id.tension_text)
        TextView tensionText;
        @Bind(R.id.tension_multi_slider)
        MultiSlider multiSlider;

        Views(View root) {
            super(root);
        }
    }

    private Views mViews;

    public static InterpolatedTensionFragment newInstance() {
        return new InterpolatedTensionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_interpolated_tension, container, false);
        mViews = new Views(root);
        mViews.seekBar.setProgress(50);
        mViews.seekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mViews.tensionView.setTension(DEFAULT_TENSION);
        mViews.tensionText.setText(getString(R.string.tension, DEFAULT_TENSION));
        mViews.tensionView.setRadii(DEFAULT_RADIUS_MIN, DEFAULT_RADIUS_MAX);
        mViews.multiSlider.setMin(RADIUS_MIN);
        mViews.multiSlider.setMax(RADIUS_MAX);
        mViews.multiSlider.getThumb(0).setValue(DEFAULT_RADIUS_MIN);
        mViews.multiSlider.getThumb(1).setValue(DEFAULT_RADIUS_MAX);
        mViews.multiSlider.setOnThumbValueChangeListener(mRadiusChangeListener);
        return root;
    }

    private float progressToTension(@IntRange(from = 0, to = 100) int progress) {
        return ((float) progress / 100 * (MAX_TENSION - MIN_TENSION)) + MIN_TENSION;
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

    private final MultiSlider.OnThumbValueChangeListener mRadiusChangeListener = new MultiSlider.OnThumbValueChangeListener() {
        @Override
        public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
            final int min = mViews.multiSlider.getThumb(0).getValue();
            final int max = mViews.multiSlider.getThumb(1).getValue();
            mViews.tensionView.setRadii(min, max);
        }
    };
}
