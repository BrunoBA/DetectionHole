package com.sample.foo.usingawarenessapi;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.awareness.snapshot.internal.Snapshot;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {

    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "SnapshotActivity";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10001;

    private Button mSnapshotButton;
    private ImageButton mFenceButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();


        CharSequence text = "App Ligado";
        Toast t = Toast.makeText( getApplicationContext(), text, Toast.LENGTH_SHORT);
        t.show();

        mFenceButton = (ImageButton) findViewById(R.id.ImageButton);
        mFenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FenceActivity.class));
            }
        });
    }

    protected void onDestroy(){
        CharSequence text = "Hole Detection Desligado";
        Toast t = Toast.makeText( getApplicationContext(), text, Toast.LENGTH_SHORT);
        t.show();
    }
}
