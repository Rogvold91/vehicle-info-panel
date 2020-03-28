package com.example.carhelper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.carhelper.service.CarData;
import com.example.carhelper.service.IDataCallback;
import com.example.carhelper.service.ISpeedService;
import com.example.carhelper.service.SpeedService;
import com.example.carhelper.ui.DataObserver;
import com.example.carhelper.ui.DepthPageTransformer;
import com.example.carhelper.ui.ScreenSlidePageFragment;
import com.example.carhelper.util.TypeControl;

import java.util.ArrayList;

import static com.example.carhelper.util.Constants.AUTO_HIDE_DELAY_MILLIS;
import static com.example.carhelper.util.Constants.UI_ANIMATION_DELAY;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager2 viewPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private FragmentStateAdapter pagerAdapter;

    private boolean mVisible;
    private ArrayList<DataObserver> mList = new ArrayList<>();
    private ISpeedService speedService;
    private final Runnable mHideRunnable = () -> hide();
    private final Runnable mShowPart2Runnable = () -> {
        // Delayed display of UI elements
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    };

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }


    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {

            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mVisible = true;
        mContentView = findViewById(R.id.main_cont);
        mContentView.setKeepScreenOn(true);
        viewPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(new DepthPageTransformer());
        viewPager.getParent().requestDisallowInterceptTouchEvent(true);
        viewPager.getChildAt(0).setOnTouchListener((v, event) -> { return  event.getPointerCount() != 2; });
        Intent startService = new Intent(FullscreenActivity.this, SpeedService.class);
        bindService(startService, FullscreenActivity.this.mService, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        try {
            speedService.removeCallback(mDataCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(mService);
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    ServiceConnection mService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            speedService = ISpeedService.Stub.asInterface(service);
            try {
                speedService.addCallback(mDataCallback);
                speedService.startStopSimulation(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "CONNECTED");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "DISCONNECTED");
        }
    };

    IDataCallback mDataCallback = new IDataCallback.Stub() {
        @Override
        public void vehicleDataChanged(CarData data) {
            for (DataObserver obs: mList) {
                obs.onCarDataUpdated(data);
            }
        }
    };

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }
        @Override
        public Fragment createFragment(int position) {
            ScreenSlidePageFragment fragment = position == 0 ?  new ScreenSlidePageFragment(TypeControl.Speedometer)
                                                             :  new ScreenSlidePageFragment(TypeControl.Tachometer);
            FullscreenActivity.this.mList.add(fragment);
            return fragment;
        }
        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
