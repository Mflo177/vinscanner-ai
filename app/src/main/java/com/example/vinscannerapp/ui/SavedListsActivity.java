package com.example.vinscannerapp.ui;

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

import com.example.vinscannerapp.R;
import com.example.vinscannerapp.adapter.SavedListsAdapter;
import com.example.vinscannerapp.entities.VinList;
import com.example.vinscannerapp.viewmodel.VinViewModel;

import java.util.List;

public class SavedListsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VinViewModel vinViewModel;
    private SavedListsAdapter adapter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_saved_lists);

        recyclerView = findViewById(R.id.id_rv_saved_lists);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Saved Lists");

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

        vinViewModel = new ViewModelProvider(this).get(VinViewModel.class);
        vinViewModel.getAllVinLists().observe(this, new Observer<List<VinList>>() {
            @Override
            public void onChanged(List<VinList> vinLists) {
                if (vinLists != null) {
                    adapter.setVinLists(vinLists);
                } else {
                    Toast.makeText(SavedListsActivity.this, "No saved lists available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
