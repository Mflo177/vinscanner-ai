package com.marioflo.vinscannerapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marioflo.vinscannerapp.R;
import com.marioflo.vinscannerapp.ui.adapter.SavedListsAdapter;
import com.marioflo.vinscannerapp.entities.VinList;
import com.marioflo.vinscannerapp.viewmodel.VinViewModel;

import java.util.List;

/**
 * SavedListsActivity displays all VIN lists saved by the user.
 * <p>
 * Responsibilities:
 * 1. Show a list of VIN lists using RecyclerView.
 * 2. Handle click events to navigate to VinListActivity for detailed view.
 * 3. Demonstrates LiveData observation and data-driven UI updates.
 * <p>
 * This activity follows MVVM architecture, using VinViewModel for accessing
 * VIN list data stored in Room database.
 */
public class SavedListsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VinViewModel vinViewModel;
    private SavedListsAdapter adapter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_saved_lists);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.id_rv_saved_lists);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up toolbar with title
        Toolbar toolbar = findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Saved Lists");

        // Initialize adapter with click listener to navigate to VinListActivity
        adapter = new SavedListsAdapter(new SavedListsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, VinList vinList) {
                Intent intent = new Intent(SavedListsActivity.this, VinListActivity.class);
                intent.putExtra("listId", vinList.getId());
                intent.putExtra("listName", vinList.getName());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        // Initialize ViewModel for data access
        vinViewModel = new ViewModelProvider(this).get(VinViewModel.class);

        // Observe LiveData from ViewModel to update UI reactively
        vinViewModel.getAllVinLists().observe(this, new Observer<List<VinList>>() {
            @Override
            public void onChanged(List<VinList> vinLists) {
                if (vinLists != null) {
                    // Update adapter with latest list data
                    adapter.setVinLists(vinLists);
                } else {
                    // Show feedback if no saved lists exist
                    Toast.makeText(SavedListsActivity.this, "No saved lists available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
