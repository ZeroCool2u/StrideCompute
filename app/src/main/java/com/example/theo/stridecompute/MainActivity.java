package com.example.theo.stridecompute;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startButton(View v) {
        Intent i = new Intent(this, AccelerometerService.class);
        startService(i);
        Toast.makeText(this, "Service started.", Toast.LENGTH_SHORT).show();
    }

    public void stopButton(View v) {
        Intent i = new Intent(this, AccelerometerService.class);
        stopService(i);
        Toast.makeText(this, "Service stopped.", Toast.LENGTH_SHORT).show();
    }


}
