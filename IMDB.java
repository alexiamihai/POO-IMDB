package org.example;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class IMDB {
    private static IMDB instance = null;
    List<User<String>> users = new ArrayList<>();
    List<Production> productions = new ArrayList<>();

    List<Actor> actors = new ArrayList<>();

    boolean loggedIn;

    public static IMDB getInstance() {
        if (instance == null) {
            instance = new IMDB();
        }
        return instance;
    }

    public void run() {
        try {
            loadUserDataFromJSON();
            authenticateUser();
        } catch (IOException e) {
            System.out.println("OH NO: " + e.getMessage());
        }
    }

    private void loadUserDataFromJSON() throws IOException {

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
                actors.add(actor);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONParser parser = new JSONParser();

        try {
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("src/test/resources/testResources/requests.json"));

            List<Request> requests = new ArrayList<>();
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

                        for (Actor searchActor : actors) {
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
        for (Production p : productions) {
            addObserversForProduction(p);
            sortRatings(p.getRatings());
        }
        for (Actor a : actors) {
            addObserversForActor(a);
        }
    }
    public void displayUserExperience(List<User<String>> users) {
        for(User u : users) {
            System.out.print("Username: " + u.getUsername() + "-");
            System.out.println("Experience: " + u.getExperience());
        }

    }

    public List<Production> getProductions() {
        return productions;
    }

    public void displayUser(User user) {
        System.out.println("Username: " + user.getUsername());
        System.out.println("Experience: " + user.getExperience());
        System.out.println("User Type: " + user.getAccountType());
        System.out.println("Email: " +  user.getInformation().getEmail());
        System.out.println("Password: " +  user.getInformation().getPassword());
        System.out.println("Name: " + user.getInformation().getName());
        System.out.println("Country: " +  user.getInformation().getCountry());
        System.out.println("Age: " +  user.getInformation().getAge());
        System.out.println("Gender: " +  user.getInformation().getGender());
        System.out.println("Birth Date: " +  user.getInformation().getBirthDate());
        System.out.println("Productions Contribution: " + user.getProductionsContribution());
        System.out.println("Actors Contribution: " + user.getActorsContribution());
        System.out.println("Favorite Actors: " + user.getFavoriteActors());
        System.out.println("Favorite Productions: " + user.getFavoriteProductions());
        System.out.println("Notifications: " + user.getNotifications());
        System.out.println("----------------------------");
    }



    public void authenticateUser() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome back! Enter your credentials: \n");
        System.out.print("email: ");
        String email = scanner.nextLine();

        System.out.print("password: ");
        String password = scanner.nextLine();
        User.Information.Credentials credentials = null;

        Optional<User<String>> authenticatedUser = users.stream()
                .filter(user -> user.getInformation().getEmail().equals(email) && user.getInformation().getPassword().equals(password))
                .findFirst();
        loggedIn = authenticatedUser.isPresent();
        if (loggedIn) {
            System.out.println("Welcome back user " + authenticatedUser.get().getUsername() + "!");
//            System.out.println("Username: " + authenticatedUser.get().getUsername());
//            if (authenticatedUser.get().getExperience() == 0) {
//                System.out.println("User experience: -");
//            } else {
//                System.out.println("User experience: " + authenticatedUser.get().getExperience());
//            }
            displayUser(authenticatedUser.get());

            while (loggedIn) {
                performActions(authenticatedUser.get().getAccountType(), authenticatedUser.get());
            }
        } else {
            System.out.println("Login failed. Incorrect username or password.");
            authenticateUser();
        }
    }

    private void performActions(AccountType userRole, User user) {
        System.out.println("Choose action:");
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        switch (userRole) {
            case REGULAR:
                System.out.println("SUNT REG:");
                System.out.println("     1) View production details");
                System.out.println("     2) View actor details");
                System.out.println("     3) View notifications");
                System.out.println("     4) Search for actor/movie/series");
                System.out.println("     5) Add/Delete actor/movie/series to/from favorites");
                System.out.println("     6) Create/Withdraw request");
                System.out.println("     7) Add/Delete rating for a production/actor");
                System.out.println("     8) Logout");

                boolean isValidInputReg = false;

                do {
                    System.out.print("Enter your choice: ");
                    String choiceInput = scanner.nextLine();

                    try {
                        choice = Integer.parseInt(choiceInput);
                        isValidInputReg = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid option (numeric value)!");
                    }
                } while (!isValidInputReg);
                switch (choice) {
                    case 1:
                        viewProductionDetails(userRole);
                        break;
                    case 2:
                        viewActorDetails(userRole);
                        break;
                    case 3:
                        viewNotifications(user);
                        break;
                    case 4:
                        searchForMedia(userRole);
                        break;
                    case 5:
                        addOrDeleteFavorite(user);
                        break;
                    case 6:
                        createOrWithdrawRequest(user);
                        break;
                    case 7:
                        addOrDeleteRating(user);
                        break;
                    case 8:
                        logout(user);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
                break;
            case CONTRIBUTOR:
                System.out.println("You are a CONTRIBUTOR!:");
                System.out.println("     1) View production details");
                System.out.println("     2) View actor details");
                System.out.println("     3) View notifications");
                System.out.println("     4) Search for actor/movie/series");
                System.out.println("     5) Add/Delete actor/movie/series to/from favorites");
                System.out.println("     6) Create/Withdraw request");
                System.out.println("     7) Add/Delete actor/movie/series from system");
                System.out.println("     8) Update Movie Details");
                System.out.println("     9) Update Actor Details");
                System.out.println("     10) Solve a request");
                System.out.println("     11) Logout");

                boolean isValidInput = false;

                do {
                    System.out.print("Enter your choice: ");
                    String choiceInput = scanner.nextLine();

                    try {
                        choice = Integer.parseInt(choiceInput);
                        isValidInput = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid option (numeric value)!");
                    }
                } while (!isValidInput);
                switch (choice) {
                    case 1:
                        viewProductionDetails(userRole);
                        break;
                    case 2:
                        viewActorDetails(userRole);
                        break;
                    case 3:
                        viewNotifications(user);
                        break;
                    case 4:
                        searchForMedia(userRole);
                        break;
                    case 5:
                        addOrDeleteFavorite(user);
                        break;
                    case 6:
                        createOrWithdrawRequest(user);
                        break;
                    case 7:
                        addOrDeleteFromSystem(user);
                        break;
                    case 8:
                        updateMovieDetails(user);
                        break;
                    case 9:
                        updateActorDetails(user);
                        break;
                    case 10:
                        solveRequest(user);
                        break;
                    case 11:
                        logout(user);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
                break;

            case ADMIN:
                System.out.println("You're an ADMIN!");
                System.out.println("     1) View production details");
                System.out.println("     2) View actor details");
                System.out.println("     3) View notifications");
                System.out.println("     4) Search for actor/movie/series");
                System.out.println("     5) Add/Delete actor/movie/series to/from favorites");
                System.out.println("     6) Add/Delete user");
                System.out.println("     7) Add/Delete actor/movie/series from system");
                System.out.println("     8) Update Movie Details");
                System.out.println("     9) Update Actor Details");
                System.out.println("     10) Solve a request");
                System.out.println("     11) Logout");


                boolean isValidInputAdm = false;

                do {
                    System.out.print("Enter your choice: ");
                    String choiceInput = scanner.nextLine();

                    try {
                        choice = Integer.parseInt(choiceInput);
                        isValidInputAdm = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid option (numeric value)!");
                    }
                } while (!isValidInputAdm);

                switch (choice) {
                    case 1:
                        viewProductionDetails(userRole);
                        break;
                    case 2:
                        viewActorDetails(userRole);
                        break;
                    case 3:
                        viewNotifications(user);
                        break;
                    case 4:
                        searchForMedia(userRole);
                        break;
                    case 5:
                        addOrDeleteFavorite(user);
                        break;
                    case 6:
                        addOrDeleteUser(user);
                        break;
                    case 7:
                        addOrDeleteFromSystem(user);
                        break;
                    case 8:
                        updateMovieDetails(user);
                        break;
                    case 9:
                        updateActorDetails(user);
                        break;
                    case 10:
                        solveRequest(user);
                        break;
                    case 11:
                        logout(user);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
                break;

            default:
                System.out.println("Invalid user role.");
        }
    }

    public List<Rating> sortRatings(List<Rating> ratings) {
        Comparator<Rating> userExperienceComparator = Comparator.comparingInt(rating -> {
            User user = findUserByUsername(rating.getUsername());

            return (user != null) ? user.getExperience() : 0;
        });

        Collections.sort(ratings, userExperienceComparator);

        return ratings;
    }

    private User findUserByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    private void viewProductionDetails(AccountType userRole) {
        //displayUserExperience(users);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to apply filters? (yes/no): ");
        String applyFilters = null;
        boolean okay = false;
        while(okay == false) {
            applyFilters = scanner.nextLine().toLowerCase();
            if("yes".equalsIgnoreCase(applyFilters) || "no".equalsIgnoreCase(applyFilters)) {
                okay = true;
            }
            else {
                System.out.println("Please enter yes/no!");
            }
        }
        if ("yes".equalsIgnoreCase(applyFilters)) {
            System.out.println("How would you like to filter the results?: ");
            System.out.println("1. By genre");
            System.out.println("2. By number of ratings");
            System.out.println("3. By genre and by number of ratings");
            System.out.println("4. By number of actors");
            System.out.println("5. By number of directors");
            System.out.println("6. By number of genres");
            System.out.println("7. By title");
            System.out.println("8. By average rating");
            Genre genre;
            int numRatings, averageRating, numActors, numDirectors, numGenres;
            char letter;
            int typeFilter = 0;
            boolean isValidInputGenre = false;

            do {
                System.out.print("Enter your choice: ");
                String choiceInput = scanner.nextLine();

                try {
                    typeFilter = Integer.parseInt(choiceInput);
                    isValidInputGenre = true;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid option (numeric value)!");
                }
            } while (!isValidInputGenre);

            List<Production> filteredProductions;

            switch (typeFilter) {
                case 1:
                    genre = readAndValidateGenre();
                    System.out.println("Filters applied - Genre: " + genre);
                    filteredProductions = filterProductionsByGenre(productions, genre);
                    if (filteredProductions == null) System.out.println("Sorry! Couldn't find any match!");
                    else {
                        filteredProductions.sort(Comparator.comparing(Production::getTitle));
                        for (Production production : filteredProductions) {
                            production.displayInfo();
                        }
                    }
                    break;
                case 2:
                    numRatings = readAndValidateNumRatings();
                    System.out.println("Filters applied - Number of Ratings: " + numRatings);
                    filteredProductions = filterProductionsByRatings(productions, numRatings);
                    if (filteredProductions == null) System.out.println("Sorry! Couldn't find any match!");
                    else {
                        filteredProductions.sort(Comparator.comparing(Production::getTitle));
                        for (Production production : filteredProductions) {
                            production.displayInfo();
                            }
                    }
                    break;
                case 3:
                    genre = readAndValidateGenre();
                    numRatings = readAndValidateNumRatings();
                    System.out.println("Filters applied - Genre: " + genre + ", Number of Ratings: " + numRatings);
                    filteredProductions = filterProductionsByGenreAndRatings(productions, genre, numRatings);
                    if (filteredProductions == null) System.out.println("Sorry! Couldn't find any match!");
                    else {
                        filteredProductions.sort(Comparator.comparing(Production::getTitle));
                        for (Production production : filteredProductions) {
                            production.displayInfo();
                        }
                    }
                    break;
                case 4:
                    numActors = readAndValidateNumActors();
                    System.out.println("Filters applied - Number of Actors: " + numActors);
                    filteredProductions = filterProductionsByNumActors(productions, numActors);
                    if (filteredProductions == null) System.out.println("Sorry! Couldn't find any match!");
                    else {
                        filteredProductions.sort(Comparator.comparing(Production::getTitle));
                        for (Production production : filteredProductions) {
                            production.displayInfo();
                        }
                    }
                    break;
                case 5:
                    numDirectors = readAndValidateNumDirectors();
                    System.out.println("Filters applied - Number of Directors: " + numDirectors);
                    filteredProductions = filterProductionsByNumDirectors(productions, numDirectors);
                    if (filteredProductions == null) System.out.println("Sorry! Couldn't find any match!");
                    else {
                        filteredProductions.sort(Comparator.comparing(Production::getTitle));
                        for (Production production : filteredProductions) {
                            production.displayInfo();
                        }
                    }
                    break;
                case 6:
                    numGenres = readAndValidateNumGenres();
                    System.out.println("Filters applied - Number of Genres: " + numGenres);
                    filteredProductions = filterProductionsByNumGenres(productions, numGenres);
                    if (filteredProductions == null) System.out.println("Sorry! Couldn't find any match!");
                    else {
                        filteredProductions.sort(Comparator.comparing(Production::getTitle));
                        for (Production production : filteredProductions) {
                            production.displayInfo();
                        }
                    }
                    break;
                case 7:
                    letter = readAndValidateTitle();
                    System.out.println("Filters applied - Title: " + letter);
                    filteredProductions = filterProductionsByTitle(productions, letter);
                    if (filteredProductions == null) System.out.println("Sorry! Couldn't find any match!");
                    else {
                        filteredProductions.sort(Comparator.comparing(Production::getTitle));
                        for (Production production : filteredProductions) {
                            production.displayInfo();
                        }
                    }
                    break;
                case 8:
                    averageRating = readAndValidateAverageRatings();
                    System.out.println("Filters applied - Average Rating: " + averageRating);
                    filteredProductions = filterProductionsByAverageRatings(productions, averageRating);
                    if (filteredProductions == null) System.out.println("Sorry! Couldn't find any match!");
                    else {
                        filteredProductions.sort(Comparator.comparing(Production::getTitle));

                        for (Production production : filteredProductions) {
                            production.displayInfo();
                        }
                    }
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        } else {
                System.out.println("No filters applied. Proceeding without filtering.");
                productions.sort(Comparator.comparing(Production::getTitle));

                for (Production production : productions) {
                    production.displayInfo();
                    System.out.println();
                }
        }
    }

    private static Genre readAndValidateGenre() {
        Scanner scanner = new Scanner(System.in);
        boolean validGenre = false;
        Genre genre = null;
        System.out.println("Enter genre:");
        while (!validGenre) {
            try {
                String genreString = scanner.nextLine();

                if (!Objects.equals(genreString, "SF")) {
                    genreString = genreString.substring(0, 1).toUpperCase() + genreString.substring(1).toLowerCase();
                }
                if (genreString.equalsIgnoreCase("SF")) {
                    genreString = genreString.toUpperCase();
                }
                genre = Genre.valueOf(genreString);
                validGenre = true;
                System.out.println("Chosen genre: " + genre);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid genre. Please choose a valid genre. Enter again:");
            }
        }
        return genre;
    }

    private static int readAndValidateNumRatings() {
        int numRatings = 0;
        boolean validNumRatings = false;
        Scanner scanner = new Scanner(System.in);
        while (!validNumRatings) {
            try {
                System.out.println("Enter the number of ratings: ");
                numRatings = Integer.parseInt(scanner.nextLine());
                if (numRatings > 0) {
                    validNumRatings = true;
                    System.out.println("Chosen number of ratings: " + numRatings);
                } else {
                    System.out.println("Invalid input. Please enter a valid positive integer for the number of ratings. Enter again:");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid positive integer for the number of ratings. Enter again:");
            }
        }
        return numRatings;
    }

    private static int readAndValidateNumActors() {
        int numActors = 0;
        boolean validNumRatings = false;
        Scanner scanner = new Scanner(System.in);
        while (!validNumRatings) {
            try {
                System.out.println("Enter the number of actors: ");
                numActors = Integer.parseInt(scanner.nextLine());
                if (numActors > 0) {
                    validNumRatings = true;
                    System.out.println("Chosen number of actors: " + numActors);
                } else {
                    System.out.println("Invalid input. Please enter a valid positive integer for the number of actors. Enter again:");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid positive integer for the number of actors. Enter again:");
            }
        }
        return numActors;
    }

    private static int readAndValidateNumDirectors() {
        int numDirectors = 0;
        boolean validNumRatings = false;
        Scanner scanner = new Scanner(System.in);
        while (!validNumRatings) {
            try {
                System.out.println("Enter the number of directors: ");
                numDirectors = Integer.parseInt(scanner.nextLine());

                if (numDirectors > 0) {
                    validNumRatings = true;
                    System.out.println("Chosen number of directors: " + numDirectors);
                } else {
                    System.out.println("Invalid input. Please enter a valid positive integer for the number of directors. Enter again:");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid positive integer for the number of directors. Enter again:");
            }
        }
        return numDirectors;
    }

    private static int readAndValidateNumGenres() {
        int numGenres = 0;
        boolean validNumRatings = false;
        Scanner scanner = new Scanner(System.in);
        while (!validNumRatings) {
            try {
                System.out.println("Enter the number of genres: ");
                numGenres = Integer.parseInt(scanner.nextLine());
                if (numGenres > 0) {
                    validNumRatings = true;
                    System.out.println("Chosen number of genres: " + numGenres);
                } else {
                    System.out.println("Invalid input. Please enter a valid positive integer for the number of genres. Enter again:");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid positive integer for the number of genres. Enter again:");
            }
        }
        return numGenres;
    }

    private static char readAndValidateTitle() {
        char letter = '\0';
        boolean validInput = false;
        Scanner scanner = new Scanner(System.in);

        while (!validInput) {
            try {
                System.out.println("Enter the first letter of the production: ");
                String input = scanner.nextLine();

                if (input.length() == 1) {
                    letter = input.charAt(0);
                    validInput = true;
                } else {
                    System.out.println("Invalid input. Please enter exactly one character. Enter again:");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid character. Enter again:");
            }
        }

        return letter;
    }

    private static int readAndValidateAverageRatings() {
        int averageRating = 0;
        boolean validAverageNumRatings = false;
        Scanner scanner = new Scanner(System.in);
        while (!validAverageNumRatings) {
            try {
                System.out.println("Enter the average rating: ");
                averageRating = Integer.parseInt(scanner.nextLine());

                if (averageRating > 0) {
                    validAverageNumRatings = true;
                    System.out.println("Chosen average rating: " + averageRating);
                } else {
                    System.out.println("Invalid input. Please enter a valid positive integer for the average rating. Enter again:");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid positive integer for the average ratings. Enter again:");
            }
        }
        return averageRating;
    }

    private List<Production> filterProductionsByGenreAndRatings(List<Production> productions, Genre genre, int minRatings) {
        System.out.println("Genre: " + genre);
        System.out.println("Minimum Ratings: " + minRatings);
        String genreString = String.valueOf(genre);
        List<Production> filteredList = productions.stream()
                .filter(production -> {
                    List<Genre> genres = production.getGenres();
                    List<Rating> ratings = production.getRatings();

                    boolean genreMatch = genres.contains(genreString);
                    boolean ratingsMatch = ratings.size() >= minRatings;
                    return genreMatch && ratingsMatch;
                })
                .collect(Collectors.toList());
        return filteredList;
    }

    private List<Production> filterProductionsByRatings(List<Production> productions, int minRatings) {
        System.out.println("Minimum Ratings: " + minRatings);
        List<Production> filteredList = productions.stream()
                .filter(production -> {
                    List<Rating> ratings = production.getRatings();
                    boolean ratingsMatch = ratings.size() >= minRatings;
                    return ratingsMatch;
                })
                .collect(Collectors.toList());
        return filteredList;
    }

    private List<Production> filterProductionsByNumActors(List<Production> productions, int minActors) {
        System.out.println("Minimum Actors: " + minActors);
        List<Production> filteredList = productions.stream()
                .filter(production -> {
                    List<String> actors = production.getActors();
                    boolean actorMatch = actors.size() >= minActors;
                    return actorMatch;
                })
                .collect(Collectors.toList());
        return filteredList;
    }

    private List<Production> filterProductionsByNumDirectors(List<Production> productions, int minDirectors) {
        System.out.println("Minimum Directors: " + minDirectors);
        List<Production> filteredList = productions.stream()
                .filter(production -> {
                    List<String> directors = production.getDirectors();
                    boolean directorMatch = directors.size() >= minDirectors;
                    return directorMatch;
                })
                .collect(Collectors.toList());
        return filteredList;
    }

    private List<Production> filterProductionsByNumGenres(List<Production> productions, int minGenres) {
        System.out.println("Minimum Genres: " + minGenres);
        List<Production> filteredList = productions.stream()
                .filter(production -> {
                    List<Genre> genres = production.getGenres();
                    boolean genreMatch = genres.size() >= minGenres;
                    return genreMatch;
                })
                .collect(Collectors.toList());
        return filteredList;
    }

    private List<Production> filterProductionsByTitle(List<Production> productions, char letter) {
        System.out.println("Title begins with letter: " + letter);
        List<Production> filteredList = productions.stream()
                .filter(production -> {
                    String title = production.getTitle();
                    boolean titleMatch = title.charAt(0) == letter;
                    return titleMatch;
                })
                .collect(Collectors.toList());
        return filteredList;
    }

    private List<Production> filterProductionsByAverageRatings(List<Production> productions, int minAverageRatings) {
        System.out.println("Minimum Average Rating: " + minAverageRatings);
        List<Production> filteredList = productions.stream()
                .filter(production -> {
                    double ratings = production.getAverageRating();
                    boolean ratingsMatch = ratings >= minAverageRatings;
                    return ratingsMatch;
                })
                .collect(Collectors.toList());
        return filteredList;
    }

    private List<Production> filterProductionsByGenre(List<Production> productions, Genre genre) {
        System.out.println("Genre: " + genre);
        String genreString = String.valueOf(genre);
        List<Production> filteredList = productions.stream()
                .filter(production -> {
                    List<Genre> genres = production.getGenres();
                    boolean genreMatch = genres.contains(genreString);
                    return genreMatch;
                })
                .collect(Collectors.toList());
        return filteredList;
    }


    private void viewActorDetails(AccountType userRole) {
        Scanner scanner = new Scanner(System.in);
        boolean done = false;

        System.out.println("Do you want to sort the results?: ");
        while(done == false) {
            String sortActors = scanner.nextLine().toLowerCase();

            if ("yes".equalsIgnoreCase(sortActors)) {
                System.out.println("Sorting by name: ");
                actors.sort(Comparator.comparing(Actor::getName));
                for (Actor actor : actors) {
                    actor.displayInfo();
                    System.out.println();
                }
                done = true;

            } else {
                if ("no".equalsIgnoreCase(sortActors)) {
                    System.out.println("No filters applied. Proceeding without filtering.");

                    for (Actor actor : actors) {
                        actor.displayInfo();
                        System.out.println();
                    }
                    done = true;
                } else {
                    System.out.println("Not a valid choice. Enter yes or no");
                }
            }
        }

    }


    private void viewNotifications(User user) {
        List<String> notifications = user.getNotifications();

        if (notifications.isEmpty()) {
            System.out.println("You have no notifications.");
        } else {
            System.out.println("These are your notifications:");

            for (String notification : notifications) {
                System.out.println("Notification: " + notification);
            }

        }
    }


    private void searchForMedia(AccountType userRole) {
        Scanner scanner = new Scanner(System.in);
        boolean valid = true;
        while (valid) {
            System.out.println("Choose an option:");
            System.out.println("1. Search for Actor");
            System.out.println("2. Search for Production");

            int choice = 0;
            boolean isValidInput = false;

            do {
                System.out.print("Enter your choice: ");
                String choiceInput = scanner.nextLine();

                try {
                    choice = Integer.parseInt(choiceInput);
                    isValidInput = true;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid option (numeric value)!");
                }
            } while (!isValidInput);
            switch (choice) {
                case 1:
                    System.out.println("Enter actor name to search:");
                    String actorName = scanner.nextLine();
                    searchActor(actorName);
                    valid = false;
                    break;

                case 2:
                    System.out.println("Enter production title to search:");
                    String productionTitle = scanner.nextLine();
                    searchProduction(productionTitle);
                    valid = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    private void searchActor(String actorName) {
        int ok = 0;
        System.out.println("Searching for actor: " + actorName);
        for (Actor actor : actors) {
            if (actorName.equalsIgnoreCase(actor.getName())) {
                actor.displayInfo();
                ok = 1;
            }
        }
        if (ok == 0) System.out.println("Sorry! Couldn't find what you were looking for!");
    }

    private void searchProduction(String productionTitle) {
        int ok = 0;
        System.out.println("Searching for production: " + productionTitle);
        for (Production production : productions) {
            if (productionTitle.equalsIgnoreCase(production.getTitle())) {
                production.displayInfo();
                ok = 1;
            }
        }
        if (ok == 0) System.out.println("Sorry! Couldn't find what you were looking for!");
    }


    private void addToFavorites(User user) {
        Scanner scanner = new Scanner(System.in);
        boolean valid = true;

        while (valid) {
            System.out.println("Choose an option:");
            System.out.println("1. Add Actor to Favorites");
            System.out.println("2. Add Production to Favorites");

            int choice = 0;
            boolean isValidInput = false;

            do {
                System.out.print("Enter your choice: ");
                String choiceInput = scanner.nextLine();

                try {
                    choice = Integer.parseInt(choiceInput);
                    isValidInput = true;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid option (numeric value)!");
                }
            } while (!isValidInput);
            int ok = 0;
            switch (choice) {
                case 1:
                    System.out.println("Enter actor name to add to favorites:");
                    String actorName = scanner.nextLine();
                    int addedact = 0;
                    for (Object actorObject : user.getFavoriteActors()) {
                            if (actorObject instanceof Actor) {;
                            Actor act = (Actor) actorObject;
                            String actName = act.getName();
                            if(actName.equals(actorName)) {
                                addedact = 1;
                                break;
                            }
                        }
                    }
                    if(addedact == 1) {
                        System.out.println("You have already added this actor to your favorites!");
                        valid = false;
                        break;
                    }
                    ok = 0;
                    for (Actor actor : actors) {
                        if (actorName.equalsIgnoreCase(actor.getName())) {
                            user.addFavoriteActor(actor);
                            ok = 1;
                        }
                    }
                    if (ok == 1) System.out.println("You have successfully added the actor to your favorites list!");
                    else System.out.println("Sorry couldn't find the actor!");
                    if(user.getFavoriteActors().isEmpty()) {
                        System.out.println("Your list of favorite actors is empty.");
                    }
                    else {
                        System.out.println("Your list of favorite actors, user " + user.getUsername() + ": ");
                        int i = 0;
                        for (Object actorObject : user.getFavoriteActors()) {
                            if (actorObject instanceof Actor) {
                                i++;
                                Actor actor = (Actor) actorObject;
                                String actorName1 = actor.getName();
                                System.out.println( i +") " + actorName1);
                            }
                        }
                    }
                    valid = false;
                    break;

                case 2:
                    System.out.println("Enter production title to add to favorites:");
                    String productionTitle = scanner.nextLine();
                    int added = 0;
                    for (Object productionObject : user.getFavoriteProductions()) {
                        if (productionObject instanceof Production) {;
                            Production pro = (Production) productionObject;
                            String productionName = pro.getTitle();
                            if(productionName.equals(productionTitle)) {
                                added = 1;
                                break;
                            }
                        }
                    }
                    if(added == 1) {
                        System.out.println("You have already added this production to your favorites!");
                        valid = false;
                        break;
                    }
                    ok = 0;
                    for (Production production : productions) {
                        if (productionTitle.equalsIgnoreCase(production.getTitle())) {
                            user.addFavoriteProduction(production);
                            ok = 1;
                            break;
                        }
                    }
                        if (ok == 1)
                            System.out.println("You have successfully added the production to your favorites list!");
                        else System.out.println("Sorry couldn't find the production!");
                    if(user.getFavoriteProductions().isEmpty()) {
                        System.out.println("Your list of favorite productions is empty.");
                    }
                    else {
                        System.out.println("Your list of favorite productions, user " + user.getUsername() + ": ");
                        int j = 0;
                        for (Object productionObject : user.getFavoriteProductions()) {
                            if (productionObject instanceof Production) {
                                j++;
                                Production production = (Production) productionObject;
                                String productionName = production.getTitle();
                                System.out.println( j +") " + productionName);
                            }
                        }
                    }
                    valid = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    private void deleteFavorite(User user) {
        Scanner scanner = new Scanner(System.in);
        boolean valid = true;

        while (valid) {
            System.out.println("Choose an option:");
            System.out.println("1. Delete Actor from Favorites");
            System.out.println("2. Delete Production from Favorites");

            int choice = 0;
            boolean isValidInput = false;

            do {
                System.out.print("Enter your choice: ");
                String choiceInput = scanner.nextLine();

                try {
                    choice = Integer.parseInt(choiceInput);
                    isValidInput = true;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid option (numeric value)!");
                }
            } while (!isValidInput);

            switch (choice) {
                case 1:
                    System.out.println("These are your favorite actors, user " + user.getUsername() + ": ");
                    int k = 0;
                    for (Object actorObject : user.getFavoriteActors()) {
                        if (actorObject instanceof Actor) {
                            k++;
                            Actor actor = (Actor) actorObject;
                            String actorName1 = actor.getName();
                            System.out.println( k +") " + actorName1);
                        }
                    }
                    System.out.println("Enter actor name to delete from favorites:");
                    String actorName = scanner.nextLine();
                    int ok = 0;
                    for (Actor actor : actors) {
                        if (actorName.equalsIgnoreCase(actor.getName())) {
                            user.deleteFavoriteActor(actor);
                            ok = 1;
                        }
                    }
                    if (ok == 1)
                        System.out.println("You have successfully deleted the actor from your favorites list!");
                    else System.out.println("Sorry! Couldn't find that actor!");
                    System.out.println("Your list of favorite productions, user " + user.getUsername() + ": ");
                    int i = 0;
                    for (Object actorObject : user.getFavoriteActors()) {
                        if (actorObject instanceof Actor) {
                            i++;
                            Actor actor = (Actor) actorObject;
                            String actorName1 = actor.getName();
                            System.out.println( i +") " + actorName1);
                        }
                    }
                    valid = false;
                    break;

                case 2:
                    System.out.println("These are your favorite productions, user " + user.getUsername() + ": ");
                    int l = 0;
                    for (Object productionObject : user.getFavoriteProductions()) {
                        if (productionObject instanceof Production) {
                            l++;
                            Production production = (Production) productionObject;
                            String productionName = production.getTitle();
                            System.out.println( l +") " + productionName);
                        }
                    }
                    System.out.println("Enter production title to delete from favorites:");
                    String productionTitle = scanner.nextLine();
                    int ok2 = 0;
                    for (Production production : productions) {
                        if (productionTitle.equalsIgnoreCase(production.getTitle())) {
                            user.deleteFavoriteProduction(production);
                            ok2 = 1;
                            break;
                        }
                    }
                    if (ok2 == 1)
                        System.out.println("You have successfully deleted the production from your favorites list!");
                    else System.out.println("Sorry! Couldn't find that production!");
                    System.out.println("Your list of favorite productions, user " + user.getUsername() + ": ");
                    int j = 0;
                    for (Object productionObject : user.getFavoriteProductions()) {
                        if (productionObject instanceof Production) {
                            j++;
                            Production production = (Production) productionObject;
                            String productionName = production.getTitle();
                            System.out.println( j +") " + productionName);
                        }
                    }
                    valid = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }


    private void addOrDeleteFavorite(User user) {
        Scanner scanner = new Scanner(System.in);
        boolean valid = true;
        while (valid) {
            System.out.println("Choose an option:");
            System.out.println("1. Add Favorite");
            System.out.println("2. Delete Favorite");

            int choice = 0;
            boolean isValidInput = false;

            do {
                System.out.print("Enter your choice: ");
                String choiceInput = scanner.nextLine();

                try {
                    choice = Integer.parseInt(choiceInput);
                    isValidInput = true;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid option (numeric value)!");
                }
            } while (!isValidInput);
            System.out.println("Enter name/title:");
            switch (choice) {
                case 1:
                    addToFavorites(user);
                    valid = false;
                    break;

                case 2:
                    deleteFavorite(user);
                    valid = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }


    private void addOrDeleteRating(User user) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Do you want to add or delete a rating for a Production (1) or an Actor (2)?");
        int deleteChoice = getUserChoice(scanner, 1, 2);

        switch (deleteChoice) {
            case 1:
                // Delete a Production
                addOrDeleteRatingProduction(user);
                break;
            case 2:
                // Delete an Actor
                addOrDeleteRatingActor(user);
                break;
            default:
                System.out.println("Invalid choice. Please choose 1 for Production or 2 for Actor.");
        }
    }

    public void addObserversForProduction(Production p) {
        for (Rating r : p.getRatings()) {
            for (User u : users) {
                if (u.getUsername().equals(r.getUsername())) {
                    r.addObserver(u);
                    break;
                }
            }

        }
    }

    public void addObserversForActor(Actor a) {
        for (Rating r : a.getRatings()) {
            for (User u : users) {
                if (u.getUsername().equals(r.getUsername())) {
                    r.addObserver(u);
                    break;
                }
            }

        }
    }

    private void addOrDeleteRatingProduction(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("These are the available productions:");
        for (Production production : productions) {
            System.out.println(production.getTitle());
        }
        Production chosenProduction = null;
        boolean validprod = true;
        while (validprod) {
            System.out.println("Choose a production:");
            String stringProduction = scanner.nextLine();
            for (Production production : productions) {
                if (stringProduction.equalsIgnoreCase(production.getTitle())) {
                    chosenProduction = production;
                    break;
                }
            }
            if (chosenProduction != null) validprod = false;
            else System.out.println("Enter a valid production!");
        }

        boolean valid = true;
        while (valid) {
            System.out.println("Choose an option:");
            System.out.println("1. Add Rating");
            System.out.println("2. Delete Rating");
            int choice = 0;

            boolean isValidInput = false;

            do {
                System.out.print("Enter your choice: ");
                String choiceInput = scanner.nextLine();

                try {
                    choice = Integer.parseInt(choiceInput);
                    isValidInput = true;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid option (numeric value)!");
                }
            } while (!isValidInput);
            switch (choice) {
                case 1:
                    boolean has_added = false;
                    for (Rating r : chosenProduction.getRatings()) {
                        if (user.getUsername().equals(r.getUsername())) {
                            System.out.println("You have already added a review! Delete it and try again!");
                            has_added = true;
                            break;
                        }
                    }
                    if (has_added == false) {

                        int ok = 0, rating = 0;
                        while (ok == 0) {
                            boolean isValidRating = false;

                            do {
                                System.out.println("Enter a rating (1-10): ");
                                String ratingInput = scanner.nextLine();

                                try {
                                    rating = Integer.parseInt(ratingInput);
                                    isValidRating = true;
                                } catch (NumberFormatException e) {
                                    System.out.println("Please enter a valid option (numeric value)!");
                                }
                            } while (!isValidRating);

                            if (rating <= 0 || rating > 10) {
                                System.out.println("Invalid rating. Try again!");
                            } else ok = 1;
                        }
                        System.out.println("Enter a comment: ");
                        String comment = scanner.nextLine();
                        user.addReview(user, chosenProduction, rating, comment, IMDB.getInstance());
                        if (user instanceof Regular || user instanceof Contributor) {
                            user.setExperienceStrategy(new AddExperienceRating());
                            user.updateExperience();
                        }
                        chosenProduction.calculateAverageRating();
                        System.out.println();
                        System.out.println("You have successfully added a review!");
                        System.out.println();
                        System.out.println("Your experience now:" + user.getExperience());
                        System.out.println();
                        sortRatings(chosenProduction.getRatings());
                        for(Rating r : chosenProduction.getRatings()) {
                            System.out.println("Rating:");
                            System.out.println("->from " + r.getUsername());
                            System.out.println("->rating: " + r.getRating());
                            System.out.println("->comment: " + r.getComment());
                            System.out.println();
                        }
                        System.out.println("Production's rating: " + chosenProduction.getAverageRating());
                    }
                    valid = false;
                    break;

                case 2:
                    boolean ok = false;
                    for (Rating todelete : chosenProduction.getRatings()) {
                        if (todelete.getUsername().equals(user.getUsername())) {
                            chosenProduction.deleteRating(todelete);
                            todelete.removeObserver(user);
                            ok = true;
                            break;
                        }
                    }
                    if (ok == true) {
                        System.out.println("You have successfully deleted your review!");
                        System.out.println();
                        for(Rating r : chosenProduction.getRatings()) {
                            System.out.println("Rating:");
                            System.out.println("->from " + r.getUsername());
                            System.out.println("->rating: " + r.getRating());
                            System.out.println("->comment: " + r.getComment());
                            System.out.println();
                        }
                    } else System.out.println("Sorry! Couldn't find your review!");
                    valid = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void addOrDeleteRatingActor(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("These are the available actors:");
        for (Actor actor : actors) {
            System.out.println(actor.getName());
        }
        System.out.println();
        Actor chosenActor = null;
        boolean validprod = true;
        while (validprod) {
            System.out.println("Choose an actor:");
            String stringActor = scanner.nextLine();
            for (Actor actor : actors) {
                if (stringActor.equalsIgnoreCase(actor.getName())) {
                    chosenActor = actor;
                    break;
                }
            }
            if (chosenActor != null) validprod = false;
            else System.out.println("Enter a valid actor!");
        }

        boolean valid = true;
        while (valid) {
            System.out.println("Choose an option:");
            System.out.println("1. Add Rating");
            System.out.println("2. Delete Rating");

            int choice = 0;

            boolean isValidInput = false;

            do {
                System.out.print("Enter your choice: ");
                String choiceInput = scanner.nextLine();

                try {
                    choice = Integer.parseInt(choiceInput);
                    isValidInput = true;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid option (numeric value)!");
                }
            } while (!isValidInput);
            switch (choice) {
                case 1:
                    boolean has_added = false;
                    for (Rating r : chosenActor.getRatings()) {
                        if (user.getUsername().equals(r.getUsername())) {
                            System.out.println("You have already added a review! Delete it and try again!");
                            has_added = true;
                            break;
                        }
                    }
                    if (has_added == false) {
                        int ok = 0, rating = 0;
                        while (ok == 0) {
                            boolean isValidRating = false;

                            do {
                                System.out.println("Enter a rating (1-10): ");
                                String ratingInput = scanner.nextLine();

                                try {
                                    rating = Integer.parseInt(ratingInput);
                                    isValidRating = true;
                                } catch (NumberFormatException e) {
                                    System.out.println("Please enter a valid option (numeric value)!");
                                }
                            } while (!isValidRating);

                            if (rating <= 0 || rating > 10) {
                                System.out.println("Invalid rating. Try again!");
                            } else ok = 1;
                        }

                        System.out.println("Enter a comment: ");
                        String comment = scanner.nextLine();
                        user.addReviewActor(user, chosenActor, rating, comment, IMDB.getInstance());
                        if (user instanceof Regular || user instanceof Contributor) {
                            user.setExperienceStrategy(new AddExperienceRating());
                            user.updateExperience();
                        }
                        chosenActor.calculateAverageRating();
                        System.out.println("You have successfully added a review!");
                        System.out.println();
                        System.out.println("Your experience now:" + user.getExperience());
                        System.out.println();
                        System.out.println("Actor's rating: " + chosenActor.getAverageRating());
                        System.out.println();
                        for(Rating r : chosenActor.getRatings()) {
                            System.out.println("Rating:");
                            System.out.println("->from " + r.getUsername());
                            System.out.println("->rating: " + r.getRating());
                            System.out.println("->comment: " + r.getComment());
                            System.out.println();
                        }
                    }
                    valid = false;
                    break;

                case 2:
                    boolean ok = false;
                    for (Rating todelete : chosenActor.getRatings()) {
                        if (todelete.getUsername().equals(user.getUsername())) {
                            chosenActor.deleteRating(todelete);
                            todelete.removeObserver(user);
                            ok = true;
                            break;
                        }
                    }
                    if (ok == true) {
                        System.out.println("You have successfully deleted your review!");
                        System.out.println();
                        for(Rating r : chosenActor.getRatings()) {
                            System.out.println("Rating:");
                            System.out.println("->from " + r.getUsername());
                            System.out.println("->rating: " + r.getRating());
                            System.out.println("->comment: " + r.getComment());
                            System.out.println();
                        }
                    } else System.out.println("Sorry! Couldn't find your review!");
                    valid = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void createRequest(User user) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("If your request is related to an actor or a production, please enter the name. Otherwise, just enter '-'.");
        String name = scanner.nextLine().trim();
        if (!name.equals("-")) {
            int ok1 = 0;
            for (Actor a : actors) {
                if (a.getName().equalsIgnoreCase(name)) {
                    ok1 = 1;
                    break;
                }
            }
            for (Production p : productions) {
                if (p.getTitle().equalsIgnoreCase(name)) {
                    ok1 = 1;
                    break;
                }
            }
            if (ok1 == 0) {
                System.out.println("Invalid name!");
                return;
            }
        }
        RequestTypes selectedType = null;
        if (!name.equals("-")) {
            System.out.println("Select the type of request:");
            System.out.println("1. ACTOR_ISSUE");
            System.out.println("2. MOVIE_ISSUE");

            int choice = 0;
            boolean isValidInput = false;

            do {
                System.out.print("Enter your choice: ");
                String choiceInput = scanner.nextLine();

                try {
                    choice = Integer.parseInt(choiceInput);
                    isValidInput = true;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid option (numeric value)!");
                }
            } while (!isValidInput);

            switch (choice) {
                case 1:
                    selectedType = RequestTypes.ACTOR_ISSUE;
                    int ok2 = 0;
                    for (Actor a : actors) {
                        if (a.getName().equalsIgnoreCase(name)) {
                            ok2 = 1;
                            break;
                        }
                    }
                    if (ok2 == 0) {
                        System.out.println("The actor does not exist.");
                        return;
                    }
                    if (user instanceof Contributor) {
                        if (user.getActorsContribution().contains(name)) {
                            System.out.println("The actor has already been added by you in the system. You cannot make a request for this actor.");
                            return;
                        }
                    }
                    break;
                case 2:
                    selectedType = RequestTypes.MOVIE_ISSUE;
                    int ok3 = 0;
                    for (Production a : productions) {
                        if (a.getTitle().equalsIgnoreCase(name)) {
                            ok3 = 1;
                            break;
                        }
                    }
                    if (ok3 == 0) {
                        System.out.println("The production does not exist.");
                        return;
                    }
                    if (user instanceof Contributor) {
                        if (user.getProductionsContribution().contains(name)) {
                            System.out.println("The production has already been added by you in the system. You cannot make a request for this actor.");
                            return;
                        }
                    }
                    break;
                default:
                    System.out.println("Invalid choice");
                    return;
            }

        } else {
            System.out.println("Select the type of request:");
            System.out.println("1. DELETE_ACCOUNT");
            System.out.println("2. OTHERS");

            int choice = 0;
            boolean isValidInput = false;

            do {
                System.out.print("Enter your choice: ");
                String choiceInput = scanner.nextLine();

                try {
                    choice = Integer.parseInt(choiceInput);
                    isValidInput = true;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid option (numeric value)!");
                }
            } while (!isValidInput);
            switch (choice) {
                case 1:
                    selectedType = RequestTypes.DELETE_ACCOUNT;
                    break;
                case 2:
                    selectedType = RequestTypes.OTHERS;
                    break;
                default:
                    System.out.println("Invalid choice");
                    return;
            }
        }

        System.out.println("Enter the reason/description for the request:");
        String description = scanner.nextLine();

        String usernameToAssign = "";
        if (selectedType == RequestTypes.DELETE_ACCOUNT || selectedType == RequestTypes.OTHERS) {
            usernameToAssign = "ADMIN";
        } else {
            if (selectedType == RequestTypes.ACTOR_ISSUE) {
                int ok = 0;
                for (User u : users) {
                    if (u.getActorsContribution().contains(name)) {
                        usernameToAssign = u.getUsername();
                        ok = 1;
                    }
                }
                if (ok == 0) {

                    for (User u : users) {
                        if (u.getAccountType() == AccountType.ADMIN) {
                            Admin a = (Admin) u;
                            List<Actor> addedActors = a.getAddedActorsByTeam();
                            if (addedActors.contains(name)) {
                                usernameToAssign = "ADMIN";
                            }
                        }
                    }
                }
            }
            if (selectedType == RequestTypes.MOVIE_ISSUE) {
                for (User u : users) {
                    if (u.getProductionsContribution().contains(name)) {
                        usernameToAssign = u.getUsername();
                    }
                }
            }
        }

        // Create a new request
        Request newRequest = new Request(
                selectedType,
                LocalDateTime.now(),
                user.getUsername(),
                usernameToAssign,
                description,
                name
        );
        user.getUserRequests().add(newRequest);
        RequestsHolder.addRequest(newRequest);
        if (usernameToAssign != "ADMIN") {
            for (User u : users) {
                if (u.getUsername().equals(newRequest.getToo())) {
                    newRequest.assignToUser(u, newRequest);
                    newRequest.addObserver(u);
                }
            }
        } else {
            for (User u : users) {
                if (u instanceof Admin) {
                    newRequest.assignToUser(u, newRequest);
                    newRequest.addObserver(u);
                }
            }
        }
        newRequest.notifyObservers("New request created: " + description);
    }

    private void withdrawRequest(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("These are your requests:");
        List<Request> yourRequests = user.getUserRequests();
        for (int i = 0; i < yourRequests.size(); i++) {
            Request request = yourRequests.get(i);
            System.out.print(i + ") ");
            System.out.println(request.toString());
            System.out.println();
        }

        if (yourRequests.isEmpty()) {
            System.out.println("No requests to withdraw.");
            return;
        }

        int selectedRequestIndex;

        do {
            System.out.println("Choose a request to withdraw (enter the corresponding number):");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next();
            }
            selectedRequestIndex = scanner.nextInt();
        } while (selectedRequestIndex < 0 || selectedRequestIndex >= yourRequests.size());

        Request selectedRequest = yourRequests.get(selectedRequestIndex);
        selectedRequest.notifyObservers("The request made by " + user.getUsername() + "has been withdrawn!");
        if (selectedRequest.getToo() == "ADMIN") {
            for (User u : users) {
                if (u instanceof Admin) {
                    selectedRequest.deleteFromUser(u, selectedRequest);
                    selectedRequest.removeObserver(u);
                }
            }
        } else {
            for (User u : users) {
                if (u.getUsername().equals(selectedRequest.getToo())) {
                    selectedRequest.deleteFromUser(u, selectedRequest);
                    selectedRequest.removeObserver(u);
                }
            }
        }
        yourRequests.remove(selectedRequest);
        System.out.println("Your request has been withdrawn!");

    }

    private void createOrWithdrawRequest(User user) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Do you want to create or withdraw a request?");
        System.out.println("1. Create Request");
        System.out.println("2. Withdraw Request");
        int choice = 0;
        boolean isValidInput = false;

        do {
            System.out.print("Enter your choice: ");
            String choiceInput = scanner.nextLine();

            try {
                choice = Integer.parseInt(choiceInput);
                isValidInput = true;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid option (numeric value)!");
            }
        } while (!isValidInput);

        switch (choice) {
            case 1:
                createRequest(user);
                break;
            case 2:
                withdrawRequest(user);
                break;
            default:
                System.out.println("Invalid choice");
                break;
        }
    }

    private void addOrDeleteFromSystem(User user) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Do you want to add (1) or delete (2)?");
        int choice = getUserChoice(scanner, 1, 2);

        switch (choice) {
            case 1:
                addProduction(user);
                break;
            case 2:
                deleteProduction(user);
                break;
            default:
                System.out.println("Invalid choice. Please choose 1 for add or 2 for delete.");
        }
    }

    private void addProduction(User user) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("What do you want to add?");
        System.out.println("1. Movie");
        System.out.println("2. Series");
        System.out.println("3. Actor");
        int choice = 0;
        boolean isValidInput = false;

        do {
            System.out.print("Enter your choice: ");
            String choiceInput = scanner.nextLine();

            try {
                choice = Integer.parseInt(choiceInput);
                isValidInput = true;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid option (numeric value)!");
            }
        } while (!isValidInput);
        switch (choice) {
            case 1:
                System.out.println("Enter movie details:");

                System.out.println("Enter title:");
                String titleMovie = scanner.nextLine();
                for (Production prod : productions) {
                    if (prod.getTitle().equalsIgnoreCase(titleMovie)) {
                        System.out.println("There's already a production with this name!");
                        return;
                    }
                }
                int numDirectorsMovie = 0;
                boolean a = false;

                do {
                    System.out.print("Enter the number of directors: ");
                    String choiceInput = scanner.nextLine();

                    try {
                        numDirectorsMovie = Integer.parseInt(choiceInput);
                        a = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid option (numeric value)!");
                    }
                } while (!a);

                List<String> directorsMovie = new ArrayList<>();
                for (int i = 0; i < numDirectorsMovie; i++) {
                    System.out.println("Enter director " + (i + 1) + ":");
                    directorsMovie.add(scanner.nextLine());
                }

                int numActorsMovie = 0;
                boolean b = false;

                do {
                    System.out.print("Enter the number of actors: ");
                    String choiceInput = scanner.nextLine();

                    try {
                        numActorsMovie = Integer.parseInt(choiceInput);
                        b = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid option (numeric value)!");
                    }
                } while (!b);
                List<String> actorsMovie = new ArrayList<>();
                for (int i = 0; i < numActorsMovie; i++) {
                    System.out.println("Enter actor " + (i + 1) + ":");
                    actorsMovie.add(scanner.nextLine());
                }
                int numGenresMovie = 0;
                boolean c = false;

                do {
                    System.out.print("Enter the number of genres: ");
                    String choiceInput = scanner.nextLine();

                    try {
                        numGenresMovie = Integer.parseInt(choiceInput);
                        c = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid option (numeric value)!");
                    }
                } while (!c);
                List<Genre> genres = new ArrayList<>();
                int ok = 0;
                for (int i = 0; i < numGenresMovie; i++) {
                    System.out.println("Enter genre " + (i + 1) + ":");

                    String genreToAdd = scanner.nextLine();
                    if (!isValidGenre(genreToAdd)) {
                        System.out.println("Invalid genre!");
                        ok = 1;
                        break;
                    }
                    if (!Objects.equals(genreToAdd, "SF")) {
                        genreToAdd = genreToAdd.substring(0, 1).toUpperCase() + genreToAdd.substring(1).toLowerCase();
                    }
                    if (genreToAdd.equalsIgnoreCase("SF")) {
                        genreToAdd = genreToAdd.toUpperCase();
                    }
                    Genre genreMovie = Genre.valueOf(genreToAdd);
                    genres.add(genreMovie);
                }
                if (ok == 1) {
                    System.out.println("Couldn't add the movie!");
                    break;
                }
                System.out.println("Enter movie description:");
                String descriptionMovie = scanner.nextLine();

                System.out.println("Enter movie duration (in minutes):");
                String movieDuration = scanner.nextLine();

                System.out.println("Enter release year:");
                int releaseYearMovie = getUserChoice(scanner, 1800, 2100);
                List<Rating> ratings = new ArrayList<Rating>();
                Movie movie = new Movie(titleMovie, directorsMovie, actorsMovie,
                        genres, ratings, descriptionMovie, 0.0,
                        releaseYearMovie, movieDuration);
                productions.add(movie);
                if (user instanceof Regular || user instanceof Contributor) {
                    user.setExperienceStrategy(new AddExperienceContribution());
                    user.updateExperience();
                }
                System.out.println();
                System.out.println("Your experience now:" + user.getExperience());
                System.out.println();
                user.getProductionsContribution().add(movie.getTitle());
                List<String> p = user.getProductionsContribution();
                System.out.println("These are your contributions(Productions):");
                for (String prod : p) {
                    System.out.println(prod);
                }
                break;

            case 2:
                System.out.println("Enter series details:");

                System.out.println("Enter title:");
                String titleSeries = scanner.nextLine();

                for (Production prod : productions) {
                    if (prod.getTitle().equalsIgnoreCase(titleSeries)) {
                        System.out.println("There's already a production with this name!");
                        return;
                    }
                }

                System.out.println("Enter the number of directors:");
                int numDirectorsSeries = 0;
                boolean d = false;

                do {
                    System.out.print("Enter the number of directors: ");
                    String choiceInput = scanner.nextLine();

                    try {
                        numDirectorsSeries = Integer.parseInt(choiceInput);
                        d = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid option (numeric value)!");
                    }
                } while (!d);
                List<String> directorsSeries = new ArrayList<>();
                for (int i = 0; i < numDirectorsSeries; i++) {
                    System.out.println("Enter director " + (i + 1) + ":");
                    directorsSeries.add(scanner.nextLine());
                }
                int numActorsSeries = 0;
                boolean f = false;

                do {
                    System.out.print("Enter the number of actors: ");
                    String choiceInput = scanner.nextLine();

                    try {
                        numActorsSeries = Integer.parseInt(choiceInput);
                        f = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid option (numeric value)!");
                    }
                } while (!f);
                List<String> actorsSeries = new ArrayList<>();
                for (int i = 0; i < numActorsSeries; i++) {
                    System.out.println("Enter actor " + (i + 1) + ":");
                    actorsSeries.add(scanner.nextLine());
                }
                int numGenresSeries = 0;
                boolean g = false;

                do {
                    System.out.print("Enter the number of genres: ");
                    String choiceInput = scanner.nextLine();

                    try {
                        numGenresSeries = Integer.parseInt(choiceInput);
                        g = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid option (numeric value)!");
                    }
                } while (!g);
                List<Genre> genresSeries = new ArrayList<>();
                int ok2 = 0;
                for (int i = 0; i < numGenresSeries; i++) {
                    System.out.println("Enter genre " + (i + 1) + ":");

                    String genreToAdd = scanner.nextLine();
                    if (!isValidGenre(genreToAdd)) {
                        System.out.println("Invalid genre!");
                        ok2 = 1;
                        break;
                    }
                    if (!Objects.equals(genreToAdd, "SF")) {
                        genreToAdd = genreToAdd.substring(0, 1).toUpperCase() + genreToAdd.substring(1).toLowerCase();
                    }
                    if (genreToAdd.equalsIgnoreCase("SF")) {
                        genreToAdd = genreToAdd.toUpperCase();
                    }
                    Genre genreSeries = Genre.valueOf(genreToAdd);
                    genresSeries.add(genreSeries);
                }
                if (ok2 == 1) {
                    System.out.println("Couldn't add the series!");
                    break;
                }
                System.out.println("Enter series description:");
                String descriptionSeries = scanner.nextLine();

                System.out.println("Enter release year:");
                int releaseYearSeries = getUserChoice(scanner, 1800, 2100);

                int numSeasons = 0;
                boolean h = false;

                do {
                    System.out.print("Enter the number of seasons: ");
                    String choiceInput = scanner.nextLine();

                    try {
                        numSeasons = Integer.parseInt(choiceInput);
                        h = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid option (numeric value)!");
                    }
                } while (!h);
                List<Rating> ratingsSeries = new ArrayList<Rating>();
                Map<String, List<Episode>> episodes = new HashMap<>();

                for (int seasonNumber = 1; seasonNumber <= numSeasons; seasonNumber++) {
                    List<Episode> seasonEpisodes = new ArrayList<>();
                    int numEpisodes = 0;
                    boolean i = false;

                    do {
                        System.out.print("Enter the number of episodes: ");
                        String choiceInput = scanner.nextLine();

                        try {
                            numEpisodes = Integer.parseInt(choiceInput);
                            i = true;
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter a valid option (numeric value)!");
                        }
                    } while (!i);
                    for (int episodeNumber = 1; episodeNumber <= numEpisodes; episodeNumber++) {
                        System.out.println("Enter title for Season " + seasonNumber + ", Episode " + episodeNumber + ":");
                        String episodeTitle = scanner.nextLine();

                        System.out.println("Enter duration for Season " + seasonNumber + ", Episode " + episodeNumber + ":");
                        String episodeDuration = scanner.nextLine();

                        Episode episode = new Episode(episodeTitle, episodeDuration);
                        seasonEpisodes.add(episode);
                    }
                    String seasonNumberString = String.valueOf(seasonNumber);
                    episodes.put(seasonNumberString, seasonEpisodes);
                }
                Series series = new Series(titleSeries, directorsSeries, actorsSeries,
                        genresSeries, ratingsSeries, descriptionSeries, 0.0,
                        releaseYearSeries, numSeasons, episodes);

                productions.add(series);
                if (user instanceof Regular || user instanceof Contributor) {
                    user.setExperienceStrategy(new AddExperienceContribution());
                    user.updateExperience();
                }
                System.out.println();
                System.out.println("Your experience now:" + user.getExperience());
                System.out.println();
                user.getProductionsContribution().add(series.getTitle());
                List<String> ps = user.getProductionsContribution();
                System.out.println("These are your contributions(Productions):");
                for (String prod : ps) {
                    System.out.println(prod);
                }
                break;

            case 3:
                System.out.println("Enter actor name:");
                String actorName = scanner.nextLine();

                for (Actor act : actors) {
                    if (act.getName().equalsIgnoreCase(actorName)) {
                        System.out.println("There's already an actor with this name!");
                        return;
                    }
                }

                System.out.println("Enter actor biography:");
                String actorBiography = scanner.nextLine();

                int numRoles = 0;
                boolean k = false;

                do {
                    System.out.print("Enter the number of roles: ");
                    String choiceInput = scanner.nextLine();

                    try {
                        numRoles = Integer.parseInt(choiceInput);
                        k = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid option (numeric value)!");
                    }
                } while (!k);
                List<Map.Entry<String, String>> rolesList = new ArrayList<>();
                for (int i = 0; i < numRoles; i++) {
                    System.out.println("Enter role " + (i + 1) + " name:");
                    String roleName = scanner.nextLine();

                    System.out.println("Enter role " + (i + 1) + " type:");
                    String roleType = scanner.nextLine();

                    rolesList.add(Map.entry(roleName, roleType));
                }
                double averageRating = 0;
                Actor actor = new Actor(actorName, rolesList, actorBiography, averageRating);
                actors.add(actor);
                if (user instanceof Regular || user instanceof Contributor) {
                    user.setExperienceStrategy(new AddExperienceContribution());
                    user.updateExperience();
                }
                System.out.println();
                System.out.println("Your experience now:" + user.getExperience());
                System.out.println();
                user.getActorsContribution().add(actor.getName());
                List<String> ac = user.getActorsContribution();
                System.out.println("Lista actori:");
                for (String prod : ac) {
                    System.out.println(prod);
                }
                break;
            default:
                System.out.println("Invalid choice. Please choose 1 for Movie, 2 for Series or 3 for Actor.");
        }
    }

    private void deleteProduction(User user) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Do you want to delete a Production (1) or an Actor (2)?");
        int deleteChoice = getUserChoice(scanner, 1, 2);

        switch (deleteChoice) {
            case 1:
                // Delete a Production
                deleteProductionByTitle(user);
                break;
            case 2:
                // Delete an Actor
                deleteActorByName(user);
                break;
            default:
                System.out.println("Invalid choice. Please choose 1 for Production or 2 for Actor.");
        }
    }

    private void deleteProductionByTitle(User user) {
        Scanner scanner = new Scanner(System.in);

        List<String> ps = user.getProductionsContribution();
        System.out.println("These are the productions you have added:");
        for (String prod : ps) {
            System.out.println(prod);
        }
        if(user instanceof Admin) {
            Admin a = (Admin) user;
            List<Production> adminProd = a.getAddedProductionsByTeam();
            System.out.println("These are the productions in the admin list:");
            if (user.getAccountType() == AccountType.ADMIN) {
                for (Production p : adminProd) {
                    System.out.println(p.getTitle());
                }
            }
        }
        System.out.println("Enter the title of the Production you want to delete:");
        String productionTitle = scanner.nextLine();
        int ok = 0;
        Production pr = null;
        if(user instanceof Admin) {
            Admin a = (Admin) user;
            List<Production> adminProd = a.getAddedProductionsByTeam();
            for (Production p : adminProd) {
                if (p.getTitle().equalsIgnoreCase(productionTitle)) {
                    ok = 1;
                    pr = p;

                }
            }
            if (ok == 1) {
                adminProd.remove(pr);
                System.out.println("Production '" + productionTitle + "' deleted successfully.");
                productions.removeIf(p -> p.getTitle().equalsIgnoreCase(productionTitle));
            }
        }
        if (user.getProductionsContribution().contains(productionTitle) && ok == 0) {
            user.getProductionsContribution().remove(productionTitle);
            System.out.println("Production '" + productionTitle + "' deleted successfully.");
            productions.removeIf(p -> p.getTitle().equalsIgnoreCase(productionTitle));
        } else {
            if (ok == 0)
                System.out.println("You do not have permission to delete production '" + productionTitle + "', or it does not exist.");
        }


        System.out.println("These are the productions you have added:");
        for (String prod : ps) {
            System.out.println(prod);
        }
        if(user instanceof Admin) {
            Admin a = (Admin) user;
            List<Production> adminProd = a.getAddedProductionsByTeam();
            System.out.println("These are the productions in the admin list:");
            if (user.getAccountType() == AccountType.ADMIN) {
                for (Production p : adminProd) {
                    System.out.println(p.getTitle());
                }
            }
        }
    }

    private void deleteActorByName(User user) {
        Scanner scanner = new Scanner(System.in);
        List<String> ps = user.getActorsContribution();
        System.out.println("These are the actors you have added:");
        for (String prod : ps) {
            System.out.println(prod);
        }
        if(user instanceof Admin) {
            Admin a = (Admin) user;
            List<Actor> adminAct = a.getAddedActorsByTeam();
            System.out.println("These are the actors in the admin list:");
            if (user.getAccountType() == AccountType.ADMIN) {
                for (Actor actor : adminAct) {
                    System.out.println("Actor: " + actor.getName());
                }
            }
        }
        System.out.println("Enter the name of the Actor you want to delete:");
        String actorName = scanner.nextLine();
        int ok = 0;
        Actor ac = null;
        if(user instanceof Admin) {
            Admin a = (Admin) user;
            List<Actor> adminAct = a.getAddedActorsByTeam();
            for (Actor acto : adminAct) {
                if (acto.getName().equalsIgnoreCase(actorName)) {
                    ok = 1;
                    ac = acto;

                }
            }
            if (ok == 1) {
                adminAct.remove(ac);
                System.out.println("Actor '" + actorName + "' deleted successfully.");
                actors.removeIf(p -> p.getName().equalsIgnoreCase(actorName));
            }
        }
        if (user.getActorsContribution().contains(actorName) && ok == 0) {
            user.getActorsContribution().remove(actorName);
            System.out.println("Actor '" + actorName + "' deleted successfully.");
            actors.removeIf(p -> p.getName().equalsIgnoreCase(actorName));
        } else {
            if (ok == 0)
                System.out.println("You do not have permission to delete Actor '" + actorName + "' or it does not exist.");
        }

        System.out.println("These are the actors you have added:");
        for (String prod : ps) {
            System.out.println(prod);
        }
        if(user instanceof Admin) {
            Admin a = (Admin) user;
            List<Actor> adminAct = a.getAddedActorsByTeam();
            System.out.println("These are the actors in the admin list:");
            if (user.getAccountType() == AccountType.ADMIN) {
                for (Actor actor : adminAct) {
                    System.out.println("Actor: " + actor.getName());
                }
            }
        }
    }

    private static boolean isValidGenre(String genre) {
        for (Genre g : Genre.values()) {
            if (g.name().equalsIgnoreCase(genre)) {
                return true;
            }
        }
        return false;
    }

    private int getUserChoice(Scanner scanner, int min, int max) {
        int choice;
        while (true) {
            try {
                choice = scanner.nextInt();
                if (choice >= min && choice <= max) {
                    break;
                } else {
                    System.out.println("Invalid choice. Please enter a number between " + min + " and " + max + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next();
            }
        }
        return choice;
    }


    private void updateMovieDetails(User user) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Available Movies:");
        for (Production production : productions) {
            System.out.println(production.getTitle());
        }

        System.out.println("Enter the title of the movie/series to update:");
        String movieTitle = scanner.nextLine();
        int ok = 0;
        Production movieToUpdate = null;
        for (Production production : productions) {
            if (production.getTitle().equalsIgnoreCase(movieTitle)) {
                movieToUpdate = production;
                System.out.println("Movie to update:" + production.getTitle());
                System.out.println("Productions contribution of user: ");
                List<String> productionsContribution = user.getProductionsContribution();
                for (String productionTitle : productionsContribution) {
                    System.out.println("Production Title: " + productionTitle);
                }

                if (user.getProductionsContribution().contains(production.getTitle())) {
                    ok = 1;
                }
                break;
            }
        }

        if (movieToUpdate != null && ok == 1) {

            Staff staffInstance = (Staff) user;
            staffInstance.updateProduction(movieToUpdate);
        } else {
            if (movieToUpdate == null) System.out.println("Movie not found in the collection.");
            if (ok == 0) System.out.println("You don't have the right to update this production.");

        }
    }

    private void updateActorDetails(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Available Actors:");
        for (Actor actor : actors) {
            System.out.println(actor.getName());
        }

        System.out.println("Enter the name of the actor to update:");
        String actorName = scanner.nextLine();
        int ok = 0;
        Actor actorToUpdate = null;
        for (Actor actor : actors) {
            if (actor.getName().equalsIgnoreCase(actorName)) {
                actorToUpdate = actor;
                if (user.getProductionsContribution().contains(actor.getName())) {
                    ok = 1;
                }
                break;
            }
        }

        if (actorToUpdate != null) {
            Staff staffInstance = (Staff) user;
            staffInstance.updateActor(actorToUpdate);
        } else {
            if (actorToUpdate == null) System.out.println("Actor not found in the collection.");
            if (ok == 0) System.out.println("You don't have the right to update this actor.");
        }
    }


    private void addOrDeleteUser(User user) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Do you want to add or delete a user? (Type 'add' or 'delete')");
        String action = scanner.nextLine().toLowerCase();

        switch (action) {
            case "add":
                addUser(user);
                break;
            case "delete":
                deleteUser(user);
                break;
            default:
                System.out.println("Invalid action. Please type 'add' or 'delete'.");
                break;
        }
        System.out.println("These are the users:");
        for (User u : users) {
            System.out.println(u.getUsername());
        }
    }

    public boolean isUsernameTaken(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public String generateUniqueUsername(String name) {
         String baseUsername = name.toLowerCase().replace(" ", "_");
        String username = baseUsername;
        int count = 1;

        while (isUsernameTaken(username)) {
            username = baseUsername + "_" + count;
            count++;
        }

        return username;
    }

    private static String generateStrongPassword(String name) {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/";

        String allChars = upperCase + lowerCase + digits + specialChars;

        StringBuilder password = new StringBuilder();
        Random random = new Random();

        // caractere aleatorii
        for (int i = 0; i < 12; i++) {
            int randomIndex = random.nextInt(allChars.length());
            password.append(allChars.charAt(randomIndex));
        }
        // adaug sufix, lungimea numelui la finalul parolei
        password.append(name.length());

        return password.toString();
    }

    public void addUser(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the name of the person who owns the account:");
        String name = scanner.nextLine();
        String username = generateUniqueUsername(name);
        System.out.println("Username:" + username);
        String password = generateStrongPassword(username);

        System.out.println("Enter the email:");
        String email = scanner.nextLine();
        System.out.println("Enter the country:");
        String country = scanner.nextLine();

        System.out.println("Enter the age:");
        int age = getUserChoice(scanner, 0, 100);

        System.out.println("Enter the gender:");
        String gender = scanner.nextLine();

        System.out.println("Enter the birth date (YYYY-MM-DD HH:mm):");
        String birthDateStr = scanner.nextLine();
        LocalDateTime birthDate = LocalDateTime.parse(birthDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        System.out.println("Select the type of account you want to create(1/2/3):");
        System.out.println("1. REGULAR");
        System.out.println("2. CONTRIBUTOR");
        System.out.println("3. ADMIN");
        String accountType = null;
        int choice = getUserChoice(scanner, 1, 3);
        switch (choice) {
            case 1:
                accountType = "Regular";
                break;
            case 2:
                accountType = "Contributor";
                break;
            case 3:
                accountType = "Admin";
                break;
            default:
                System.out.println("Invalid choice");
                break;
        }
        System.out.println("Creating a new user...");

        int experience = 0;
        assert accountType != null;
        List<String> notifications = new ArrayList<>();

        List<String> productionsContribution = new ArrayList<>();
        List<String> actorsContribution = new ArrayList<>();
        SortedSet<Production> favoriteProductions = new TreeSet<>();
        SortedSet<Actor> favoriteActors = new TreeSet<>();


        User.Information information = new User.Information.Builder()
                .credentials(email, password)
                .name(name)
                .country(country)
                .age(age)
                .gender(gender)
                .birthDate(birthDate)
                .build();

        User<String> newUser = UserFactory.createUser(accountType, username, experience, information, productionsContribution, actorsContribution, favoriteProductions, favoriteActors, notifications);
        users.add(newUser);
        System.out.println("Username: " + newUser.getUsername());
        System.out.println("Experience: " + newUser.getExperience());
        System.out.println("User Type: " + newUser.getAccountType());
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
        System.out.println("Name: " + newUser.getInformation().getName());
        System.out.println("Country: " + country);
        System.out.println("Age: " + age);
        System.out.println("Gender: " + gender);
        System.out.println("Birth Date: " + birthDate);
        System.out.println("Productions Contribution: " + productionsContribution);
        System.out.println("Actors Contribution: " + actorsContribution);
        System.out.println("Favorite Actors: " + favoriteActors);
        System.out.println("Favorite Productions: " + favoriteProductions);
        System.out.println("Notifications: " + user.getNotifications());
        System.out.println("----------------------------");
    }

    public void deleteUser(User adminuser) {
        Scanner scanner = new Scanner(System.in);
        Admin a = (Admin) adminuser;
        User user = null;
        System.out.println("These are the users:");
        for (User u : users) {
            System.out.println(u.getUsername());
        }
        System.out.println("Enter the name of the user you would like to delete:");
        String username = scanner.nextLine();
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                user = u;
            }
        }
        if (user == null) {
            System.out.println("The user you have selected doesn't exist!");
            return;
        }
        if (user.getAccountType().equals(AccountType.CONTRIBUTOR)) {
            List<String> prod = user.getProductionsContribution();
            List<String> act = user.getActorsContribution();
            for (String production : prod) {
                // Adaugam producția la lista de producții adaugate de echipa de admini
                for (Production p : productions) {
                    if (p.getTitle().equalsIgnoreCase(production)) {
                        a.getAddedProductionsByTeam().add(p);
                        break;
                    }
                }
            }
            for (String actor : act) {
                // Adaugam producția la lista de producții adaugate de echipa de admini
                for (Actor ac : actors) {
                    if (ac.getName().equalsIgnoreCase(actor)) {
                        a.getAddedActorsByTeam().add(ac);
                        break;
                    }
                }
            }
            System.out.println("Contributor's productions and actors marked as added by admin team.");
            List<Actor> listActor = a.getAddedActorsByTeam();
            for (Actor ac : listActor) {
                System.out.println("Actor: " + ac.getName());
            }
            List<Production> listPro = a.getAddedProductionsByTeam();
            for (Production pr : listPro) {
                System.out.println("Production: " + pr.getTitle());
            }

        }
        users.remove(user);
        System.out.println("User deleted successfully.");
    }

    public void displayUsers() {
        for (User user : users) {
            if (user != null) {
                System.out.println("Username: " + user.getUsername());
                System.out.println("Experience: " + user.getExperience());
                System.out.println("User Type: " + user.getAccountType());
                System.out.println("Email: " + user.getInformation().getEmail());
                System.out.println("Password: " + user.getInformation().getPassword());
                System.out.println("Name: " + user.getInformation().getName());
                System.out.println("Country: " + user.getInformation().getCountry());
                System.out.println("Age: " + user.getInformation().getAge());
                System.out.println("Gender: " + user.getInformation().getGender());
                System.out.println("Birth Date: " + user.getInformation().getBirthDate());
                System.out.println("Productions Contribution: " + user.getProductionsContribution());
                System.out.println("Actors Contribution: " + user.getActorsContribution());
                System.out.println("Favorite Actors: " + user.getFavoriteActors());
                System.out.println("Favorite Productions: " + user.getFavoriteProductions());
                System.out.println("Notifications: " + user.getNotifications());
                System.out.println("----------------------------");
            }
        }
    }


    private void solveRequest(User user) {
        if (!(user instanceof Staff)) {
            System.out.println("Invalid user type for solving requests.");
            return;
        }

        Staff staff = (Staff) user;
        List<Request> assignedRequests = staff.getAssignedRequests();

        System.out.println("These are your requests:");
        for (int i = 0; i < assignedRequests.size(); i++) {
            Request request = assignedRequests.get(i);
            System.out.print(i + ") ");
            System.out.println(request.toString());
            System.out.println();
        }

        if (assignedRequests.isEmpty()) {
            System.out.println("No requests to solve.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        int selectedRequestIndex;

        do {
            System.out.println("Choose a request (enter the corresponding number):");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next();
            }
            selectedRequestIndex = scanner.nextInt();
        } while (selectedRequestIndex < 0 || selectedRequestIndex >= assignedRequests.size());

        System.out.println("Choose an action:");
        System.out.println("1. Resolve Request");
        System.out.println("2. Reject Request");

        int userChoice;
        do {
            System.out.println("Enter your choice (1 or 2):");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter 1 or 2.");
                scanner.next();
            }
            userChoice = scanner.nextInt();
        } while (userChoice < 1 || userChoice > 2);

        Request selectedRequest = assignedRequests.get(selectedRequestIndex);

        if (userChoice == 1) {
            // Rezolvarea cererii
            selectedRequest.removeObserver(user);
            Iterator<Observer> iterator = selectedRequest.getObservers().iterator();
            while (iterator.hasNext()) {
                Observer o = iterator.next();
                if (o instanceof User) {
                    User u = (User) o;
                    if (u instanceof Admin) {
                        iterator.remove();
                    }
                }
            }


            for (User u : users) {
                if (u.getUsername().equals(selectedRequest.getUsername())) {
                    selectedRequest.addObserver(u);
                    // i-am scos request ul din lista daca a fost tratat
                    u.getUserRequests().remove(selectedRequest);
                    break;
                }
            }
            String usernameRequest = selectedRequest.getUsername();
            User searchUser = null;
            for (User u : users) {
                u.getUsername().equals(usernameRequest);
                searchUser = u;
            }
            if (searchUser instanceof Regular || searchUser instanceof Contributor) {
                searchUser.setExperienceStrategy(new AddExperienceRequest());
                searchUser.updateExperience();
            }
            System.out.println();
            System.out.println("Your experience now:" + searchUser.getExperience());
            System.out.println();
            selectedRequest.resolveRequest();
            System.out.println("Request resolved.");
            staff.resolveUserRequests();
        } else {
            // Respingerea cererii
            selectedRequest.removeObserver(user);
            Iterator<Observer> iterator = selectedRequest.getObservers().iterator();
            while (iterator.hasNext()) {
                Observer o = iterator.next();
                if (o instanceof User) {
                    User u = (User) o;
                    if (u instanceof Admin) {
                        iterator.remove();
                    }
                }
            }

            for (User u : users) {
                if (u.getUsername().equals(selectedRequest.getUsername())) {
                    selectedRequest.addObserver(u);
                    // i-am scos request ul din lista daca a fost tratat
                    u.getUserRequests().remove(selectedRequest);
                    break;
                }
            }
            selectedRequest.rejectRequest();
            System.out.println("Request rejected.");
        }

        // Eliminarea cererii din lista de cereri a utilizatorului
        assignedRequests.remove(selectedRequest);
    }


    private void logout(User user) {
        user.logoutmethod(IMDB.getInstance());
    }

    public static void main(String[] args) {
        IMDB imdb = IMDB.getInstance();
        imdb.run();
    }


}
