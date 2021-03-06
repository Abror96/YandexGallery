package com.example.abror.yandexgallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {

    private Context ctx;
    private LayoutInflater layoutInflater;
    private List<String> images_list;


    public ImagePagerAdapter(Context c, List<String> images) {
        ctx = c;
        images_list = images;
    }

    @Override
    public int getCount() {
        return images_list.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = layoutInflater.inflate(R.layout.custom_swipe_item, container, false);

        // загрузка изображения с url и вставка в imageview посредством библиотеки picasso
        ImageView imageView = itemView.findViewById(R.id.fullImage);
        Picasso.get().load(images_list.get(position)).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                // при успешной загрузке убирать окно загрузки progressDialog
                FullImageActivity.mProgressDialog.dismiss();
            }

            @Override
            public void onError(Exception e) {

            }
        });
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

}
