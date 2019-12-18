package com.gohachi.tugcair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdatePersonActivity extends AppCompatActivity {

    private EditText mFullname, mNoKtp, mAddressKtp, mAddressNow, mContactPhone;
    private Button mBtnSubmit;

    private FirebaseFirestore mDatabase;

    final String TAG = "UpdatePersonActivity";
//    String PersonID = getIntent().getStringExtra("Person_Id");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_update_person);

        final String PersonId = getIntent().getStringExtra("Person_Id");
        String NamaLengkap = getIntent().getStringExtra("Nama_Lengkap");
        String NomorKTP = getIntent().getStringExtra("Nomor_KTP");
        String AlamatKTP = getIntent().getStringExtra("Alamat_KTP");
        String AlamatNow = getIntent().getStringExtra("Alamat_Now");
        String NomorKontak = getIntent().getStringExtra("Nomor_KTP");

        this.mFullname = (EditText) findViewById(R.id.txtFullnamez);
        this.mNoKtp = (EditText) findViewById(R.id.txtNoCard);
        this.mAddressKtp = (EditText) findViewById(R.id.txtAddressCard);
        this.mAddressNow = (EditText) findViewById(R.id.txtAddressNow);
        this.mContactPhone = (EditText) findViewById(R.id.txtContactPerson);
        this.mBtnSubmit = (Button) findViewById(R.id.btnSubmit);

        mFullname.setText(NamaLengkap);
        mNoKtp.setText(NomorKTP);
        mAddressKtp.setText(AlamatKTP);
        mAddressNow.setText(AlamatNow);
        mContactPhone.setText(NomorKontak);

        FirestoreInit();

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = mFullname.getText().toString();
                String noktp = mNoKtp.getText().toString();
                String alamat = mAddressKtp.getText().toString();
                String alamatsekarang = mAddressNow.getText().toString();
                String nohp = mContactPhone.getText().toString();

                DocumentReference personRef = mDatabase.collection("persons").document(PersonId);
                personRef
                        .update(
                                "fullname", fullname,
                                "no_card", noktp,
                                "address", alamat,
                                "address_now", alamatsekarang,
                                "no_phone", nohp
                        )
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UpdatePersonActivity.this, "DocumentSnapshot successfully updated!", Toast.LENGTH_SHORT).show();
                                redirectPage(UpdatePersonActivity.this, ListPersonActivity.class);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(UpdatePersonActivity.this, "DocumentSnapshot Fail to updated! error : "+e, Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
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

}
