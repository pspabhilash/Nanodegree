package com.myapp.abhilash.popularmoviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

class MovieAdapter extends ArrayAdapter<Movie> {

    private final Context context;
    private final int layoutResourceId;

    public MovieAdapter(Context context, List<Movie> movies) {
        super(context, R.layout.grid_item_movie, movies);
        this.layoutResourceId = R.layout.grid_item_movie;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layoutResourceId, parent, false);
        }

        ImageView movieImage = (ImageView) convertView.findViewById(R.id.movie_image);

        //Load URL to ImageView
        Picasso.with(context).load(movie.getPoster_path()).into(movieImage);

        return convertView;
    }

}