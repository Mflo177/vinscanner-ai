package com.example.vinscannerapp.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
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
import com.example.vinscannerapp.entities.VinList;
import com.example.vinscannerapp.viewmodel.VinViewModel;

import java.util.List;

public class VinListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VinViewModel vinViewModel;
    private VinInfoAdapter adapter;
    private VinList currentVinList;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_vin_list); // Set the correct layout file here

        Toolbar toolbar = findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);

        // Retrieve list name from Intent and set as toolbar title
        Intent intent = getIntent();
        String listName = intent.getStringExtra("listName");
        int listId = intent.getIntExtra("listId", -1);

        if (listName != null) {
            getSupportActionBar().setTitle(listName);
        }

        recyclerView = findViewById(R.id.id_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new VinInfoAdapter();
        recyclerView.setAdapter(adapter);

        vinViewModel = new ViewModelProvider(this).get(VinViewModel.class);

        vinViewModel.getVinInfoForList(listId).observe(this, new Observer<List<VinInfo>>() {
            @Override
            public void onChanged(List<VinInfo> vinInfos) {
                adapter.setVinInfos(vinInfos);
            }
        });

        vinViewModel.getVinList(listId).observe(this, new Observer<VinList>() {
            @Override
            public void onChanged(VinList vinList) {
                currentVinList = vinList;
                if(vinList != null) {
                    getSupportActionBar().setTitle(vinList.getName());
                }
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
        } else if (id == R.id.id_edit_listName) {
            showEditListNameDialog();
        }
        else if (id == R.id.id_deleteList) {
            // Handle delete list click
            Toast.makeText(this, "Delete List Clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEditListNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit List Name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newListName = input.getText().toString();
                if (!newListName.isEmpty() && currentVinList != null) {
                    currentVinList.setName(newListName);
                    vinViewModel.updateVinList(currentVinList);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

}



