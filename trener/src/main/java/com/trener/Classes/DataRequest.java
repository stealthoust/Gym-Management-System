package com.trener.Classes;

import com.google.gson.Gson;
import com.trener.RequestClasses.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DataRequest {

    public static ObservableList<Kategoria> getCategories() throws IOException, InterruptedException {
        Gson gson = new Gson();
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create("http://localhost:3000/data/categories")).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String odpowiedz = response.body();
        Kategoria[] kategorie = gson.fromJson(odpowiedz, Kategoria[].class);
        ObservableList<Kategoria> lista = FXCollections.observableArrayList(kategorie);
        return lista;
    }

    public static ObservableList<User> getVerifiedUsersCoach(int id) throws IOException, InterruptedException {
        Gson gson = new Gson();
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create("http://localhost:3000/staff/verified/" + id)).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String odpowiedz = response.body();
        User[] podopieczni = gson.fromJson(odpowiedz, User[].class);
        ObservableList<User> lista = FXCollections.observableArrayList(podopieczni);
        return lista;
    }
    public static ObservableList<User> getUnverifiedUsersCoach(int id) throws IOException, InterruptedException {
        Gson gson = new Gson();
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create("http://localhost:3000/staff/unverified/" + id)).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String odpowiedz = response.body();
        User[] podopieczni = gson.fromJson(odpowiedz, User[].class);
        ObservableList<User> lista = FXCollections.observableArrayList(podopieczni);
        return lista;
    }
    public static ObservableList<User> getUsersAdmin() throws IOException, InterruptedException {
        Gson gson = new Gson();
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create("http://localhost:3000/admin/allusers/" )).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String odpowiedz = response.body();
        User[] podopieczni = gson.fromJson(odpowiedz, User[].class);
        ObservableList<User> lista = FXCollections.observableArrayList(podopieczni);
        return lista;
    }
    public static ObservableList<Coach> getCoachesAdmin() throws IOException, InterruptedException {
        Gson gson = new Gson();
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create("http://localhost:3000/admin/allcoaches/" )).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String odpowiedz = response.body();
        Coach[] trenerzy = gson.fromJson(odpowiedz, Coach[].class);
        ObservableList<Coach> lista = FXCollections.observableArrayList(trenerzy);
        return lista;
    }
    public static ObservableList<Cwiczenie> getExercisesFromCategory(int id) throws IOException, InterruptedException {
        Gson gson = new Gson();
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create("http://localhost:3000/data/exercises_category/" + id)).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String odpowiedz = response.body();
        Cwiczenie[] cwiczenia = gson.fromJson(odpowiedz, Cwiczenie[].class);
        ObservableList<Cwiczenie> lista = FXCollections.observableArrayList(cwiczenia);
        return lista;
    }

    public static ObservableList<Plan> getTrainingPlans(int id) throws IOException, InterruptedException {
        Gson gson = new Gson();
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create("http://localhost:3000/users/plans/" + id)).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String odpowiedz = response.body();
        Plan[] plany = gson.fromJson(odpowiedz, Plan[].class);
        ObservableList<Plan> lista = FXCollections.observableArrayList(plany);
        return lista;
    }

    public static ObservableList<PlanCounter> getTrainingPlansCount(int id) throws IOException, InterruptedException {
        Gson gson = new Gson();
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create("http://localhost:3000/users/plan_trainings/count/" + id)).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String odpowiedz = response.body();
        PlanCounter[] plany = gson.fromJson(odpowiedz, PlanCounter[].class);
        ObservableList<PlanCounter> lista = FXCollections.observableArrayList(plany);
        return lista;
    }

    public static ObservableList<Cwiczenie> getExercisesFromTrainingPlan(int id) throws IOException, InterruptedException {
        Gson gson = new Gson();
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create("http://localhost:3000/users/plan/exercises/" + id)).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String odpowiedz = response.body();
        Cwiczenie[] cwiczenia = gson.fromJson(odpowiedz, Cwiczenie[].class);
        ObservableList<Cwiczenie> lista = FXCollections.observableArrayList(cwiczenia);
        return lista;
    }

    public static ObservableList<UserInfo> getUserInfo(int id) throws IOException, InterruptedException {
        Gson gson = new Gson();
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create("http://localhost:3000/users/profile/" + id)).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String odpowiedz = response.body();
        UserInfo userInfos = gson.fromJson(odpowiedz, UserInfo.class);
        ObservableList<UserInfo> lista = FXCollections.observableArrayList(userInfos);

            lista.get(0).setWaga(userInfos.getWaga());
            lista.get(0).setWzrost(userInfos.getWzrost());
            lista.get(0).setAktywnosc(userInfos.getAktywnosc());
            lista.get(0).setCel(userInfos.getCel());
            lista.get(0).setZapotrzebowanie(userInfos.getZapotrzebowanie());

        return lista;
    }

    public static ObservableList<UserPomiar> getUserMeasurements(int id) throws IOException, InterruptedException {
        Gson gson = new Gson();
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create("http://localhost:3000/staff/measurements_user/" + id)).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String odpowiedz = response.body();
        UserPomiar[] plany = gson.fromJson(odpowiedz, UserPomiar[].class);
        ObservableList<UserPomiar> lista = FXCollections.observableArrayList(plany);
        return lista;
    }
}
