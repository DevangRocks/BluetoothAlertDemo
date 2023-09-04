package com.example.bluetoothalertdemo.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.example.bluetoothalertdemo.R;

public class BluetoothConnectService extends BroadcastReceiver {
    public static SharedPreferences preferances1;

    Context mContext;
    SharedPreferences preferences;
    SharedPreferences sharedPreferences;
    int spfLevel;
    String nm, temValue;

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferances1 = context.getSharedPreferences("Mydata", Context.MODE_PRIVATE);
        nm = preferances1.getString("name", "1");

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        temValue = preferences.getString("image", "C");
        boolean boolBleAlert = preferences.getBoolean("boolBleAlert", false);
        boolean alertSound = preferences.getBoolean("boolAlertSound", false);

        BluetoothAdapter blueToothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (blueToothAdapter == null){
            //
        } else {
            if (boolBleAlert == true && alertSound == true) {
                if (blueToothAdapter.isEnabled()) {
                    showBleNotification(context);
                } else {
                    context.unregisterReceiver(this);
                }
            }
        }
    }

    private void showBleNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Bluetooth Alert", "Bluetooth Alert", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            VibrationEffect vibrationEffect = VibrationEffect.createWaveform(vibrationPattern, -1);
//
//            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//
//            if (vibrator != null) {
//                vibrator.vibrate(vibrationEffect);
//            }
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new Notification.Builder(context, "Bluetooth Alert")
                    .setContentTitle("Bluetooth Alert")
                    .setContentText("Bluetooth on")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setAutoCancel(true)
                    .build();

            notificationManager.notify(0, notification);
        }
    }
}
