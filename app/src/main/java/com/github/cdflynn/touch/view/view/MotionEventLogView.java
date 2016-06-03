package com.github.cdflynn.touch.view.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.cdflynn.touch.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class MotionEventLogView extends LinearLayout {

    private static final int CAPACITY = 12;

    private static final class LoggedEvent {

        LoggedEvent(MotionEvent e) {
            this.action = e.getAction();
            this.x = e.getX();
            this.y = e.getY();
        }

        void overwrite(int action, float x, float y) {
            this.action = action;
            this.x = x;
            this.y = y;
        }

        int action;
        float x;
        float y;
    }

    static class Views extends BaseViews {
        @Bind(R.id.motion_event_log_history)
        TextView history;

        Views(View root) {
            super(root);
        }
    }

    private Views mViews;
    private List<LoggedEvent> mEventLog;

    public MotionEventLogView(Context context) {
        super(context);
        init(context);
    }

    public MotionEventLogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MotionEventLogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public MotionEventLogView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        inflate(context, R.layout.view_motion_event_log, this);
        mViews = new Views(this);
        mEventLog = new ArrayList<>(CAPACITY);
    }

    public void log(MotionEvent event) {
        if (event == null) {
            return;
        }
        addOrUpdate(event);
        trim();
        showHistory();
    }

    private void trim() {
        if (mEventLog.size() > CAPACITY) {
            mEventLog.remove(mEventLog.size() - 1);
        }
    }

    private void addOrUpdate(MotionEvent e) {
        if (mEventLog.isEmpty()) {
            mEventLog.add(0, new LoggedEvent(e));
            return;
        }

        if (e.getAction() == mEventLog.get(0).action) {
            mEventLog.get(0).overwrite(e.getAction(), e.getX(), e.getY());
        } else {
            mEventLog.add(0, new LoggedEvent(e));
        }

    }

    private void showHistory() {
        if (mEventLog.isEmpty()) {
            return;
        }
        SpannableStringBuilder history = new SpannableStringBuilder();
        for (int i = (mEventLog.size() - 1); i >= 0; i--) {
            history.append(from(mEventLog.get(i)));
            if (i != 0) {
                history.append('\n');
            }
        }
        mViews.history.setText(history);
    }

    private SpannableString from(LoggedEvent e) {
        SpannableString s = new SpannableString(MotionEvent.actionToString(e.action) + location(e));
        int color = textColorFrom(e);
        s.setSpan(new ForegroundColorSpan(color), 0, s.length(), 0);
        return s;
    }

    private String location(LoggedEvent e) {
        if (e == null) {
            return "";
        }
        return " (" + e.x + ", " + e.y + ")";
    }

    private int textColorFrom(LoggedEvent e) {
        switch (e.action) {
            case MotionEvent.ACTION_DOWN:
                return ContextCompat.getColor(getContext(), R.color.textColorPrimary);
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return ContextCompat.getColor(getContext(), R.color.textColorAccentError);
            case MotionEvent.ACTION_MOVE:
            default:
                return ContextCompat.getColor(getContext(), R.color.textColorAccent);
        }
    }
}
