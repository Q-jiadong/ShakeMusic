package com.game.shakemusic;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Administrator on 2016-01-08.
 * QJD
 */
public class ShakeFragment extends Fragment implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private SoundSample mSoundSample;
    private int mImageResId;
    public static ShakeFragment newInstance(int ResId) {
        ShakeFragment sf = new ShakeFragment();
        sf.mImageResId = ResId;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSoundSample = SoundSample.getInstance();
        startSensor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.shake_fragment_layout, container, false);
        ImageView shakeStuff = (ImageView)fragmentView.findViewById(R.id.shake_stuff);
        shakeStuff.setImageResource(mImageResId);
        return fragmentView;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float sensorX = event.values[0];
        //float sensorY = event.values[1];
        //float z_offset = event.values[2];
        if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        if(sensorX < -10 || sensorX > 10) {
            mSoundSample.playSound();
            //Toast.makeText(getContext(), ""+sensorX, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onPause() {
        super.onPause();
        stopSensor();
    }

    @Override
    public void onResume() {
        super.onResume();
        startSensor();
    }

    public void startSensor() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stopSensor() {
        mSensorManager.unregisterListener(this, mAccelerometer);
    }


}
