package in.figos.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PatternMatcher;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.rpc.context.AttributeContext;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {

    // Components
    private ImageButton profile_backBtn;
    private EditText profile_emailET, profile_nameET, profile_phoneET, profile_dobET;
    private Button profile_maleGenderBtn, profile_femaleGenderBtn, profile_vegDietBtn, profile_nonVegDietBtn, profile_gorayaBtn, profile_nakodarBtn, profile_savePersonalDetailsBtn, profile_logoutBtn, profile_deleteAccountBtn;
    private TextView profile_nameTV, profile_emailTV, profile_dobTV;

    // Firebase Components
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseReference mDatabase;

    // Variables
    private boolean mandatoryNotFilled;
    private String phone, gender, diet, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize Components
        profile_backBtn = findViewById(R.id.profile_backBtn);
        profile_emailET = findViewById(R.id.profile_emailET);
        profile_nameET = findViewById(R.id.profile_nameET);
        profile_phoneET = findViewById(R.id.profile_phoneET);
        profile_dobET = findViewById(R.id.profile_dobET);

        profile_maleGenderBtn = findViewById(R.id.profile_maleGenderBtn);
        profile_femaleGenderBtn = findViewById(R.id.profile_femaleGenderBtn);
        profile_vegDietBtn = findViewById(R.id.profile_vegDietBtn);
        profile_nonVegDietBtn = findViewById(R.id.profile_nonVegDietBtn);
        profile_gorayaBtn = findViewById(R.id.profile_gorayaBtn);
        profile_nakodarBtn = findViewById(R.id.profile_nakodarBtn);
        profile_savePersonalDetailsBtn = findViewById(R.id.profile_savePersonalDetailsBtn);
        profile_logoutBtn = findViewById(R.id.profile_logoutBtn);
        profile_deleteAccountBtn = findViewById(R.id.profile_deleteAccountBtn);

        profile_nameTV = findViewById(R.id.profile_nameTV);
        profile_emailTV = findViewById(R.id.profile_emailTV);
        profile_dobTV = findViewById(R.id.profile_dobTV);

        profile_savePersonalDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mandatoryNotFilled = false;
                restoreMandatoryHeadings();

                if (TextUtils.isEmpty(profile_nameET.getText().toString().trim())){
                    profile_nameTV.setText("Name (Required)");
                    profile_nameTV.setTextColor(Color.parseColor("#DE212A"));
                    mandatoryNotFilled = true;
                }

                if (TextUtils.isEmpty(profile_emailET.getText().toString().trim())){
                    profile_emailTV.setText("Email (Required)");
                    profile_emailTV.setTextColor(Color.parseColor("#DE212A"));
                    mandatoryNotFilled = true;
                }

                if (TextUtils.isEmpty(profile_dobET.getText().toString().trim()) || (profile_dobET.getText().toString().trim().length() < 8)){
                    profile_dobTV.setText("Date of Birth (Required - DD/MM/YYYY)");
                    profile_dobTV.setTextColor(Color.parseColor("#DE212A"));
                    mandatoryNotFilled = true;
                } else {
                    String temp = profile_dobET.getText().toString().trim();
                    if (temp.contains("/")){
                        if ((!Pattern.matches("^(0[1-9]|[1-2][0-9]|3[01])\\/(0[1-9]|1[012])\\/\\d{4}$", temp))){
                            profile_dobTV.setText("Date of Birth (Required - DD/MM/YYYY)");
                            profile_dobTV.setTextColor(Color.parseColor("#DE212A"));
                            mandatoryNotFilled = true;
                        } else {
                            profile_dobTV.setText("Date of Birth");
                            profile_dobTV.setTextColor(Color.parseColor("#78716C"));
                        }

                    } else {
                        String newDOB = temp.substring(0,2)+'/'+temp.substring(2,4)+'/'+temp.substring(4);
                        if (!Pattern.matches("^(0[1-9]|[1-2][0-9]|3[01])\\/(0[1-9]|1[012])\\/\\d{4}$", newDOB)) {
                            profile_dobTV.setText("Date of Birth (Required - DD/MM/YYYY)");
                            profile_dobTV.setTextColor(Color.parseColor("#DE212A"));
                            mandatoryNotFilled = true;
                        } else {
                            profile_dobET.setText(newDOB);
                            profile_dobTV.setText("Date of Birth");
                            profile_dobTV.setTextColor(Color.parseColor("#78716C"));
                        }
                    }
                }

                if (!mandatoryNotFilled){

                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    mDatabase.child("users").child(userID).child("name").setValue(profile_nameET.getText().toString().trim());
                    mDatabase.child("users").child(userID).child("email").setValue(profile_emailET.getText().toString().trim());
                    mDatabase.child("users").child(userID).child("dateOfBirth").setValue(profile_dobET.getText().toString().trim());
                    mDatabase.child("users").child(userID).child("gender").setValue(gender);
                    mDatabase.child("users").child(userID).child("diet").setValue(diet);
                    mDatabase.child("users").child(userID).child("location").setValue(location);

                    Toast.makeText(ProfileActivity.this, "Profile updated successfully.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(ProfileActivity.this, "Some details are missing. Please check all fields and try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

        profile_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        profile_maleGenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Male";
                profile_maleGenderBtn.setActivated(true);
                profile_femaleGenderBtn.setActivated(false);
            }
        });

        profile_femaleGenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Female";
                profile_femaleGenderBtn.setActivated(true);
                profile_maleGenderBtn.setActivated(false);
            }
        });

        profile_vegDietBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diet = "Vegetarian";
                profile_vegDietBtn.setActivated(true);
                profile_nonVegDietBtn.setActivated(false);
            }
        });

        profile_nonVegDietBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diet = "Non-Vegetarian";
                profile_nonVegDietBtn.setActivated(true);
                profile_vegDietBtn.setActivated(false);
            }
        });

        profile_gorayaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = "Goraya";
                profile_gorayaBtn.setActivated(true);
                profile_nakodarBtn.setActivated(false);
            }
        });

        profile_nakodarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = "Nakodar";
                profile_nakodarBtn.setActivated(true);
                profile_gorayaBtn.setActivated(false);
            }
        });

        profile_logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        profile_deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date currentTime = Calendar.getInstance().getTime();

                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userDeactivateRequest").setValue("YES");
                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userDeactivateRequestAt").setValue(currentTime.toString());
                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userDeactivateRequestTimestamp").setValue(System.currentTimeMillis());

                FirebaseAuth.getInstance().signOut();
                Toast.makeText(ProfileActivity.this, "Account deactivated. You can re-activate account within 30 days by login, or it will be deleted permanently afterwards.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ProfileActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.undo_from_right, R.anim.undo_to_right);
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        profile_phoneET.setText(user.getPhoneNumber());
        phone = user.getPhoneNumber();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                fetchUserDetails();
            }
        });
    }

    private void fetchUserDetails(){

        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    for (DataSnapshot child : task.getResult().getChildren()) {
                        switch (child.getKey()) {
                            case "name":
                                profile_nameET.setText(child.getValue().toString());
                                break;
                            case "email":
                                profile_emailET.setText(child.getValue().toString());
                                break;
                            case "dateOfBirth":
                                profile_dobET.setText(child.getValue().toString());
                                break;
                            case "gender":
                                gender = child.getValue().toString();
                                break;
                            case "diet":
                                diet = child.getValue().toString();
                                break;
                            case "location":
                                location = child.getValue().toString();
                                break;
                        }
                    }
                    enableBoxes();
                }
            }
        });
    }

    private void restoreMandatoryHeadings(){
        profile_nameTV.setText("Name");
        profile_nameTV.setTextColor(Color.parseColor("#78716C"));
        profile_emailTV.setText("Email");
        profile_emailTV.setTextColor(Color.parseColor("#78716C"));
        profile_dobTV.setText("Date of Birth");
        profile_dobTV.setTextColor(Color.parseColor("#78716C"));
    }

    private void enableBoxes(){

        if (TextUtils.equals(gender, "Male")){
            gender = "Male";
            profile_maleGenderBtn.setActivated(true);
        } else {
            gender = "Female";
            profile_femaleGenderBtn.setActivated(true);
        }

        if (TextUtils.equals(diet, "Vegetarian")){
            diet = "Vegetarian";
            profile_vegDietBtn.setActivated(true);
        } else {
            diet = "Non-Vegetarian";
            profile_nonVegDietBtn.setActivated(true);
        }

        if (TextUtils.equals(location, "Goraya")){
            location = "Goraya";
            profile_gorayaBtn.setActivated(true);
        } else {
            location = "Nakodar";
            profile_nakodarBtn.setActivated(true);
        }
    }
}