package com.game.shakemusic;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

/**
 * Created by Administrator on 2016-01-08.
 * QJD
 */
public class SoundSample {

    private static SoundSample mSoundSample;
    private SoundPool mSoundPool;
    private final static int COMPLETE_LOADING = 1;
    private HashMap mMusicId = new HashMap();
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
        mMusicId.put(1, mSoundPool.load(context, R.raw.c2, 1));
        mMusicId.put(2, mSoundPool.load(context, R.raw.c2m, 1));
        mMusicId.put(3, mSoundPool.load(context, R.raw.d2, 1));
        mMusicId.put(4, mSoundPool.load(context, R.raw.d2m, 1));
        mMusicId.put(5, mSoundPool.load(context, R.raw.e2, 1));
        mMusicId.put(6, mSoundPool.load(context, R.raw.f2, 1));
        mMusicId.put(7, mSoundPool.load(context, R.raw.f2m, 1));
        mMusicId.put(8, mSoundPool.load(context, R.raw.g2, 1));
        mMusicId.put(9, mSoundPool.load(context, R.raw.g2m, 1));
        mMusicId.put(10, mSoundPool.load(context, R.raw.a2, 1));
        mMusicId.put(11, mSoundPool.load(context, R.raw.a2m, 1));
        mMusicId.put(12, mSoundPool.load(context, R.raw.b2, 1));
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                ShakeMusicActivity.mHandler.sendEmptyMessage(COMPLETE_LOADING);
            }
        });
    }

    public void playSound(int i) {
        mSoundPool.play(i,1,1,0,0,1);
    }
}