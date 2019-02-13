package ink.plink.plinkApp;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Printer used for Display
    private Printer mPrinter;

    //Views
    private Switch activePrinterSwitch;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_printer_settings, container, false);
        // Find and update the Printer views
        TextView printerNameText = v.findViewById(R.id.textView_printer_name);
        TextView printerTypeText = v.findViewById(R.id.textView_printer_price);
        TextView printerStatusText = v.findViewById(R.id.textView_printer_status);
        activePrinterSwitch = v.findViewById(R.id.switch_active_printer);

        printerNameText.setText(mPrinter.getName());
        printerTypeText.setText(mPrinter.getPrinterType());
        if (mPrinter.getStatus()) {
            printerStatusText.setText(R.string.switch_active_printer_text);
        } else {
            printerStatusText.setText(R.string.switch_offline_printer_text);
        }
        //Set the toolbar
        //setToolbar(v);
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
       Button buttonSaveChanges =  v.findViewById(R.id.button_save_changes);
       buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(getContext(), "Changes Saved", Toast.LENGTH_SHORT).show();
               getFragmentManager().popBackStack();
           }
       });
    }

    private void hideKeyboard(View v) {
        v.findViewById(R.id.frameLayout_printer_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) view.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        v.findViewById(R.id.editText2).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                } else {
                }
            }
        });

    }

    private void setToolbar(View v) {
        setHasOptionsMenu(true);
        Toolbar fragmentToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        Toolbar activityToolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        activityToolbar.setVisibility(View.GONE);
        ((AppCompatActivity)getActivity()).setSupportActionBar(fragmentToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentToolbar.setOverflowIcon(getContext().getDrawable(R.drawable.ic_baseline_edit_24px));
        fragmentToolbar.setTitle(R.string.title_printer_settings);
        fragmentToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }
}
