package com.marioflo.vinscannerapp.scanner;

import android.content.Context;
import android.net.Uri;

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


/**
 * VinScanner handles VIN detection from images using ML Kit's Barcode Scanner
 * and Text Recognition APIs. It validates VINs using regex (17-character alphanumeric
 * format without I, O, Q) and returns results via a callback interface.
 */
public class VinScanner {

    private static final String TAG = "VinScanner";

    public interface Callback {
        void onVinDetected(String vinCode);
        void onError(String message);
    }

    /**
     * Processes an image file to detect a VIN code.
     * First tries barcode scanning, then text recognition as a fallback.
     *
     * @param context  The context to access files and resources.
     * @param imageFile The image file to process.
     * @param callback The callback interface for VIN detection or error.
     */
    public static void processImage(Context context, File imageFile, Callback callback) {
        try {
            InputImage image = InputImage.fromFilePath(context, Uri.fromFile(imageFile));

            // First try barcode scanning
            BarcodeScanner scanner = BarcodeScanning.getClient();
            scanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        for (Barcode barcode : barcodes) {
                            String rawValue = barcode.getRawValue();
                            if (isVin(rawValue)) {
                                callback.onVinDetected(rawValue);
                                return;
                            }
                        }
                        // If no VIN barcode found, try text recognition
                        recognizeText(image, callback);
                    })
                    .addOnFailureListener(e -> recognizeText(image, callback));

        } catch (IOException e) {
            callback.onError("Failed to process image: " + e.getMessage());
        }
    }

    private static void recognizeText(InputImage image, Callback callback) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(image)
                .addOnSuccessListener(result -> {
                    for (Text.TextBlock block : result.getTextBlocks()) {
                        String recognizedText = block.getText();
                        if (isVin(recognizedText)) {
                            callback.onVinDetected(recognizedText);
                            return;
                        }
                    }
                    callback.onError("No VIN detected");
                })
                .addOnFailureListener(e -> callback.onError("Text recognition failed: " + e.getMessage()));
    }

    private static boolean isVin(String text) {

        // VIN regex: 17 characters, letters A-H, J-N, P-R, Z, digits 0-9
        return text != null && text.length() == 17 && text.matches("[A-HJ-NPR-Z0-9]+");
    }


}
