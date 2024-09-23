package com.example.vinscannerapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinscannerapp.R;
import com.example.vinscannerapp.entities.VinInfo;
import com.example.vinscannerapp.ui.EditVinActivity;

import java.util.ArrayList;
import java.util.List;

public class VinInfoAdapter extends RecyclerView.Adapter<VinInfoAdapter.VinInfoHolder> {

    private static final int REQUEST_CODE_EDIT_VIN = 2; // Declare the request code constant
    private List<VinInfo> vinInfos = new ArrayList<>();
    private Context context;
    private OnItemDeleteListener onItemDeleteListener;

    public VinInfoAdapter(Context context) {
        this.context = context;
    }

    public interface OnItemDeleteListener {
        void onItemDelete(VinInfo vinInfo);
    }

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
        VinInfo currentVinInfo = vinInfos.get(position);
        holder.textViewVinCount.setText(String.valueOf(position + 1));
        holder.textViewVinNumber.setText(currentVinInfo.getVinNumber());
        holder.textViewLotLocation.setText(currentVinInfo.getRowLetter());
        holder.textViewExtraNotes.setText(currentVinInfo.getExtraNotes());
    }

    @Override
    public int getItemCount() {
        return vinInfos.size();
    }

    public void setVinInfos(List<VinInfo> vinInfos) {
        this.vinInfos = vinInfos;
        notifyDataSetChanged();
    }

    public List<VinInfo> getVinInfos() {
        return vinInfos;
    }

    public void deleteVinInfo(int position) {
        VinInfo vinInfo = vinInfos.get(position);
        vinInfos.remove(position);
        notifyItemRemoved(position);
        if (onItemDeleteListener != null) {
            onItemDeleteListener.onItemDelete(vinInfo);
        }
    }


    public Context getContext() {
        return context;
    }

    class VinInfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewVinCount;
        private TextView textViewVinNumber;
        private TextView textViewLotLocation;
        private TextView textViewExtraNotes;

        public VinInfoHolder(@NonNull View itemView) {
            super(itemView);
            textViewVinCount = itemView.findViewById(R.id.id_vin_count);
            textViewVinNumber = itemView.findViewById(R.id.id_vin_number);
            textViewLotLocation = itemView.findViewById(R.id.id_lot_location);
            textViewExtraNotes = itemView.findViewById(R.id.id_extra_notes);

            itemView.setOnClickListener(this); // Set click listener
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                VinInfo selectedVinInfo = vinInfos.get(position);
                // Pass data
                Intent intent = new Intent(context, EditVinActivity.class);
                intent.putExtra("LIST_ID", selectedVinInfo.getListId());
                intent.putExtra("VIN_INFO_ID", selectedVinInfo.getId());
                ((Activity) context).startActivityForResult(intent, REQUEST_CODE_EDIT_VIN);
            }


        }
    }

}
