package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Actor implements Comparable<Actor> {
    private String name;
    private List<Map.Entry<String, String>> roles;
    private String biography;
    private List<Rating> ratings;
    private double averageRating;

    public Actor(String name, List<Map.Entry<String, String>> roles, String biography, double averageRating) {
        this.name = name;
        this.roles = roles;
        this.biography = biography;
        this.ratings = new ArrayList<>();
        this.averageRating = calculateAverageRating();
    }
    @Override
    public int compareTo(Actor other) {
        return this.getName().compareTo(other.getName());
    }
    public String getName() {
        return name;
    }

    public List<Map.Entry<String, String>> getRoles() {
        return roles;
    }

    public String getBiography() {
        return biography;
    }
    public List<Rating> getRatings() {
        return ratings;
    }
    public double getAverageRating(){return averageRating;}

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public void setName(String newInfo) {
        name = newInfo;
    }

    public void addRating(Rating rating) {
        this.ratings.add(rating);
        averageRating = calculateAverageRating();
    }
    public void deleteRating(Rating rating) {
        this.ratings.remove(rating);
        averageRating = calculateAverageRating();
    }
    public void displayInfo() {
        System.out.println("Actor: " + name);
        System.out.println("Biography: " + biography);
        System.out.println("Roles:");

        for (Map.Entry<String, String> role : roles) {
            System.out.println("  - " + role.getKey() + ": " + role.getValue());
        }
        System.out.println("Ratings: " + ratings);
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

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Actor: ").append(name).append("\n");
        result.append("Biography: ").append(biography).append("\n");
        result.append("Roles:\n");

        for (Map.Entry<String, String> role : roles) {
            result.append("  - ").append(role.getKey()).append(": ").append(role.getValue()).append("\n");
        }
        result.append("Ratings: ").append(ratings);

        return result.toString();
    }
}

