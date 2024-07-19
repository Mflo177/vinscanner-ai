package com.example.vinscannerapp.ui;
import android.app.AlertDialog;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.vinscannerapp.R;
import com.example.vinscannerapp.entities.VinInfo;
import com.example.vinscannerapp.viewmodel.VinViewModel;
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

public class CameraActivity extends AppCompatActivity {
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private int listId;
    private VinViewModel vinViewModel;
    private CameraControl cameraControl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);
        Button captureButton = findViewById(R.id.captureButton);
        cameraExecutor = Executors.newSingleThreadExecutor();

        listId = getIntent().getIntExtra("listId", -1);
        vinViewModel = new ViewModelProvider(this).get(VinViewModel.class);

        captureButton.setOnClickListener(v -> capturePhoto());

        startCamera();

        // Set touch listener for focus
        previewView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                focusOnTap(event.getX(), event.getY());
            }
            return true;
        });

        findViewById(R.id.captureButton).setOnClickListener(v -> capturePhoto());
    }

    private void startCamera() {
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

    private void capturePhoto() {
        if (imageCapture == null) {
            return;
        }

        // Create output options object which contains file + metadata
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(new File(getFilesDir(), "VIN_capture.jpg")).build();

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(outputOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(() -> {
                    processImage();
                });
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                runOnUiThread(() -> {
                    Toast.makeText(CameraActivity.this, "Photo capture failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void processImage() {
        File file = new File(getFilesDir(), "VIN_capture.jpg");
        InputImage image;

        try {
            image = InputImage.fromFilePath(this, Uri.fromFile(file));

            // Barcode scanning
            BarcodeScanner scanner = BarcodeScanning.getClient();
            scanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        boolean vinFound = false;
                        for (Barcode barcode : barcodes) {
                            String rawValue = barcode.getRawValue();
                            if (rawValue != null && rawValue.length() == 17 && rawValue.matches("[A-HJ-NPR-Z0-9]+")) {  //Ask GPT what <-- regex"[write actual code to gpt]"
                                vinFound = true;
                                handleVinCode(rawValue);
                                break;
                            }
                        }
                        if (!vinFound) {
                            // Text recognition as a last resort
                            recognizeText(image);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // If barcode scanning fails, try text recognition
                        recognizeText(image);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recognizeText(InputImage image) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(image)
                .addOnSuccessListener(result -> {
                    for (Text.TextBlock block : result.getTextBlocks()) {
                        String recognizedText = block.getText();
                        if (recognizedText.length() == 17 && recognizedText.matches("[A-HJ-NPR-Z0-9]+")) {
                            handleVinCode(recognizedText);
                            break;
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CameraActivity.this, "Text recognition failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void handleVinCode(String vinCode) {
        // Pass the VIN code back to VinListActivity
        VinInfo vinInfo = new VinInfo(vinCode, listId);
        vinViewModel.insertVinInfo(vinInfo);
        showConfirmationDialog(vinCode);
    }

    private void showConfirmationDialog(String vinCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("VIN Code Added");
        builder.setMessage("The VIN code " + vinCode + " has been successfully added to the list.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

}
