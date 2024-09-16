package org.example;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;
public class Movie extends Production {
    private String movieDuration;
    private int releaseYear;


    public Movie(String title, List<String> directors, List<String> actors,
                 List<Genre> genres, List<Rating> ratings, String description,
                 double averageRating, int releaseYear, String movieDuration) {
        super(title, directors, actors, genres, ratings, description, averageRating);
        this.movieDuration = movieDuration;
        this.releaseYear = releaseYear;
    }



    public String getMovieDuration() {
        return movieDuration;
    }

    public void setMovieDuration(String movieDuration) {
        this.movieDuration = movieDuration;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }
    @Override
    public void displayInfo() {
        System.out.println("Movie Title: " + getTitle());
        System.out.println("Directors: " + getDirectors());
        System.out.println("Actors: " + getActors());
        System.out.println("Genres: " + getGenres());
        System.out.println("Ratings: " + getRatings());
        System.out.println("Description: " + getDescription());
        System.out.println("Average Rating: " + getAverageRating());
        System.out.println("Movie duration: " + getMovieDuration());
        System.out.println("Release year: " + getReleaseYear());
    }
    @Override
    public String toString() {
        return "Movie{" +
                "movieDuration='" + movieDuration + '\'' +
                ", releaseYear=" + releaseYear +
                "} " + super.toString();
    }
}
