package org.example;

import java.util.List;
import java.util.SortedSet;

public class Regular<T extends Comparable<T>> extends User<T> {

//
    public Regular(Information information, AccountType accountType, String username, int experience,
                   List<String> productionsContribution,
                   List<String> actorsContribution,
                   SortedSet<Production> favoriteProductions,
                   SortedSet<Actor> favoriteActors, List<String> notifications) {
        super(information, accountType, username, experience, productionsContribution, actorsContribution, favoriteProductions,favoriteActors, notifications);
    }
    public Regular createRegularUser(String username, int experience, Information information, List<String> productionsContribution,
                                     List<String> actorsContribution,
                                     SortedSet<Production> favoriteProductions,
                                     SortedSet<Actor> favoriteActors, List<String> notifications) {
        return (Regular) UserFactory.createUser("Regular", username, experience, information, productionsContribution, actorsContribution, favoriteProductions,favoriteActors, notifications);
    }

    @Override
    public void update(String notification) {
        super.getNotifications().add(notification);
        System.out.println(super.username + notification);
    }

}
