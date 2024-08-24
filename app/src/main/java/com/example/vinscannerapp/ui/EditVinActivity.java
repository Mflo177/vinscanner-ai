package com.example.vinscannerapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.vinscannerapp.R;
import com.example.vinscannerapp.entities.VinInfo;
import com.example.vinscannerapp.viewmodel.VinViewModel;

public class EditVinActivity extends AppCompatActivity {

    private VinViewModel vinViewModel;
    private TextView vinNumberTextView;
    private Spinner lotLocationSpinner;
    private EditText notesEditText;
    private Button updateButton;
    private Button cancelButton;

    private VinInfo originalVinInfo; // Store the original VinInfo
    private boolean isLotLocationModified = false;
    private boolean isExtraNotesModified = false;


    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_edit_vin);

        Toolbar toolbar = findViewById(R.id.id_toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update VIN");

        // Initialize UI components
        vinNumberTextView = findViewById(R.id.id_vin_number);
        lotLocationSpinner = findViewById(R.id.id_lot_location_spinner);
        notesEditText = findViewById(R.id.id_notes_edit_text);
        updateButton = findViewById(R.id.id_update_button);
        cancelButton = findViewById(R.id.id_cancel_button);

        // Disable update button initially
        updateButton.setEnabled(false);

        // Populate the spinner with lot locations
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lot_locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lotLocationSpinner.setAdapter(adapter);

        // Initialize ViewModel
        vinViewModel = new ViewModelProvider(this).get(VinViewModel.class);


        // Retrieve data from Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("VIN_INFO_ID")) {
            int vinInfoId = intent.getIntExtra("VIN_INFO_ID", -1);

            // Fetch the VinInfo data using the ViewModel
            vinViewModel.getVinInfoById(vinInfoId).observe(this, vinInfo -> {
                if (vinInfo != null) {
                    originalVinInfo = vinInfo;

                    // Prefill the fields with original VinInfo data
                    vinNumberTextView.setText(originalVinInfo.getVinNumber());
                    notesEditText.setText(originalVinInfo.getExtraNotes());
                    setSpinnerSelection(originalVinInfo.getLotLocation());

                    // Set listeners to detect changes
                    setFieldListeners();
                }
            });

            // Set button actions
            updateButton.setOnClickListener(v -> updateVinInfo());
            cancelButton.setOnClickListener(v -> finish());
        }

    }

    private void setSpinnerSelection(String lotLocation) {
        // Assuming you have a method to set the spinner selection
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) lotLocationSpinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(lotLocation);
            if (position >= 0) {
                lotLocationSpinner.setSelection(position);
            } else {
                Log.d("EditVinActivity", "Lot location not found: " + lotLocation);
            }
        }
    }

    private void setFieldListeners() {
        // Listener for lotLocationSpinner
        lotLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLotLocation = parent.getItemAtPosition(position).toString();
                isLotLocationModified = !selectedLotLocation.equals(originalVinInfo.getLotLocation());
                checkForModifications();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Listener for notesEditText
        notesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Compare trimmed strings to avoid triggering changes due to extra spaces
                isExtraNotesModified = !s.toString().trim().equals(originalVinInfo.getExtraNotes().trim());
                checkForModifications();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void checkForModifications() {
        // Enable the update button only if at least one field is modified
        updateButton.setEnabled(isLotLocationModified || isExtraNotesModified);
    }

    private void updateVinInfo() {
        if (originalVinInfo != null) {
            if (isLotLocationModified) {
                String newLotLocation = lotLocationSpinner.getSelectedItem().toString();
                originalVinInfo.setLotLocation(newLotLocation);
            }

            if (isExtraNotesModified) {
                String newExtraNotes = notesEditText.getText().toString();
                originalVinInfo.setExtraNotes(newExtraNotes);
            }

            vinViewModel.updateVinInfo(originalVinInfo);
            Toast.makeText(this, "VIN info updated", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}