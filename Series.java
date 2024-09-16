package org.example;
import java.util.*;
public class Series extends Production {
    private int releaseYear;
    private int numSeasons;
    private Map<String, List<Episode>> episodes;

    public Series(String title, List<String> directors, List<String> actors,
                  List<Genre> genres, List<Rating> ratings, String description, double averageRating,
                  int releaseYear, int numSeasons, Map<String, List<Episode>> episodes) {
        super(title, directors, actors, genres, ratings, description, averageRating);
        this.releaseYear = releaseYear;
        this.numSeasons = numSeasons;
        this.episodes = episodes;
    }
    // GETTERI SI SETTERI
    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getNumSeasons() {
        return numSeasons;
    }

    public void setNumSeasons(int numSeasons) {
        this.numSeasons = numSeasons;
    }

    public Map<String, List<Episode>> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(Map<String, List<Episode>> episodes) {
        this.episodes = episodes;
    }

    @Override
    public void displayInfo() {
        System.out.println("Title: " + getTitle());
        System.out.println("Type: Series");
        System.out.println("Release Year: " + releaseYear);
        System.out.println("Number of Seasons: " + numSeasons);

        if (episodes != null) {
            for (Map.Entry<String, List<Episode>> seasonEntry : episodes.entrySet()) {
                String seasonNumber = seasonEntry.getKey();
                List<Episode> seasonEpisodes = seasonEntry.getValue();

                System.out.println("Season " + seasonNumber + " Episodes:");
                for (Episode episode : seasonEpisodes) {
                    System.out.println("  - Episode " + episode.getTitle() + ": " + episode.getEpisodeDuration());
                }
            }
        }
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Series{" +
                "numSeasons=" + numSeasons +
                ", episodes=[");

        episodes.forEach((seasonNumber, episodeList) -> {
            result.append("\n  Season ").append(seasonNumber).append(":");
            episodeList.forEach(episode -> {
                result.append("\n    Episode Name: ").append(episode.getTitle());
                result.append(", Duration: ").append(episode.getEpisodeDuration());
            });
        });

        result.append("\n]} ").append(super.toString());

        return result.toString();
    }

}