package com.example.abror.yandexgallery;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView imagesView;
    private RecyclerView.Adapter adapter;

    private List<Images> imageItems;

    private SwipeRefreshLayout swipeContainer;

    private ImageView noInternetIc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeContainer = findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(this);

        imagesView = findViewById(R.id.imagesView);
        imagesView.setHasFixedSize(true);
        imagesView.setLayoutManager(new GridLayoutManager(this, 2));

        imageItems = new ArrayList<>();

        noInternetIc = findViewById(R.id.no_internet_ic);

        // Checking the internet connection
        if(!isNetworkStatusAvialable (getApplicationContext())) {
            imagesView.setVisibility(View.GONE);
            noInternetIc.bringToFront();
        }

        new ParseTask().execute();
    }

    @Override
    public void onRefresh() {
        new ParseTask().execute();
        checkNetwork();

    }


    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("https://api-fotki.yandex.ru/api/users/abbasov894/favorites/?format=json");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            imageItems.clear();
            JSONObject dataJsonObj = null;
            try {

                Log.d("abror json", strJson);

                dataJsonObj = new JSONObject(strJson);

                JSONArray images = dataJsonObj.getJSONArray("entries");

                // 2. перебираем и выводим контакты каждого друга
                for (int i = 0; i < images.length(); i++) {
                    JSONObject images_obj = images.getJSONObject(i);

                    JSONObject image = images_obj.getJSONObject("img");
                    JSONObject image_size = image.getJSONObject("L");

                    String imageUrl = image_size.getString("href");

                    Images imageItem = new Images(imageUrl);

                    imageItems.add(imageItem);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            adapter = new ImagesAdapter(imageItems, getApplicationContext());
            imagesView.setAdapter(adapter);

            swipeContainer.setRefreshing(false);
        }
    }

    public void checkNetwork(){
        if(isNetworkStatusAvialable (getApplicationContext())) {
            imagesView.setVisibility(View.VISIBLE);
            noInternetIc.setVisibility(View.GONE);
        } else {
            imagesView.setVisibility(View.GONE);
            noInternetIc.setVisibility(View.VISIBLE);
            noInternetIc.bringToFront();
        }
    }

    public static boolean isNetworkStatusAvialable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if(netInfos != null)
                if(netInfos.isConnected())
                    return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkNetwork();

    }

    @Override
    protected void onStop() {
        super.onStop();
        checkNetwork();
    }
}
