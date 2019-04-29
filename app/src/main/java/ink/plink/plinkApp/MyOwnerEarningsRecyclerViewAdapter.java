package ink.plink.plinkApp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ink.plink.plinkApp.OwnerEarningsFragment.OnOwnerEarningsFragmentInteractionListener;
import ink.plink.plinkApp.databaseObjects.Printer;
import ink.plink.plinkApp.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 */
public class MyOwnerEarningsRecyclerViewAdapter extends RecyclerView.Adapter<MyOwnerEarningsRecyclerViewAdapter.ViewHolder> {

    private final List<Printer> mPrinterList;
    private final OnOwnerEarningsFragmentInteractionListener mListener;

    public MyOwnerEarningsRecyclerViewAdapter(List<Printer> printers, OnOwnerEarningsFragmentInteractionListener listener) {
        mPrinterList = printers;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_owner_earnings, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mPrinter = mPrinterList.get(position);
        holder.mIdView.setText(mPrinterList.get(position).getName());
        holder.mTotalPrice.setText(String.format("Total Earnings Last 30 Days: %.2f ", mPrinterList.get(position).getTotalPriceMonth()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.onPrinterOwnerClickDisplay(holder.mPrinter);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.onPrinterOwnerLongClickDisplay(holder.mPrinter);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPrinterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mTotalPrice;
        public Printer mPrinter;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.printer_name);
            mTotalPrice = (TextView) view.findViewById(R.id.total_price);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mIdView.getText() + "'";
        }
    }
}
