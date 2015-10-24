package com.myapp.abhilash.popularmoviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        //Get intent
        Intent i = getActivity().getIntent();
        String currentTitle = i.getStringExtra("title");
        String currentReleaseDate = i.getStringExtra("releaseDate");
        String currentPosterPath = i.getStringExtra("posterPath");
        String currentVoteAverage = i.getStringExtra("voteAverage");
        String currentOverview = i.getStringExtra("overview");

        //Fine all Views
        TextView tvTitle = (TextView) rootView.findViewById(R.id.title);
        TextView tvReleaseDate = (TextView) rootView.findViewById(R.id.release_date);
        //TextView tvPosterPath = (TextView) rootView.findViewById(R.id.poster);
        ImageView ivPoster = (ImageView) rootView.findViewById(R.id.poster_image);
        TextView tvVoteAverage = (TextView) rootView.findViewById(R.id.vote_average);
        TextView tvOverview = (TextView) rootView.findViewById(R.id.plot_synopsis);

        //Put together
        tvTitle.setText(currentTitle);
        tvReleaseDate.setText(currentReleaseDate);
        //tvPosterPath.setText(currentPosterPath);
        Picasso.with(getContext()).load(currentPosterPath).into(ivPoster);
        tvVoteAverage.setText(currentVoteAverage);
        tvOverview.setText(currentOverview);

        return rootView;
    }
}
