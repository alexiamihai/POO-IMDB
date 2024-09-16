package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class JsonManager {
    private static JsonManager instance = null;
    private JsonManager() {
    }

    public static JsonManager getInstance() {
        if (instance == null) {
            instance = new JsonManager();
        }
        return instance;
    }


    public void loadUserDataFromJSON(List<User<String>> users, List <Production> productions, List <Actor> Gactors, List <Request> requests) throws IOException {

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            File jsonFile = new File("src/test/resources/testResources/production.json");

            JsonNode jsonArray = objectMapper.readTree(jsonFile);

            for (JsonNode productionNode : jsonArray) {
                String title = productionNode.get("title").asText();
                String type = productionNode.get("type").asText();
                List<String> directors = objectMapper.convertValue(productionNode.get("directors"), List.class);
                List<String> actors = objectMapper.convertValue(productionNode.get("actors"), List.class);
                List<Genre> genres = objectMapper.convertValue(productionNode.get("genres"), List.class);
                List<Rating> ratings = objectMapper.convertValue(productionNode.get("ratings"), new TypeReference<List<Rating>>() {
                });
                String plot = productionNode.get("plot").asText();
                double averageRating = productionNode.get("averageRating").asDouble();

                if ("Movie".equals(type)) {
                    JsonNode releaseYearNode = productionNode.get("releaseYear");
                    int releaseYear = (releaseYearNode != null && !releaseYearNode.isNull()) ? releaseYearNode.asInt() : 0;

                    String duration = productionNode.get("duration").asText();

                    Movie movie = new Movie(title, directors, actors, genres, ratings, plot, averageRating, releaseYear, duration);
                    productions.add(movie);
                } else if ("Series".equals(type)) {
                    int releaseYear = productionNode.get("releaseYear").asInt();
                    int numSeasons = productionNode.get("numSeasons").asInt();

                    JsonNode seasonsNode = productionNode.get("seasons");
                    Map<String, List<Episode>> episodes = new HashMap<>();

                    if (seasonsNode != null) {
                        Iterator<Map.Entry<String, JsonNode>> seasonIterator = seasonsNode.fields();

                        while (seasonIterator.hasNext()) {
                            Map.Entry<String, JsonNode> seasonEntry = seasonIterator.next();
                            String seasonNumber = seasonEntry.getKey();
                            JsonNode episodesNode = seasonEntry.getValue();

                            if (episodesNode != null) {
                                List<Episode> episodeList = new ArrayList<>();

                                Iterator<JsonNode> episodeIterator = episodesNode.iterator();
                                while (episodeIterator.hasNext()) {
                                    JsonNode episodeNode = episodeIterator.next();
                                    String episodeName = episodeNode.get("episodeName").asText();
                                    String duration = episodeNode.get("duration").asText();

                                    Episode episode = new Episode(episodeName, duration);
                                    episodeList.add(episode);
                                }

                                episodes.put(seasonNumber, episodeList);
                            }
                        }
                    }
                    Series series = new Series(title, directors, actors, genres, ratings, plot, averageRating, releaseYear, numSeasons, episodes);
                    productions.add(series);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonArray = objectMapper.readTree(new File("src/test/resources/testResources/actors.json"));

            for (JsonNode actorNode : jsonArray) {
                String name = actorNode.get("name").asText();
                List<Map.Entry<String, String>> roles = new ArrayList<>();

                for (JsonNode performanceNode : actorNode.get("performances")) {
                    String title = (performanceNode.get("title") != null) ? performanceNode.get("title").asText() : null;
                    String type = (performanceNode.get("type") != null) ? performanceNode.get("type").asText() : null;

                    roles.add(Map.entry(title, type));
                }

                String biography = (actorNode.get("biography") != null) ? actorNode.get("biography").asText() : null;
                double averageRating = 0;

                Actor actor = new Actor(name, roles, biography, averageRating);
                Gactors.add(actor);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONParser parser = new JSONParser();

        try {
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("src/test/resources/testResources/requests.json"));

            for (Object obj : jsonArray) {
                JSONObject requestObj = (JSONObject) obj;

                String typeString = (String) requestObj.get("type");
                RequestTypes type = (typeString != null) ? RequestTypes.valueOf(typeString) : null;
                String createdDateString = (String) requestObj.get("createdDate");
                LocalDateTime createdDate = LocalDateTime.parse(createdDateString, DateTimeFormatter.ISO_DATE_TIME);
                String username = (String) requestObj.get("username");
                String to = (String) requestObj.get("to");
                String description = (String) requestObj.get("description");
                String actorName = (String) requestObj.get("actorName");
                String movieTitle = (String) requestObj.get("movieTitle");
                String subject = "";

                if (actorName != null) {
                    subject = actorName;
                } else if (movieTitle != null) {
                    subject = movieTitle;
                }

                Request request = new Request(type, createdDate, username, to, description, subject);
                requests.add(request);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        try {

            ObjectMapper objectMapper = new ObjectMapper();

            File jsonFile = new File("src/test/resources/testResources/accounts.json");

            JsonNode jsonArray = objectMapper.readTree(jsonFile);

            for (JsonNode userNode : jsonArray) {
                String username = userNode.get("username").asText();
                String experienceAsString = userNode.get("experience").asText();
                int experience = "null".equals(experienceAsString) ? 0 : Integer.parseInt(experienceAsString);
                String userType = userNode.get("userType").asText();

                JsonNode informationNode = userNode.get("information");
                String email = informationNode.path("credentials").get("email").asText();
                String password = informationNode.path("credentials").get("password").asText();
                String name = informationNode.get("name").asText();
                String country = informationNode.get("country").asText();
                int age = informationNode.get("age").asInt();
                String gender = informationNode.get("gender").asText();
                String birthDate = informationNode.get("birthDate").asText();

                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                LocalDate date = LocalDate.parse(birthDate, inputFormatter);
                birthDate = outputFormatter.format(date);

                JsonNode productionsContribution = userNode.get("productionsContribution");
                JsonNode actorsContribution = userNode.get("actorsContribution");
                JsonNode favoriteProductions = userNode.get("favoriteProductions");
                JsonNode favoriteActors = userNode.get("favoriteActors");
                JsonNode notificationsJson = userNode.get("notifications");

                List<String> notifications = new ArrayList<>();
                if (notificationsJson != null) {
                    for (JsonNode not : notificationsJson) {
                        String notif = not.asText();
                        notifications.add(notif);
                    }
                }

                List<String> productionContributions = new ArrayList<>();
                if (productionsContribution != null) {
                    for (JsonNode production : productionsContribution) {
                        String movieTitle = production.asText();
                        productionContributions.add(movieTitle);
                    }
                }
                List<String> actorContributions = new ArrayList<>();
                if (actorsContribution != null) {
                    for (JsonNode actors : actorsContribution) {
                        String actorName = actors.asText();
                        actorContributions.add(actorName);
                    }
                }
                SortedSet<Production> favoriteP = new TreeSet<>();
                if (favoriteProductions != null) {
                    for (JsonNode production : favoriteProductions) {
                        String prodName = production.asText();
                        for (Production searchProduction : productions) {
                            if (prodName.equalsIgnoreCase(searchProduction.getTitle())) {
                                favoriteP.add(searchProduction);
                                break;
                            }
                        }
                    }
                }
                SortedSet<Actor> favoriteA = new TreeSet<>();
                if (favoriteActors != null) {
                    for (JsonNode actor : favoriteActors) {
                        String actName = actor.asText();

                        for (Actor searchActor : Gactors) {
                            if (actName.equalsIgnoreCase(searchActor.getName())) {
                                favoriteA.add(searchActor);
                                break;
                            }
                        }
                    }
                }
                User.Information information = new User.Information.Builder()
                        .credentials(email, password)
                        .name(name)
                        .country(country)
                        .age(age)
                        .gender(gender)
                        .birthDate(LocalDate.parse(birthDate).atStartOfDay())
                        .build();

                User<String> user = UserFactory.createUser(userType, username, experience, information, productionContributions, actorContributions, favoriteP, favoriteA, notifications);

                users.add(user);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
