package org.example;


import java.util.List;

public abstract class Production implements Comparable<Production> {
    private String title;
    private List<String> directors;
    private List<String> actors;
    private List<Genre> genres;
    private List<Rating> ratings;
    private String description;
    private double averageRating;

    public Production(String title, List<String> directors, List<String> actors,
                      List<Genre> genres, List<Rating> ratings,  String description, double averageRating) {
        this.title = title;
        this.directors = directors;
        this.actors = actors;
        this.genres = genres;
        this.ratings = ratings;
        this.description = description;
        this.averageRating = calculateAverageRating();
    }

    public abstract void displayInfo();
    @Override
    public String toString() {
        return "Production{" +
                "title='" + title + '\'' +
                ", directors=" + directors +
                ", actors=" + actors +
                ", genres=" + genres +
                ", ratings=" + ratings +
                ", description='" + description + '\'' +
                ", averageRating=" + averageRating +
                '}';
    }
    @Override
    public int compareTo(Production other) {
        return this.title.compareTo(other.title);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getDirectors() {
        return directors;
    }

    public void setDirectors(List<String> directors) {
        this.directors = directors;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
        this.averageRating = calculateAverageRating();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public double calculateAverageRating() {
        if (ratings.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        for (Rating rating : ratings) {
            sum += rating.getRating();
        }

        return sum / ratings.size();
    }

    public void addRating(Rating rating) {
        this.ratings.add(rating);
        this.averageRating = calculateAverageRating();
    }
    public void deleteRating(Rating rating) {
        this.ratings.remove(rating);
        this.averageRating = calculateAverageRating();
    }
}


