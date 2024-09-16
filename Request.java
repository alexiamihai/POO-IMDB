package org.example;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String notification);
}
public class Request implements Subject{
    private RequestTypes type;
    private LocalDateTime createdDate;
    private String username;
    private String to;
    private String description;

    private String subject;
    private List<Observer> observers = new ArrayList<>();
    private String notification;

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String notification) {
        this.notification = notification;
        for (Observer observer : observers) observer.update(notification);
    }
    // Constructor
    public Request(RequestTypes type, LocalDateTime createdDate, String username, String to, String description, String subject) {
        this.type = type;
        this.createdDate = createdDate;
        this.username = username;
        this.to = to;
        this.description = description;
        this.subject = subject;
    }


    public String toString() {
        return "Request{" +
                "\n  type=" + type +
                "\n  createdDate=" + getFormattedDate() +
                "\n  username='" + username + '\'' +
                "\n  to='" + to + '\'' +
                "\n  description='" + description + '\'' +
                "\n  subject='" + subject + '\'' +
                "\n}";
    }

    public void assignToUser(User user, Request newRequest) {
        Staff s = (Staff) user;
        s.getAssignedRequests().add(newRequest);
    }
    public void deleteFromUser(User user, Request newRequest) {
        Staff s = (Staff) user;
        s.getAssignedRequests().remove(newRequest);
    }

    public void resolveRequest() {
        System.out.println("Request has been solved for " + username);
        notifyObservers("Your request has been solved!");
    }

    public void rejectRequest() {
        System.out.println("Request has been rejected for " + username);
        notifyObservers("Your request has been rejected, " + username + "!");
    }

    // Getter pentru type
    public RequestTypes getType() {
        return type;
    }

    // Setter pentru type
    public void setType(RequestTypes type) {
        this.type = type;
    }

    // Getter pentru createdDate
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    // Setter pentru createdDate
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    // Getter pentru username
    public String getUsername() {
        return username;
    }

    // Setter pentru username
    public void setUsername(String username) {
        this.username = username;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return createdDate.format(formatter);
    }

    public String getToo() {
        return to;
    }

    // Setter pentru to
    public void setToo(String to) {
        this.to = to;
    }

    // Getter pentru description
    public String getDescription() {
        return description;
    }

    // Setter pentru description
    public void setDescription(String description) {
        this.description = description;
    }


    // Getter pentru movieTitle
    public String getSubject() {
        return subject;
    }

    // Setter pentru movieTitle
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<Observer> getObservers() {return observers;}


}
