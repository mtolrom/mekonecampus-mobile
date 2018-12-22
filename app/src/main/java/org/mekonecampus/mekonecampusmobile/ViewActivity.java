package org.mekonecampus.mekonecampusmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ViewActivity extends AppCompatActivity {
    static String category;
    static int count = 0;
    static List<Article> articles = new ArrayList<>();
    static Article arto = new Article();
    static InputStream inputStream = new InputStream() {
        @Override
        public int read() throws IOException {
            return 0;
        }
    };
    Drawable drawable = null;
    static Bitmap bitmap = null;
    private TextView title;
    private ImageView img;
    private TextView likes;
    private TextView views;
    private Button likeimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        // Receive data
        Intent intent = getIntent();
        category = intent.getExtras().getString("Id");

        img = (ImageView) findViewById(R.id.view_pic_id);
        title = (TextView) findViewById(R.id.title_text);
        likes = (TextView) findViewById(R.id.likes_text);
        views = (TextView) findViewById(R.id.views_text);
        likeimg = (Button) findViewById(R.id.likes_pic);

        //get list of articles
        /*Article article1 = new Article();
        article1.PicUrl = "https://mekonecampusapistorage.blob.core.windows.net/campusimages/diasporaLogoBig.JPG";
        article1.Title = "MékonéCapus, une organistion initiée par un membre de la diapora des Etats Unis d'Amérique anime le Centre Polyvalent MékonéCampus implanté au quartier Guelkol à Moundou.";
        article1.ViewsNumber = 0;
        article1.LikesNumber = 0;
        article1.Category = "Welcome";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        article1.Timestamp = now + "";
        articles.add(article1);*/

        //call api
        try {
            new GetArticles(this).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(articles.size() > 0) {
            if (articles.get(count) != null) {
                Picasso.with(getApplicationContext())
                        .load(articles.get(count).PicUrl)
                        .fit()
                        .into(img);
            }
            title.setText(articles.get(count).Title);
            likes.setText(articles.get(count).LikesNumber + " likes");
            views.setText(articles.get(count).ViewsNumber + " views");
        }

        final Button btnNotifications = findViewById(R.id.notifications);
        final Button btnPrev = findViewById(R.id.preview);
        final Button btnList = findViewById(R.id.list);
        final Button btnNext = findViewById(R.id.next);
        final Button btnLike = findViewById(R.id.likes_pic);

        btnLike.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    new UpdateArticleTask(ViewActivity.this, arto).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                likes.setText(arto.LikesNumber + " likes");
                Toast.makeText(ViewActivity.this, "You liked it!", Toast.LENGTH_LONG).show();
            }
        });
        btnNotifications.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(articles.size() > 0) {
                    count--;
                    if(count < 0){
                        count = 0;
                        Toast.makeText(ViewActivity.this, "This is the first article!", Toast.LENGTH_LONG).show();
                    }
                    if (articles.get(count) != null) {
                        Picasso.with(getApplicationContext())
                                .load(articles.get(count).PicUrl)
                                .fit()
                                .into(img);
                    }
                    try {
                        new UpdateViewsTask(ViewActivity.this, arto).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    title.setText(articles.get(count).Title);
                    likes.setText(articles.get(count).LikesNumber + " likes");
                    views.setText(arto.ViewsNumber + " views");
                }
            }
        });
        btnList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ListActivity.class);
                startActivity(intent);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(articles.size() > 0) {
                    count++;
                    if(count > articles.size() - 1){
                        count = articles.size() - 1;
                        Toast.makeText(ViewActivity.this, "This is the last article!", Toast.LENGTH_LONG).show();
                    }
                    if (articles.get(count) != null) {
                        Picasso.with(getApplicationContext())
                                .load(articles.get(count).PicUrl)
                                .fit()
                                .into(img);
                    }
                    try {
                        new UpdateViewsTask(ViewActivity.this, arto).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    title.setText(articles.get(count).Title);
                    likes.setText(articles.get(count).LikesNumber + " likes");
                    views.setText(arto.ViewsNumber + " views");
                }
            }
        });
    }

    //make api call
    public static List<Article> CallMekone(String category) throws IOException {
        try {
            URL url = new URL("http://mekonecampusapi.azurewebsites.net/api/Articles/mekonecampus?category=" + category + "&status=active");
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
                articles = mapper.readValue(output, new TypeReference<List<Article>>() {});
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return articles;
    }

    public static Article CallMekone() throws IOException {
        try {
            arto = articles.get(count);
            arto.LikesNumber += 1;
            URL url = new URL("http://mekonecampusapi.azurewebsites.net/api/Articles");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            String input = mapper.writeValueAsString(arto);
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.write(input.getBytes("UTF-8"));
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            //System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                arto = mapper.readValue(output, new TypeReference<Article>() {});
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arto;
    }

    public static Article CallMekone2() throws IOException {
        try {
            arto = articles.get(count);
            arto.ViewsNumber += 1;
            URL url = new URL("http://mekonecampusapi.azurewebsites.net/api/Articles");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            String input = mapper.writeValueAsString(arto);
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.write(input.getBytes("UTF-8"));
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            //System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                arto = mapper.readValue(output, new TypeReference<Article>() {});
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arto;
    }

    private static class GetArticles extends AsyncTask<Void, Void, List<Article>> {
        private WeakReference<Activity> weakActivity;
        public GetArticles(Activity activity) {
            weakActivity = new WeakReference<>(activity);
        }
        @Override
        protected List<Article> doInBackground(Void... voids) {
            Activity activity = weakActivity.get();
            if(activity == null) {
                return null;
            }
            try {
                articles = CallMekone(category);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(articles.size() == 0) {
                return null;
            }
            return articles;
        }
    }

    private static class UpdateArticleTask extends AsyncTask<Void, Void, Article> {
        private WeakReference<Activity> weakActivity;
        private Article article;
        UpdateArticleTask(Activity activity, Article article) {
            weakActivity = new WeakReference<>(activity);
            this.article = article;
        }
        @Override
        protected Article doInBackground(Void... voids) {
            Activity activity = weakActivity.get();
            if (activity == null) {
                return null;
            }
            try {
                arto = CallMekone();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return arto;
        }
    }

    private static class UpdateViewsTask extends AsyncTask<Void, Void, Article> {
        private WeakReference<Activity> weakActivity;
        private Article article;
        UpdateViewsTask(Activity activity, Article article) {
            weakActivity = new WeakReference<>(activity);
            this.article = article;
        }
        @Override
        protected Article doInBackground(Void... voids) {
            Activity activity = weakActivity.get();
            if (activity == null) {
                return null;
            }
            try {
                arto = CallMekone2();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return arto;
        }
    }
}
