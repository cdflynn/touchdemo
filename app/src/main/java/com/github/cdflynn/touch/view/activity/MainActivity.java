package com.github.cdflynn.touch.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.github.cdflynn.touch.R;
import com.github.cdflynn.touch.view.fragment.BaseFragment;
import com.github.cdflynn.touch.view.fragment.NoisyFragment;
import com.github.cdflynn.touch.view.fragment.TouchSlopFragment;
import com.github.cdflynn.touch.view.view.MotionEventLogView;
import com.github.cdflynn.touch.view.view.NoisyMotionEventView;
import com.github.cdflynn.touch.view.interfaces.MotionEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        showFragment(NoisyFragment.newInstance());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        BaseFragment currentFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (currentFragment != null) {
            String currentFragmentTag = currentFragment.getClass().getSimpleName();
            switch(item.getItemId()) {
                case R.id.action_noisy:
                    if (!currentFragmentTag.equals(NoisyFragment.class.getSimpleName())) {
                        navigateTo(NoisyFragment.newInstance());
                        return true;
                    }
                case R.id.action_touch_slop:
                    if (!currentFragmentTag.equals(TouchSlopFragment.class.getSimpleName())) {
                        navigateTo(TouchSlopFragment.newInstance());
                        return true;
                    }
            }
        }
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
                R.anim.shrink,
                R.anim.grow,
                R.anim.slide_out_right);
        ft.remove(currentFragment)
                .replace(R.id.content_frame, fragment, tag)
                .addToBackStack(tag)
                .commit();
        getSupportFragmentManager().executePendingTransactions();

    }

}
