package com.example.bluetoothalertdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.bluetoothalertdemo.service.BluetoothConnectService;
import com.example.bluetoothalertdemo.service.MyBleForegroundService;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    ToggleButton sbBoot, sbVibration;
    private Button btnDevice, btnGet;
    private ToggleButton bleAlert;
    private ToggleButton alertSound;
    SharedPreferences.Editor editor;

    TextView textview1;
    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter blueToothAdapter;

    private ListView lstvw;
    private ArrayAdapter aAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sbBoot = findViewById(R.id.sb_boot);
        sbVibration = findViewById(R.id.sb_vibration);

        bleAlert = findViewById(R.id.switchBluetooth);
        alertSound = findViewById(R.id.switchAlertSound);
        btnDevice = (Button) findViewById(R.id.btnDevice);
        btnGet = (Button) findViewById(R.id.btnGet);

        initView();

        loadSavePrefrence();

        btnDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, HomeScreen.class);
                startActivity(i);
            }
        });

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBluetoothState();
            }
        });
    }

    public void initView() {
        textview1 = (TextView) findViewById(R.id.textView1);
        blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
        CheckBluetoothState();
//        textview1.append("\nAdapter: " + blueToothAdapter);
        BluetoothConnectService bluetoothConnectService = new BluetoothConnectService();

        if (sbBoot.isChecked()) {
            sbBoot.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_on));
        } else {
            sbBoot.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_off));
        }

        if (sbVibration.isChecked()) {
            sbVibration.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_on));
        } else {
            sbVibration.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_off));
        }

        if (bleAlert.isChecked()) {
            bleAlert.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_on));
        } else {
            bleAlert.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_off));
        }

        if (alertSound.isChecked()) {
            alertSound.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_on));
        } else {
            alertSound.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_off));
        }

        sbBoot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                savePreferencesBoolean("boolBoot", sbBoot.isChecked());
            }
        });
        sbVibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                savePreferencesBoolean("boolVibration", sbVibration.isChecked());
            }
        });

        bleAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                savePreferencesBoolean("boolBleAlert", bleAlert.isChecked());
                savePreferencesInt("dismissval", 1);
                if (bleAlert.isChecked()) {
                    bleAlert.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_on));

                } else {
                    bleAlert.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_off));
                }
            }
        });

        alertSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                savePreferencesBoolean("boolAlertSound", alertSound.isChecked());
                savePreferencesInt("dismissval", 1);
                if (alertSound.isChecked()) {
                    alertSound.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_on));
                } else {
                    alertSound.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_off));
                }
            }
        });

        if (blueToothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        } else {
            if (blueToothAdapter.isEnabled()) {
                Intent serviceIntent = new Intent(getApplicationContext(), MyBleForegroundService.class);
                startService(serviceIntent);

            } else {
                Toast.makeText(MainActivity.this, "Bluetooth is Off", Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(getApplicationContext(), MyBleForegroundService.class);
                stopService(serviceIntent);
            }

        }

    }

    public void showBluetoothNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Test")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("BlueTooth off")
                    .setContentText("Bluetooth is currently turned off.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            Intent settingsIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    settingsIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );
            builder.setContentIntent(pendingIntent);
            notificationManager.notify(1, builder.build());
        }
    }

    private void savePreferencesBoolean(String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private void savePreferencesInt(String key, int value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private void loadSavePrefrence() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean boolVibration = sharedPreferences.getBoolean("boolVibration", false);
        boolean boolBoot = sharedPreferences.getBoolean("boolBoot", false);
        boolean boolBleAlert = sharedPreferences.getBoolean("boolBleAlert", false);
        boolean boolAlertSound = sharedPreferences.getBoolean("boolAlertSound", false);

        if (boolVibration) {
            sbVibration.setChecked(true);
        } else {
            sbVibration.setChecked(false);
        }

        if (boolBoot) {
            sbBoot.setChecked(true);
        } else {
            sbBoot.setChecked(false);
        }
        if (boolBleAlert) {
            bleAlert.setChecked(true);
        } else {
            bleAlert.setChecked(false);
        }
        if (boolAlertSound) {
            alertSound.setChecked(true);
        } else {
            alertSound.setChecked(false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void CheckBluetoothState() {
        if (blueToothAdapter == null) {
            textview1.append("\nBluetooth NOT supported. Aborting.");
            return;
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Set<BluetoothDevice> pairedDevices = blueToothAdapter.getBondedDevices();
            ArrayList list = new ArrayList();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    String devicename = device.getName();
                    String macAddress = device.getAddress();
                    list.add("Name: " + devicename + "MAC Address: " + macAddress);
                }
                lstvw = (ListView) findViewById(R.id.deviceList);
                aAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);
                lstvw.setAdapter(aAdapter);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            CheckBluetoothState();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}