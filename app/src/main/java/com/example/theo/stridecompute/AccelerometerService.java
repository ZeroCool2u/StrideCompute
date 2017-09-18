package com.example.theo.stridecompute;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.System.currentTimeMillis;

public class AccelerometerService extends Service implements SensorEventListener {
    protected static boolean RUNFLAG;
    protected static LinkedBlockingQueue<long[]> incomingReadings = new LinkedBlockingQueue<>();
    protected static File saveLocation;
    protected static FileWriter f;
    protected SensorManager sensorManager;
    protected Sensor accelerometer;
    protected Sensor gyroscope;
    protected NotificationManager notificationManager;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public AccelerometerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Error");
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);

        if (!isExternalStorageWritable()) {
            throw new RuntimeException("External storage is not writable, damn get it together.");
        }


        createExternalStoragePublicCSV();

        RUNFLAG = true;

        new Thread(new Runnable() {
            public void run() {
                while (RUNFLAG) {
                    if(incomingReadings.size() != 0){
                    long[] nextEvent = new long[5];
                    try {
                        nextEvent = incomingReadings.take();
                        Log.i("test", String.valueOf(nextEvent[0]));
                        Log.i("test", String.valueOf(nextEvent[1]));
                        Log.i("test", String.valueOf(nextEvent[2]));
                        Log.i("test", String.valueOf(nextEvent[3]));
                        Log.i("test", String.valueOf(nextEvent[4]));
                    } catch (InterruptedException e) {
                        Log.i("Exception", "Reading retrieval from queue failed.");
                    }
                    // a potentially  time consuming task
                    writeToFile(nextEvent);
                    }
                }
            }
        }).start();


        Log.i("Acceleration", "StartID: " + startID + "-> " + intent);

        return START_STICKY;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);

        }


    void createExternalStoragePublicCSV() {
        // Create a path where we will place our csv in the user's
        // public downloads directory.
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        String fileName = "storedStepData_" + String.valueOf(currentTimeMillis()) + ".csv";

        File file = new File(path, fileName);
        // Make sure the Downloads directory exists.
        path.mkdirs();
        saveLocation = file;
        Log.i("Testing", "Passed saveLocation");
        try {
            f = new FileWriter(saveLocation, true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Seriously, we just had this, what happened!?");
        }
    }

    private void writeToFile(long[] data) {
        String dataPoint;
        if (data[1] == 0) {
            dataPoint = data[0] + ",A," + data[2] + "," + data[3] + "," + data[4] + "\n";
        } else {
            dataPoint = data[0] + ",G," + data[2] + "," + data[3] + "," + data[4] + "\n";
        }

        try {
            f.write(dataPoint);
            Log.i("Disk Write", "Buffer written to disk!");
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this, gyroscope);
        Log.i("Gyroscope", "Gyroscope listener unregistered.");
        sensorManager.unregisterListener(this, accelerometer);
        Log.i("Acceleration", "Acceleration listener unregistered.");
        RUNFLAG = false;
        try {
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("Output", "File closed.");
    }


    @Override
    public void onSensorChanged(SensorEvent e) {
        long[] nextEvent = new long[5];
        nextEvent[0] = e.timestamp;
        if (e.sensor.getType() == accelerometer.getType()) {
            nextEvent[1] = 0;
        } else if(e.sensor.getType() == gyroscope.getType()) {
            nextEvent[1] = 1;
        }
        else{
            Log.i("Sensor", "Unknown sensor data received.");
        }
        nextEvent[2] = (long) e.values[0];
        nextEvent[3] = (long) e.values[1];
        nextEvent[4] = (long) e.values[2];

        incomingReadings.offer(nextEvent);

        //Log.i("Acceleration", e.values[0] + "," + e.values[1] + "," + e.values[2]);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}