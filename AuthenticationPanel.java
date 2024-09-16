package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;

public class AuthenticationPanel extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private List<User<String>> users;
    private List<Production> productions;
    private List<Actor> actors;
    private List<Request> requests;


    public AuthenticationPanel(ActionListener loginActionListener, List<User<String>> users, List<Production> productions, List<Actor> actors, List<Request> requests) {

        this.users = users;
        this.productions = productions;
        this.actors = actors;
        this.requests = requests;
        initializeComponents();


        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JLabel("Welcome back! Enter your credentials:"));

        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emailPanel.add(new JLabel("Email: "));
        emailPanel.add(emailField);
        add(emailPanel);

        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordPanel.add(new JLabel("Password: "));
        passwordPanel.add(passwordField);
        add(passwordPanel);

        add(loginButton);

        loginButton.addActionListener(e -> {
            String enteredEmail = getEmail();
            char[] enteredPassword = getPassword();

            Optional<User<String>> authenticatedUser = users.stream()
                    .filter(user -> {


//                                ", Password: " + user.getInformation().getPassword());

                        return user.getInformation().getEmail().equals(enteredEmail) &&
                                Arrays.equals(user.getInformation().getPassword().toCharArray(), enteredPassword);
                    })
                    .findFirst();

            boolean loggedIn = authenticatedUser.isPresent();
            System.out.println("Logged In: " + loggedIn);

            if (loggedIn) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                openMainApplication();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.");
            }
        });
    }


    private void openMainApplication() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainApplicationWindow mainAppWindow = new MainApplicationWindow(users, productions, actors, requests);
                mainAppWindow.setVisible(true);
            }
        });
    }

    private void initializeComponents() {
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
    }

    public String getEmail() {
        return emailField.getText();
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public void clearFields() {
        emailField.setText("");
        passwordField.setText("");
    }

    public void setDefaultValues() {
        emailField.setText("default@example.com");
        passwordField.setText("defaultPassword");
    }
}
