package ink.plink.plinkApp;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ink.plink.plinkApp.ManageJobFragment.OnManageJobsFragmentInteractionListener;
import ink.plink.plinkApp.databaseObjects.Job;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a  and makes a call to the
 * specified {@link OnManageJobsFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyJobRecyclerViewAdapter extends RecyclerView.Adapter<MyJobRecyclerViewAdapter.ViewHolder> {

    private final List<Job> mJobs;
    private final OnManageJobsFragmentInteractionListener mListener;

    public MyJobRecyclerViewAdapter(List<Job> jobs, OnManageJobsFragmentInteractionListener listener) {
        mJobs = jobs;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_job, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mJob = mJobs.get(position);
        holder.mIdView.setText(mJobs.get(position).getPrinterName());
        holder.mContentView.setText(String.format("%.2f", mJobs.get(position).getPrice()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.onListFragmentInteraction(holder.mJob);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mJobs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Job mJob;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
