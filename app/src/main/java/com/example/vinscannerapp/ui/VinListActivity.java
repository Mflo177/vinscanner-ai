package com.example.vinscannerapp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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

        Toolbar toolbar = findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);

        // Set the toolbar title text color programmatically (if not done in XML)
        getSupportActionBar().setTitle("Your Title");
        toolbar.setTitleTextColor(Color.WHITE);  // Ensure the title is white

        // Change the 3-dots (overflow menu) icon color to white
        Drawable overflowIcon = toolbar.getOverflowIcon();
        if (overflowIcon != null) {
            // Change the overflow menu icon color to white
            overflowIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }

        // Retrieve list name from Intent and set as toolbar title
        Intent intent = getIntent();
        listName = intent.getStringExtra("listName");
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
        } else if(id == R.id.id_share_list) {
            shareList();
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareList() {
        // Get the list data
        List<VinInfo> vinInfos = adapter.getVinInfos();

        // Check if the list is empty
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

                    // Navigate back to the appropriate activity after deletion
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



