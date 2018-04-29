package com.example.abror.yandexgallery;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class FullImageActivity extends AppCompatActivity {

    private List<String> imagesList;
    public static ProgressDialog mProgressDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        mProgressDialog= new ProgressDialog(this);
        mProgressDialog.setTitle("Загрузка изображения");
        mProgressDialog.setMessage("Пожалуйста подождите пока картинка загрузится");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();



        // Заменяем текст в action bar на номер картинки с общим кол-вом картинок
        String imageId = getIntent().getStringExtra("imageId");
        int image_id = Integer.parseInt(imageId);
        String listItems = getIntent().getStringExtra("fullImagesList");
        Log.d("fullimagelist", listItems);
        imagesList = Arrays.asList(listItems.split("\\s*,\\s*"));

        ViewPager imagePager = (HackyViewPager) findViewById(R.id.imagesPager);
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(this, imagesList);
        imagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                getSupportActionBar().setTitle(position+1 + " из " + imagesList.size());
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        imagePager.setAdapter(imagePagerAdapter);
        imagePager.setCurrentItem(image_id);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
