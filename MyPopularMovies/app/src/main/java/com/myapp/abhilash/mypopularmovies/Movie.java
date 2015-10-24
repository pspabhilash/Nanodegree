package com.myapp.abhilash.mypopularmovies;

public class Movie {

    String title;
    String releaseDate;
    String posterPath;
    String voteAverage;
    String overview;

    public Movie(String title, String release_date, String poster_path, String vote_average, String overview) {
        this.title = title;
        this.releaseDate = release_date;
        this.posterPath = poster_path;
        this.voteAverage = vote_average;
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public String getRelease_date() {
        return releaseDate;
    }

    public String getPoster_path() {
        return posterPath;
    }

    public String getVote_average() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }


}
