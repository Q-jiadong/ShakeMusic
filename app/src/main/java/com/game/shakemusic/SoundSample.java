package com.game.shakemusic;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by Administrator on 2016-01-08.
 * QJD
 */
public class SoundSample {

    private static SoundSample mSoundSample;
    private SoundPool mSoundPool;
    private final static int COMPLETE_LOADING = 1;
    public static synchronized SoundSample getInstance() {
        if(mSoundSample == null){
            mSoundSample = new SoundSample();
        }
        return mSoundSample;
    }

    public SoundSample() {
        mSoundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
    }

    public void loadSound(Context context) {
        mSoundPool.load(context, R.raw.c1, 1);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                ShakeMusicActivity.mHandler.sendEmptyMessage(COMPLETE_LOADING);
            }
        });
    }

    public void playSound() {
        mSoundPool.play(1,1,1,0,0,1);
    }
}