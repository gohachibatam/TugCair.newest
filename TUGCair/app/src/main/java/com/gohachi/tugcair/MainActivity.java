package com.gohachi.tugcair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText mUsername, mPassword;
    private Button mBtnSignIn;
    private boolean loggedIn;
    private boolean isGPS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        setContentView(R.layout.activity_main);

        mUsername = findViewById(R.id.txtUsername);
        mPassword = findViewById(R.id.txtPassword);
        mBtnSignIn = findViewById(R.id.btnSignIn);

        // Buttons
//        findViewById(R.id.btnSignIn).setOnClickListener(this);
//        findViewById(R.id.btnSignUp).setOnClickListener(this);

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        FirebaseInit();

        loggedIn = isLoggedIn();
        if (loggedIn) {
            //  go to dashboard
            goToDashboard();
        }

        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if (username.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Username is Empty!", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Password is Empty!", Toast.LENGTH_SHORT).show();
                } else if (username.isEmpty() && password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(username.isEmpty() && password.isEmpty())) {
                    showProgress();
                    mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            hideProgress();

                            showMessageBox("hallo"+String.valueOf(task.isSuccessful()));

                            if (task.isSuccessful()) {
                                //  login sucess
                                //  progressbar
                                //  timeout
                                //  go to dashboard
                                goToDashboard();
                            } else {
                                //  login failed
                                showMessageBox("Login failed. Please check username and password!");
                            }
                        }
                    });
                }
            }
        });

//        loggedIn = isLoggedIn();
//        if (loggedIn) {
//            //  go to dashboard
////            goToDashboard();
//        }

    }

    private void showMessageBox(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Login");
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void FirebaseInit() {
        mAuth = FirebaseAuth.getInstance();
    }

    public boolean isLoggedIn() {
        if (mAuth.getCurrentUser() != null) {
            //  user logged in
            return true;
        } else {
            return false;
        }
    }

    private void goToDashboard() {
        Intent intent = new Intent(MainActivity.this, DashboardActitvity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void hideProgress() {
//        RelativeLayout.setVisibility(View.GONE);
        mUsername.setEnabled(true);
        mPassword.setEnabled(true);
    }

    private void showProgress() {
//        RelativeLayout.setVisibility(View.VISIBLE);
        mUsername.setEnabled(false);
        mPassword.setEnabled(false);
    }
}
