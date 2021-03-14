package com.example.imagesearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class ImageListAdapter extends BaseAdapter {
    ArrayList<Bitmap> images;
    Context context;
    int width;

    public ImageListAdapter(ArrayList<Bitmap> images,Context context, int width){
        this.images = images;
        this.context = context;
        this.width = width;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bitmap image = images.get(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        ImageView iv = convertView.findViewById(R.id.image);
        iv.setImageBitmap(image);
        return convertView;
    }

}
