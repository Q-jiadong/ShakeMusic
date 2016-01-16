package com.game.shakemusic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Administrator on 2016-01-07.
 * QJD
 */
public class ShakeMusicActivity extends FragmentActivity {

    private static boolean mIsExit =false;
    private static final int COMPLETE_LOADING = 1;
    private SoundSample mSoundSample;
    private View mLoadingView;
    private View mShakingView;

    public static Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_layout);

        mLoadingView = findViewById(R.id.loading_view);
        mShakingView = findViewById(R.id.shaking_view);
        mSoundSample = SoundSample.getInstance();
        mLoadingView.setVisibility(View.VISIBLE);

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);

        new Thread() {
            @Override
            public void run() {
                mSoundSample.loadSound(getApplicationContext());
            }
        }.start();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case 0:
                        mIsExit = false;
                        break;
                    case COMPLETE_LOADING:
                        mLoadingView.setVisibility(View.GONE);
                        mShakingView.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread() {
            @Override
            public void run() {
                mSoundSample.loadSound(getApplicationContext());
            }
        }.start();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(!mIsExit) {
                mIsExit = true;
                Toast.makeText(getApplicationContext(), R.string.quit_message, Toast.LENGTH_SHORT).show();
                mHandler.sendEmptyMessageDelayed(0, 2000);
            } else {
                finish();
            }
        }
        return true;
    }
}

class FragmentAdapter extends FragmentPagerAdapter {
    protected static final int[] ICONS = new int[] {
            R.drawable.ak47,
            R.drawable.piano
    };

    private int mCount = ICONS.length;

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return ShakeFragment.newInstance(ICONS[position % mCount]);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}