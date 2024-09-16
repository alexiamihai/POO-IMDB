package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.List;

public class MainApplicationWindow extends JFrame {
    private JButton viewProductionsButton;
    private JButton viewActorsButton;
    private JButton viewNotificationsButton;
    private JButton searchButton;
    private JButton logoutButton;

    private java.util.List<User<String>> users;
    private java.util.List<Production> productions;
    private java.util.List<Actor> actors;
    private List<Request> requests;


    public MainApplicationWindow(List<User<String>> users, List<Production> productions, List<Actor> actors, List<Request> requests) {
        this.users = users;
        this.productions = productions;
        this.actors = actors;
        this.requests = requests;
        setTitle("IMDb Main Window");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeComponents();
        setLayout(new GridLayout(5, 1));

        add(viewProductionsButton);
        add(viewActorsButton);
        add(viewNotificationsButton);
        add(searchButton);
        add(logoutButton);

        setVisible(true);
    }

    private void initializeComponents() {
        viewProductionsButton = new JButton("View Productions");
        viewActorsButton = new JButton("View Actors");
        viewNotificationsButton = new JButton("View Notifications");
        searchButton = new JButton("Search");
        logoutButton = new JButton("Logout");

        addActionListeners();
    }

    private void addActionListeners() {
        viewProductionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainApplicationWindow.this, "View Productions button clicked!");
            }
        });

        viewActorsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewActorDetails();
            }
        });

        viewNotificationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainApplicationWindow.this, "View Notifications button clicked!");
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainApplicationWindow.this, "Search button clicked!");
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(MainApplicationWindow.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    openAuthenticationPanel();
                }
            }
        });
    }

    private void openAuthenticationPanel() {
        AuthenticationPanel authenticationPanel = new AuthenticationPanel(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openMainApplication();
            }
        }, users, productions, actors, requests);
        setContentPane(authenticationPanel);
        revalidate();
        repaint();
    }

    private void openMainApplication() {
        MainApplicationWindow mainAppWindow = new MainApplicationWindow(users,productions,actors,requests);
        mainAppWindow.setVisible(true);
        dispose();
    }

    private void viewActorDetails() {
        int option = JOptionPane.showConfirmDialog(this, "Do you want to sort the results?", "Sort Actors", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            System.out.println("Sorting by name: ");
            actors.sort(Comparator.comparing(Actor::getName));
        }

        int batchSize = 10;

        for (int i = 0; i < actors.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, actors.size());
            List<Actor> batch = actors.subList(i, endIndex);

            StringBuilder actorDetails = new StringBuilder();
            for (Actor actor : batch) {
                actorDetails.append(actor.toString()).append("\n\n");
            }

            JTextArea textArea = new JTextArea(actorDetails.toString());
            textArea.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            JOptionPane.showMessageDialog(this, scrollPane, "Actor Details", JOptionPane.PLAIN_MESSAGE);
        }
    }
}
