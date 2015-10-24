package com.myapp.abhilash.popularmoviesapp;

class Movie {

    private final String title;
    private final String releaseDate;
    private final String posterPath;
    private final String voteAverage;
    private final String overview;

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
