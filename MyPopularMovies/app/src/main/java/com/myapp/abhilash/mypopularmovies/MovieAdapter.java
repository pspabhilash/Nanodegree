package com.myapp.abhilash.mypopularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {

    private Context context;
    int layoutResourceId;
    List<Movie> movies = new ArrayList<>();

    public MovieAdapter(Context context, int layoutResourceId, List<Movie> movies) {
        super(context, layoutResourceId, movies);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.movies = movies;
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