package com.example.abror.yandexgallery;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private List<Images> listItems;
    private Context context;


    public ImagesAdapter(List<Images> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Images imageItem = listItems.get(position);

        Picasso.get().load(imageItem.getImage()).centerCrop().fit().into(holder.image);

        String images_list = "";
        for (int i = 0; i<listItems.size(); i++) {
            images_list += listItems.get(i).getImage() + ", ";
        }

        final String finalImages_list = images_list;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent imageIntent = new Intent(v.getContext(), FullImageActivity.class);
                imageIntent.putExtra("imageId", String.valueOf(position));
                imageIntent.putExtra("image_list", finalImages_list);
                v.getContext().startActivity(imageIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);

        }
    }

}
