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

import java.util.ArrayList;
import java.util.List;


    public class EditVinActivity extends AppCompatActivity {

        private VinViewModel vinViewModel;
        private TextView vinNumberTextView;
        private Spinner lotLocationSpinner;
        private Spinner spaceNumberSpinner;
        private EditText notesEditText;
        private Button updateButton;
        private Button cancelButton;

        private VinInfo originalVinInfo; // Store the original VinInfo
        private boolean isLotLocationModified = false;
        private boolean isSpaceNumberModified = false; // Track space number changes
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
            spaceNumberSpinner = findViewById(R.id.id_space_number_spinner);
            notesEditText = findViewById(R.id.id_notes_edit_text);
            updateButton = findViewById(R.id.id_update_button);
            cancelButton = findViewById(R.id.id_cancel_button);

            // Disable update button initially
            updateButton.setEnabled(false);

            // Populate the spinners
            populateSpinners();

            // Initialize ViewModel
            vinViewModel = new ViewModelProvider(this).get(VinViewModel.class);

            // Retrieve data from Intent
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra("VIN_INFO_ID")) {
                int vinInfoId = intent.getIntExtra("VIN_INFO_ID", -1);
                vinViewModel.getVinInfoById(vinInfoId).observe(this, vinInfo -> {
                    if (vinInfo != null) {
                        originalVinInfo = vinInfo;
                        prefillFields();
                        setFieldListeners();
                    }
                });

                // Set button actions
                updateButton.setOnClickListener(v -> updateVinInfo());
                cancelButton.setOnClickListener(v -> finish());
            }
        }

        private void populateSpinners() {
            // Populate the lot location spinner
            ArrayAdapter<CharSequence> lotLocationAdapter = ArrayAdapter.createFromResource(this,
                    R.array.row_letter, android.R.layout.simple_spinner_item);
            lotLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lotLocationSpinner.setAdapter(lotLocationAdapter);

            // Populate the space number spinner
            List<String> spaceNumbers = new ArrayList<>();
            spaceNumbers.add("-"); // Default value
            for (int i = 1; i <= 50; i++) {
                spaceNumbers.add(String.valueOf(i));
            }
            ArrayAdapter<String> spaceNumberAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, spaceNumbers);
            spaceNumberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spaceNumberSpinner.setAdapter(spaceNumberAdapter);
        }

        private void prefillFields() {
            vinNumberTextView.setText(originalVinInfo.getVinNumber());
            notesEditText.setText(originalVinInfo.getExtraNotes());

            // Set lot location spinner selection
            ArrayAdapter<CharSequence> lotLocationAdapter = (ArrayAdapter<CharSequence>) lotLocationSpinner.getAdapter();
            if (lotLocationAdapter != null) {
                int lotLocationPosition = lotLocationAdapter.getPosition(originalVinInfo.getRowLetter());
                if (lotLocationPosition >= 0) {
                    lotLocationSpinner.setSelection(lotLocationPosition);
                }
            }

            // Set space number spinner selection
            ArrayAdapter<String> spaceNumberAdapter = (ArrayAdapter<String>) spaceNumberSpinner.getAdapter();
            if (spaceNumberAdapter != null) {
                int spaceNumberPosition = spaceNumberAdapter.getPosition(String.valueOf(originalVinInfo.getSpaceNumber()));
                if (spaceNumberPosition >= 0) {
                    spaceNumberSpinner.setSelection(spaceNumberPosition);
                }
            }
        }
        private void setFieldListeners() {
            lotLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedLotLocation = parent.getItemAtPosition(position).toString();
                    String currentLotLocation = originalVinInfo.getRowLetter() == null ? "-" : originalVinInfo.getRowLetter();
                    isLotLocationModified = !selectedLotLocation.equals(currentLotLocation);
                    checkForModifications();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            spaceNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedSpaceNumber = parent.getItemAtPosition(position).toString();
                    String currentSpaceNumber = originalVinInfo.getSpaceNumber() == null ? "-" : String.valueOf(originalVinInfo.getSpaceNumber());
                    isSpaceNumberModified = !selectedSpaceNumber.equals(currentSpaceNumber);
                    checkForModifications();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            notesEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String currentNotes = originalVinInfo.getExtraNotes() == null ? "" : originalVinInfo.getExtraNotes().trim();
                    isExtraNotesModified = !s.toString().trim().equals(currentNotes);
                    checkForModifications();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        private void checkForModifications() {
            updateButton.setEnabled(isLotLocationModified || isSpaceNumberModified || isExtraNotesModified);
        }

        private void updateVinInfo() {
            if (originalVinInfo != null) {
                // Update the rowLetter (lotLocation) only if modified
                if (isLotLocationModified) {
                    String newLotLocation = lotLocationSpinner.getSelectedItem().toString();
                    originalVinInfo.setRowLetter(newLotLocation.equals("-") ? null : newLotLocation);

                }

                // Update the spaceNumber only if modified
                if (isSpaceNumberModified) {
                    String newSpaceNumber = spaceNumberSpinner.getSelectedItem().toString();
                    originalVinInfo.setSpaceNumber(newSpaceNumber.equals("-") ? null : newSpaceNumber);
                }

                // Update the extraNotes only if modified
                if (isExtraNotesModified) {
                    String newExtraNotes = notesEditText.getText().toString().trim();
                    originalVinInfo.setExtraNotes(newExtraNotes.isEmpty() ? null : newExtraNotes);

                }

                // Save the changes through the ViewModel
                vinViewModel.updateVinInfo(originalVinInfo);
                Toast.makeText(this, "VIN info updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
