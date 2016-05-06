package com.github.cdflynn.touch.view.control;

import android.content.Context;
import android.graphics.Color;
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

public class MotionEventLogView extends LinearLayout {

    private static final int CAPACITY = 6;

    static class Views {
        TextView newLine;
        TextView history;

        Views(View root) {
            newLine = (TextView) root.findViewById(R.id.motion_event_log_new_line);
            history = (TextView) root.findViewById(R.id.motion_event_log_history);
        }
    }

    private Views mViews;
    private List<MotionEvent> mEventLog;

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
        mEventLog.add(0, event);
        trim();
        showEvents(mEventLog);
    }

    private void trim() {
        if (mEventLog.size() > CAPACITY) {
            mEventLog.remove(mEventLog.size() -1);
        }
    }

    private void showEvents(List<MotionEvent> events) {
        if (events.isEmpty()) {
            return;
        }
        mViews.newLine.setText(from(events.get(0)));
        if (events.size() > 1) {
            SpannableStringBuilder history = new SpannableStringBuilder();
            for (int i = 1; 1 < events.size(); i++) {
                history.append(from(events.get(i)));
                if (i != (events.size()+1)) {
                    history.append('\n');
                }
            }
            mViews.history.setText(history.toString());
        }
    }

    private SpannableString from(MotionEvent e) {
        SpannableString s = new SpannableString(MotionEvent.actionToString(e.getAction()));
        int color = textColorFrom(e);
        s.setSpan(new ForegroundColorSpan(color), 0, s.length(), 0);
        return s;
    }

    private int textColorFrom(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                return Color.GREEN;
            case MotionEvent.ACTION_CANCEL:
                return Color.RED;
            default:
                return Color.WHITE;
        }
    }
}
