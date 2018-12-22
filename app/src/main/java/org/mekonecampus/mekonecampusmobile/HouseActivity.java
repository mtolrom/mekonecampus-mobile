package org.mekonecampus.mekonecampusmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HouseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house);

        final Button btnNotifications = findViewById(R.id.notifications);
        final Button btnMain = findViewById(R.id.home);
        final Button btnLike = findViewById(R.id.like);
        final Button btnSettings = findViewById(R.id.settings);

        btnNotifications.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        btnMain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        btnLike.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(HouseActivity.this, "Thanks for liking us!", Toast.LENGTH_LONG).show();
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SettingActivity.class);
                startActivity(intent);
            }
        });
    }
}
