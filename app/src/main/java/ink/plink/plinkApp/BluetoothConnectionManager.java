package ink.plink.plinkApp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.itextpdf.text.pdf.qrcode.ByteArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.UUID;

import ink.plink.plinkApp.owner.PrinterSetupPageFragment;

public class BluetoothConnectionManager {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private BluetoothDevice bluetoothDevice;
    private final Handler bluetoothHandler;
    private final String uuidString;
    private BluetoothAdapter bluetoothAdapter;
    private ConnectedThread connectedThread;
    private boolean isConnected = false;

//    public BluetoothConnectionManager(Fragment fragment, BluetoothAdapter bluetoothAdapter) {
//        this.callbackFragment = (PrinterSetupPageFragment)fragment;
//        this.bluetoothAdapter = bluetoothAdapter;
//    }

    public BluetoothConnectionManager(Context context, Handler handler) {
        this.uuidString = context.getString(R.string.iot_uuid);
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothHandler = handler;
    }

    public void startConnection() {
        new ConnectThread(bluetoothDevice).start();
    }

    public void startDiscovery() {
        bluetoothAdapter.startDiscovery();
        bluetoothHandler
                .obtainMessage(BluetoothProgressInterface.DISCOVERY_STARTED)
                .sendToTarget();
        Log.i("BTConnectManager", "Discovery started");
    }

    public void setDeviceToConnect(BluetoothDevice device) {
        this.bluetoothDevice = device;
        startConnection();
    }

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    public interface BluetoothProgressInterface {
        int ERROR = -1;
        int DISCOVERY_STARTED = 0;
        int CONNECTION_CREATED = 1;
        int DEVICE_CONNECTED = 2;
        int READ_MESSAGE = 3;
        int WRITE_MESSAGE = 4;
        int DEVICE_NAME = 5;
        int START_DISCOVERY = 6;
        int UI_SUCCESS = 7;
        int UI_FAILURE = 8;

        long SERIAL_DELAY = 100;

        String NETWORK_SUCCESS = "Connected";
        String NETWORK_FAIL = "Fail";

        // Method to update the UI through the Fragment
        void updateUI(int progressCode);
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;
            Log.e(TAG, "ConnectThread is running");

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                UUID uuid = new UUID(new BigInteger(uuidString.substring(0, 16), 16).longValue(), new BigInteger(uuidString.substring(16), 16).longValue());
                tmp = device.createRfcommSocketToServiceRecord(uuid);
                Log.i("UUID", uuid.toString());
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                Log.e(TAG, "ConnectThread is trying to connect");
                if (!mmSocket.isConnected()) {
                    mmSocket.connect();
                    isConnected = true;
                    bluetoothHandler
                            .obtainMessage(BluetoothProgressInterface.CONNECTION_CREATED)
                            .sendToTarget();
                }
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {

                    mmSocket.close();
                    isConnected = false;
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.

            Log.e(TAG, "ConnectThread is calling ConnectedThread");
            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket mmSocket) {
        connectedThread = new ConnectedThread(mmSocket);
        connectedThread.start();
    }

    public void write(String string) {
        if (!isConnected) {
            return;
        }
        ConnectedThread r;
        synchronized (this) {
            r = connectedThread;
        }
        if (connectedThread != null) {
            Log.d(TAG, "ConnectedThread Writing");
            r.write(string.getBytes());
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
                isConnected = false;
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
                isConnected = false;
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.e(TAG, "ConnectedThread is running");
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = bluetoothHandler.obtainMessage(
                            BluetoothProgressInterface.READ_MESSAGE, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    isConnected = false;
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
                Log.d(TAG, "ConnectedThread write successful");
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);
                isConnected = false;

                // Send a failure message back to the activity.
                Message writeErrorMsg = bluetoothHandler.obtainMessage(
                        BluetoothProgressInterface.ERROR);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                bluetoothHandler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
                isConnected = false;
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

}
