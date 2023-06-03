package com.example.tfc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> urls;
    String url;

    public SpinnerAdapter(Context context, List<String> urls) {
        super(context, R.layout.spinner_item, urls);
        this.context=context;
        this.urls=urls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, R.layout.spinner_item);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, R.layout.spinner_item);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent, int layoutId) {
        View view=convertView;
        if (view==null) {
            LayoutInflater inflater=LayoutInflater.from(context);
            view=inflater.inflate(layoutId, parent, false);
        }

        ImageView imageView=view.findViewById(R.id.imageView);

        this.url=urls.get(position);

        Glide.with(context).load(url).into(imageView);
        return view;
    }

    public String getUrlOptionSpinner() {
        return url;
    }
}

