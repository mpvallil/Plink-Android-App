package ink.plink.plinkApp;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ink.plink.plinkApp.PrinterOwnerFragment.OnPrinterOwnerFragmentInteractionListener;
import ink.plink.plinkApp.databaseObjects.Printer;
import ink.plink.plinkApp.networking.NetworkFragment;

import java.util.List;

/**
 * specified {@link OnPrinterOwnerFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyPrinterOwnerRecyclerViewAdapter extends RecyclerView.Adapter<MyPrinterOwnerRecyclerViewAdapter.ViewHolder> {

    private final List<Printer> mPrinterList;
    private final OnPrinterOwnerFragmentInteractionListener mListener;

    public MyPrinterOwnerRecyclerViewAdapter(List<Printer> printers, OnPrinterOwnerFragmentInteractionListener listener) {
        mPrinterList = printers;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_printerowner, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mPrinter = mPrinterList.get(position);
        holder.mIdView.setText(mPrinterList.get(position).getName());
        holder.mStatus.setText(mPrinterList.get(position).getStatusAsString());
        holder.mContentView.setText(String.format("Price per Print: %.2f", holder.mPrinter.getPrice()));
        holder.mPrinterPriceColor.setText(String.format("Price per Color Print: %.2f", holder.mPrinter.getColorPrice()));
        if (!holder.mPrinter.getStatus()) {
            holder.mPrinterIcon.setImageResource(R.drawable.ic_round_print_disabled_48px);
        }

        holder.mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mListener.onPrinterOwnerFragmentInteraction(holder.mPrinter);
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onPrinterOwnerClickDisplay(holder.mPrinter);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onPrinterOwnerLongClickDisplay(holder.mPrinter);
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
        public final TextView mContentView;
        public final TextView mStatus;
        public final ImageView mPrinterIcon;
        public final TextView mPrinterPriceColor;
        public Printer mPrinter;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPrinterPriceColor = (TextView) view.findViewById(R.id.printer_color_price);
            mIdView = (TextView) view.findViewById(R.id.printer_name);
            mContentView = (TextView) view.findViewById(R.id.content);
            mStatus = (TextView) view.findViewById(R.id.textView_status);
            mPrinterIcon = (ImageView) view.findViewById(R.id.printer_icon);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
