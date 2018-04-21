package com.example.abror.yandexgallery;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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

public class MainActivity extends AppCompatActivity {

    private RecyclerView imagesView;
    private RecyclerView.Adapter adapter;

    private List<Images> imageItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagesView = findViewById(R.id.imagesView);
        imagesView.setHasFixedSize(true);
        imagesView.setLayoutManager(new GridLayoutManager(this, 2));

        imageItems = new ArrayList<>();

        new ParseTask().execute();
    }


    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("http://www.omdbapi.com/?apikey=9ae0c3b9&s=cars");

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


            JSONObject dataJsonObj = null;
            try {

                Log.d("abror json", strJson);

                dataJsonObj = new JSONObject(strJson);

                JSONArray images = dataJsonObj.getJSONArray("Search");

                // 2. перебираем и выводим контакты каждого друга
                for (int i = 0; i < images.length(); i++) {
                    JSONObject image = images.getJSONObject(i);

                    String imageUrl = image.getString("Poster");

                    Images imageItem = new Images(imageUrl);

                    imageItems.add(imageItem);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            adapter = new ImagesAdapter(imageItems, getApplicationContext());
            imagesView.setAdapter(adapter);
        }
    }
}
