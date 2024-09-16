package org.example;

import java.time.*;
import java.util.*;


interface Observer {
    void update(String notification);
}

public abstract class User<T extends Comparable<T>> implements Observer {
    private Information information;

    static class Information {
        private Credentials credentials;
        private String name;
        private String country;
        private int age;
        private String gender;
        private LocalDateTime birthDate;

        private Information(Builder builder) {
            this.credentials = builder.credentials;
            this.name = builder.name;
            this.country = builder.country;
            this.age = builder.age;
            this.gender = builder.gender;
            this.birthDate = builder.birthDate;
        }
        public String getPassword() {
            return credentials.getPassword();
        }
        public String getEmail() {
            return credentials.getEmail();
        }
        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }

        public int getAge() {
            return age;
        }

        public String getGender() {
            return gender;
        }

        public LocalDateTime getBirthDate() {
            return birthDate;
        }
        public static class Builder {
            private Credentials credentials;
            private String name;
            private String country;
            private int age;
            private String gender;
            private LocalDateTime birthDate;

            public Builder credentials(String email, String password) {
                this.credentials = new Credentials(email, password);
                return this;
            }

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder country(String country) {
                this.country = country;
                return this;
            }

            public Builder age(int age) {
                this.age = age;
                return this;
            }

            public Builder gender(String gender) {
                this.gender = gender;
                return this;
            }

            public Builder birthDate(LocalDateTime birthDate) {
                this.birthDate = birthDate;
                return this;
            }

            public Information build() {
                return new Information(this);
            }
        }

        static class Credentials {
            private String email;
            private String password;

            private Credentials(String email, String password) {
                this.email = email;
                this.password = password;
            }

            public String getEmail() {
                return email;
            }

            public String getPassword() {
                return password;
            }
        }
    }

    private AccountType accountType;
    public String username;
    int experience;
    ExperienceStrategy experienceS;
    private List<String> notifications;
    private List<Observer> observers;
    private SortedSet<Production> favoriteProductions;
    private SortedSet<Actor> favoriteActors;
    private List<String> productionsContribution;
    private List<String> actorsContribution;
    public List<Request> userRequests;

    public User(Information information, AccountType accountType, String username, int experience,
                List<String> productionsContribution,
                List<String> actorsContribution,
                SortedSet<Production> favoriteProductions,
                SortedSet<Actor> favoriteActors, List<String> notifications) {
        this.information = information;
        this.accountType = accountType;
        this.username = username;
        this.experience = experience;
        this.notifications = notifications;
        this.observers = new ArrayList<>();
        this.productionsContribution = productionsContribution;
        this.actorsContribution = actorsContribution;
        this.favoriteProductions = favoriteProductions;
        this.favoriteActors = favoriteActors;
        this.userRequests = new ArrayList<>();
    }
    public void addFavoriteActor(Actor actor) {
        favoriteActors.add(actor);
    }
    public void deleteFavoriteActor(Actor actor) {
        favoriteActors.remove(actor);
    }
    public void addFavoriteProduction(Production production) {
        favoriteProductions.add(production);
    }
    public void deleteFavoriteProduction(Production production) {
        favoriteProductions.remove(production);
    }

    public List<String> getProductionsContribution() {
        return productionsContribution;
    }

    public List<String> getActorsContribution() {
        return actorsContribution;
    }
    public SortedSet<Production> getFavoriteProductions() {
        return favoriteProductions;
    }

    public SortedSet<Actor> getFavoriteActors() {
        return favoriteActors;
    }

    public List<Request> getUserRequests() {
        return userRequests;
    }

    public Information getInformation() {
        return information;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public String getUsername() {
        return username;
    }

    public int getExperience() {
        return experience;
    }


    public List<String> getNotifications() {
        return notifications;
    }


    public void setExperienceStrategy(ExperienceStrategy experienceStrategy) {
        this.experienceS = experienceStrategy;
    }

    public void updateExperience() {

        experience = experience + (experienceS != null ? experienceS.calculateExperience() : 0);
    }


    public List<Observer> getObservers() {
        return observers;
    }

public void logoutmethod(IMDB imdb) {
    System.out.println("Logging out... Bye!");
    imdb.loggedIn = false;
    imdb.authenticateUser();
}

    public void addReview(User user, Production production, int rating, String comment, IMDB imdb) {
        Rating newRating = new Rating(user.getUsername(), rating, comment);
        production.addRating(newRating);
        AccountType type = null;

        for (Rating existingRating : production.getRatings()) {
            if (!existingRating.getUsername().equals(user.getUsername())) {

                String notification = "The production you reviewed has received a new rating from user \"" +
                        user.getUsername() + "\" with a rating of " + rating;
                existingRating.notifyObservers(notification);
            }
        }
        for (User u : imdb.users) {
            List<String> contributions = u.getProductionsContribution();
            if (contributions.contains(production.getTitle())) {
                String notification = "The production you contributed to has received a new rating from user \"" +
                        user.getUsername() + "\" with a rating of " + rating;
                u.update(notification);
            }
        }
        int check = 0;
        for (User u : imdb.users) {
            if (u instanceof Admin) {
                Admin a = (Admin) u;
                List<Production> admins = a.getAddedProductionsByTeam();
                if (admins.contains(production)) {
                    check = 1;
                }
            }
        }
        if(check == 1) {
            for (User u : imdb.users) {
                if (u instanceof Admin) {
                    String notification = "The actor you contributed to has received a new rating from user \"" +
                            user.getUsername() + "\" with a rating of " + rating;
                    u.update(notification);

                }
            }
        }
        newRating.addObserver(user);


    }
    public void addReviewActor(User user, Actor actor, int rating, String comment, IMDB imdb) {
        Rating newRating = new Rating(user.getUsername(), rating, comment);
        actor.addRating(newRating);
        AccountType type = null;

        for (Rating existingRating : actor.getRatings()) {
            if (!existingRating.getUsername().equals(user.getUsername())) {

                String notification = "The actor you reviewed has received a new rating from user \"" +
                        user.getUsername() + "\" with a rating of " + rating;
                existingRating.notifyObservers(notification);
            }
        }
        for (User u : imdb.users) {
            List<String> contributions = u.getActorsContribution();
            if (contributions.contains(actor.getName())) {
                String notification = "The actor you contributed to has received a new rating from user \"" +
                        user.getUsername() + "\" with a rating of " + rating;
                u.update(notification);
            }
        }
        int check = 0;
        for (User u : imdb.users) {
            if (u instanceof Admin) {
                Admin a = (Admin) u;
                List<Actor> admins = a.getAddedActorsByTeam();
                if (admins.contains(actor)) {
                    check = 1;
                }
            }
        }
        if(check == 1) {
            for (User u : imdb.users) {
                if (u instanceof Admin) {
                    String notification = "The actor you contributed to has received a new rating from user \"" +
                            user.getUsername() + "\" with a rating of " + rating;
                    u.update(notification);

                }
            }
        }
        newRating.addObserver(user);


    }
    @Override
    public void update(String notification) {
        System.out.println("User " + username + " received notification: " + notification);
    }


}

