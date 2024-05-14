package in.figos.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class AboutActivity extends AppCompatActivity {

    private ImageButton about_backBtn;
    private Button about_aboutUsBtn, about_ourLocationsBtn, about_franchiseBtn, about_figosBtn, about_openGorayaLocationBtn, about_openNakodarLocationBtn, about_involveBtn;
    private RelativeLayout about_aboutUsLayout, about_ourLocationsLayout, about_franchiseLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        about_backBtn = findViewById(R.id.about_backBtn);
        about_aboutUsBtn = findViewById(R.id.about_aboutUsBtn);
        about_ourLocationsBtn = findViewById(R.id.about_ourLocationsBtn);
        about_franchiseBtn = findViewById(R.id.about_franchiseBtn);
        about_figosBtn = findViewById(R.id.about_figosBtn);
        about_openGorayaLocationBtn = findViewById(R.id.about_openGorayaLocationBtn);
        about_openNakodarLocationBtn = findViewById(R.id.about_openNakodarLocationBtn);
        about_involveBtn = findViewById(R.id.about_involveBtn);
        about_aboutUsLayout = findViewById(R.id.about_aboutUsLayout);
        about_ourLocationsLayout = findViewById(R.id.about_ourLocationsLayout);
        about_franchiseLayout = findViewById(R.id.about_franchiseLayout);

        about_aboutUsBtn.setActivated(true);

        about_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        about_aboutUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                about_aboutUsBtn.setActivated(true);
                about_aboutUsLayout.setVisibility(View.VISIBLE);

                about_ourLocationsBtn.setActivated(false);
                about_franchiseBtn.setActivated(false);

                about_ourLocationsLayout.setVisibility(View.GONE);
                about_franchiseLayout.setVisibility(View.GONE);
            }
        });

        about_ourLocationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                about_ourLocationsBtn.setActivated(true);
                about_ourLocationsLayout.setVisibility(View.VISIBLE);

                about_aboutUsBtn.setActivated(false);
                about_franchiseBtn.setActivated(false);

                about_aboutUsLayout.setVisibility(View.GONE);
                about_franchiseLayout.setVisibility(View.GONE);
            }
        });

        about_franchiseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                about_franchiseBtn.setActivated(true);
                about_franchiseLayout.setVisibility(View.VISIBLE);

                about_aboutUsBtn.setActivated(false);
                about_ourLocationsBtn.setActivated(false);

                about_aboutUsLayout.setVisibility(View.GONE);
                about_ourLocationsLayout.setVisibility(View.GONE);
            }
        });

        about_figosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://figos.in/about-us/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        about_openGorayaLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=31.123841,75.769625"));
                startActivity(intent);
            }
        });

        about_openNakodarLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=31.128995,75.485179"));
                startActivity(intent);
            }
        });

        about_involveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://figos.in/get-franchise/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.undo_from_right, R.anim.undo_to_right);
    }
}