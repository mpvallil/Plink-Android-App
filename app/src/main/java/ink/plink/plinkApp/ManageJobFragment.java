package ink.plink.plinkApp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import ink.plink.plinkApp.databaseObjects.Job;
import ink.plink.plinkApp.databaseObjects.Printer;
import ink.plink.plinkApp.networking.NetworkFragment;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnManageJobsFragmentInteractionListener}
 * interface.
 */
public class ManageJobFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnManageJobsFragmentInteractionListener mListener;
    private List<Job> jobsList;

    RecyclerView recyclerView;
    TextView noJobsText;
    ProgressBar progressBar;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ManageJobFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ManageJobFragment newInstance(int columnCount) {
        ManageJobFragment fragment = new ManageJobFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_list, container, false);
        setToolbar(view);
        recyclerView = view.findViewById(R.id.list);
        noJobsText = view.findViewById(R.id.no_jobs_text);
        // Set the adapter
        if (recyclerView != null) {
            Context context = view.getContext();
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            progressBar = view.findViewById(R.id.progressBar);
            getJobsByUserList();
        }
        return view;
    }

    private void getJobsByUserList() {
        NetworkFragment.getGetJobsByUserInstance(getChildFragmentManager()).startDownload();
    }

    private void setToolbar(View v) {
        setHasOptionsMenu(true);
        Toolbar fragmentToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        Toolbar activityToolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        activityToolbar.setVisibility(View.GONE);
        ((AppCompatActivity)getActivity()).setSupportActionBar(fragmentToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentToolbar.setTitle(R.string.nav_drawer_Manage_Jobs);
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
        inflater.inflate(R.menu.toolbar_menu_manage_jobs, menu);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnManageJobsFragmentInteractionListener) {
            mListener = (OnManageJobsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_refresh: {
                showNoJobsText(false);
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(null);
                getJobsByUserList();
            }
            case R.id.action_add_printer: {

            }
        }
        return false;
    }

    public void setJobsList(String jobsListJSON) {
        if (jobsListJSON.length() > 20) {
            jobsList = Job.getJobsList(jobsListJSON);
            recyclerView.setAdapter(new MyJobRecyclerViewAdapter(jobsList, mListener));
        } else {
            showNoJobsText(true);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void showNoJobsText(boolean noJobs) {
        if (noJobs) {
            noJobsText.setVisibility(View.VISIBLE);
        } else {
            noJobsText.setVisibility(View.GONE);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnManageJobsFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Job job);
    }
}
