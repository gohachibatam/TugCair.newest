package com.gohachi.tugcair.signature;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gohachi.tugcair.R;
import com.squareup.picasso.Picasso;

public class ViewDetailActivity extends AppCompatActivity {

    private TextView mFullname, mNoKTP, mAddressKTP, mAddressNow, mContactNo;
    private ImageView mImageSignature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_view_detail);

        String NamaLengkap = getIntent().getStringExtra("Fullname");
        String NomorKTP = getIntent().getStringExtra("Nomor_KTP");
        String AlamatKTP = getIntent().getStringExtra("Alamat_KTP");
        String AlamatNow = getIntent().getStringExtra("Alamat_Now");
        String NomorKontak = getIntent().getStringExtra("Nomor_Kontak");
        String Filename = getIntent().getStringExtra("Filename_signature");

        mFullname = (TextView) findViewById(R.id.txtViewFullName);
        mNoKTP = (TextView) findViewById(R.id.txtViewNoKTP);
        mAddressKTP = (TextView) findViewById(R.id.txtViewAddressKTP);
        mAddressNow = (TextView) findViewById(R.id.txtViewAddressNow);
        mContactNo = (TextView) findViewById(R.id.txtViewContactPerson);
        mImageSignature = (ImageView) findViewById(R.id.imageViewSignature);

        mFullname.setText(NamaLengkap);
        mNoKTP.setText(NomorKTP);
        mAddressKTP.setText(AlamatKTP);
        mAddressNow.setText(AlamatNow);
        mContactNo.setText(NomorKontak);

        String token = "a0d6af09-ba9f-4975-af47-7a818d1233da";
        String link = "https://firebasestorage.googleapis.com/v0/b/tugcair-323e7.appspot.com/o/Pictures%2F" + Filename + "?alt=media&token="+token;
        Picasso.with(ViewDetailActivity.this).load(link).into(mImageSignature);

    }
}
