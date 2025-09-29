package com.marioflo.vinscannerapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.marioflo.vinscannerapp.R;
import com.marioflo.vinscannerapp.entities.VinList;
import com.marioflo.vinscannerapp.viewmodel.VinViewModel;


/**
 * MainActivity is the entry point of the VinScanner App.
 * <p>
 * Responsibilities:
 * 1. Display main menu options: create a new VIN list or view saved lists.
 * 2. Handle user interactions for creating a new list.
 * 3. Demonstrates LiveData observation and navigation between Activities.
 */
public class MainActivity extends AppCompatActivity {

    // ViewModel to interact with VIN data (Repository + Room database)
    private VinViewModel vinViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ViewModel for observing and updating VIN lists
        vinViewModel = new ViewModelProvider(this).get(VinViewModel.class);

        // Button to create a new VIN list
        Button createButton = findViewById(R.id.btn_create_new_vin_list);
        // Button to view all saved VIN lists
        Button savedListsButton = findViewById(R.id.btn_view_saved_lists);


        // Set click listener to show the create-list dialog
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateListDialog();
            }
        });

        // Navigate to SavedListsActivity when the saved lists button is clicked
        savedListsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SavedListsActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Displays a dialog for creating a new VIN list.
     * <p>
     * Users enter a name for the new list. Upon creation, the latest list ID is
     * observed via LiveData and the user is navigated to VinListActivity.
     */
    private void showCreateListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_list, null);
        builder.setView(dialogView);

        EditText editTextListName = dialogView.findViewById(R.id.id_enter_listName);
        Button buttonCancel = dialogView.findViewById(R.id.id_btn_cancel);
        Button buttonCreate = dialogView.findViewById(R.id.id_btn_create);


        AlertDialog dialog = builder.create();

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // Create button handles VIN list creation
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String listName = editTextListName.getText().toString();
                if (!listName.isEmpty()) {
                    VinList vinList = new VinList(listName);

                    // Insert the new list into the database via ViewModel
                    vinViewModel.insertVinList(vinList);
                    dialog.dismiss();

                    // Observe LiveData to get the latest list ID for navigation
                    vinViewModel.getAllVinLists().observe(MainActivity.this, vinLists -> {
                        if (vinLists != null && !vinLists.isEmpty()) {
                            VinList latestList = vinLists.get(vinLists.size() - 1);

                            // Navigate to VinListActivity, passing list ID and name
                            Intent intent = new Intent(MainActivity.this, VinListActivity.class);
                            intent.putExtra("listId", latestList.getId());
                            intent.putExtra("listName", latestList.getName()); // Pass the list name
                            intent.putExtra("isNewList", true); // Pass the isNewList flag
                            startActivity(intent);
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "List name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();

    }

}