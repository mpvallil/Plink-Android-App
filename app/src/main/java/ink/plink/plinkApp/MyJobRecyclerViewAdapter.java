package ink.plink.plinkApp;

import android.annotation.SuppressLint;
import android.graphics.Color;
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
        holder.mPrice.setText(String.format("Price: $%.2f", mJobs.get(position).getPrice()));
        holder.mJobName.setText(mJobs.get(position).getDocuentTitle());
        holder.mStatus.setText(mJobs.get(position).getStatus());
        switch (mJobs.get(position).getStatus()) {
            case "succeeded": {
                holder.mStatus.setTextColor(Color.parseColor("#00CC00")); //Green
                break;
            }
            case "failed": {
                holder.mStatus.setTextColor(Color.parseColor("#FF0000")); //Red
                break;
            }
        }
        holder.mTime.setText(String.format("Submitted: %s", mJobs.get(position).getTime()));
        holder.mPrinterName.setText(String.format("Printer: %s", mJobs.get(position).getPrinterName()));

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
        public final TextView mStatus;
        public final TextView mPrice;
        public final TextView mTime;
        public final TextView mPrinterName;
        public final TextView mJobName;
        public Job mJob;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mStatus = (TextView) view.findViewById(R.id.textView_job_status);
            mPrice = (TextView) view.findViewById(R.id.job_price);
            mTime = (TextView) view.findViewById(R.id.job_time);
            mPrinterName = (TextView) view.findViewById(R.id.printer_name);
            mJobName = (TextView) view.findViewById(R.id.job_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mStatus.getText() + "'";
        }
    }
}
