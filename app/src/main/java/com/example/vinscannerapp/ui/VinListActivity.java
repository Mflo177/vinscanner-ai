package com.example.vinscannerapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinscannerapp.R;
import com.example.vinscannerapp.adapter.VinInfoAdapter;
import com.example.vinscannerapp.entities.VinInfo;
import com.example.vinscannerapp.viewmodel.VinViewModel;

import java.util.List;

public class VinListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VinViewModel vinViewModel;
    private VinInfoAdapter adapter;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_vin_list); // Set the correct layout file here

        Toolbar toolbar = findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);

        // Retrieve list name from Intent and set as toolbar title
        Intent intent = getIntent();
        String listName = intent.getStringExtra("listName");
        if (listName != null) {
            getSupportActionBar().setTitle(listName);
        }

        recyclerView = findViewById(R.id.id_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new VinInfoAdapter();
        recyclerView.setAdapter(adapter);

        vinViewModel = new ViewModelProvider(this).get(VinViewModel.class);
        int listId = getIntent().getIntExtra("listId", -1);

        vinViewModel.getVinInfoForList(listId).observe(this, new Observer<List<VinInfo>>() {
            @Override
            public void onChanged(List<VinInfo> vinInfos) {
                adapter.setVinInfos(vinInfos);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_vin_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.id_scanIcon) {
            // Handle scan icon click
            Toast.makeText(this, "Scan Icon Clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.id_deleteList) {
            // Handle delete list click
            Toast.makeText(this, "Delete List Clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}



