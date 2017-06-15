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
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.BeaconFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;

public class FenceActivity extends AppCompatActivity implements SensorEventListener{
    private static final String TAG = "FenceActivity";

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10001;
    private static final String MY_FENCE_RECEIVER_ACTION = "MY_FENCE_ACTION";
    public static final String DRIVING_FENCE_KEY = "DrivingFenceKey";

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private GoogleApiClient mGoogleApiClient;

    private FenceBroadcastReceiver mFenceReceiver;

    private Button mAddAction1Button, mRemoveAction1Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fence);

        mGoogleApiClient = new GoogleApiClient.Builder(FenceActivity.this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();

        mFenceReceiver = new FenceBroadcastReceiver();

        mAddAction1Button = (Button) findViewById(R.id.add1Button);
        mAddAction1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDriverFence();
            }
        });
        mRemoveAction1Button = (Button) findViewById(R.id.remove1Button);
        mRemoveAction1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFence(DRIVING_FENCE_KEY);
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Override
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Float x = event.values[0];
        Float y = event.values[1];
        Float z = event.values[2];


//        if(getOnDriving) {
//            //verificar se houve alteração no eixo vertical (buraco).
//            //salvar no banco.
//        }
    }

    private void addDriverFence() {
        Intent intent = new Intent(MY_FENCE_RECEIVER_ACTION);
        PendingIntent mFencePendingIntent = PendingIntent.getBroadcast(FenceActivity.this,
                10001,
                intent,
                0);

        AwarenessFence driveFence = DetectedActivityFence.during();
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
    }

    private void removeFence(final String fenceKey) {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(fenceKey)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                String info = "Fence " + fenceKey + " successfully removed.";
                Log.i(TAG, info);
                Toast.makeText(FenceActivity.this, info, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Status status) {
                String info = "Fence " + fenceKey + " could NOT be removed.";
                Log.i(TAG, info);
                Toast.makeText(FenceActivity.this, info, Toast.LENGTH_LONG).show();
            }
        });
    }
}
