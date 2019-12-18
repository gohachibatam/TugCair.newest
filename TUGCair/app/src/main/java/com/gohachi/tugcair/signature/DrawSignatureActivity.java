package com.gohachi.tugcair.signature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gohachi.tugcair.AddPersonActivity;
import com.gohachi.tugcair.DashboardActitvity;
import com.gohachi.tugcair.ListPersonActivity;
import com.gohachi.tugcair.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import io.opencensus.tags.Tag;

public class DrawSignatureActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;

    private FirebaseFirestore mDatabase;

    final String TAG = "DrawSignatureActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(DrawSignatureActivity.this);
        setContentView(R.layout.activity_draw_signature);

        FirestoreInit();

        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
//                Toast.makeText(DrawSignatureActivity.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
                Log.d("DrawSignatureActivity", "OnStartSigning");
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton = (Button) findViewById(R.id.clear_button);
        mSaveButton = (Button) findViewById(R.id.save_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                Toast.makeText(DrawSignatureActivity.this, "Signature Bitmap : "+ signatureBitmap.toString(), Toast.LENGTH_SHORT).show();
                Log.d("DrawSignatureActivity", "Signature Bitmap is : "+ signatureBitmap.toString());
                if (addJpgSignatureToGallery(signatureBitmap)) {
                    Toast.makeText(DrawSignatureActivity.this, "Uploading Data", Toast.LENGTH_SHORT).show();
                    UploadSignatureToCloudStore(signatureBitmap);
                } else {
                    Toast.makeText(DrawSignatureActivity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                }
                if (addSvgSignatureToGallery(mSignaturePad.getSignatureSvg())) {
                    Toast.makeText(DrawSignatureActivity.this, "SVG Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DrawSignatureActivity.this, "Unable to store the SVG signature", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void UploadSignatureToCloudStore(Bitmap signatureBitmap) {
        final String namefile = String.format("Signature_%d.jpg", System.currentTimeMillis());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference mountainImagesRef = storageRef.child("Pictures/"+namefile);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("DrawSignatureActivity", "Fail! upload images. error : "+exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.d("DrawSignatureActivity", "Success upload images");

                addPersonToFirestore(namefile);
            }
        });
    }

    public void addPersonToFirestore(String namefile){

        String NamaLengkap = getIntent().getStringExtra("Fullname");
        String NomorKTP = getIntent().getStringExtra("No_card");
        String AlamatKTP = getIntent().getStringExtra("Address");
        String AlamatNow = getIntent().getStringExtra("Address_now");
        String NomorKontak = getIntent().getStringExtra("No_Phone");
        String LocationCoord = getIntent().getStringExtra("Location_Coord");

        Map<String, Object> user = new HashMap<>();
        user.put("fullname", NamaLengkap);
        user.put("no_card", NomorKTP);
        user.put("address", AlamatKTP);
        user.put("address_now", AlamatNow);
        user.put("no_phone", NomorKontak);
        user.put("locationcoord", LocationCoord);
        user.put("filename_signature", namefile);
        // Add a new document with a generated ID
        mDatabase.collection("persons")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                                                Toast.makeText(AddPersonActivity.this, "Data berhasil ditambahkan! : " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(DrawSignatureActivity.this, "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                        redirectPage(DrawSignatureActivity.this, ListPersonActivity.class);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(DrawSignatureActivity.this, "Error! : "+e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void FirestoreInit(){
        mDatabase = FirebaseFirestore.getInstance();
    }

    private void redirectPage(Activity activity, Class goTo) {
        Intent intent = new Intent(activity, goTo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(DrawSignatureActivity.this, "Cannot write images to external storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        String namefile = String.format("Signature_%d.jpg", System.currentTimeMillis());
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), namefile);
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if(result){
//            includesForUploadFiles(namefile);
//        }
        return result;
    }

    private void includesForUploadFiles(String namefile) throws FileNotFoundException {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        // Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child("Pictures/"+namefile+".jpg");
        mountainImagesRef.getName().equals(mountainImagesRef.getName());    // true
        mountainImagesRef.getPath().equals(mountainImagesRef.getPath());    // false

    }



    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        DrawSignatureActivity.this.sendBroadcast(mediaScanIntent);
    }

    public boolean addSvgSignatureToGallery(String signatureSvg) {
        boolean result = false;
        try {
            File svgFile = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.svg", System.currentTimeMillis()));
            OutputStream stream = new FileOutputStream(svgFile);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(signatureSvg);
            writer.close();
            stream.flush();
            stream.close();
            scanMediaFile(svgFile);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity the activity from which permissions are checked
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
