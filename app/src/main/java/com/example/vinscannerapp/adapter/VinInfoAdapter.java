package com.example.vinscannerapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinscannerapp.R;
import com.example.vinscannerapp.entities.VinInfo;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class VinInfoAdapter extends RecyclerView.Adapter<VinInfoAdapter.VinInfoHolder> {

    private List<VinInfo> vinInfos = new ArrayList<>();

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

    }

    @Override
    public int getItemCount() {
        return vinInfos.size();
    }

    public void setVinInfos(List<VinInfo> vinInfos) {
        this.vinInfos = vinInfos;
        notifyDataSetChanged();
    }


    class VinInfoHolder extends RecyclerView.ViewHolder {
        private TextView textViewVinCount;
        private TextView textViewVinNumber;

        public VinInfoHolder(@NonNull View itemView) {
            super(itemView);
            textViewVinCount = itemView.findViewById(R.id.id_vin_count);
            textViewVinNumber = itemView.findViewById(R.id.id_vin_number);
        }
    }

}
