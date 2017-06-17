package com.sample.foo.usingawarenessapi;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.snapshot.internal.Snapshot;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "MainActivity";

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10001;
    private static final String MY_FENCE_RECEIVER_ACTION = "MY_FENCE_ACTION";
    public static final String DRIVING_FENCE_KEY = "DrivingFenceKey";
    private static final int IN_VEHICLE = 0;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private GoogleApiClient mGoogleApiClient;

    private FenceBroadcastReceiver mFenceReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();

        mFenceReceiver = new FenceBroadcastReceiver();

        addDriverFence();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    protected void onStart() {
        super.onStart();

        // We want to receive Broadcasts when activity is paused
        registerReceiver(mFenceReceiver, new IntentFilter(MY_FENCE_RECEIVER_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mFenceReceiver);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

//    protected void onResume() {
//        super.onResume();
//        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Float x = event.values[0];
        Float y = event.values[1];
        Float z = event.values[2];


        if(mFenceReceiver.getOnDriving()) {
            //verificar se houve alteração no eixo vertical (buraco).

        }
    }

    private void addDriverFence() {
        Intent intent = new Intent(MY_FENCE_RECEIVER_ACTION);
        PendingIntent mFencePendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                10001,
                intent,
                0);

        AwarenessFence driveFence = DetectedActivityFence.during(	IN_VEHICLE   );
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .addFence(DRIVING_FENCE_KEY, driveFence, mFencePendingIntent)
                        .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Fence was successfully registered.");
                        } else {
                            Log.e(TAG, "Fence could not be registered: " + status);
                        }
                    }
                });

        Context context = getApplicationContext();
        CharSequence text = "";
        if(mFenceReceiver.getOnDriving()){
            text = "DRIVING ON";
        }else{
            text = "DRIVING OFF";
        }
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
