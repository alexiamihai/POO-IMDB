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
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class IMDBgui extends JFrame{

    private static IMDBgui instance = null;
    private JsonManager jsonManager;
    List<User<String>> users = new ArrayList<>();
    List<Production> productions = new ArrayList<>();
    List<Actor> actors = new ArrayList<>();
    List<Request> requests = new ArrayList<>();
    public static IMDBgui getInstance() {
        if (instance == null) {
            instance = new IMDBgui();
        }
        return instance;
    }

    public void run() {
        try {
            jsonManager = JsonManager.getInstance();
            jsonManager.loadUserDataFromJSON(users, productions, actors, requests);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }




    private AuthenticationPanel authenticationPanel;

    public IMDBgui() {
        setTitle("IMDb Application");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeComponents();
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(authenticationPanel);

        setVisible(true);
    }

    private void initializeComponents() {
        authenticationPanel = new AuthenticationPanel(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        }, users,productions,actors,requests);
    }

    private void openMainApplication() {
        MainApplicationWindow mainAppWindow = new MainApplicationWindow(users,productions,actors,requests);
        mainAppWindow.setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                IMDBgui imdbGUI = IMDBgui.getInstance();
                imdbGUI.run();
            }
        });
    }

}
