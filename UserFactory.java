package org.example;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class UserFactory {
    public static <T extends Comparable<T>> User<T> createUser(String userType, String username, int experience,
                                                               User.Information information,
                                                               List<String> productionsContribution,
                                                               List<String> actorsContribution,
                                                               SortedSet<Production> favoriteProductions,
                                                               SortedSet<Actor> favoriteActors, List<String> notifications) {
        switch (userType) {
            case "Regular":
                return new Regular<>(information, AccountType.REGULAR, username, experience,
                        productionsContribution, actorsContribution, favoriteProductions,favoriteActors, notifications);
            case "Contributor":
                return new Contributor(information, AccountType.CONTRIBUTOR, username, experience,
                        productionsContribution, actorsContribution, favoriteProductions,favoriteActors, notifications);
            case "Admin":
                return new Admin<>(information, AccountType.ADMIN, username, experience,
                        productionsContribution, actorsContribution, favoriteProductions,favoriteActors, notifications);
            default:
                return null;
        }
    }
}

