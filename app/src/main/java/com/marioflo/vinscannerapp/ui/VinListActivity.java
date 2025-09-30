package com.marioflo.vinscannerapp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import com.marioflo.vinscannerapp.R;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marioflo.vinscannerapp.R;
import com.marioflo.vinscannerapp.ui.adapter.SwipeToDeleteCallback;
import com.marioflo.vinscannerapp.ui.adapter.VinInfoAdapter;
import com.marioflo.vinscannerapp.entities.VinInfo;
import com.marioflo.vinscannerapp.entities.VinList;
import com.marioflo.vinscannerapp.viewmodel.VinViewModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * VinListActivity displays all VIN entries in a specific VIN list.
 * <p>
 * Features:
 * - RecyclerView with swipe-to-delete functionality.
 * - Edit list name and delete the entire list.
 * - Share list as an Excel spreadsheet.
 * - Launch CameraActivity to scan and add VINs.
 * <p>
 * Follows MVVM architecture, uses VinViewModel for data handling.
 * Implements reactive UI using LiveData and RecyclerView adapter updates.
 */
public class VinListActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SCAN = 1;

    private VinViewModel vinViewModel;
    private VinInfoAdapter adapter;
    private VinList currentVinList;
    private boolean isNewList;
    private String listName;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_vin_list); // Set the correct layout file here

        setupToolbar();
        setupRecyclerView();
        setupViewModelAndObservers();
        }


    /**
     * Set up Toolbar with title, color, and overflow menu icon color.
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        listName = intent.getStringExtra("listName");
        int listId = intent.getIntExtra("listId", -1);
        isNewList = intent.getBooleanExtra("isNewList", false);

        if (listName != null) {
            getSupportActionBar().setTitle(listName);
        }

        toolbar.setTitleTextColor(Color.WHITE);

        Drawable overflowIcon = toolbar.getOverflowIcon();
        if (overflowIcon != null) {
            overflowIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
    }
    /**
     * Initialize RecyclerView with LinearLayoutManager and VinInfoAdapter.
     * Attach ItemTouchHelper for swipe-to-delete functionality.
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.id_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new VinInfoAdapter(this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter, vinViewModel));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    /**
     * Initialize ViewModel and observe LiveData to update RecyclerView and Toolbar.
     */
    private void setupViewModelAndObservers() {
        vinViewModel = new ViewModelProvider(this).get(VinViewModel.class);

        // Observe VIN entries in the current list
        int listId = getIntent().getIntExtra("listId", -1);
        vinViewModel.getVinInfoForList(listId).observe(this, vinInfos -> adapter.setVinInfos(vinInfos));

        // Observe list metadata to update Toolbar title
        vinViewModel.getVinList(listId).observe(this, vinList -> {
            currentVinList = vinList;
            if (vinList != null) {
                getSupportActionBar().setTitle(vinList.getName());
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_vin_list, menu);

        // Change Delete List item title color to red
        MenuItem deleteItem = menu.findItem(R.id.id_deleteList);
        SpannableString spannableString = new SpannableString(deleteItem.getTitle());
        spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        deleteItem.setTitle(spannableString);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.id_scanIcon) {
            startCameraActivity();
        } else if (id == R.id.id_edit_listName) {
            showEditListNameDialog();
        } else if (id == R.id.id_deleteList) {
            showDeleteConfirmationDialog();
        } else if (id == R.id.id_share_list) {
            shareList();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Launch CameraActivity to scan a VIN for this list.
     */
    private void startCameraActivity() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("listId", currentVinList.getId());
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    /**
     * Share the current list as an Excel spreadsheet.
     */
    private void shareList() {
        List<VinInfo> vinInfos = adapter.getVinInfos();
        if (vinInfos == null || vinInfos.isEmpty()) {
            Toast.makeText(this, "Cannot share an empty list", Toast.LENGTH_SHORT).show();
            return;
        }


        // Create a Excel file
        File excelFile = createExcelFile(vinInfos);

        // Share the Excel file
        if(excelFile != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            Uri uri = FileProvider.getUriForFile(this, "com.yourapp.fileprovider", excelFile);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

            // Set email subject
            String emailSubject = "VinList: " + listName;
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);

            startActivity(Intent.createChooser(shareIntent, "Share List"));
        } else {
            Toast.makeText(this, "Error creating Excel file", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Generate an Excel file for sharing using Apache POI.
     */
    private File createExcelFile(List<VinInfo> vinInfos) {
        File excelFile = null;
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Vin List");

            // Set column widths
            sheet.setColumnWidth(0, 5000); // VIN Number column
            sheet.setColumnWidth(1, 2000); // Location column
            sheet.setColumnWidth(2, 7000); // Extra Notes column

            // Create a bold font for the header row
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);

            // Create a cell style with the bold font
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // Create header row
            Row headerRow = sheet.createRow(0);
            Cell vinCell = headerRow.createCell(0);
            vinCell.setCellValue("VIN");
            vinCell.setCellStyle(headerCellStyle);

            Cell locationCell = headerRow.createCell(1);
            locationCell.setCellValue("Location");
            locationCell.setCellStyle(headerCellStyle);

            Cell notesCell = headerRow.createCell(2);
            notesCell.setCellValue("Notes");
            notesCell.setCellStyle(headerCellStyle);

            // Write data rows
            int rowNum = 1;
            for (VinInfo vinInfo : vinInfos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(vinInfo.getVinNumber());

                String rowLetter = vinInfo.getRowLetter() != null ? vinInfo.getRowLetter() : "";
                String spaceNumber = vinInfo.getSpaceNumber() != null ? vinInfo.getSpaceNumber() : "";
                String location;

                if (!rowLetter.isEmpty() && !spaceNumber.isEmpty()) {
                    location = rowLetter + "-" + spaceNumber;
                } else if (!rowLetter.isEmpty()) {
                    location = rowLetter;
                } else if (!spaceNumber.isEmpty()) {
                    location = spaceNumber;
                } else {
                    location = "";
                }

                row.createCell(1).setCellValue(location);

                String extraNotes = vinInfo.getExtraNotes() != null ? vinInfo.getExtraNotes() : "";
                row.createCell(2).setCellValue(extraNotes);
            }

            // Write the file to external storage
            excelFile = new File(getExternalFilesDir(null), "VinList.xlsx");
            FileOutputStream fileOut = new FileOutputStream(excelFile);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return excelFile;
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

    /**
     * Show a dialog to edit the current VIN list name.
     */
    private void showEditListNameDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_edit_list_name, null);

        // Find the views in the inflated layout
        EditText editText = dialogView.findViewById(R.id.id_edit_list_name);
        Button cancelButton = dialogView.findViewById(R.id.id_btn_cancel);
        Button saveButton = dialogView.findViewById(R.id.id_btn_save);

        // Pre-display the current list name in the EditText
        editText.setText(listName);
        editText.setSelection(listName.length()); // Move cursor to end of text

        // Disable save button initially
        saveButton.setEnabled(false);

        // Text change listener for EditText
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable save button if text is modified
                saveButton.setEnabled(!s.toString().trim().isEmpty() && !s.toString().equals(listName));
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        // Set up the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Edit List Name")
                .setCancelable(false);

        AlertDialog dialog = builder.create();

        // Set the button actions
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        saveButton.setOnClickListener(v -> {
            String newListName = editText.getText().toString();

            // Handle the list name update logic here
            if (!newListName.equals(listName)) {
                currentVinList.setName(newListName);
                vinViewModel.updateVinList(currentVinList);
                getSupportActionBar().setTitle(newListName);
            }

            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }

    /**
     * Show confirmation dialog before deleting the current VIN list.
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Delete List")
                .setMessage("Are you sure you want to delete this list?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (currentVinList != null) {
                        vinViewModel.deleteVinList(currentVinList);
                        Toast.makeText(this, "List deleted", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, isNewList ? MainActivity.class : SavedListsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());

        builder.show();
    }

}



