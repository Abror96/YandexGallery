package com.example.abror.yandexgallery;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private List<Images> listImageItems;
    private String fullImagesString;


    public ImagesAdapter(List<Images> listItems, String fullImagesString) {
        this.listImageItems = listItems;
        this.fullImagesString = fullImagesString;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Images imageItem = listImageItems.get(position);
        final int item_pos = position;

        // загружаем картинки в imageview на главной странице
        Picasso.get().load(imageItem.getImage()).resize(250, 250).centerCrop().into(holder.image, new Callback() {
            @Override
            public void onSuccess() {
                // при успешной загрузке убирать окно загрузки progressDialog
                MainActivity.mProgressDialog.dismiss();
            }

            @Override
            public void onError(Exception e) {

            }
        });

        // передаем в другое активити список с url картинок
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent imageIntent = new Intent(v.getContext(), FullImageActivity.class);
                imageIntent.putExtra("imageId", String.valueOf(item_pos));
                imageIntent.putExtra("fullImagesList", fullImagesString);
                v.getContext().startActivity(imageIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listImageItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);

        }
    }

}
