package com.marioflo.vinscannerapp.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marioflo.vinscannerapp.R;
import com.marioflo.vinscannerapp.entities.VinInfo;
import com.marioflo.vinscannerapp.ui.EditVinActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for displaying VIN information in a list.
 * Each row represents a {@link VinInfo} object with details such as VIN number,
 * lot location, space number, and extra notes.
 *
 * <p>This adapter also supports row clicks to edit VIN details
 * and deleting items via an external listener.</p>
 *
 * Usage:
 * - Call {@link #setVinInfos(List)} to populate the adapter with VIN entries.
 * - Implement {@link OnItemDeleteListener} to handle delete events.
 */
public class VinInfoAdapter extends RecyclerView.Adapter<VinInfoAdapter.VinInfoHolder> {

    /** Request code constant for starting EditVinActivity. */
    private static final int REQUEST_CODE_EDIT_VIN = 2; // Declare the request code constant
    private List<VinInfo> vinInfos = new ArrayList<>();
    private Context context;
    private OnItemDeleteListener onItemDeleteListener;

    /**
     * Listener interface for handling delete actions from the adapter.
     */
    public interface OnItemDeleteListener {
        void onItemDelete(VinInfo vinInfo);
    }

    /**
     * Constructor for VinInfoAdapter.
     *
     * @param context The context in which the adapter is being used (e.g., an Activity).
     */
    public VinInfoAdapter(Context context) {
        this.context = context;
    }


    /**
     * Assign a listener for delete actions.
     *
     * @param listener The {@link OnItemDeleteListener} to notify when an item is deleted.
     */
    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.onItemDeleteListener = listener;
    }

    @NonNull
    @Override
    public VinInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View itemView = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.vin_info_item, parent, false);

        return new VinInfoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VinInfoHolder holder, int position) {
        // Get the current VIN entry
        VinInfo currentVinInfo = vinInfos.get(position);

        // Display sequential count (1-based index)
        holder.textViewVinCount.setText(String.valueOf(position + 1));

        // Display sequential count (1-based index)
        holder.textViewVinNumber.setText(currentVinInfo.getVinNumber());


        // Display row letter, defaulting to "-" if null
        String rowLetter = currentVinInfo.getRowLetter();
        holder.textViewLotLocation.setText(rowLetter != null ? rowLetter : "-");

        // Display space number, prefixed with "#" if valid, otherwise "-"
        String spaceNumber = currentVinInfo.getSpaceNumber();
        holder.textViewSpaceNumber.setText(spaceNumber != null && !spaceNumber.equals("-") ? "#" + spaceNumber : "-");

        // Display extra notes if available, otherwise empty
        String extraNotes = currentVinInfo.getExtraNotes();
        holder.textViewExtraNotes.setText(extraNotes != null && !extraNotes.trim().isEmpty() ? extraNotes : "");
    }

    @Override
    public int getItemCount() {
        return vinInfos.size();
    }

    /**
     * Updates the adapter with a new list of VIN entries.
     *
     * @param vinInfos List of {@link VinInfo} objects to display.
     */
    public void setVinInfos(List<VinInfo> vinInfos) {
        this.vinInfos = vinInfos;
        notifyDataSetChanged();
    }

    /**
     * Returns the current list of VIN entries.
     */
    public List<VinInfo> getVinInfos() {
        return vinInfos;
    }


    /**
     * Deletes a VIN entry at a given position and notifies the listener.
     *
     * @param position The adapter position of the VIN to remove.
     */
    public void deleteVinInfo(int position) {
        VinInfo vinInfo = vinInfos.get(position);
        vinInfos.remove(position);
        notifyItemRemoved(position);
        if (onItemDeleteListener != null) {
            onItemDeleteListener.onItemDelete(vinInfo);
        }
    }

    /**
     * Returns the adapter's context (useful for activities and intents).
     */
    public Context getContext() {
        return context;
    }

    /**
     * ViewHolder class that represents each VIN item row in the RecyclerView.
     */
    class VinInfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewVinCount;
        private TextView textViewVinNumber;
        private TextView textViewLotLocation;
        private TextView textViewExtraNotes;
        private TextView textViewSpaceNumber;

        public VinInfoHolder(@NonNull View itemView) {
            super(itemView);

            // Bind UI elements
            textViewVinCount = itemView.findViewById(R.id.id_vin_count);
            textViewVinNumber = itemView.findViewById(R.id.id_vin_number);
            textViewLotLocation = itemView.findViewById(R.id.id_lot_location);
            textViewExtraNotes = itemView.findViewById(R.id.id_extra_notes);
            textViewSpaceNumber = itemView.findViewById(R.id.id_space_num);

            // Register click listener for editing
            itemView.setOnClickListener(this); // Set click listener
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                VinInfo selectedVinInfo = vinInfos.get(position);

                // Launch EditVinActivity with the selected VIN details
                Intent intent = new Intent(context, EditVinActivity.class);
                intent.putExtra("LIST_ID", selectedVinInfo.getListId());
                intent.putExtra("VIN_INFO_ID", selectedVinInfo.getId());

                // Since adapter has only Context, cast to Activity for startActivityForResult
                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, REQUEST_CODE_EDIT_VIN);
                }
            }
        }
    }
}
