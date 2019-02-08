package ink.plink.plinkApp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.dropin.utils.PaymentMethodType;
import com.braintreepayments.api.models.VenmoAccountNonce;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrinterDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrinterDisplayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PRINTER = "param1";
    private static final int REQUEST_CODE_BRAINTREE = 99;

    //Callback For MainActivity
    private OnPrinterDisplayInteractionListener mListener;

    // Request code for getting files off device
    private static final int READ_REQUEST_CODE = 45;

    //Printer Used
    private Printer mPrinter;

    //Buttons in the Display
    private Button printButton;
    private Uri contentUri;
    //private TextView documentNameText;
    private String paymentNonce;

    private View v;

    //Toolbar
    Toolbar thisToolbar;

    //File selected for print
    private File selectedFile;
    private int selectedFilePageCount;

    public PrinterDisplayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment PrinterSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PrinterDisplayFragment newInstance(Printer printer) {
        PrinterDisplayFragment fragment = new PrinterDisplayFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setPrinter(Printer printer) {
        this.mPrinter = printer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PrinterDisplayFragment.OnPrinterDisplayInteractionListener) {
            mListener = (PrinterDisplayFragment.OnPrinterDisplayInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = null;
        if (mPrinter != null) {
            v = inflater.inflate(R.layout.fragment_printer_display, container, false);
            this.v = v;
            //Set the toolbar
            setToolbar(v);
            // Set Display
            setPrinterDisplay(v);
            //Set Keyboard Activity
            hideKeyboard(v);
        }
        return v;
    }

    @SuppressLint("DefaultLocale")
    private void setPrinterDisplay(View v) {
        TextView printerNameText = v.findViewById(R.id.textView_printer_name);
        TextView printerPriceText = v.findViewById(R.id.textView_printer_price);
        TextView printerStatusText = v.findViewById(R.id.textView_printer_status);
        Button chooseDocumentButton = v.findViewById(R.id.button_choose_document);
        TextView pricePerPrintText = v.findViewById(R.id.textView_price_per_print);

        //numberOfPages = v.findViewById(R.id.textView_number_of_pages);
        //totalPrice = v.findViewById(R.id.textView_total_price);
        //numberOfCopies = v.findViewById(R.id.editText_number_of_copies);
        //priceProgressBar = v.findViewById(R.id.progressBar_after_document_selection);
        //layoutSettings = v.findViewById(R.id.tableLayout);
        //layoutPrice = v.findViewById(R.id.layout_price);
        //documentNameText = v.findViewById(R.id.textView_document_name);
        printButton = v.findViewById(R.id.button_print);

        printerNameText.setText(mPrinter.getName());
        //printerTypeText.setText(mPrinter.getPrinterType());
        pricePerPrintText.setText(String.format("%1.2f", mPrinter.getPrice()));
        if (mPrinter.getStatus()) {
            printerStatusText.setText(R.string.switch_active_printer_text);
        } else {
            printerStatusText.setText(R.string.switch_offline_printer_text);
        }
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (true) {
//                    mListener.onPrinterDisplayInteraction(contentUri, mPrinter.getPrinterId());
//                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//                    alert.setMessage("You printed: "+documentNameText.getText()+ " to "+mPrinter.getName())
//                            .setTitle("Print Success!");
//                    alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            switch(which) {
//                                case (DialogInterface.BUTTON_POSITIVE): {
//                                    break;
//                                }
//                            }
//                        }
//                    });
//                    alert.create().show();
//                    documentNameText.setText(getString(R.string.text_no_document));
//                    enablePrintButton(false);
//                    getActivity().getSupportFragmentManager().popBackStack();
//                    contentUri = null;
                    onBraintreeSubmit(v);
                }
            }
        });
        chooseDocumentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });
    }

    private void setToolbar(View v) {
        setHasOptionsMenu(true);
        Toolbar fragmentToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        Toolbar activityToolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        activityToolbar.setVisibility(View.GONE);
        thisToolbar = fragmentToolbar;
        ((AppCompatActivity)getActivity()).setSupportActionBar(fragmentToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentToolbar.setTitle(R.string.title_printer_display);
        fragmentToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.toolbar_menu_printer_display, menu);
    }

    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("application/pdf");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    public void onBraintreeSubmit(View v) {
        @SuppressLint("DefaultLocale") DropInRequest dropInRequest = new DropInRequest()
                .clientToken(MainActivity.currentSignedInUser.getTokenBraintree())
                .amount(((TextView)this.v.findViewById(R.id.textView_total_price)).getText().toString());
        startActivityForResult(dropInRequest.getIntent(getContext()), REQUEST_CODE_BRAINTREE);
    }

    private void showDocumentInformation(int numberOfPages, String documentName) {
        ((TextView)v.findViewById(R.id.textView_document_name)).setText(documentName);
        this.selectedFilePageCount = numberOfPages;
        ((TextView)v.findViewById(R.id.textView_number_of_pages)).setText(Integer.toString(numberOfPages));
        getTotalPrice();
        showLoadingScreenForFile(false);
    }

    private void getTotalPrice() {
        TextView totalPrice = ((TextView)v.findViewById(R.id.textView_total_price));
        if (totalPrice.getText().toString().equals("")) {
            totalPrice.setText(R.string.number_one);
        }
        int copies = Integer.parseInt(((EditText)v.findViewById(R.id.editText_number_of_copies)).getText().toString());
        double totalPriceAmount = selectedFilePageCount * copies * mPrinter.getPrice();
        totalPrice.setText(String.format("%.2f", totalPriceAmount));
    }

    private void hideKeyboard(final View v) {
        v.findViewById(R.id.frameLayout_printer_display).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) view.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        final EditText et = v.findViewById(R.id.editText_number_of_copies);
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    getTotalPrice();
                } else {
                }
            }
        });
        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == 66) {
                    InputMethodManager imm = (InputMethodManager) view.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    et.clearFocus();
                    return true; //this is required to stop sending key event to parent
                }
                return false;
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == REQUEST_CODE_BRAINTREE) {
            if (resultCode == Activity.RESULT_OK) {
                // use the result to update your UI and send the payment method nonce to your server
                DropInResult result = resultData.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                paymentNonce = result.getPaymentMethodNonce().getNonce();
                String deviceData = result.getDeviceData();

                if (result.getPaymentMethodType() == PaymentMethodType.PAY_WITH_VENMO) {
                    VenmoAccountNonce venmoAccountNonce = (VenmoAccountNonce) result.getPaymentMethodNonce();
                    String venmoUsername = venmoAccountNonce.getUsername();
                }
                String amount = ((TextView)this.v.findViewById(R.id.textView_total_price)).getText().toString();
                mListener.onPrinterDisplayPrintInteraction(contentUri, mPrinter.getPrinterId(), paymentNonce, amount);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) resultData.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                contentUri = resultData.getData();
                new DocumentTask().execute(contentUri);
                showLoadingScreenForFile(true);
            }
        }
    }

    private void showLoadingScreenForFile(boolean isLoading) {
        ProgressBar priceProgressBar = v.findViewById(R.id.progressBar_after_document_selection);
        ConstraintLayout layoutSettings = v.findViewById(R.id.tableLayout);
        ConstraintLayout layoutPrice = v.findViewById(R.id.layout_price);
        if(isLoading) {
            layoutSettings.setVisibility(View.INVISIBLE);
            layoutPrice.setVisibility(View.INVISIBLE);
            priceProgressBar.setVisibility(View.VISIBLE);
        } else {
            layoutSettings.setVisibility(View.VISIBLE);
            layoutPrice.setVisibility(View.VISIBLE);
            priceProgressBar.setVisibility(View.GONE);
        }
    }

    private void enablePrintButton(boolean b) {
        if (b) {
            printButton.setAlpha(1.0f);
            printButton.setClickable(true);
        } else {
            printButton.setAlpha(.5f);
            printButton.setClickable(false);
        }
    }

    private class DocumentTask extends AsyncTask<Uri, Integer, DocumentTask.Result> {

        class Result {
            public String documentName;
            public int pageCount;
            public void setDocumentName(String documentName) {
                this.documentName = documentName;
            }
            public void setPageCount(int pageCount) {
                this.pageCount = pageCount;
            }
        }

        @Override
        protected DocumentTask.Result doInBackground(Uri... uris) {
            DocumentTask.Result dtr = new DocumentTask.Result();
            dtr.setDocumentName(dumpFileMetaData(contentUri));
            try {
                dtr.setPageCount(efficientPDFPageCount(getActivity().getContentResolver().openInputStream(contentUri)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return dtr;
        }

        @Override
        protected void onPostExecute(DocumentTask.Result result) {
            enablePrintButton(true);
            showDocumentInformation(result.pageCount, result.documentName);
        }

        private int efficientPDFPageCount(InputStream is) {
            RandomAccessFile raf = null;
            PdfReader reader = null;
            try {
                //raf = new RandomAccessFile(file, "r");
                //RandomAccessFileOrArray pdfFile = new RandomAccessFileOrArray(
                //        new RandomAccessSourceFactory().createSource(raf));
                reader = new PdfReader(is, new byte[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
            return reader.getNumberOfPages();
        }

        public String dumpFileMetaData(Uri uri) {

            String displayName = "No Document";
            // The query, since it only applies to a single document, will only return
            // one row. There's no need to filter, sort, or select fields, since we want
            // all fields for one document.
            ContentResolver cr = getActivity().getContentResolver();
            Cursor cursor = cr
                    .query(uri, null, null, null, null, null);

            try {
                // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
                // "if there's anything to look at, look at it" conditionals.
                if (cursor != null && cursor.moveToFirst()) {

                    // Note it's called "Display Name".  This is
                    // provider-specific, and might not necessarily be the file name.
                    displayName = cursor.getString(
                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    Log.i("ManageDocument", "Display Name: " + displayName);
                    //
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    // If the size is unknown, the value stored is null.  But since an
                    // int can't be null in Java, the behavior is implementation-specific,
                    // which is just a fancy term for "unpredictable".  So as
                    // a rule, check if it's null before assigning to an int.  This will
                    // happen often:  The storage API allows for remote files, whose
                    // size might not be locally known.
                    String size = null;
                    if (!cursor.isNull(sizeIndex)) {
                        // Technically the column stores an int, but cursor.getString()
                        // will do the conversion automatically.
                        size = cursor.getString(sizeIndex);
                    } else {
                        size = "Unknown";
                    }
                    Log.i("ManageDocument", "Size: " + size);
                }
            } finally {
                cursor.close();
            }
            return displayName;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPrinterDisplayInteractionListener {
        // TODO: Update argument type and name
        void onPrinterDisplayPrintInteraction(Uri uri, String printer_id, String paymentNonce, String amount);
    }
}
