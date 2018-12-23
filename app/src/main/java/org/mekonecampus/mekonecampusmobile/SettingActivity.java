package org.mekonecampus.mekonecampusmobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.mekonecampus.mekonecampusmobile.ViewActivity.arto;

public class SettingActivity extends AppCompatActivity {
    static Setting setting = new Setting();
    static String online;
    static String status;
    static String alert;
    Switch switchOnline;
    Switch switchAlert;
    Switch switchStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        switchAlert = (Switch) findViewById(R.id.alert);
        switchOnline = (Switch) findViewById(R.id.online);
        switchStatus = (Switch) findViewById(R.id.status);

        //call api
        try {
            new SettingActivity.GetSettings(this).execute();
        } catch (Exception e) {
            e.printStackTrace();
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
                try {
                    new SettingActivity.ResetSettingsTask(SettingActivity.this, setting).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(SettingActivity.this, "Settings reset successful!", Toast.LENGTH_LONG).show();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    new SettingActivity.UpdateSettingsTask(SettingActivity.this, setting).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(SettingActivity.this, "Settings save successful!", Toast.LENGTH_LONG).show();
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        // Set a checked change listener for switch button
        switchAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    alert = "Yes";
                    Toast.makeText(SettingActivity.this, "...turning alert on!", Toast.LENGTH_SHORT).show();
                } else {
                    alert = "No";
                    Toast.makeText(SettingActivity.this, "...turning alert off!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        switchOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    online = "On";
                    Toast.makeText(SettingActivity.this, "...turning online on!", Toast.LENGTH_SHORT).show();
                } else {
                    online = "Off";
                    Toast.makeText(SettingActivity.this, "...turning online off!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        switchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    status = "active";
                    Toast.makeText(SettingActivity.this, "...turning location on!", Toast.LENGTH_SHORT).show();
                } else {
                    status = "active";
                    Toast.makeText(SettingActivity.this, "...turning location off!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        //final SharedPreferences.Editor e = settings.edit();
        //e.putBoolean("switch", false);
        //e.commit();

        if(setting != null) {
            if (setting.Notification != null) {
                if (setting.Notification.equals("Yes")) {
                    switchAlert.setChecked(true);
                } else {
                    switchAlert.setChecked(false);
                }
            }
            if (setting.OnlineStatus != null) {
                if (setting.OnlineStatus.equals("On")) {
                    switchOnline.setChecked(true);
                } else {
                    switchOnline.setChecked(false);
                }
            }
            if (setting.Status != null) {
                if (setting.Status.equals("active")) {
                    switchStatus.setChecked(true);
                } else {
                    switchStatus.setChecked(false);
                }
            }
        }
    }

    public static Setting CallMekone() throws IOException {
        try {
            URL url = new URL("http://mekonecampusapi.azurewebsites.net/api/Settings?Email=emekone1990@gmail.com");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            /*if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }*/
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null) {
                ObjectMapper mapper = new ObjectMapper();
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

    private static class UpdateSettingsTask extends AsyncTask<Void, Void, Setting> {
        private WeakReference<Activity> weakActivity;
        private Setting setting;
        UpdateSettingsTask(Activity activity, Setting setting) {
            weakActivity = new WeakReference<>(activity);
            this.setting = setting;
        }
        @Override
        protected Setting doInBackground(Void... voids) {
            Activity activity = weakActivity.get();
            if (activity == null) {
                return null;
            }
            try {
                setting = CallMekone2();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return setting;
        }
    }

    private static class ResetSettingsTask extends AsyncTask<Void, Void, Setting> {
        private WeakReference<Activity> weakActivity;
        private Setting setting;
        ResetSettingsTask(Activity activity, Setting setting) {
            weakActivity = new WeakReference<>(activity);
            this.setting = setting;
        }
        @Override
        protected Setting doInBackground(Void... voids) {
            Activity activity = weakActivity.get();
            if (activity == null) {
                return null;
            }
            try {
                setting = CallMekone3();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return setting;
        }
    }

    public static Setting CallMekone2() throws IOException {
        try {
            if(setting != null) {
                setting.Status = "active";
                setting.OnlineStatus = online;
                setting.Notification = alert;
            }
            URL url = new URL("http://mekonecampusapi.azurewebsites.net/api/Settings");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            String input = mapper.writeValueAsString(setting);
            OutputStream os = conn.getOutputStream();
            //os.write(input.getBytes());
            os.write(input.getBytes("UTF-8"));
            os.flush();

            /*if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }*/
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null) {
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

    public static Setting CallMekone3() throws IOException {
        try {
            if(setting != null) {
                setting.Status = "active";
                setting.OnlineStatus = status;
                setting.Notification = alert;
            }
            URL url = new URL("http://mekonecampusapi.azurewebsites.net/api/Settings");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            String input = mapper.writeValueAsString(setting);
            OutputStream os = conn.getOutputStream();
            //os.write(input.getBytes());
            os.write(input.getBytes("UTF-8"));
            os.flush();

            /*if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }*/
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null) {
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
}
