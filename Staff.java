package org.example;

import java.util.*;

public abstract class Staff<T extends Comparable<T>> extends User implements StaffInterface {
    public List<Request> assignedRequests;


    public Staff(Information information, AccountType accountType, String username, int experience,
                 List<String> productionsContribution,
                 List<String> actorsContribution,
                 SortedSet<Production> favoriteProductions,
                 SortedSet<Actor> favoriteActors, List<String> notifications) {
        super(information, accountType, username, experience, productionsContribution, actorsContribution, favoriteProductions, favoriteActors, notifications);
        this.assignedRequests = new ArrayList<>();
    }
    public List<Request> getAssignedRequests() {
        return assignedRequests;
    }

    @Override
    public void update(String notification) {

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
    @Override
    public void addProductionSystem(Production p) {
        System.out.println("Staff " + getUsername() + " added a production system: " + p.getTitle());
    }

    @Override
    public void addActorSystem(Actor a) {
        System.out.println("Staff " + getUsername() + " added an actor system: " + a.getName());
    }

    @Override
    public void removeProductionSystem(String name) {
    }

    @Override
    public void removeActorSystem(String name) {
    }


    @Override
    public void updateProduction(Production p) {

        Scanner scanner = new Scanner(System.in);


        System.out.println("Select the property to update for the movie:");
        System.out.println("1. Title");
        System.out.println("2. Directors");
        System.out.println("3. Genres");
        System.out.println("4. Description");
        System.out.println("5. Actors");
        System.out.println("6. Movie Duration");
        System.out.println("7. Release Year");
        System.out.print("Enter your choice: ");

        int propertyChoice = scanner.nextInt();
        scanner.nextLine();
        switch (propertyChoice) {
            case 1:
                System.out.println("Enter the new title for the movie: ");
                String newTitle = scanner.nextLine();
                p.setTitle(newTitle);
                System.out.println("Title updated to: " + newTitle);
                break;
            case 2:
                System.out.println("Do you want to add or delete a director?");
                System.out.println("1. Add director");
                System.out.println("2. Delete director");
                int choiceDir = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter director:");
                switch (choiceDir) {
                    case 1:
                        String directorToAdd = scanner.nextLine();
                        if (!p.getDirectors().contains(directorToAdd)) {
                            p.getDirectors().add(directorToAdd);
                            System.out.println("Director added: " + directorToAdd);
                        } else {
                            System.out.println("Director already exists: " + directorToAdd);
                        }
                        break;
                    case 2:
                        String directorToDelete = scanner.nextLine();
                        p.getDirectors().removeIf(director -> director.equalsIgnoreCase(directorToDelete));
                        break;
                    default:
                        System.out.println("Invalid choice.");
                        break;
                }
                break;
            case 3:
                System.out.println("Do you want to add or delete a genre?");
                System.out.println("1. Add genre");
                System.out.println("2. Delete genre");
                int choiceGenre = scanner.nextInt();
                scanner.nextLine();

                System.out.println("Enter genre:");

                switch (choiceGenre) {
                    case 1:
                        String genreToAdd = scanner.nextLine();

                        if (!isValidGenre(genreToAdd)) {
                            System.out.println("Invalid genre!");
                            break;
                        }
                        if(!Objects.equals(genreToAdd, "SF")) {
                            genreToAdd = genreToAdd.substring(0, 1).toUpperCase() + genreToAdd.substring(1).toLowerCase();
                        }
                        if(genreToAdd.equalsIgnoreCase("SF")) {
                            genreToAdd = genreToAdd.toUpperCase();
                        }
                        Genre genre = Genre.valueOf(genreToAdd);

                        if (!p.getGenres().contains(genreToAdd)) {
                            p.getGenres().add(genre);
                            System.out.println("Genre added: " + genre);
                        } else {
                            System.out.println("Genre already in the list!");
                        }
                        break;
                    case 2:
                        String genreToDelete = scanner.nextLine();

                        if (!isValidGenre(genreToDelete)) {
                            System.out.println("Invalid genre!");
                            break;
                        }
                        if(!Objects.equals(genreToDelete, "SF")) {
                            genreToDelete = genreToDelete.substring(0, 1).toUpperCase() + genreToDelete.substring(1).toLowerCase();
                        }
                        if(genreToDelete.equalsIgnoreCase("SF")) {
                            genreToDelete = genreToDelete.toUpperCase();
                        }
                        Genre genreD = Genre.valueOf(genreToDelete);

                        if (p.getGenres().contains(genreD)) {
                            p.getGenres().remove(genreD);
                            System.out.println("Genre removed: " + genreD);
                        } else {
                            System.out.println("Genre not found in the list!");
                        }
                        break;
                    default:
                        System.out.println("Invalid choice.");
                        break;
                }

                break;
            case 4:
                System.out.print("Enter the new description for the movie: ");
                String newDescription = scanner.nextLine();
                p.setDescription(newDescription);
                System.out.println("Description updated to: " + newDescription);
                break;
            case 5:
                System.out.println("Do you want to add or delete an actor?");
                System.out.println("1. Add actor");
                System.out.println("2. Delete actor");
                int choiceActor = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter actor:");
                switch (choiceActor) {
                    case 1:
                        String actorToAdd = scanner.nextLine();
                        if (!p.getActors().contains(actorToAdd)) {
                            p.getActors().add(actorToAdd);
                            System.out.println("Actor added: " + actorToAdd);
                        } else {
                            System.out.println("Actor already exists: " + actorToAdd);
                        }
                        break;
                    case 2:
                        String actorToDelete = scanner.nextLine();
                        p.getActors().removeIf(actor -> actor.equalsIgnoreCase(actorToDelete));
                        break;
                    default:
                        System.out.println("Invalid choice.");
                        break;
                }
                break;
            case 6:
                System.out.println("Enter the new movie duration for the movie: ");
                String newMovieDuration = scanner.nextLine();
                Movie m = (Movie) p;
                m.setMovieDuration(newMovieDuration);
                System.out.println("Movie Duration updated to: " + newMovieDuration);
                break;
            case 7:
                System.out.println("Enter the new release year for the movie: ");
                int newReleaseYear = getUserChoice(scanner, 1800, 2100);
                Movie mov = (Movie) p;
                mov.setReleaseYear(newReleaseYear);
                System.out.println("Release year updated to: " + newReleaseYear);
                break;
            default:
                System.out.println("Invalid choice. No property updated.");
        }
    p.displayInfo();

    }

    private static boolean isValidGenre(String genre) {
        for (Genre g : Genre.values()) {
            if (g.name().equalsIgnoreCase(genre)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void updateActor(Actor a) {


        Scanner scanner = new Scanner(System.in);


        System.out.println("Select the property to update for the actor:");
        System.out.println("1. Name");
        System.out.println("2. Roles");
        System.out.println("3. Biography");
        System.out.print("Enter your choice: ");

        int propertyChoice = scanner.nextInt();
        scanner.nextLine();

        switch (propertyChoice) {
            case 1:
                System.out.println("Enter the new name for the actor: ");
                String newName = scanner.nextLine();
                a.setName(newName);
                System.out.println("Name updated to: " + newName);
                break;
            case 2:
                System.out.println("Do you want to add or delete a role?");
                System.out.println("1. Add role");
                System.out.println("2. Delete role");
                int choiceDir = scanner.nextInt();
                scanner.nextLine();

                switch (choiceDir) {
                    case 1:
                        System.out.println("Enter role name:");
                        String roleNameToAdd = scanner.nextLine();

                        System.out.println("Enter role type:");
                        String roleTypeToAdd = scanner.nextLine();

                        Map.Entry<String, String> roleToAdd = new AbstractMap.SimpleEntry<>(roleNameToAdd, roleTypeToAdd);

                        if (!a.getRoles().contains(roleToAdd)) {
                            a.getRoles().add(roleToAdd);
                            System.out.println("Role added: " + roleToAdd);
                        } else {
                            System.out.println("Role already exists: " + roleToAdd);
                        }
                        break;

                    case 2:
                        System.out.println("Enter role name to delete:");
                        String roleNameToDelete = scanner.nextLine();

                        System.out.println("Enter role type to delete:");
                        String roleTypeToDelete = scanner.nextLine();

                        Map.Entry<String, String> roleToDelete = new AbstractMap.SimpleEntry<>(roleNameToDelete, roleTypeToDelete);

                        if (a.getRoles().contains(roleToDelete)) {
                            a.getRoles().removeIf(role -> role.equals(roleToDelete));
                            System.out.println("Role deleted: " + roleToDelete);
                        } else {
                            System.out.println("Role not found: " + roleToDelete);
                        }
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
                break;

            case 3:
                System.out.print("Enter the new biography for the movie: ");
                String newBiography = scanner.nextLine();
                a.setBiography(newBiography);
                System.out.println("Biography updated to: " + newBiography);
                break;
            default:
                System.out.println("Invalid choice. No property updated.");
        }
        a.displayInfo();
    }

    @Override
    public void resolveUserRequests() {
        System.out.println("Staff " + getUsername() + " resolved user requests.");
    }


}
