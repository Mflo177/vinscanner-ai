package com.example.vinscannerapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vinscannerapp.R;
import com.example.vinscannerapp.entities.VinList;
import com.example.vinscannerapp.viewmodel.VinViewModel;

public class MainActivity extends AppCompatActivity {

    private VinViewModel vinViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vinViewModel = new ViewModelProvider(this).get(VinViewModel.class);

        Button createButton = findViewById(R.id.btn_create_new_vin_list);
        Button savedListsButton = findViewById(R.id.btn_view_saved_lists);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateListDialog();
            }
        });

        savedListsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SavedListsActivity.class);
                startActivity(intent);
            }
        });
    }

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

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String listName = editTextListName.getText().toString();
                if (!listName.isEmpty()) {
                    VinList vinList = new VinList(listName);
                    vinViewModel.insertVinList(vinList);
                    dialog.dismiss();

                    // Observe the insertion result to get the latest list ID and navigate
                    vinViewModel.getAllVinLists().observe(MainActivity.this, vinLists -> {
                        if (vinLists != null && !vinLists.isEmpty()) {
                            VinList latestList = vinLists.get(vinLists.size() - 1);
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