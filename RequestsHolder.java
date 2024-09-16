package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RequestsHolder {

    private static List<Request> requestsList = new ArrayList<>();


    public static void addRequest(Request request) {
        requestsList.add(request);
    }


    public static void removeRequest(Request request) {
        requestsList.remove(request);
    }


    public static List<Request> getAllRequests() {
        return new ArrayList<>(requestsList);
    }



}

