package com.github.cdflynn.touch.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.cdflynn.touch.R;
import com.github.cdflynn.touch.view.fragment.AnimatedBezierFragment;
import com.github.cdflynn.touch.view.fragment.BaseFragment;
import com.github.cdflynn.touch.view.fragment.BezierFragment;
import com.github.cdflynn.touch.view.fragment.NoisyFragment;
import com.github.cdflynn.touch.view.fragment.TensionFragment;
import com.github.cdflynn.touch.view.fragment.TouchSlopFragment;

public class MainActivity extends AppCompatActivity {

    private static final String SAVED_STATE_FRAGMENT = "the fragment that was last showing";

    private int mCurrentFragmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentFragmentId = savedInstanceState != null?
                savedInstanceState.getInt(SAVED_STATE_FRAGMENT)
                : R.id.action_noisy;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        showFragment(fromSavedState(savedInstanceState));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_STATE_FRAGMENT, mCurrentFragmentId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        mCurrentFragmentId = id;
        navigateTo(fromId(id));
        return super.onOptionsItemSelected(item);
    }

    private void showFragment(BaseFragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content_frame, fragment, fragment.getClass().getSimpleName());
        ft.commit();
    }

    private void navigateTo(BaseFragment fragment) {
        BaseFragment currentFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (currentFragment != null && currentFragment.hasOnSaveInstanceStateBeenCalled()) {
            return;
        }
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right,
                0,
                0,
                R.anim.slide_out_right);
        ft.remove(currentFragment)
                .replace(R.id.content_frame, fragment, tag)
                .commit();
        getSupportFragmentManager().executePendingTransactions();

    }

    private BaseFragment fromSavedState(Bundle savedState) {
        if (savedState == null || !savedState.containsKey(SAVED_STATE_FRAGMENT)) {
            return NoisyFragment.newInstance();
        }
        return fromId(savedState.getInt(SAVED_STATE_FRAGMENT, R.id.action_noisy));
    }

    private BaseFragment fromId(int id) {
        switch (id) {
            case R.id.action_noisy:
                return NoisyFragment.newInstance();
            case R.id.action_touch_slop:
                return TouchSlopFragment.newInstance();
            case R.id.action_bezier:
                return BezierFragment.newInstance();
            case R.id.action_animated_bezier:
                return AnimatedBezierFragment.newInstance();
            case R.id.action_tension:
                return TensionFragment.newInstance();
            default:
                return NoisyFragment.newInstance();
        }
    }

}
