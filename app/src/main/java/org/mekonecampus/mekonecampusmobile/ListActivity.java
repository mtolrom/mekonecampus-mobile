package org.mekonecampus.mekonecampusmobile;

import android.content.Intent;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    List<Category> categories =  new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        final Button btnNotifications = findViewById(R.id.notifications);
        final Button btnMain = findViewById(R.id.home);
        final Button btnAbout = findViewById(R.id.about);
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
        btnAbout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HouseActivity.class);
                startActivity(intent);
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

        Category category1 = new Category();
        category1.categoryName = "Technology";
        category1.categoryPicture = R.drawable.logoenglish;
        categories.add(category1);

        Category category2 = new Category();
        category2.categoryName = "Cartoon";
        category2.categoryPicture = R.drawable.logoenglish;
        categories.add(category2);

        Category category3 = new Category();
        category3.categoryName = "Business";
        category3.categoryPicture = R.drawable.logoenglish;
        categories.add(category3);

        Category category4 = new Category();
        category4.categoryName = "Events";
        category4.categoryPicture = R.drawable.logoenglish;
        categories.add(category4);

        RecyclerView myRecyclerview = (RecyclerView) findViewById(R.id.recyclerView_id);
        RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(this,categories);
        myRecyclerview.setLayoutManager(new GridLayoutManager(this,2));
        myRecyclerview.setAdapter(myAdapter);
    }
}
