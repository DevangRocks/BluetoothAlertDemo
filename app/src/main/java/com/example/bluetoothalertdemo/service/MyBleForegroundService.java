package com.example.bluetoothalertdemo.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.bluetoothalertdemo.MainActivity;
import com.example.bluetoothalertdemo.R;

import java.util.Calendar;
import java.util.Random;

public class MyBleForegroundService extends Service {
    private Looper mServiceLooper;

    private final static String TAG = "MyForegroundService";

    Random r;

    SharedPreferences sharedPreferences;
    int spLevel, iLevel;
    String s1, e1;

    PendingIntent pendingIntent;
    Intent my_intent;
    Calendar datetime;
    Context context = this;

    private String soundPath;
    private Uri uri;
    private int batteryTemperature;
    private int tempValue;
    private boolean isServiceRunning = true;

    public static SharedPreferences preferances1;
    String nm, temValue;
    String buttonID;
    Context mContext;
    SharedPreferences preferences;
    int spfLevel;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferances1 = context.getSharedPreferences("Mydata", Context.MODE_PRIVATE);
        nm = preferances1.getString("name", "1");

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        temValue = preferences.getString("image", "C");
        boolean boolBleAlert = preferences.getBoolean("boolBleAlert", false);
        boolean alertSound = preferences.getBoolean("boolAlertSound", false);

        BluetoothAdapter blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent notificationIntent = new Intent(this, MainActivity.class);

        if (blueToothAdapter == null){
            //
        } else {
            if (boolBleAlert == true && alertSound == true) {
                if (blueToothAdapter.isEnabled()) {
                    String input = intent.getStringExtra("inputExtra");
                    createNotificationChannel();
                    PendingIntent pendingIntent = PendingIntent.getActivity(this,
                            0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
                } else {
                    context.stopService(notificationIntent);
                }
            }
        }
        Notification notification = new NotificationCompat.Builder(this, TAG)
                .setContentText("Bluetooth is On")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    TAG,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
