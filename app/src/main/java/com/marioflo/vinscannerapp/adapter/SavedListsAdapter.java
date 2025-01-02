package com.marioflo.vinscannerapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marioflo.vinscannerapp.R;
import com.marioflo.vinscannerapp.entities.VinList;

import java.util.ArrayList;
import java.util.List;

public class SavedListsAdapter extends RecyclerView.Adapter<SavedListsAdapter.SavedListsViewHolder> {

        private List<VinList> vinLists = new ArrayList<>();
        private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View view, VinList vinList);
    }

    public SavedListsAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SavedListsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vin_list, parent, false);
        return new SavedListsViewHolder(itemView);    }

    @Override
    public void onBindViewHolder(@NonNull SavedListsViewHolder holder, int position) {
        VinList currentVinList = vinLists.get(position);
        holder.textViewName.setText(currentVinList.getName());
    }

    @Override
    public int getItemCount() {
        return vinLists.size();
    }

    public void setVinLists(List<VinList> vinLists) {
        this.vinLists = vinLists;
        notifyDataSetChanged();
    }

    class SavedListsViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;

        public SavedListsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.id_text_listName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(view, vinLists.get(position));
                    }
                }
            });
        }
    }
}
