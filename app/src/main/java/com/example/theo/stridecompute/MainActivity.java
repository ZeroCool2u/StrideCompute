package com.example.theo.stridecompute;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startButton(View v) {
        Log.i("DroneTeamHW1", "Service started");

        Intent i = new Intent(getApplicationContext(), AccelerometerService.class);
        startService(i);
    }

    public void stopButton(View v) {
        Log.i("DroneTeamHW1", "Service stopped");

        Intent i = new Intent(getApplicationContext(), AccelerometerService.class);
        stopService(i);
    }


}
