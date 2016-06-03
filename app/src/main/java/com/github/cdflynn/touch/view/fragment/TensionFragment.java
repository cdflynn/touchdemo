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
import com.github.cdflynn.touch.view.view.TensionView;

import butterknife.Bind;

public class TensionFragment extends BaseFragment {

    private static final float DEFAULT_TENSION = .5f;
    private static final float TENSION_MAX = .9f;
    private static final float TENSION_MIN = .1f;

    static class Views extends BaseViews {

        @Bind(R.id.tension_view)
        TensionView tensionView;
        @Bind(R.id.tension_text)
        TextView tensionText;
        @Bind(R.id.tension_seek_bar)
        SeekBar seekBar;

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
        mViews.tensionText.setText(getString(R.string.tension, DEFAULT_TENSION));
        mViews.seekBar.setOnSeekBarChangeListener(mSeekBarListener);
        return root;
    }

    private float progressToTension(@IntRange(from = 0, to = 100) int progress) {
        return (TENSION_MAX - TENSION_MIN) * ((float) (progress) / 100) + TENSION_MIN;
    }

    private final SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            final float tension = progressToTension(progress);
            mViews.tensionText.setText(getString(R.string.tension, tension));
            mViews.tensionView.setTension(tension);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
