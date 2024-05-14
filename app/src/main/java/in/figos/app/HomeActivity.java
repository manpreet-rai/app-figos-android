package in.figos.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class HomeActivity extends AppCompatActivity {

    // Components
    private ImageButton home_profileBtn, home_menuBtn;
    private EditText home_nameET, home_emailET, home_dobET;
    private Button home_maleGenderBtn, home_femaleGenderBtn, home_vegDietBtn, home_nonVegDietBtn, home_gorayaBtn, home_nakodarBtn, home_callNowBtn, home_orderOnlineBtn, home_specialCallNowBtn, home_specialOrderOnlineBtn, home_savePersonalDetailsBtn, home_aboutUsBtn;
    private TextView home_notificationLabel, home_nameTV, home_emailTV, home_dobTV;
    private ScrollView home_personalInfoSV;
    private RelativeLayout home_dailyOfferLayout, home_specialOfferLayout;
    private ImageView home_dailyOfferImage, home_specialOfferImage;

    // Firebase Components
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Variables
    private String gender, diet, location;
    private boolean mandatoryNotFilled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize Components
        home_profileBtn = findViewById(R.id.home_profileBtn);
        home_menuBtn = findViewById(R.id.home_menuBtn);
        home_personalInfoSV = findViewById(R.id.home_personalInfoSV);

        home_nameET = findViewById(R.id.home_nameET);
        home_emailET = findViewById(R.id.home_emailET);
        home_dobET = findViewById(R.id.home_dobET);
        home_maleGenderBtn = findViewById(R.id.home_maleGenderBtn);
        home_femaleGenderBtn = findViewById(R.id.home_femaleGenderBtn);
        home_vegDietBtn = findViewById(R.id.home_vegDietBtn);
        home_nonVegDietBtn = findViewById(R.id.home_nonVegDietBtn);
        home_gorayaBtn = findViewById(R.id.home_gorayaBtn);
        home_nakodarBtn = findViewById(R.id.home_nakodarBtn);
        home_savePersonalDetailsBtn = findViewById(R.id.home_savePersonalDetailsBtn);
        home_notificationLabel = findViewById(R.id.home_notificationLabel);
        home_nameTV = findViewById(R.id.home_nameTV);
        home_emailTV = findViewById(R.id.home_emailTV);
        home_dobTV = findViewById(R.id.home_dobTV);

        home_dailyOfferLayout = findViewById(R.id.home_dailyOfferLayout);
        home_dailyOfferImage = findViewById(R.id.home_dailyOfferImage);
        home_callNowBtn = findViewById(R.id.home_callNowBtn);
        home_orderOnlineBtn = findViewById(R.id.home_orderOnlineBtn);

        home_specialOfferLayout = findViewById(R.id.home_specialOfferLayout);
        home_specialOfferImage = findViewById(R.id.home_specialOfferImage);
        home_specialCallNowBtn = findViewById(R.id.home_specialCallNowBtn);
        home_specialOrderOnlineBtn = findViewById(R.id.home_specialOrderOnlineBtn);
        home_aboutUsBtn = findViewById(R.id.home_aboutUsBtn);

        home_profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                overridePendingTransition(R.anim.from_right, R.anim.to_right);
            }
        });

        home_menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AboutActivity.class));
                overridePendingTransition(R.anim.from_right, R.anim.to_right);
            }
        });

        home_savePersonalDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mandatoryNotFilled = false;
                restoreMandatoryHeadings();

                if (TextUtils.isEmpty(home_nameET.getText().toString().trim())){
                    home_nameTV.setText("Name (Required)");
                    home_nameTV.setTextColor(Color.parseColor("#DE212A"));
                    mandatoryNotFilled = true;
                }

                if (TextUtils.isEmpty(home_emailET.getText().toString().trim())){
                    home_emailTV.setText("Email (Required)");
                    home_emailTV.setTextColor(Color.parseColor("#DE212A"));
                    mandatoryNotFilled = true;
                }

                if (TextUtils.isEmpty(home_dobET.getText().toString().trim()) || (home_dobET.getText().toString().trim().length() < 8)){
                    home_dobTV.setText("Date of Birth (Required - DD/MM/YYYY)");
                    home_dobTV.setTextColor(Color.parseColor("#DE212A"));
                    mandatoryNotFilled = true;
                } else {
                    String temp = home_dobET.getText().toString().trim();
                    if (temp.contains("/")){
                        if ((!Pattern.matches("^(0[1-9]|[1-2][0-9]|3[01])\\/(0[1-9]|1[012])\\/\\d{4}$", temp))){
                            home_dobTV.setText("Date of Birth (Required - DD/MM/YYYY)");
                            home_dobTV.setTextColor(Color.parseColor("#DE212A"));
                            mandatoryNotFilled = true;
                        } else {
                            home_dobTV.setText("Date of Birth");
                            home_dobTV.setTextColor(Color.parseColor("#78716C"));
                        }

                    } else {
                        String newDOB = temp.substring(0,2)+'/'+temp.substring(2,4)+'/'+temp.substring(4);
                        if (!Pattern.matches("^(0[1-9]|[1-2][0-9]|3[01])\\/(0[1-9]|1[012])\\/\\d{4}$", newDOB)) {
                            home_dobTV.setText("Date of Birth (Required - DD/MM/YYYY)");
                            home_dobTV.setTextColor(Color.parseColor("#DE212A"));
                            mandatoryNotFilled = true;
                        } else {
                            home_dobET.setText(newDOB);
                            home_dobTV.setText("Date of Birth");
                            home_dobTV.setTextColor(Color.parseColor("#78716C"));
                        }
                    }
                }

                if (!mandatoryNotFilled){

                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    mDatabase.child("users").child(userID).child("name").setValue(home_nameET.getText().toString().trim());
                    mDatabase.child("users").child(userID).child("email").setValue(home_emailET.getText().toString().trim());
                    mDatabase.child("users").child(userID).child("dateOfBirth").setValue(home_dobET.getText().toString().trim());
                    mDatabase.child("users").child(userID).child("gender").setValue(gender);
                    mDatabase.child("users").child(userID).child("diet").setValue(diet);
                    mDatabase.child("users").child(userID).child("location").setValue(location);
                    mDatabase.child("users").child(userID).child("phone").setValue(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

                    home_personalInfoSV.setVisibility(View.GONE);

                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("location").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){
                                if (task.getResult().getValue() != null){
                                    if (task.getResult().getValue().toString().equals("Goraya")){
                                        mDatabase.child("offers").child("gorayaOffer").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    if (task.getResult().getValue() != null){
                                                        new DownloadImageTask((ImageView) findViewById(R.id.home_dailyOfferImage)).execute(task.getResult().getValue().toString());
                                                    }
                                                }
                                            }
                                        });
                                    } else {
                                        mDatabase.child("offers").child("nakodarOffer").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    if (task.getResult().getValue() != null){
                                                        new DownloadImageTask((ImageView) findViewById(R.id.home_dailyOfferImage)).execute(task.getResult().getValue().toString());
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });

                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("special").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){
                                if (task.getResult().getValue() != null){
                                    new DownloadImageTaskTwo((ImageView) findViewById(R.id.home_specialOfferImage)).execute(task.getResult().getValue().toString());
                                }
                            }
                        }
                    });

                } else {
                    Toast.makeText(HomeActivity.this, "Some details are missing. Please check all fields and try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

        home_maleGenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Male";
                home_maleGenderBtn.setActivated(true);
                home_femaleGenderBtn.setActivated(false);
            }
        });

        home_femaleGenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Female";
                home_femaleGenderBtn.setActivated(true);
                home_maleGenderBtn.setActivated(false);
            }
        });

        home_vegDietBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diet = "Vegetarian";
                home_vegDietBtn.setActivated(true);
                home_nonVegDietBtn.setActivated(false);
            }
        });

        home_nonVegDietBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diet = "Non-Vegetarian";
                home_nonVegDietBtn.setActivated(true);
                home_vegDietBtn.setActivated(false);
            }
        });

        home_gorayaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = "Goraya";
                home_gorayaBtn.setActivated(true);
                home_nakodarBtn.setActivated(false);
            }
        });

        home_nakodarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = "Nakodar";
                home_nakodarBtn.setActivated(true);
                home_gorayaBtn.setActivated(false);
            }
        });

        home_dailyOfferLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://fk.petpooja.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        home_specialOfferLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://fk.petpooja.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        home_callNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String dial = "tel:+918146549183";
                intent.setData(Uri.parse(dial));

                startActivity(intent);
            }
        });

        home_orderOnlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://fk.petpooja.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        home_specialCallNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String dial = "tel:+918146549183";
                intent.setData(Uri.parse(dial));

                startActivity(intent);
            }
        });

        home_specialOrderOnlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://fk.petpooja.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        home_aboutUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AboutActivity.class));
                overridePendingTransition(R.anim.from_right, R.anim.to_right);
            }
        });

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                updateUserLastLogin();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        home_notificationLabel.setVisibility(View.VISIBLE);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                fetchUserDetailsAndOffer();
            }
        });
    }

    private void fetchUserDetailsAndOffer(){

        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("phone").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getValue() == null){
                        home_personalInfoSV.setVisibility(View.VISIBLE);
                        enableBoxes();
                    } else {
                        home_personalInfoSV.setVisibility(View.GONE);
                    };
                }
            }
        });

        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("location").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getValue() != null){
                        if (task.getResult().getValue().toString().equals("Goraya")){
                            mDatabase.child("offers").child("gorayaOffer").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (task.getResult().getValue() != null){
                                            new DownloadImageTask((ImageView) findViewById(R.id.home_dailyOfferImage)).execute(task.getResult().getValue().toString());
                                        }
                                    }
                                }
                            });
                        } else {
                            mDatabase.child("offers").child("nakodarOffer").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (task.getResult().getValue() != null){
                                            new DownloadImageTask((ImageView) findViewById(R.id.home_dailyOfferImage)).execute(task.getResult().getValue().toString());
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("special").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getValue() != null){
                        new DownloadImageTaskTwo((ImageView) findViewById(R.id.home_specialOfferImage)).execute(task.getResult().getValue().toString());
                    }
                }
            }
        });
    }

    private void restoreMandatoryHeadings(){
        home_nameTV.setText("Name");
        home_nameTV.setTextColor(Color.parseColor("#78716C"));
        home_emailTV.setText("Email");
        home_emailTV.setTextColor(Color.parseColor("#78716C"));
        home_dobTV.setText("Date of Birth");
        home_dobTV.setTextColor(Color.parseColor("#78716C"));
    }

    private void enableBoxes(){

        if (TextUtils.equals(gender, "Male")){
            gender = "Male";
            home_maleGenderBtn.setActivated(true);
        } else {
            gender = "Female";
            home_femaleGenderBtn.setActivated(true);
        }

        if (TextUtils.equals(diet, "Vegetarian")){
            diet = "Vegetarian";
            home_vegDietBtn.setActivated(true);
        } else {
            diet = "Non-Vegetarian";
            home_nonVegDietBtn.setActivated(true);
        }

        if (TextUtils.equals(location, "Goraya")){
            location = "Goraya";
            home_gorayaBtn.setActivated(true);
        } else {
            location = "Nakodar";
            home_nakodarBtn.setActivated(true);
        }
    }

    private void updateUserLastLogin(){
        Date currentTime = Calendar.getInstance().getTime();
        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userLastLoginAt").setValue(currentTime.toString());
        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userLastLoginTimestamp").setValue(System.currentTimeMillis());
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            home_notificationLabel.setVisibility(View.GONE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImageTaskTwo extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTaskTwo(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            home_specialOfferLayout.setVisibility(View.VISIBLE);
            home_notificationLabel.setVisibility(View.GONE);
        }
    }
}