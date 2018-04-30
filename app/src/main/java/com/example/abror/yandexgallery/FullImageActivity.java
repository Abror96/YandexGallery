package com.example.abror.yandexgallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class FullImageActivity extends AppCompatActivity {

    private List<String> imagesList;
    public static ProgressDialog mProgressDialog;
    private int indexOfImage;

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
                indexOfImage = position;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.full_image_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.save_photo:
                Picasso.get()
                    .load(imagesList.get(indexOfImage))
                    .into(getTarget());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Target getTarget(){
        Target target = new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            FileOutputStream outStream = null;
                            File sdCard = Environment.getExternalStorageDirectory();
                            File dir = new File(sdCard.getAbsolutePath() + "/YandexImages");
                            dir.mkdirs();
                            String fileName = String.format("%d.jpg", System.currentTimeMillis());
                            File outFile = new File(dir, fileName);
                            outStream = new FileOutputStream(outFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                            outStream.flush();
                            outStream.close();
                            MediaScannerConnection.scanFile(getApplicationContext(), new String[] { outFile.getPath() }, new String[] { "image/jpeg" }, null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                Toast.makeText(getApplicationContext(), "Картинка успешно сохранена", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }
}
