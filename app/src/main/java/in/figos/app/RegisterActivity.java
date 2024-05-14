package in.figos.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    // Components
    private EditText register_phoneET, register_otpET;
    private Button register_proceedBtn, register_registerBtn, register_resendOTPBtn, register_loginBtn;
    private TextView register_notificationMessage, register_registerPageTV;
    private RelativeLayout register_parentRV, register_progressBarLayout;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Global Variables
    private String phone, otp, mVerificationId;
    private boolean registerMode, verificationInProgress;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize Components
        register_parentRV = findViewById(R.id.register_parentRV);
        register_registerPageTV = findViewById(R.id.register_registerPageTV);
        register_phoneET = findViewById(R.id.register_phoneET);
        register_otpET = findViewById(R.id.register_otpET);
        register_proceedBtn = findViewById(R.id.register_proceedBtn);
        register_registerBtn = findViewById(R.id.register_registerBtn);
        register_resendOTPBtn = findViewById(R.id.register_resendOTPBtn);
        register_loginBtn = findViewById(R.id.register_loginBtn);
        register_notificationMessage = findViewById(R.id.register_notificationMessage);
        register_progressBarLayout = findViewById(R.id.register_progressBarLayout);

        registerMode = false;

        if (verificationInProgress){
            reVerify();
        }

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w("CREATED", "onVerificationFailed", e);
                Toast.makeText(RegisterActivity.this, "Some problem with phone number, try again later", Toast.LENGTH_LONG).show();
                verificationInProgress = false;
                register_phoneET.setEnabled(true);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Toast.makeText(RegisterActivity.this, "Verification OTP has been sent. Please fill in when arrives", Toast.LENGTH_LONG).show();

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                register_phoneET.setEnabled(false);
                register_proceedBtn.setVisibility(View.GONE);
                register_otpET.setVisibility(View.VISIBLE);
                register_registerBtn.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (verificationInProgress){
                            register_resendOTPBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }, 60000);
            }
        };

        register_phoneET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                register_notificationMessage.setVisibility(View.GONE);
                if (Pattern.matches("^\\d{10}$", register_phoneET.getText().toString().trim())){
                    register_proceedBtn.setEnabled(true);
                } else {
                    register_proceedBtn.setEnabled(false);
                }
                register_otpET.setVisibility(View.GONE);
                register_registerBtn.setVisibility(View.GONE);
                register_otpET.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        register_proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!register_checkInternet()){
                    Toast.makeText(RegisterActivity.this, "Please connect to internet.", Toast.LENGTH_LONG).show();
                    return;
                }
                phone = "+91"+register_phoneET.getText().toString().trim();

                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(phone)       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(RegisterActivity.this)                 // Activity (for callback binding)
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
                verificationInProgress = true;

            }
        });

        register_registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!register_checkInternet()){
                    Toast.makeText(RegisterActivity.this, "Please connect to internet.", Toast.LENGTH_LONG).show();
                    return;
                }

                otp = register_otpET.getText().toString().trim();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
                signInWithPhoneAuthCredential(credential);

            }
        });

        register_loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerMode = !registerMode;
                if (registerMode){
                    register_parentRV.setBackground(getResources().getDrawable(R.drawable.gfx_back_signup, getApplication().getTheme()));
                    register_loginBtn.setText(getResources().getString(R.string.have_account_sign_in));
                    register_registerPageTV.setText(getResources().getString(R.string.sign_up));
                    register_registerBtn.setText(getResources().getString(R.string.create_account));
                } else {
                    register_parentRV.setBackground(getResources().getDrawable(R.drawable.gfx_back, getApplication().getTheme()));
                    register_loginBtn.setText(getResources().getString(R.string.sign_up));
                    register_registerPageTV.setText(getResources().getString(R.string.login));
                    register_registerBtn.setText(getResources().getString(R.string.login));
                }
            }
        });

        register_resendOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reVerify();
                register_resendOTPBtn.setVisibility(View.GONE);
            }
        });

    }

    private boolean register_checkInternet(){
        ConnectivityManager cm = (ConnectivityManager) RegisterActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("verificationInProgress", verificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        verificationInProgress = savedInstanceState.getBoolean("verificationInProgress");
    }

    private void reVerify(){
        phone = "+91"+register_phoneET.getText().toString().trim();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(RegisterActivity.this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        verificationInProgress = false;
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final Date currentTime = Calendar.getInstance().getTime();

                            final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            mDatabase.child("users").child(userID).child("userCreatedAt").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (task.getResult().getValue() == null){
                                            mDatabase.child("users").child(userID).child("userCreatedAt").setValue(currentTime.toString());
                                            mDatabase.child("users").child(userID).child("userCreatedTimestamp").setValue(System.currentTimeMillis());
                                        }
                                    }
                                }
                            });

                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            if (task.isSuccessful()) {
                                                String token = task.getResult();
                                                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("token").setValue(token);
                                            }
                                        }
                                    });

                            mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (DataSnapshot child : task.getResult().getChildren()) {
                                            switch (child.getKey()) {
                                                case "userDeactivateRequest":
                                                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userDeactivateRequest").setValue(null);
                                                    break;
                                                case "userDeactivateRequestAt":
                                                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userDeactivateRequestAt").setValue(null);
                                                    break;
                                                case "userDeactivateRequestTimestamp":
                                                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userDeactivateRequestTimestamp").setValue(null);
                                                    break;
                                            }
                                        }
                                    }
                                }
                            });

                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                            finish();

                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(RegisterActivity.this, "You entered wrong verification code, please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onResume() {

        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
            finish();
        }
    }
}