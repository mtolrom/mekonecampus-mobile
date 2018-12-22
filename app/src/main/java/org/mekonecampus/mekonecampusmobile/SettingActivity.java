package org.mekonecampus.mekonecampusmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    static Setting setting = new Setting();
    Switch switchOnline;
    Switch switchAlert;
    Switch switchLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //call api
        try {
            new SettingActivity.GetSettings(this).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        switchAlert = (Switch) findViewById(R.id.alert);
        switchOnline = (Switch) findViewById(R.id.online);
        switchLocation = (Switch) findViewById(R.id.location);

        if(setting.Notification != null) {
            if (setting.Notification.equals("Yes")) {
                switchAlert.setChecked(true);
            } else {
                switchAlert.setChecked(false);
            }
        }
        if(setting.OnlineStatus != null) {
            if (setting.OnlineStatus.equals("On")) {
                switchOnline.setChecked(true);
            } else {
                switchOnline.setChecked(false);
            }
        }

        final Button btnNotifications = findViewById(R.id.notifications);
        final Button btnReset = findViewById(R.id.reset);
        final Button btnSave = findViewById(R.id.save);
        final Button btnHome = findViewById(R.id.home);

        btnNotifications.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "Settings reset successful!", Toast.LENGTH_LONG).show();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "Settings save successful!", Toast.LENGTH_LONG).show();
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public static Setting CallMekone() throws IOException {
        try {
            URL url = new URL("http://mekonecampusapi.azurewebsites.net/api/Settings?email=emekone1990@gmail.com");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            //System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                ObjectMapper mapper = new ObjectMapper();
                //articles = mapper.readValue(output, List<Article.class>);
                setting = mapper.readValue(output, new TypeReference<Setting>() {});
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return setting;
    }

    private static class GetSettings extends AsyncTask<Void, Void, Setting> {
        private WeakReference<Activity> weakActivity;
        public GetSettings(Activity activity) {
            weakActivity = new WeakReference<>(activity);
        }
        @Override
        protected Setting doInBackground(Void... voids) {
            Activity activity = weakActivity.get();
            if(activity == null) {
                return null;
            }
            try {
                setting = CallMekone();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(setting == null) {
                return null;
            }
            return setting;
        }
    }

}
