package com.example.mdp_team04;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothUtils bluetoothUtils;
    private static final String TAG = "MainActivity";

    private final int LOCATION_PERMISSION_REQUEST = 101;
    private final int SELECT_DEVICE = 102;

    public static final int MESSAGE_STATE_CHANGED = 0;
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;
    public static final int MESSAGE_TOAST = 4;
    public static final int MESSAGE_READ_STATUS = 5;


    public static final String DEVICE_NAME = "deviceName";
    public static final String TOAST = "toast";
    private String connectedDevice;

    private static GridMap gridMap;
    static TextView xAxisTextView, yAxisTextView;

    private int[] tabIcons = {
            R.drawable.ic_baseline_message_24,
            R.drawable.ic_baseline_map_24,
            R.drawable.ic_baseline_control_camera_24
    };

    ToggleButton startTimerBtn, exploreTypeBtn;
    private Chronometer startTimer;
    private boolean running;

    private String tempMsg;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_STATE_CHANGED:
                    switch (message.arg1) {
                        case BluetoothUtils.STATE_NONE:
                            setState("Not Connected");
                            break;
                        case BluetoothUtils.STATE_LISTEN:
                            setState("Not Connected");
                            break;
                        case BluetoothUtils.STATE_CONNECTING:
                            setState("Connecting...");
                            break;
                        case BluetoothUtils.STATE_CONNECTED:
                            setState("Connected: " + connectedDevice);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] buffer1 = (byte[]) message.obj;
                    String outputBuffer = new String(buffer1);
                    FragMsg.addToAdapterSentMessages("Me: ", outputBuffer);
                    break;
                case MESSAGE_READ:
                    String inputBuffer = (String) message.obj;
                    if (inputBuffer.equals("s") && running) {
                        startTimer.stop();
                        running = false;
                        startTimerBtn.toggle();
                    }
                    FragMsg.addToAdapterReceivedMessages(connectedDevice + ": ", inputBuffer);
                    break;
                case MESSAGE_READ_STATUS:
                    String inputBufferStatus = (String) message.obj;
                    String inputBufferStatusExtra = null;
                    String[] inputArray = inputBufferStatus.split("\\s*,\\s*");
                    String[] inputArrayAdditional = null;
                    if (inputArray.length > 5 && !inputBufferStatus.contains("STM")) {
                        int index = inputBufferStatus.indexOf("AN", inputBufferStatus.indexOf("AN") + 1);
                        inputBufferStatusExtra = inputBufferStatus.substring(index);
                        inputBufferStatus = inputBufferStatus.substring(0, index);
                        inputArray = inputBufferStatus.split("\\s*,\\s*");
                        inputArrayAdditional = inputBufferStatusExtra.split("\\s*,\\s*");
                    }
                    if (inputBufferStatus.startsWith("AN") && running) {
                        handleMessageRead(inputBufferStatus, inputArray);
                        FragMsg.addToAdapterReceivedMessages(connectedDevice + ": ", inputBufferStatus);
                    }
                    if (inputBufferStatusExtra != null && inputBufferStatusExtra.startsWith("AN") && running) {
                        handleMessageRead(inputBufferStatusExtra, inputArrayAdditional);
                        FragMsg.addToAdapterReceivedMessages(connectedDevice + ": ", inputBufferStatusExtra);
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    connectedDevice = message.getData().getString(DEVICE_NAME);
                    showLog(connectedDevice);
                    break;
                case MESSAGE_TOAST:
                    showLog(message.getData().getString(TOAST));
                    break;
            }
            return false;
        }
    });

    private void handleMessageRead(String inputBuffer, String[] inputArray) {
        if (inputBuffer.contains("StopIE") && running) {
            startTimer.stop();
            running = false;
            startTimerBtn.toggle();
        } else if (inputBuffer.contains("StopFP") && running) {
            startTimer.stop();
            running = false;
            startTimerBtn.toggle();
        } else {
            gridMap.handleMessageReceive(inputArray);
        }
    }

    private void setState(CharSequence subTitle) {
        showLog("Entering setBluetoothState");
        getSupportActionBar().setSubtitle(subTitle);
        showLog("Exiting setBluetoothState");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        context = this;

        initBluetooth();
        bluetoothUtils = new BluetoothUtils(context, handler);
        initLayout();

        // Map
        gridMap = new GridMap(this);
        gridMap = findViewById(R.id.mapView);
        xAxisTextView = findViewById(R.id.xAxisTextView);
        yAxisTextView = findViewById(R.id.yAxisTextView);

        // Timer
        startTimerBtn = findViewById(R.id.startTimerBtn);
        startTimer = findViewById(R.id.startTimer);
        exploreTypeBtn = findViewById(R.id.exploreTypeBtn);
        initStartTimer();
    }

    private void initStartTimer() {
        showLog("Entering initStartTimer");
        startTimer.setFormat("%s");
        startTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempMsg = "PC,";
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    if (startTimerBtn.getText().equals("STOP")) {
                        if (exploreTypeBtn.getText().equals("Image Exploration")) {
                            tempMsg += "StartIE";
                            BluetoothUtils.write(tempMsg.getBytes());
                        } else if (exploreTypeBtn.getText().equals("Fastest Path")) {
                            //tempMsg += "StartFP";
                            tempMsg = "STM," + "w";
                            BluetoothUtils.write(tempMsg.getBytes());
                        }
                        startTimer.setBase(SystemClock.elapsedRealtime());
                        startTimer.start();
                        running = true;
                    } else if (startTimerBtn.getText().equals("START")) {
                        if (exploreTypeBtn.getText().equals("Image Exploration")) {
                            tempMsg += "StopIE";
                            BluetoothUtils.write(tempMsg.getBytes());
                        } else if (exploreTypeBtn.getText().equals("Fastest Path")) {
                            tempMsg += "StopFP";
                            BluetoothUtils.write(tempMsg.getBytes());
                        }
                        startTimer.stop();
                        running = false;
                    }
                } else {
                    startTimerBtn.toggle();
                    showToast("Not connected. Please connect to a bluetooth device");
                }
            }
        });
        showLog("Exiting initStartTimer");
    }

    private void initBluetooth() {
        showLog("Entering initBluetooth");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "No bluetooth found", Toast.LENGTH_SHORT).show();
        }
        showLog("Exiting initBluetooth");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search_devices:
                checkPermissions();
                return true;
            case R.id.menu_enable_bluetooth:
                enableBluetooth();
                return true;
            case R.id.menu_disconnect_devices:
                bluetoothUtils.stop();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkPermissions() {
        showLog("Entering checkPermissions");
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            Intent intent = new Intent(context, DeviceListProcess.class);
            startActivityForResult(intent, SELECT_DEVICE);
        }
        showLog("Exiting checkPermissions");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_DEVICE && resultCode == RESULT_OK) {
            String address = data.getStringExtra("deviceAddress");
            bluetoothUtils.connect(bluetoothAdapter.getRemoteDevice(address));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(context, DeviceListProcess.class);
                startActivityForResult(intent, SELECT_DEVICE);
            } else {
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setMessage("Location permission is required.\n Please grant")
                        .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                checkPermissions();
                            }
                        })
                        .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.this.finish();
                            }
                        }).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void enableBluetooth() {
        showLog("Entering enableBluetooth");
        if (!bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothAdapter.enable();
        }

        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoveryIntent);
        }
        showLog("Exiting enableBluetooth");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothUtils != null) {
            bluetoothUtils.stop();
        }
    }


    private void initLayout() {
        showLog("Entering initLayout");
        TabLayout tabLayout = findViewById(R.id.tabsLayout);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText("Message");
                    tab.setIcon(tabIcons[position]);
                }
                else if (position == 1) {
                    tab.setText("Map");
                    tab.setIcon(tabIcons[position]);
                }
                else if (position == 2) {
                    tab.setText("Controller");
                    tab.setIcon(tabIcons[position]);
                }
            }
        }).attach();
        showLog("Exiting initLayout");
    }

    public static GridMap getGridMap() {
        return gridMap;
    }

    private static void showLog(String message) {
        Log.d(TAG, message);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}