package ink.plink.plinkApp.owner;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import ink.plink.plinkApp.BluetoothConnectionManager;
import ink.plink.plinkApp.R;

public class PrinterSetupPagerActivity extends AppCompatActivity implements BluetoothConnectionManager.BluetoothProgressInterface {

    public static final String KEY_USER_ID = "User ID";
    private String mUserId;

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 5;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter pagerAdapter;

    // String of IoT Device name
    public static String bluetoothDeviceName;

    // Reference to Fragment that contains Bluetooth Setup information
    private PrinterSetupPageFragment bluetoothPagerFragment;
    // Reference to Fragment that contains Wifi info to send to IoT
    private PrinterSetupPageFragment wifiPagerFragment;
    // BluetoothConnectionManager that passes data to and form IoT
    private BluetoothConnectionManager bluetoothConnectionManager;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() != null && bluetoothDeviceName != null) {
                    if (device.getName().equals(bluetoothDeviceName)) {
                        bluetoothConnectionHandler
                                .obtainMessage(BluetoothConnectionManager.BluetoothProgressInterface.DEVICE_CONNECTED)
                                .sendToTarget();
                        bluetoothConnectionManager.setDeviceToConnect(device);

                    }
                }
            }
        }
    };

    // Create a message Handler to handle messages from the BluetoothConnectionManager back to the Fragments
    @SuppressLint("HandlerLeak")
    private final Handler bluetoothConnectionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothConnectionManager.BluetoothProgressInterface.WRITE_MESSAGE: {
                    bluetoothConnectionManager.write((String)msg.obj);
                    break;
                }
                case BluetoothConnectionManager.BluetoothProgressInterface.START_DISCOVERY: {
                    bluetoothConnectionManager.startDiscovery();
                    break;
                }
                case BluetoothConnectionManager.BluetoothProgressInterface.CONNECTION_CREATED:
                case BluetoothConnectionManager.BluetoothProgressInterface.DEVICE_CONNECTED: {
                    bluetoothPagerFragment.updateUI(msg.what);
                    break;
                }
                case BluetoothConnectionManager.BluetoothProgressInterface.ERROR: {
                    Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                    break;
                }
                case BluetoothConnectionManager.BluetoothProgressInterface.READ_MESSAGE: {
                    String networkState = new String((byte[])msg.obj, 0, msg.arg1);
                    Log.d("FROM PI", networkState);
                    switch(networkState) {
                        case BluetoothConnectionManager.BluetoothProgressInterface.NETWORK_SUCCESS: {
                            wifiPagerFragment.updateUI(BluetoothConnectionManager.BluetoothProgressInterface.UI_SUCCESS);
                            break;
                        }
                        case BluetoothConnectionManager.BluetoothProgressInterface.NETWORK_FAIL: {
                            wifiPagerFragment.updateUI(BluetoothConnectionManager.BluetoothProgressInterface.UI_FAILURE);
                            break;
                        }
                    }
                    break;
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create BluetoothConnectionManager
        bluetoothConnectionManager = new BluetoothConnectionManager(getApplicationContext(), bluetoothConnectionHandler);
        // Get User ID from args
        Bundle args = getIntent().getExtras();
        if (args != null) {
            mUserId = args.getString(KEY_USER_ID);
        }
        setContentView(R.layout.activity_printer_setup);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new PrinterSetupPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
        setToolbar();
        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.printer_setup_toolbar);
        toolbar.setTitle("Printer Setup");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void updateUI(int progressCode) {

    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class PrinterSetupPagerAdapter extends FragmentStatePagerAdapter {
        private static final int PAGE_BLUETOOTH_SETUP = 1;
        private static final int PAGE_WIFI_SETUP = 2;

        public PrinterSetupPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            PrinterSetupPageFragment frag = PrinterSetupPageFragment.newInstance(position);
            if (position == PAGE_BLUETOOTH_SETUP) {
                frag.setHandler(bluetoothConnectionHandler);
                bluetoothPagerFragment = frag;
            }
            if (position == PAGE_WIFI_SETUP) {
                frag.setHandler(bluetoothConnectionHandler);
                wifiPagerFragment = frag;
            }
            return frag;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
