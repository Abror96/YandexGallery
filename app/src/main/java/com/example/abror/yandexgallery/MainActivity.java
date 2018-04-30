package com.example.abror.yandexgallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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

    private RecyclerView imagesRecyclerView;

    private List<Images> thumbImageItemsList;

    private SwipeRefreshLayout swipeRefreshContainer;

    private ImageView noInternetIc;

    private int num_of_cols;

    private String fullImageUrl = "";

    public static ProgressDialog mProgressDialog;

    // объявляем разрешение, которое нам нужно получить
    private static final String WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // проверяем разрешения на запись на память телефона
        if (!isPermissionGranted(WRITE_EXTERNAL_STORAGE_PERMISSION)) {
            requestPermission(WRITE_EXTERNAL_STORAGE_PERMISSION);
        }

        swipeRefreshContainer = findViewById(R.id.swipe_container);
        swipeRefreshContainer.setOnRefreshListener(this);

        imagesRecyclerView = findViewById(R.id.imagesView);
        imagesRecyclerView.setHasFixedSize(true);
        num_of_cols = 2;
        imagesRecyclerView.setLayoutManager(new GridLayoutManager(this, num_of_cols));
        imagesRecyclerView.setMotionEventSplittingEnabled(false);

        thumbImageItemsList = new ArrayList<>();

        noInternetIc = findViewById(R.id.no_internet_ic);

        // вывод окна загрузки пока скачиваются картинки
        mProgressDialog= new ProgressDialog(this);
        mProgressDialog.setTitle("Загрузка изображений");
        mProgressDialog.setMessage("Пожалуйста подождите пока картинки загружаются");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        // проверка интернет соединения
        checkNetwork();

        // увеличение и уменьшение количества столбцов gridlyaout
        final ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (detector.getCurrentSpan() > 150 && detector.getTimeDelta() > 150) {
                    if (detector.getCurrentSpan() - detector.getPreviousSpan() < -1) {
                        // увеличение количества столбцов
                        if (num_of_cols < 4) num_of_cols++;
                        imagesRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), num_of_cols));
                        return true;
                    } else if (detector.getCurrentSpan() - detector.getPreviousSpan() > 1) {
                        // уменьшение количества столбцов
                        if (num_of_cols > 2) num_of_cols--;
                        imagesRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), num_of_cols));
                        return true;
                    }
                }
                return false;
            }
        });

        imagesRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        imagesRecyclerView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleGestureDetector.onTouchEvent(event);
                return false;
            }
        });

        new ParseTask().execute();
    }

    private boolean isPermissionGranted(String permission) {
        // проверяем разрешение - есть ли оно у нашего приложения
        int permissionCheck = ActivityCompat.checkSelfPermission(this, permission);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Разрешения получены", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Разрешения не получены", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestPermission(String permission) {
        // запрашиваем разрешение
        ActivityCompat.requestPermissions(this, new String[]{permission}, 2);
    }

    // действия при свайпе вниз (обновение)
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

            thumbImageItemsList.clear();
            JSONObject dataJsonObj = null;
            try {

                dataJsonObj = new JSONObject(strJson);
                JSONArray images = dataJsonObj.getJSONArray("entries");

                for (int i = 0; i < images.length(); i++) {
                    JSONObject images_obj = images.getJSONObject(i);

                    JSONObject image = images_obj.getJSONObject("img");
                    JSONObject image_size = image.getJSONObject("L");
                    JSONObject imageFullSize = image.getJSONObject("XXXL");

                    String imageUrl = image_size.getString("href");
                    fullImageUrl += imageFullSize.getString("href") + ", ";

                    Images imageItem = new Images(imageUrl);

                    thumbImageItemsList.add(imageItem);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RecyclerView.Adapter adapter = new ImagesAdapter(thumbImageItemsList, fullImageUrl);
            imagesRecyclerView.setAdapter(adapter);

            swipeRefreshContainer.setRefreshing(false);
        }
    }

    // метод для показывания картинки "Нет интернет соединения" или
    // списка картинок в зависимости от наличия интернет подключения
    public void checkNetwork(){
        if(isNetworkStatusAvailable (getApplicationContext())) {
            imagesRecyclerView.setVisibility(View.VISIBLE);
            noInternetIc.setVisibility(View.GONE);
        } else {
            imagesRecyclerView.setVisibility(View.GONE);
            noInternetIc.setVisibility(View.VISIBLE);
            noInternetIc.bringToFront();
        }
    }

    // метод для проверки интернет соединения
    public static boolean isNetworkStatusAvailable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if(netInfos != null)
                return netInfos.isConnected();
        }
        return false;
    }

    //не дестроить приложение при нажатии на системную кнопку назад
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            this.moveTaskToBack(true);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
