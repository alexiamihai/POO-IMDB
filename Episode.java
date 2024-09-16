package org.example;
import java.util.*;
public class Episode {
    String title;
    String episodeDuration;

    public Episode(String episodeTitle, String duration) {
        this.title = episodeTitle;
        this.episodeDuration = duration;
    }


    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getEpisodeDuration() {
        return episodeDuration;
    }
    public void setEpisodeDuration(String episodeDuration) {
        this.episodeDuration = episodeDuration;
    }
    @Override
    public String toString() {
        return "Episode{" +
                "episodeTitle='" + title + '\'' +
                ", duration='" + episodeDuration + '\'' +
                '}';
    }
}
