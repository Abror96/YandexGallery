package com.example.abror.yandexgallery;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        imagesList = Arrays.asList(listItems.split("\\s*,\\s*"));

        ViewPager imagePager = (HackyViewPager) findViewById(R.id.imagesPager);
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(this, imagesList);

        // изменяем порядковый номер картинки и вставляем общее число картинок
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
            case R.id.share_photo: {
                shareImage(imagesList.get(indexOfImage));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // загружаем url в bitmap посредством библиотеки Picasso
    public void shareImage(String url) {
        Picasso.get().load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                startActivity(Intent.createChooser(i, "Share Image"));
            }
            @Override public void onBitmapFailed(Exception e, Drawable errorDrawable) { }
            @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });
    }
    // преобразовываем bitmap в Uri
    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    // сохранение картинки в памяти телефона
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
