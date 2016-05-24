package com.github.cdflynn.touch.view.view;

import android.view.View;

import butterknife.ButterKnife;

public abstract class BaseViews {
    public BaseViews(View root) {
        ButterKnife.bind(this, root);
    }
}
