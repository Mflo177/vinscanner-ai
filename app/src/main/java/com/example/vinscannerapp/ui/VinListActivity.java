package com.example.vinscannerapp.ui;

import android.app.Activity;
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

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinscannerapp.R;
import com.example.vinscannerapp.adapter.SwipeToDeleteCallback;
import com.example.vinscannerapp.adapter.VinInfoAdapter;
import com.example.vinscannerapp.entities.VinInfo;
import com.example.vinscannerapp.entities.VinList;
import com.example.vinscannerapp.viewmodel.VinViewModel;

import java.util.List;

public class VinListActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SCAN = 1;


    private VinViewModel vinViewModel;
    private VinInfoAdapter adapter;
    private VinList currentVinList;
    private boolean isNewList;

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
        isNewList = intent.getBooleanExtra("isNewList", false);

        if (listName != null) {
            getSupportActionBar().setTitle(listName);
        }

        RecyclerView recyclerView = findViewById(R.id.id_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new VinInfoAdapter(this);
        recyclerView.setAdapter(adapter);

        vinViewModel = new ViewModelProvider(this).get(VinViewModel.class);

        vinViewModel.getVinInfoForList(listId).observe(this, vinInfos -> adapter.setVinInfos(vinInfos));


        vinViewModel.getVinList(listId).observe(this, vinList -> {
            currentVinList = vinList;
            if(vinList != null) {
                getSupportActionBar().setTitle(vinList.getName());
            }
        });

        // Attach ItemTouchHelper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter, vinViewModel));
        itemTouchHelper.attachToRecyclerView(recyclerView);

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
            Intent intent = new Intent(VinListActivity.this, CameraActivity.class);
            intent.putExtra("listId", currentVinList.getId());
            startActivityForResult(intent, REQUEST_CODE_SCAN);
            return true;
        } else if (id == R.id.id_edit_listName) {
            showEditListNameDialog();
        }
        else if (id == R.id.id_deleteList) {
            // Handle delete list click
            showDeleteConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String vinCode = data.getStringExtra("VIN_CODE");
                if (vinCode != null) {
                    // Add the VIN code to the list
                    VinInfo vinInfo = new VinInfo(vinCode, currentVinList.getId());
                    vinViewModel.insertVinInfo(vinInfo);
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent;
        if (isNewList) {
            intent = new Intent(VinListActivity.this, MainActivity.class);
        } else {
            intent = new Intent(VinListActivity.this, SavedListsActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete List");
        builder.setMessage("Are you sure you want to delete this list?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (currentVinList != null) {
                    vinViewModel.deleteVinList(currentVinList);
                    Toast.makeText(VinListActivity.this, "List deleted", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after deletion
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}



