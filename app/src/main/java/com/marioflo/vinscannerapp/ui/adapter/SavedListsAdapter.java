package com.marioflo.vinscannerapp.ui.adapter;

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

/**
 * Adapter class for displaying a list of {@link VinList} objects in a {@link RecyclerView}.
 * <p>
 * Handles item click events via the {@link OnItemClickListener} interface
 * and binds VIN list names to the corresponding view holder.
 * </p>
 */
public class SavedListsAdapter extends RecyclerView.Adapter<SavedListsAdapter.SavedListsViewHolder> {

        private List<VinList> vinLists = new ArrayList<>();
        private OnItemClickListener listener;

    /**
     * Interface to handle item click events from the adapter.
     */
    public interface OnItemClickListener {
        void onItemClick(View view, VinList vinList);
    }

    /**
     * Constructs a new {@link SavedListsAdapter}.
     *
     * @param listener The callback for handling item clicks.
     */
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

    /**
     * Updates the list of VIN lists displayed by the adapter.
     *
     * @param vinLists The new list of {@link VinList} objects.
     */
    public void setVinLists(List<VinList> vinLists) {
        this.vinLists = vinLists;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for {@link VinList} items.
     */
    class SavedListsViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;

        public SavedListsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.id_text_listName);

            // Forward click events to the listener, with safety check
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
