package com.example.pocketguard;

import static android.content.ContentValues.TAG;

import static com.example.pocketguard.BudgetActivity.REQUEST_IMAGE_CAPTURE;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.pocketguard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BudgetActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_CODE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri currentImageUri;
    private ImageView imageView;

    private FirebaseFirestore db;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        imageView = findViewById(R.id.imageView);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result) {
                imageView.setImageURI(currentImageUri);
            } else {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnTakeReceiptPhoto = findViewById(R.id.btn_take_receipt_photo);
        btnTakeReceiptPhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(BudgetActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSIONS_CODE);
            } else {
                captureImage();
            }
        });

        Button btnUploadImage = findViewById(R.id.upload);
        btnUploadImage.setOnClickListener(v -> {
            if (currentImageUri != null) {
                uploadImageToFirebaseStorage();
            } else {
                Toast.makeText(BudgetActivity.this, "No image to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void captureImage() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        currentImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (currentImageUri != null) {
            takePictureLauncher.launch(currentImageUri);
        }
    }

    private void uploadImageToFirebaseStorage() {
        if (currentImageUri != null) {
            // Get the default bucket instance
            FirebaseStorage storage = FirebaseStorage.getInstance();

            // Specify the path within the bucket (this assumes you're using the default bucket)
            String fileName = UUID.randomUUID().toString() + ".jpg";
            StorageReference fileRef = storage.getReference().child("images/" + fileName);

            fileRef.putFile(currentImageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(BudgetActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BudgetActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(BudgetActivity.this, "No image to upload", Toast.LENGTH_SHORT).show();
        }
    }

}
