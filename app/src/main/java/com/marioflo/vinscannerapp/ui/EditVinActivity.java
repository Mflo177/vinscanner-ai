package com.marioflo.vinscannerapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.marioflo.vinscannerapp.R;
import com.marioflo.vinscannerapp.data.entities.VinInfo;
import com.marioflo.vinscannerapp.viewmodel.VinViewModel;


/**
 * Activity to edit details of a single VIN entry.
 * <p>
 * Allows users to update:
 * - Lot location (row letter)
 * - Space number
 * - Extra notes
 * <p>
 * Utilizes ViewModel for MVVM architecture, LiveData observation,
 * and enables/disables the update button based on modifications.
 */
public class EditVinActivity extends AppCompatActivity {

        private VinViewModel vinViewModel;
        private TextView vinNumberTextView;
        private Spinner lotLocationSpinner;
        private Spinner spaceNumberSpinner;
        private EditText notesEditText;
        private Button updateButton;
        private Button cancelButton;

        private VinInfo originalVinInfo; // Original VinInfo loaded from database
        private boolean isLotLocationModified = false;
        private boolean isSpaceNumberModified = false;
        private boolean isExtraNotesModified = false;

        @Override
        protected void onCreate(Bundle savedInstance) {
            super.onCreate(savedInstance);
            setContentView(R.layout.activity_edit_vin);

            setupToolbar();
            initializeUIComponents();
            populateSpinners();
            initializeViewModel();
            loadVinInfoFromIntent();
        }

    /**
     * Set up Toolbar with title.
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.id_toolbar2);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Update Info");
        }
    }

    /**
     * Initialize references to UI components.
     */
    private void initializeUIComponents() {
        vinNumberTextView = findViewById(R.id.id_vin_number);
        lotLocationSpinner = findViewById(R.id.id_lot_location_spinner);
        spaceNumberSpinner = findViewById(R.id.id_space_number_spinner);
        notesEditText = findViewById(R.id.id_notes_edit_text);
        updateButton = findViewById(R.id.id_update_button);
        cancelButton = findViewById(R.id.id_cancel_button);
        updateButton.setEnabled(false); // Disable update until fields are modified
    }

    /**
     * Populate spinners with predefined values from resources.
     */
        private void populateSpinners() {
            // Populate the lot location spinner
            ArrayAdapter<CharSequence> lotLocationAdapter = ArrayAdapter.createFromResource(this,
                    R.array.row_letter, android.R.layout.simple_spinner_item);
            lotLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lotLocationSpinner.setAdapter(lotLocationAdapter);

            // Populate the space number spinner using the string-array from resources
            ArrayAdapter<CharSequence> spaceNumberAdapter = ArrayAdapter.createFromResource(this,
                    R.array.space_numbers, android.R.layout.simple_spinner_item);
            spaceNumberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spaceNumberSpinner.setAdapter(spaceNumberAdapter);
        }

    /**
     * Initialize the ViewModel for accessing and updating VIN data.
     */
    private void initializeViewModel() {
        vinViewModel = new ViewModelProvider(this).get(VinViewModel.class);
    }

    /**
     * Load the VinInfo object passed via Intent and prefill UI fields.
     */
    private void loadVinInfoFromIntent() {
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra("VIN_INFO_ID")) return;

        int vinInfoId = intent.getIntExtra("VIN_INFO_ID", -1);
        vinViewModel.getVinInfoById(vinInfoId).observe(this, vinInfo -> {
            if (vinInfo != null) {
                originalVinInfo = vinInfo;
                prefillFields();
                setFieldListeners();
            }
        });

        // Button actions
        updateButton.setOnClickListener(v -> updateVinInfo());
        cancelButton.setOnClickListener(v -> finish());
    }


    /**
     * Prefill UI fields with data from the original VinInfo object.
     */
    private void prefillFields() {
            vinNumberTextView.setText(originalVinInfo.getVinNumber());
            notesEditText.setText(originalVinInfo.getExtraNotes());

            selectSpinnerItem(lotLocationSpinner, originalVinInfo.getRowLetter());
            selectSpinnerItem(spaceNumberSpinner, originalVinInfo.getSpaceNumber());
        }


    /**
     * Set spinner and text change listeners to track modifications.
     */
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

    /**
     * Enable the update button if any field has been modified.
     */
        private void checkForModifications() {
            updateButton.setEnabled(isLotLocationModified || isSpaceNumberModified || isExtraNotesModified);
        }

    /**
     * Update the VinInfo object if modifications are detected.
     * Saves the updated object via ViewModel.
     */
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

    /**
     * Helper to select a spinner item by its value.
     */
    private void selectSpinnerItem(Spinner spinner, String value) {
        if (value == null) return;
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        if (adapter == null) return;

        int position = adapter.getPosition(value);
        if (position >= 0) spinner.setSelection(position);
    }
    }
