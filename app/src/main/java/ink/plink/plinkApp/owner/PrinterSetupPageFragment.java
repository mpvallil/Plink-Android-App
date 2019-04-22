package ink.plink.plinkApp.owner;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;

import ink.plink.plinkApp.BluetoothConnectionManager;
import ink.plink.plinkApp.R;

import static ink.plink.plinkApp.MainActivity.currentSignedInUser;


public class PrinterSetupPageFragment extends Fragment {

    public static final String KEY_PAGE_NUM = "Page Number";
    private static final int REQUEST_ENABLE_BT = 70;
    private static final String KEY_HANDLER = "Handler";
    private TextView progressUpdateTextView;
    private ProgressBar progressUpdateBar;
    private String bluetoothDeviceName;
    BluetoothAdapter bluetoothAdapter;
    private Handler bluetoothConnectionHandler;

    public static PrinterSetupPageFragment newInstance(int pageNumber) {
        PrinterSetupPageFragment pageFragment = new PrinterSetupPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PAGE_NUM, pageNumber);
        pageFragment.setArguments(bundle);
        return pageFragment;
    }

    public void setHandler(Handler bluetoothConnectionHandler) {
        this.bluetoothConnectionHandler = bluetoothConnectionHandler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_printer_setup_page, container, false);
        int pageNumber = getArguments().getInt(KEY_PAGE_NUM);
        setViewOnPageNumber(pageNumber, rootView);

        return rootView;
    }

    private void setViewOnPageNumber(int pageNumber, ViewGroup rootView) {
        rootView.findViewById(R.id.layout_page_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });
        TextView textTitle = rootView.findViewById(R.id.page_title);
        TextView textBody = rootView.findViewById(R.id.page_body);
        FrameLayout layout1 = rootView.findViewById(R.id.frame_layout_1);
        FrameLayout layout2 = rootView.findViewById(R.id.frame_layout_2);
        FrameLayout layout4 = rootView.findViewById(R.id.frame_layout_4);
        String pageNum = Integer.toString(pageNumber);
        if (pageNumber < 1) {
            textTitle.setText("Register a Printer");
        } else {
            textTitle.setText("Step " + pageNum);
        }

        switch (pageNumber) {

            case 0: {
                textBody.setText(getString(R.string.text_page_1_bt_setup));
                break;
            }

            case 1: {
                textBody.setText(getString(R.string.text_page_2_bt_setup));
                // Connect to IoT via Bluetooth
                // TODO: Instantiate Bluetooth class and look for IoT device

                progressUpdateTextView = new TextView(getContext());
                progressUpdateBar = new ProgressBar(getContext());
                progressUpdateBar.setIndeterminate(true);
                FrameLayout frameLayout3 = rootView.findViewById(R.id.frame_layout_3);
                layout4.addView(progressUpdateTextView);
                toggleProgressBar(false);
                frameLayout3.addView(progressUpdateBar);

                final EditText bluetoothName = new EditText(getContext());
                bluetoothName.setHint("Device Name");
                bluetoothName.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE);
                bluetoothName.setInputType(InputType.TYPE_CLASS_TEXT);
                bluetoothName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            //TODO: Start Bluetooth Discovery Process and upate UI
                            hideKeyboard(textView);
                            toggleProgressBar(true);
                            bluetoothDeviceName = bluetoothName.getText().toString();
                            PrinterSetupPagerActivity.bluetoothDeviceName = bluetoothDeviceName;
                            bluetoothNameEntered(true);
                            handleBluetoothProcess();
                            handled = true;
                        }
                        return handled;
                    }
                });
                layout1.addView(bluetoothName);
                break;
            }

            case 2: {
                textBody.setText(getString(R.string.text_page_3_bt_setup));

                progressUpdateTextView = new TextView(getContext());
                progressUpdateBar = new ProgressBar(getContext());
                progressUpdateBar.setIndeterminate(true);
                FrameLayout frameLayout3 = rootView.findViewById(R.id.frame_layout_3);
                layout4.addView(progressUpdateTextView);
                toggleProgressBar(false);
                frameLayout3.addView(progressUpdateBar);

                // Set Wifi information
                final EditText wifiName = new EditText(getContext());
                wifiName.setHint("Wifi Name");
                wifiName.setImeActionLabel("Next", EditorInfo.IME_ACTION_NEXT);
                wifiName.setInputType(InputType.TYPE_CLASS_TEXT);
                TextInputLayout textInputLayout1 = new TextInputLayout(getContext());
                layout1.addView(textInputLayout1);
                textInputLayout1.addView(wifiName);

                final EditText wifiPassword = new EditText(getContext());
                wifiPassword.setHint("Password");
                wifiPassword.setImeActionLabel("Send", EditorInfo.IME_ACTION_DONE);
                wifiPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                TextInputLayout textInputLayout = new TextInputLayout(getContext());
                textInputLayout.setPasswordVisibilityToggleEnabled(true);
                layout2.addView(textInputLayout);
                wifiPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            //TODO: Send Wifi creds to IoT
                            hideKeyboard(textView);
                            String ssid = "ssid=\""+wifiName.getText().toString()+"\"";
                            String psk = "psk=\""+wifiPassword.getText().toString()+"\"";
                            bluetoothConnectionHandler
                                    .obtainMessage(BluetoothConnectionManager.BluetoothProgressInterface.WRITE_MESSAGE, ssid).sendToTarget();
                            Message pskMsg = bluetoothConnectionHandler
                                    .obtainMessage(BluetoothConnectionManager.BluetoothProgressInterface.WRITE_MESSAGE, psk);
                            bluetoothConnectionHandler
                                    .sendMessageDelayed(pskMsg, BluetoothConnectionManager.BluetoothProgressInterface.SERIAL_DELAY);
                            Message userMsg = bluetoothConnectionHandler
                                    .obtainMessage(BluetoothConnectionManager.BluetoothProgressInterface.WRITE_MESSAGE, "user_id=\""+currentSignedInUser.getUserAccount().getId()+"\"");
                            bluetoothConnectionHandler
                                    .sendMessageDelayed(userMsg, 2*BluetoothConnectionManager.BluetoothProgressInterface.SERIAL_DELAY);
                            handled = true;
                            updateUI(BluetoothConnectionManager.BluetoothProgressInterface.WRITE_MESSAGE);
                            toggleProgressBar(true);
                        }
                        return handled;
                    }
                });
                textInputLayout.addView(wifiPassword);
                break;
            }

            case 3: {
                textBody.setText(getString(R.string.text_page_4_bt_setup));
                Button doneButton = new Button(getContext());
                doneButton.setText("Done");
                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().finish();
                    }
                });
                layout2.addView(doneButton);
                break;
            }
        }

    }

    private void toggleProgressBar(boolean isVisible) {
        if (isVisible) {
            progressUpdateBar.setVisibility(View.VISIBLE);
        } else {
            progressUpdateBar.setVisibility(View.GONE);
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void bluetoothNameEntered(boolean isNameEntered) {
        if (isNameEntered) {
            
        }
    }

    private void handleBluetoothProcess() {
        Log.i("bt handle", "BT HANDLING");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            startBluetoothConnect();
        }
    }

    private void startBluetoothConnect() {
       bluetoothConnectionHandler
               .obtainMessage(BluetoothConnectionManager.BluetoothProgressInterface.START_DISCOVERY)
               .sendToTarget();
       updateUI(BluetoothConnectionManager.BluetoothProgressInterface.START_DISCOVERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                startBluetoothConnect();
            }
        }
    }

    public void updateUI(int progressCode) {
        switch(progressCode) {
            case BluetoothConnectionManager.BluetoothProgressInterface.START_DISCOVERY: {
                progressUpdateTextView.setText("Device Discovery Started...");
                break;
            }
            case BluetoothConnectionManager.BluetoothProgressInterface.DEVICE_CONNECTED: {
                progressUpdateTextView.setText("Device Found. Connecting...");
                break;
            }
            case BluetoothConnectionManager.BluetoothProgressInterface.CONNECTION_CREATED: {
                progressUpdateTextView.setText("Connected to Device. Swipe to Enter Wifi");
                toggleProgressBar(false);
                break;
            }
            case BluetoothConnectionManager.BluetoothProgressInterface.WRITE_MESSAGE: {
                progressUpdateTextView.setText("Waiting for Network Connection...");
                break;
            }
            case BluetoothConnectionManager.BluetoothProgressInterface.UI_SUCCESS: {
                progressUpdateTextView.setText("Success! Device is connected");
                toggleProgressBar(false);
                break;
            }
            case BluetoothConnectionManager.BluetoothProgressInterface.UI_FAILURE: {
                progressUpdateTextView.setText("Connection Failed. Try Again");
                toggleProgressBar(false);
                break;
            }
        }
    }
}
