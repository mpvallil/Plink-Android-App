package ink.plink.plinkApp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import ink.plink.plinkApp.databaseObjects.Printer;
import ink.plink.plinkApp.networking.NetworkFragment;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrinterSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrinterSettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Callback For MainActivity
    private OnPrinterSettingsInteractionListener mListener;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Printer used for Display
    private Printer mPrinter;

    //Views
    private Switch activePrinterSwitch;
    private Switch locationSwitch;
    private Button buttonSaveChanges;
    private EditText printerPrice;
    private EditText printerPriceColor;
    private Spinner colorSpinner;
    private EditText printerName;

    public PrinterSettingsFragment() {
        // Required empty public constructor
    }

    public void setPrinter(Printer printer) {
        this.mPrinter = printer;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PrinterSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PrinterSettingsFragment newInstance(String param1, String param2) {
        PrinterSettingsFragment fragment = new PrinterSettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PrinterDisplayFragment.OnPrinterDisplayInteractionListener) {
            mListener = (PrinterSettingsFragment.OnPrinterSettingsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_printer_settings, container, false);
        // Find and update the Printer views
        TextView printerNameText = v.findViewById(R.id.textView_printer_name);
        printerPrice = v.findViewById(R.id.editText_set_price);
        printerPriceColor = v.findViewById(R.id.editText_set_price_color);
        colorSpinner = v.findViewById(R.id.spinner);
        locationSwitch = v.findViewById(R.id.switch_set_location);
        printerName = v.findViewById(R.id.textView_printer_name);

        printerPrice.setText(String.format("%.2f", mPrinter.getPrice()));
        printerPriceColor.setText(String.format("%.2f", mPrinter.getColorPrice()));

        printerNameText.setText(mPrinter.getName());
        //Set the toolbar
        setToolbar(v);
        //Hide Keyboard after changing price
        hideKeyboard(v);
        //Set Buttons in View
        setButtons(v);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.toolbar_menu_printer_owner, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setButtons(View v) {
       buttonSaveChanges =  v.findViewById(R.id.button_save_changes);
       buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               saveChanges(v);
           }
       });
        activePrinterSwitch = v.findViewById(R.id.switch_active_printer2);
        activePrinterSwitch.setText(R.string.text_printer_status);
        activePrinterSwitch.setChecked(mPrinter.getStatus());
        activePrinterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mPrinter.setStatus(b);
            }
        });
        colorSpinner.setSelection(mPrinter.getColor() ? 1 : 0);
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private static final int INDEX_COLOR = 1;
            private static final int INDEX_BLACK_AND_WHITE = 0;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPrinter.setColor(i > 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void saveChanges(View v) {
        boolean isValid = true;
        try {
            mPrinter.setPrice(Double.parseDouble(printerPrice.getText().toString()));
            mPrinter.setColorPrice(Double.parseDouble(printerPriceColor.getText().toString()));
            if (locationSwitch.isChecked()) {
                mPrinter.setLocation(GoogleMapsFragment.currentLocation);
            }
            if (mPrinter.getLocation().equals(new LatLng(0.0, 0.0))
                    && activePrinterSwitch.isChecked()
                    && !locationSwitch.isChecked()) {
                isValid = false;
                Toast.makeText(getContext(), "Printer must have a Location before it is Activated!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            isValid = false;
            Toast.makeText(getContext(), "Invalid Price", Toast.LENGTH_LONG).show();
        }
        if (isValid) {
            Toast.makeText(getContext(), "Changes Saved: "+mPrinter.getName(), Toast.LENGTH_LONG).show();
            mListener.onPrinterSettingsSaveInteraction(mPrinter);
            getFragmentManager().popBackStack();
        }
    }

    private void hideKeyboard(View v) {
        v.findViewById(R.id.frameLayout_printer_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        TextView.OnEditorActionListener oeal = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // do something, e.g. set your TextView here via .setText()
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return false;
                }
                return false;
            }
        };
        View.OnFocusChangeListener ofcl = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                } else {
                }
            }
        };

        printerName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    // do something, e.g. set your TextView here via .setText()
                    InputMethodManager imm = (InputMethodManager) printerName.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    mPrinter.setName(printerName.getText().toString());
                    return true;
                }
                return false;
            }
        });
        printerName.setOnFocusChangeListener(ofcl);
        printerPrice.setOnFocusChangeListener(ofcl);
        printerPriceColor.setOnFocusChangeListener(ofcl);

        printerPriceColor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // do something, e.g. set your TextView here via .setText()
                    InputMethodManager imm = (InputMethodManager) printerPriceColor.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        printerPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // do something, e.g. set your TextView here via .setText()
                    InputMethodManager imm = (InputMethodManager) printerPrice.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    private void setToolbar(View v) {
        setHasOptionsMenu(true);
        Toolbar fragmentToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        Toolbar activityToolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        activityToolbar.setVisibility(View.GONE);
        //thisToolbar = fragmentToolbar;
        ((AppCompatActivity)getActivity()).setSupportActionBar(fragmentToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentToolbar.setTitle(R.string.title_printer_edit);
        fragmentToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    public interface OnPrinterSettingsInteractionListener {
        // TODO: Update argument type and name
        void onPrinterSettingsSaveInteraction(Printer printer);
    }
}
