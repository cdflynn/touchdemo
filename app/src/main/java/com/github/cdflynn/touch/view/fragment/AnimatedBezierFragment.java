package com.github.cdflynn.touch.view.fragment;

import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.cdflynn.touch.R;
import com.github.cdflynn.touch.view.interfaces.MotionEventListener;
import com.github.cdflynn.touch.view.view.AnimatedBezierView;
import com.github.cdflynn.touch.view.view.BaseViews;

import butterknife.Bind;

public class AnimatedBezierFragment extends BaseFragment {

    private static final int ANIMATION_DURATION_MS_MAX = 1000;
    private static final int ANIMATION_DURATION_MS_MIN = 100;

    static class Views extends BaseViews {

        @Bind(R.id.animated_bezier_touch_target)
        AnimatedBezierView animatedBezierView;
        @Bind(R.id.animation_duration_text)
        TextView durationText;
        @Bind(R.id.animation_duration_seekbar)
        SeekBar seekBar;
        @Bind(R.id.interpolator_spinner)
        Spinner interpolatorSpinner;

        Views(View root) {
            super(root);
        }
    }

    private Views mViews;

    public static AnimatedBezierFragment newInstance() {
        return new AnimatedBezierFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_animated_bezier, container, false);
        mViews = new Views(root);
        mViews.durationText.setText(getString(R.string.duration, 100));
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.interpolator_array,
                android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mViews.interpolatorSpinner.setAdapter(spinnerAdapter);
        mViews.interpolatorSpinner.setOnItemSelectedListener(mSpinnerListener);
        mViews.seekBar.setOnSeekBarChangeListener(mSeekBarListener);
        mViews.animatedBezierView.setMotionEventListener(mMotionEventListener);
        return root;
    }

    /**
     * Map the seek bar progress (0-100) to animation duration in milliseconds.
     */
    private int seekBarProgressToAnimationDuration(@IntRange(from = 0, to = 100) int progress) {

        if (progress <= 0) {
            return ANIMATION_DURATION_MS_MIN;
        }
        if (progress >= 100) {
            return ANIMATION_DURATION_MS_MAX;
        }
        final float progressPercentage = ((float) progress) / 100f;
        return (int)(progressPercentage * (ANIMATION_DURATION_MS_MAX - ANIMATION_DURATION_MS_MIN)) + ANIMATION_DURATION_MS_MIN;
    }

    /**
     * Map the spinner position to the type of interpolator.
     */
    private Interpolator getInterpolatorFromSpinnerPosition(int position) {
        switch (position) {
            case 0:
                return new AccelerateInterpolator(.5f);
            case 1:
                return new AccelerateDecelerateInterpolator();
            case 2:
                return new AnticipateInterpolator();
            case 3:
                return new AnticipateOvershootInterpolator();
            case 4:
                return new BounceInterpolator();
            case 5:
                return new DecelerateInterpolator();
            case 6:
                return new FastOutLinearInInterpolator();
            case 7:
                return new FastOutSlowInInterpolator();
            case 8:
                return new LinearInterpolator();
            case 9:
                return new OvershootInterpolator();
            default:
                return new AccelerateInterpolator(.5f);
        }
    }

    private final SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int newDuration = seekBarProgressToAnimationDuration(progress);
            mViews.durationText.setText(getString(R.string.duration, newDuration));
            mViews.animatedBezierView.setAnimationDuration(newDuration);
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

    private final AdapterView.OnItemSelectedListener mSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mViews.animatedBezierView.setInterpolator(
                    getInterpolatorFromSpinnerPosition(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // do nothing
        }
    };

    private final MotionEventListener mMotionEventListener = new MotionEventListener() {
        @Override
        public void onMotionEvent(MotionEvent e) {
            // do nothing
        }
    };
}
