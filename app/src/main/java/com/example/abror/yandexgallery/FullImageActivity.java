package com.example.abror.yandexgallery;

import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class FullImageActivity extends AppCompatActivity {

    private List<String> images;
    private int image_id;

    private ViewPager imagePager;
    private ImagePagerAdapter imagePagerAdapter;

    private RelativeLayout mainLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        // Заменяем текст в action bar на номер картинки с общим кол-вом картинок
        String imageId = getIntent().getStringExtra("imageId");
        image_id = Integer.parseInt(imageId);
        String listItems = getIntent().getStringExtra("image_list");
        images = Arrays.asList(listItems.split("\\s*,\\s*"));

        imagePager = findViewById(R.id.imagesPager);
        imagePagerAdapter = new ImagePagerAdapter(this, images);
        imagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                getSupportActionBar().setTitle(position+1 + " из " + images.size());
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

        mainLayout = findViewById(R.id.fullImageLayout);

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
