package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class Contributor extends Staff implements RequestsManager {

    private List<Request> assignedRequests;


    public Contributor(Information information, AccountType accountType, String username, int experience,
                       List<String> productionsContribution,
                       List<String> actorsContribution,
                       SortedSet<Production> favoriteProductions,
                       SortedSet<Actor> favoriteActors, List<String> notifications) {
        super(information, accountType, username, experience, productionsContribution, actorsContribution, favoriteProductions, favoriteActors, notifications);
        this.assignedRequests = new ArrayList<>();
    }

    @Override
    public void createRequest(Request request) {
        assignedRequests.add(request);
        System.out.println("Contributor " + getUsername() + " created a request: " + request.getDescription());
    }

    @Override
    public void removeRequest(Request request) {
        assignedRequests.remove(request);
        System.out.println("Contributor " + getUsername() + " removed a request: " + request.getDescription());
    }

    @Override
    public void update(String notification) {
        super.getNotifications().add(notification);
        System.out.println("Contributor User " + super.getUsername() + " received notification: " + notification);
    }

}
