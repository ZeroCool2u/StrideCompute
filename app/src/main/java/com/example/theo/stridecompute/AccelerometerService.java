package com.example.theo.stridecompute;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class AccelerometerService extends Service implements SensorEventListener {
    protected SensorManager sensorManager;
    protected Sensor accelerometer;
    protected NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Error");
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        Log.i("Acceleration", "StartID: " + startID + "-> " + intent);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        Log.i("Acceleration", "Listener unregistered.");
    }


    @Override
    public void onSensorChanged(SensorEvent e) {
        Log.i("Acceleration", e.values[0] + "," + e.values[1] + "," + e.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
