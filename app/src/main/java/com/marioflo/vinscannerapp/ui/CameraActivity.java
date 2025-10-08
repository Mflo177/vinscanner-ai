package com.marioflo.vinscannerapp.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.marioflo.vinscannerapp.R;
import com.marioflo.vinscannerapp.data.entities.VinInfo;
import com.marioflo.vinscannerapp.scanner.VinScanner;
import com.marioflo.vinscannerapp.viewmodel.VinViewModel;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * CameraActivity is responsible for:
 * 1. Capturing VIN images using CameraX.
 * 2. Allowing touch-to-focus on the camera preview.
 * 3. Delegating VIN detection to VinScanner (ML Kit or custom logic).
 * 4. Displaying a dialog for adding additional info before saving.
 * <p>
 * Demonstrates CameraX usage, Android MVVM, and UI interactions for entry-level Android/ML roles.
 */
public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private CameraControl cameraControl;
    private Vibrator vibrator;

    private int listId;
    private VinViewModel vinViewModel;
    private boolean isDialogShown = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Initialize Vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        previewView = findViewById(R.id.previewView);
        Button captureButton = findViewById(R.id.captureButton);
        cameraExecutor = Executors.newSingleThreadExecutor();

        listId = getIntent().getIntExtra("listId", -1);
        vinViewModel = new ViewModelProvider(this).get(VinViewModel.class);

        // Capture button click listener
        captureButton.setOnClickListener(v -> capturePhoto());

        startCamera();

        // Touch-to-focus support
        previewView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                focusOnTap(event.getX(), event.getY());
            }
            return true;
        });

        findViewById(R.id.captureButton).setOnClickListener(v -> capturePhoto());
    }

    /**
     * Initialize CameraX and bind lifecycle.
     */    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // Handle any errors (including cancellation) here.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * Bind camera preview and image capture to lifecycle.
     *
     * @param cameraProvider ProcessCameraProvider instance
     */
    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        imageCapture = new ImageCapture.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        cameraControl = camera.getCameraControl(); // Initialize the CameraControl
    }

    /**
     * Trigger camera focus at the touch location.
     *
     * @param x X coordinate of touch
     * @param y Y coordinate of touch
     */
    private void focusOnTap(float x, float y) {
        if (previewView.getMeteringPointFactory() == null || cameraControl == null) {
            return;
        }

        // Convert touch coordinates to metering point
        MeteringPointFactory factory = previewView.getMeteringPointFactory();
        PointF point = new PointF(x, y);
        MeteringPoint meteringPoint = factory.createPoint(point.x, point.y);

        FocusMeteringAction focusMeteringAction = new FocusMeteringAction.Builder(meteringPoint)
                .setAutoCancelDuration(2, TimeUnit.SECONDS)
                .build();

        cameraControl.startFocusAndMetering(focusMeteringAction)
                .addListener(() -> {
                }, ContextCompat.getMainExecutor(this));
    }

    /** Capture & process **/
    private void capturePhoto() {
        if (imageCapture == null) return;

        File file = new File(getFilesDir(), "VIN_capture.jpg");
        ImageCapture.OutputFileOptions options =
                new ImageCapture.OutputFileOptions.Builder(file).build();

        imageCapture.takePicture(options, cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        runOnUiThread(() -> processImage(file));
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        runOnUiThread(() ->
                                Toast.makeText(CameraActivity.this,
                                        "Photo capture failed: " + exception.getMessage(),
                                        Toast.LENGTH_SHORT).show());
                    }
                });
    }

    /**
     * Process captured image and detect VIN.
     *
     * @param file captured image file
     */
    private void processImage(File file) {
        VinScanner.processImage(this, file, new VinScanner.Callback() {
            @Override
            public void onVinDetected(String vinCode) {
                handleVinCode(vinCode);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(CameraActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Handle a detected VIN by showing a dialog to add details.
     *
     * @param vinCode detected VIN
     */    private void handleVinCode(String vinCode) {
        Log.d(TAG, "VIN detected: " + vinCode);

        // Show a dialog to get additional information before adding the VinInfo
        showVinInfoDialog(vinCode);
    }

    /**
     * Display a dialog to input VIN details before saving to database.
     *
     * @param vinCode detected VIN
     */
    private void showVinInfoDialog(String vinCode) {
        if (isDialogShown) return; // If a dialog is already shown, do nothing

        isDialogShown = true; // Set the flag to true when the dialog is about to be shown

        // Vibrate the device once when a VIN is detected
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_vin_info, null);
        builder.setView(dialogView);

        TextView vinNumberTextView = dialogView.findViewById(R.id.id_vin_number);
        Spinner rowLetterSpinner = dialogView.findViewById(R.id.id_row_letter_spinner);
        EditText notesEditText = dialogView.findViewById(R.id.id_notes_edit_text);
        Button addButton = dialogView.findViewById(R.id.id_add_button);
        Button cancelButton = dialogView.findViewById(R.id.id_cancel_button);
        Spinner spaceNumberSpinner = dialogView.findViewById(R.id.id_space_number_spinner);

        // Set the VIN number
        vinNumberTextView.setText(vinCode);

        setupSpinner(rowLetterSpinner, R.array.row_letter, "-");
        setupSpinner(spaceNumberSpinner, R.array.space_numbers, "-");

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        addButton.setOnClickListener(v -> {
            VinInfo vinInfo = new VinInfo(vinCode, listId);
            vinInfo.setRowLetter(getSpinnerValue(rowLetterSpinner));
            vinInfo.setSpaceNumber(getSpinnerValue(spaceNumberSpinner));
            vinInfo.setExtraNotes(getEditTextValue(notesEditText));

            vinViewModel.insertVinInfo(vinInfo);
            dialog.dismiss();
            showSuccessToast();
            isDialogShown = false;
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
            isDialogShown = false;
        });

        dialog.setOnDismissListener(dialogInterface -> isDialogShown = false);
        dialog.show();
    }

    /**
     * Helper to initialize spinner with default value.
     */
    private void setupSpinner(Spinner spinner, int arrayResId, String defaultValue) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        int position = adapter.getPosition(defaultValue);
        if (position >= 0) spinner.setSelection(position);
    }

    private String getSpinnerValue(Spinner spinner) {
        String value = spinner.getSelectedItem().toString();
        return "-".equals(value) ? null : value;
    }

    private String getEditTextValue(EditText editText) {
        String value = editText.getText().toString().trim();
        return value.isEmpty() ? null : value;
    }

    /**
     * Show a small toast indicating VIN addition success.
     */
    private void showSuccessToast() {
        Toast toast = Toast.makeText(this, "Successfully added VIN", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 200); // Adjust vertical offset if needed
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
