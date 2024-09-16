package org.example;

import java.util.*;

public class Admin<T extends Comparable<T>> extends Staff {
    private List<Production> addedProductionsByTeam = new ArrayList<>();
    private List<Actor> addedActorsByTeam = new ArrayList<>();


    public Admin(Information information, AccountType accountType, String username, int experience,
                 List<String> productionsContribution,
                 List<String> actorsContribution,
                 SortedSet<Production> favoriteProductions,
                 SortedSet<Actor> favoriteActors, List<String> notifications) {
        super(information, accountType, username, experience, productionsContribution, actorsContribution, favoriteProductions,favoriteActors, notifications);
    }

    public List<Actor> getAddedActorsByTeam() {
        return addedActorsByTeam;
    }
    public List<Production> getAddedProductionsByTeam() {
        return addedProductionsByTeam;
    }
    @Override
    public void addProductionSystem(Production production) {
        addedProductionsByTeam.add(production);
        System.out.println("Admin " + getUsername() + " added a production: " + production.getTitle());
    }
    @Override
    public void removeProductionSystem(String name) {
        //addedProductionsByTeam.remove(name);
        System.out.println("Admin " + getUsername() + " removed a production: " + name);
    }

    @Override
    public void addActorSystem(Actor actor) {
        addedActorsByTeam.add(actor);
        System.out.println("Admin " + getUsername() + " added an actor: " + actor.getName());
    }

    @Override
    public void removeActorSystem(String name) {

        System.out.println("Admin " + getUsername() + " removed an actor: " + name);
    }

    @Override
    public void resolveUserRequests() {
        System.out.println("Admin " + getUsername() + " resolving user requests.");
    }

    public void removeUser(String usernameToRemove) {
        System.out.println("Admin " + getUsername() + " removed user: " + usernameToRemove);
    }

    @Override
    public void update(String notification) {
        super.getNotifications().add(notification);
        System.out.println("Admin User " + super.getUsername() + " received notification: " + notification);
    }

}
