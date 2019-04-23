package ink.plink.plinkApp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.net.CookieHandler;
import java.net.CookieManager;

import ink.plink.plinkApp.databaseObjects.Job;
import ink.plink.plinkApp.databaseObjects.Printer;
import ink.plink.plinkApp.databaseObjects.User;
import ink.plink.plinkApp.filter.FilterParams;
import ink.plink.plinkApp.networking.DownloadCallback;
import ink.plink.plinkApp.networking.NetworkFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DownloadCallback<String>, SettingsFragment.OnSettingsInteractionListener,
                        GoogleMapsFragment.OnMapsInteractionListener, ManageDocument.OnManageDocumentInteractionListener,
                        PrinterOwnerFragment.OnPrinterOwnerFragmentInteractionListener, PrinterDisplayFragment.OnPrinterDisplayInteractionListener,
                        PrinterFilterFragment.PrinterFilterFragmentListener, ManageJobFragment.OnManageJobsFragmentInteractionListener,
                        PrinterSettingsFragment.OnPrinterSettingsInteractionListener {
    //Bundle arguments
    public static final String KEY_USER = "User Key";
    public static final String KEY_USER_ACCOUNT = "User Account Key";
    public static final String TAG_MANAGE_JOBS_FRAGMENT = "MANAGE ";
    public static User currentSignedInUser;


    /** Fragment references */
    // Fields for naming Fragments from the Nav Menu
    private static final String TAG_GOOGLE_MAPS_FRAG = "GOOGLE_MAPS_FRAG";
    private static final String TAG_PRINTER_OWNER_FRAGMENT = "PRINTER_OWNER_FRAG";
    private static final String TAG_SETTINGS_FRAG = "SETTINGS_FRAG";
    public static final String TAG_PRINTER_SETTINGS_FRAGMENT = "PRINTER_SETTINGS_FRAGMENT";
    private static final String TAG_PRINTER_DISPLAY_FRAG = "PRINTER_DISPLAY_FRAG";
    // References to Nav Menu Fragments
    GoogleMapsFragment mGoogleMapsFragment;
    Fragment currentFragment;
    private FragmentManager fm;
    CookieManager cookieManager;
    private final FragmentManager.OnBackStackChangedListener backStackListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            if (fm.getBackStackEntryCount() > 0) {
                toggle.setDrawerIndicatorEnabled(false);
                Log.i("backStackChanged", "no Map");
            } else {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                findViewById(R.id.my_toolbar).setVisibility(View.VISIBLE);
                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                toggle.setDrawerIndicatorEnabled(true);
                toggle.setToolbarNavigationClickListener(null);
                //mGoogleMapsFragment.setUserVisibleHint(true);
                Log.i("backStackChanged", "Map");
            }
        }
    };

    /** Network Activity fields */
    // Keep a reference to the NetworkFragment, which owns the AsyncTask object
    // that is used to execute network ops.
    private NetworkFragment mNetworkFragment;
    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean mDownloading = false;

    /** Layout fields */
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    ActionBarDrawerToggle toggle;
    MenuItem drawerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get Google User Account
        Bundle args = getIntent().getExtras();
        if (args != null) {
            Log.d("args", "arrived");
            currentSignedInUser = (User) getIntent().getSerializableExtra(KEY_USER);
            currentSignedInUser.setUserAccount((GoogleSignInAccount)args.getParcelable(KEY_USER_ACCOUNT));
        }

        setToolbar();
        setDrawerLayout();

        fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(backStackListener);

        mGoogleMapsFragment = GoogleMapsFragment.newInstance();
        fm
                .beginTransaction()
                .add(R.id.flContent, mGoogleMapsFragment, TAG_GOOGLE_MAPS_FRAG)
                .commit();
        currentFragment = mGoogleMapsFragment;
    }

    private void setDrawerLayout() {
        // Find the DrawerLayout
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                return super.onOptionsItemSelected(item);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                boolean canLockDrawer = true;
                if (drawerItem != null) {
                    int id = drawerItem.getItemId();
                    Fragment newFragment;
                    mGoogleMapsFragment.setUserVisibleHint(false);
                    switch (id) {
                        case R.id.nav_drawer_SendDocument: {
                            newFragment = new ManageJobFragment();
                            fm.beginTransaction()
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .add(R.id.flContent, newFragment, TAG_MANAGE_JOBS_FRAGMENT)
                                    .addToBackStack(null)
                                    .commit();
                            currentFragment = newFragment;
                            break;
                        }
                        case R.id.nav_drawer_Settings: {
                            newFragment = new SettingsFragment();
                            fm.beginTransaction()
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .add(R.id.flContent, newFragment, TAG_SETTINGS_FRAG)
                                    .addToBackStack(null)
                                    .commit();
                            currentFragment = newFragment;
                            break;
                        }
                        case R.id.nav_drawer_PrinterLanding: {
                            newFragment = new PrinterOwnerFragment();
                            mGoogleMapsFragment.setUserVisibleHint(false);
                            fm.beginTransaction()
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .add(R.id.flContent, newFragment, TAG_PRINTER_OWNER_FRAGMENT)
                                    .addToBackStack(null)
                                    .commit();
                            currentFragment = newFragment;
                            break;
                        }
                        case R.id.nav_drawer_About: {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plink.ink/about"));
                            startActivity(browserIntent);
                            canLockDrawer = false;
                            break;
                        }
                        case R.id.nav_drawer_Logout: {
                            startActivity(new Intent(MainActivity.this, SplashActivity.class)
                                    .putExtra(SplashActivity.KEY_SIGN_OUT, currentSignedInUser)
                                    .putExtra(SplashActivity.KEY_USER_ACCOUNT, currentSignedInUser.getUserAccount()));
                            finish();
                            break;
                        }
                        default: {
                            canLockDrawer = false;
                        }
                    }
                    drawerItem = null;
                    if (canLockDrawer) {
                        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    }
                }
            }
        };
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        // Find Nav Drawer and associated elements
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setNavDrawerMenu(currentSignedInUser);
        nvDrawer.setNavigationItemSelectedListener(this);
    }

    private void setNavDrawerMenu(User user) {
        if (user != null) {
            boolean isOwner = user.isOwner();
            if (isOwner) {
                nvDrawer.inflateMenu(R.menu.drawer_view_owner);
            } else {
                nvDrawer.inflateMenu(R.menu.drawer_view_user);
            }
            View nvDrawerHeader = nvDrawer.getHeaderView(0);
            ImageView nvDrawerHeaderImage = nvDrawerHeader.findViewById(R.id.imageView_user_picture);
            TextView nvDrawerHeaderName = nvDrawerHeader.findViewById(R.id.texView_user_name);
            TextView nvDrawerHeaderEmail = nvDrawerHeader.findViewById(R.id.textView_user_email);
            //Set header fields
            Picasso.get().load(user.getUserAccount().getPhotoUrl()).into(nvDrawerHeaderImage);
            nvDrawerHeaderName.setText(currentSignedInUser.getUserAccount().getDisplayName());
            nvDrawerHeaderEmail.setText(currentSignedInUser.getUserAccount().getEmail());
        } else {
            nvDrawer.inflateMenu(R.menu.drawer_view_user);
        }
    }

    private void setToolbar() {
        // Set Toolbar
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        toolbar.setBackground(null);
        getMenuInflater().inflate(R.menu.filter_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter: {
                DialogFragment dialogFragment = new PrinterFilterFragment();
                dialogFragment.show(fm, "filter");
                return false;
            }
            case R.id.action_refresh_main: {
                GoogleMapsFragment mapsfrag = (GoogleMapsFragment) fm.findFragmentByTag(TAG_GOOGLE_MAPS_FRAG);
                mapsfrag.getLocalPrintersRequest();
                return false;
            }
            default: {
                return toggle.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        item.setChecked(false);
        this.drawerItem = item;
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startDownload() {
        if (mNetworkFragment != null && !mDownloading) {
            mNetworkFragment.startDownload();
            mDownloading = true;
        }
    }

    @Override
    public void updateFromDownload(String result, String tag) {
        Log.i("NF tag ", tag);
        switch (tag) {
            case NetworkFragment.URL_GET_LOCAL_PRINTERS: {
                GoogleMapsFragment frag = (GoogleMapsFragment) fm.findFragmentByTag(TAG_GOOGLE_MAPS_FRAG);
                if (frag != null && result.charAt(0) == '[') {
                    frag.getLocalPrinters(result);
                }
                break;
            }

            case NetworkFragment.URL_GET_PRINTERS_BY_OWNER: {
                PrinterOwnerFragment frag = (PrinterOwnerFragment) fm.findFragmentByTag(TAG_PRINTER_OWNER_FRAGMENT);
                if (frag != null) {
                    frag.setPrinterList(result);
                }
                break;
            }

            case NetworkFragment.URL_GET_JOBS_BY_USER: {
                ManageJobFragment frag = (ManageJobFragment) fm.findFragmentByTag(TAG_MANAGE_JOBS_FRAGMENT);
                if (frag != null) {
                    frag.setJobsList(result);
                }
                break;
            }
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        Log.i("onProgressUpdate", "arrived");
        switch(progressCode) {

            case Progress.ERROR:

                break;
            case Progress.CONNECT_SUCCESS:

                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:

                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:

                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:

                break;
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSettingsInteraction(Uri uri) {

    }

    @Override
    public void onMapsInteractionSelectPrinter(Printer printer) {
        PrinterDisplayFragment mPrinterDisplayFragment = new PrinterDisplayFragment();
        mPrinterDisplayFragment.setPrinter(printer);
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.flContent, mPrinterDisplayFragment, TAG_PRINTER_DISPLAY_FRAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onMapsInteractionGetLocalPrinters(LatLng location) {
        mNetworkFragment = NetworkFragment.getGetLocalPrintersInstance(fm, location);
        startDownload();
    }

    @Override
    public void onManageDocumentInteraction(NetworkFragment documentNetworkFragment) {
        mNetworkFragment = documentNetworkFragment;
        startDownload();
    }

    @Override
    public void onPrinterOwnerClickDisplay(Printer printer) {
        PrinterDisplayFragment mPrinterDisplayFragment = new PrinterDisplayFragment();
        mPrinterDisplayFragment.setPrinter(printer);
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.flContent, mPrinterDisplayFragment, TAG_PRINTER_DISPLAY_FRAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPrinterOwnerLongClickDisplay(Printer printer) {
        PrinterSettingsFragment mPrinterSettingsFragment = new PrinterSettingsFragment();
        mPrinterSettingsFragment.setPrinter(printer);
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.flContent, mPrinterSettingsFragment, TAG_PRINTER_SETTINGS_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPrinterOwnerFragmentGetPrinters() {
        mNetworkFragment = NetworkFragment.getGetPrintersByOwnerInstance(fm);
        startDownload();
    }

    @Override
    public void onPrinterDisplayPrintInteraction(Uri uri, String printer_id, String paymentNonce, String amount, int copies, boolean printIsColor) {
        NetworkFragment.getPrintRequestInstance(fm, uri, printer_id, paymentNonce, amount, copies, printIsColor).startDownload();
    }

    @Override
    public void onPrinterFilterPositiveClick(FilterParams params) {
        mGoogleMapsFragment.filterPrinters(params);
    }

    @Override
    public void onPrinterFilterClearClick(DialogFragment dialog) {

    }

    @Override
    public void onGetJobsFragmentInteraction(Job job) {

    }

    @Override
    public void onPrinterSettingsSaveInteraction(Printer printer) {
        NetworkFragment.getUpdatePrinterInstance(fm, printer).startDownload();
    }
}
