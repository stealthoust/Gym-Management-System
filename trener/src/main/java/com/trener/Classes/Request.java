package com.trener.Classes;

import com.trener.ControllersCoach.*;
import com.trener.RequestClasses.Account;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Request {


    public static int getAllUnverifiedUsers(int id) throws Exception {
        var url = "http://localhost:3000/staff/count_unverified/" + id;
        HttpURLConnection con = null;
        URL myurl = new URL(url);
        con = (HttpURLConnection) myurl.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Java client");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        int responseCode = con.getResponseCode();
       /* System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);*/
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONArray resultsArray;
        if (response.toString().contains("liczba")) {
            resultsArray = new JSONArray(response.toString());
            return resultsArray.getJSONObject(0).getInt("liczba");
        } else {
            return 0;
        }

    }

    public static void registerCoach(String login, String haslo, String email, String imie, String nazwisko) throws Exception {
        String url = "http://localhost:3000/staff/register/";
        String urlParameters = "login=" + login + "&password=" + haslo + "&email=" + email + "&imie=" + imie + "&nazwisko=" + nazwisko;
        HttpURLConnection con = null;
        var myurl = new URL(url);
        con = (HttpURLConnection) myurl.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Java client");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
/*        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);*/
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);

        }
        in.close();
        if (response.toString().contains("Exists")) {
            System.out.println("Trener z podanym emailem lub loginem istnieje");
            Alerts.alertError("Błąd", "Trener z podanym emailem lub loginem istnieje");
        } else if (response.toString().contains("registered")) {
            System.out.println("pomyslnie zarejestrowano");
            RegisterController.success = 1;
            Alerts.alertSuccess("Sukces", "Pomyslnie zarejestrowano. Sprawdź swój email w celu aktywacji konta!");
        } else {
            System.out.println("Wprowadzono błędne dane");
            Alerts.alertError("Błąd", "Cos poszlo nie tak. Być może dane, które wprowadziłeś są nieprawidłowe.");
        }
    }

    public static void zaloguj(String login, String haslo) throws Exception {
        if(!Functions.validateLoginData(login, haslo)) return;
        Account.clearFields();
        var url = "http://localhost:3000/staff/login/";
        var urlParameters = "login=" + login + "&password=" + haslo;
        HttpURLConnection con = null;
        var myurl = new URL(url);
        try {
            con = (HttpURLConnection) myurl.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
        } catch (Exception e) {
            System.out.println("Błąd połączenia");
            Alerts.alertError("Błąd", "Błąd połączenia z serwerem");
            return;
        }

        try{
            JSONObject jsonObj;
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);

            }
            in.close();
            if (response.toString().contains("Wrong") || response.toString().contains("doesnt")) {
                System.out.println("Nieprawidłowy login lub hasło!");
                Alerts.alertError("Błąd", "Nieprawidłowy login lub hasło!");
            } else if (response.toString().contains("imie")) {
                jsonObj = new JSONObject(response.toString());
                new Account(jsonObj.getInt("id"), jsonObj.getString("login"), jsonObj.getString("imie"), jsonObj.getString("nazwisko"), jsonObj.getString("email"), jsonObj.getString("rola"), jsonObj.getString("weryfikacja"));
            } else {
                System.out.println("Cos poszlo nie tak");
                Alerts.alertError("Błąd", "Wprowadzono błędne dane!");
            }
        }
        catch (Exception exception)
        {
            Alerts.alertError("Błąd", "Błąd połączenia z serwerem");
        }



    }

    public static void addPlan(String nazwa, String opis, String user, ArrayList<String> cwiczenia) {
        Map<String, Object> lista = new HashMap<>();
        lista.put("nazwa", nazwa);
        lista.put("opis", opis);
        lista.put("user", user);
        lista.put("lista", cwiczenia);
        String response = RestAssured.given().header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON).body(lista).
                when().post("http://localhost:3000/staff/addplan/").then().extract().response().asPrettyString();
        if (response.contains("successfully")) {
            System.out.println("Udalo sie");
            System.out.println(response);
            AddTrainingPlan.success = 1;

        } else {
            System.out.println(response);
            System.out.println("Nie udalo sie");
        }

    }

    public static void updatePlan(String nazwa, String opis, String plan, ArrayList<String> cwiczenia) {
        Map<String, Object> lista = new HashMap<>();
        lista.put("nazwa", nazwa);
        lista.put("opis", opis);
        lista.put("plan", plan);
        lista.put("lista", cwiczenia);
        String response = RestAssured.given().header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON).body(lista).
                when().post("http://localhost:3000/staff/updateplan/").then().extract().response().asPrettyString();
        if (response.contains("successfully")) {
            System.out.println("Udalo sie");
            System.out.println(response);
            ModifyTrainingPlan.success = 1;

        } else {
            System.out.println(response);
            System.out.println("Nie udalo sie");
        }

    }

    public static void deletePlan(int id) {
        String response = RestAssured.given().header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON).
                when().delete("http://localhost:3000/staff/plan/delete/" + id).then().extract().response().asPrettyString();
        if (response.contains("successfully")) {
            System.out.println("Udalo sie");
            System.out.println(response);
            ModifyTrainingPlan.success = 1;

        } else {
            System.out.println(response);
            System.out.println("Nie udalo sie");
        }

    }

    public static void verifyUser(int id) throws IOException {
        var url = "http://localhost:3000/staff/verifyuser/";
        var urlParameters = "id=" + id;
        HttpURLConnection con = null;
        var myurl = new URL(url);
        con = (HttpURLConnection) myurl.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Java client");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);

        }
        in.close();
        if (response.toString().contains("successfully")) {
            System.out.println("Udało się zweryfikować");
            Alerts.alertSuccess("Sukces", "Użytkownik został zweryfikowany");
        } else if (response.toString().contains("Problem")) {
            System.out.println("Problem z weryfikacja");
            Alerts.alertError("Błąd", "Wystąpił problem z weryfikacją użytkownika!");
        } else {
            System.out.println("Nie udało się połączyć z serwerem");
            Alerts.alertError("Brak połączenia", "Nie udało się połączyć z serwerem!");
        }


    }

    public static void dismissUser(int id) throws IOException {
        var url = "http://localhost:3000/staff/dismissuser/";
        var urlParameters = "id=" + id;
        HttpURLConnection con = null;
        var myurl = new URL(url);
        con = (HttpURLConnection) myurl.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Java client");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);

        }
        in.close();
        if (response.toString().contains("successfully")) {
            System.out.println("Pomyślnie usunięto użytkownika");
            Alerts.alertSuccess("Sukces", "Użytkownik został odrzucony");
        } else if (response.toString().contains("dismissing")) {
            System.out.println("Problem z odrzuceniem");
            Alerts.alertError("Błąd", "Wystąpił problem z odrzuceniem użytkownika!");
        } else {
            System.out.println("Nie udało się połączyć z serwerem");
            Alerts.alertError("Brak połączenia", "Nie udało się połączyć z serwerem!");
        }

    }

    public static void addExercise(String nazwa, String pozycja, String ruch, String wskazowki, String url, int kategoria) {
        Map<String, Object> lista = new HashMap<>();
        lista.put("nazwa", nazwa);
        lista.put("pozycja", pozycja);
        lista.put("ruch", ruch);
        lista.put("wskazowki", wskazowki);
        lista.put("url", url);
        lista.put("kategoria", kategoria);
        String response = RestAssured.given().header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON).body(lista).
                when().post("http://localhost:3000/data/exercise/add/").then().extract().response().asPrettyString();
        if (response.contains("dodano")) {
            System.out.println("Udalo sie dodac cwiczenie");
            System.out.println(response);
            ModifyExercise.success = 1;

        } else {
            System.out.println(response);
            System.out.println("Nie udalo sie dodac cwiczenia");
        }
    }

    public static void updateExercise(String nazwa, String pozycja, String ruch, String wskazowki, String url, int id_cwiczenia) {
        Map<String, Object> lista = new HashMap<>();
        lista.put("nazwa", nazwa);
        lista.put("pozycja", pozycja);
        lista.put("ruch", ruch);
        lista.put("wskazowki", wskazowki);
        lista.put("url", url);
        lista.put("id", id_cwiczenia);
        String response = RestAssured.given().header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON).body(lista).
                when().post("http://localhost:3000/data/exercise/update/").then().extract().response().asPrettyString();
        if (response.contains("zaktualizowano")) {
            System.out.println("Udalo sie zaaktualizować cwiczenie");
            System.out.println(response);
            ModifyExercise.success = 1;

        } else {
            System.out.println(response);
            System.out.println("Nie udalo sie zaktualizować cwiczenia");
        }
    }

    public static void deleteExercise(int id) {
        String response = RestAssured.given().header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON).
                when().delete("http://localhost:3000/data/exercise/delete/" + id).then().extract().response().asPrettyString();
        if (response.contains("successfully")) {
            System.out.println("Udalo sie");
            System.out.println(response);
            ModifyExercise.success = 1;

        } else {
            System.out.println(response);
            System.out.println("Nie udalo sie");
        }

    }

    public static void changePassword(int id, String old_pass, String new_pass) {
        Map<String, Object> lista = new HashMap<>();
        lista.put("id", id);
        lista.put("old_password", old_pass);
        lista.put("new_password", new_pass);
        String response = RestAssured.given().header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON).body(lista).
                when().post("http://localhost:3000/staff/password/change/").then().extract().response().asPrettyString();
        if (response.contains("updated")) {
            System.out.println("Udalo sie zaaktualizować haslo");
            System.out.println(response);
            Alerts.alertSuccess("Sukces", "Hasło zostało zmienione. Zostaniesz wylogowany.");
            SettingsCoach.success = 1;

        } else if (response.contains("Wrong")) {
            System.out.println(response);
            System.out.println("Podałeś złe hasło");
            Alerts.alertError("Błąd", "Podane przez ciebie hasło jest nieprawidłowe");
        } else {
            System.out.println(response);
            System.out.println("Nie udalo sie zaktualizować hasła");
            Alerts.alertError("Błąd", "Wystąpił problem z zmianą hasła!");
        }
    }

    public static void changeEmail(int id, String email) {
        Map<String, Object> lista = new HashMap<>();
        lista.put("id", id);
        lista.put("email", email);
        String response = RestAssured.given().header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON).body(lista).
                when().post("http://localhost:3000/staff/email/change/").then().extract().response().asPrettyString();
        if (response.contains("updated")) {
            System.out.println("Udalo sie zaaktualizować email");
            System.out.println(response);
            Alerts.alertSuccess("Sukces", "Email został zmieniony. Zostaniesz wylogowany.");
            SettingsCoach.success = 1;

        } else if (response.contains("already")) {
            System.out.println(response);
            System.out.println("Nie udalo sie zaktualizować emaila");
            Alerts.alertError("Błąd", "Email jest już zajęty");
        } else {
            {
                System.out.println(response);
                System.out.println("Nie udalo sie zaktualizować emaila");
                Alerts.alertError("Błąd", "Wystąpił problem z zmianą emaila!");
            }
        }
    }

    public static void changeUserDataAdmin(int id, String imie, String nazwisko, String dataUrodzenia) {
        Map<String, Object> lista = new HashMap<>();
        lista.put("id", id);
        lista.put("imie", imie);
        lista.put("nazwisko", nazwisko);
        lista.put("dataUrodzenia", dataUrodzenia);
        String response = RestAssured.given().header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON).body(lista).
                when().post("http://localhost:3000/admin/updateuser").then().extract().response().asPrettyString();
        if (response.contains("updated")) {
            System.out.println("Udalo sie zaaktualizować dane");
            System.out.println(response);
            Alerts.alertSuccess("Sukces", "Dane użytkownika zostały zmienione");


        } else if (response.contains("date")) {
            System.out.println(response);
            System.out.println("Nie udalo sie zaktualizować danych");
            Alerts.alertError("Błąd", "Wprowadzileś nieprawidłowy format daty!\n(Wzór: YYYY-MM-DD)!");
        } else {
            {
                System.out.println(response);
                System.out.println("Nie udalo sie zaktualizować danych");
                Alerts.alertError("Błąd", "Wystąpił problem z aktualizacją danych!");
            }
        }
    }

    public static void deleteUser(int id) {
        Map<String, Object> lista = new HashMap<>();
        lista.put("id", id);
        String response = RestAssured.given().header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON).body(lista).
                when().post("http://localhost:3000/admin/deleteuser").then().extract().response().asPrettyString();
        if (response.contains("successfully")) {
            System.out.println("Udalo sie usunac uzytkownika");
            System.out.println(response);
            Alerts.alertSuccess("Sukces", "Użytkownik został usunięty!");

        } else {
            {
                System.out.println(response);
                System.out.println("Nie udało się usunąć użytkownika!");
                Alerts.alertError("Błąd", "Nie udało się usunąć użytkownika!");
            }
        }
    }

    public static void changeCoachDataAdmin(int id, String imie, String nazwisko, String rola, String weryfikacja) {
        Map<String, Object> lista = new HashMap<>();
        lista.put("id", id);
        lista.put("imie", imie);
        lista.put("nazwisko", nazwisko);
        lista.put("rola", rola);
        lista.put("weryfikacja", weryfikacja);
        String response = RestAssured.given().header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON).body(lista).
                when().post("http://localhost:3000/admin/updatecoach").then().extract().response().asPrettyString();
        if (response.contains("updated")) {
            System.out.println("Udalo sie zaaktualizować dane");
            System.out.println(response);
            Alerts.alertSuccess("Sukces", "Dane trenera zostały zmienione");

        } else {
            {
                System.out.println(response);
                System.out.println("Nie udalo sie zaktualizować danych");
                Alerts.alertError("Błąd", "Wystąpił problem z aktualizacją danych!");
            }
        }
    }

    public static void deleteCoach(int id) {
        Map<String, Object> lista = new HashMap<>();
        lista.put("id", id);
        String response = RestAssured.given().header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON).body(lista).
                when().post("http://localhost:3000/admin/deletecoach").then().extract().response().asPrettyString();
        if (response.contains("successfully")) {
            System.out.println("Udalo sie usunac trenera");
            System.out.println(response);
            Alerts.alertSuccess("Sukces", "Trener został usunięty!");

        } else {
            {
                System.out.println(response);
                System.out.println("Nie udało się usunąć użytkownika!");
                Alerts.alertError("Błąd", "Nie udało się usunąć użytkownika!");
            }
        }
    }

    public static void resetPassword(String email) {
        LoginController.success = 0;
        Map<String, Object> lista = new HashMap<>();
        lista.put("email", email);
        String response = RestAssured.given().header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON).body(lista).
                when().post("http://localhost:3000/staff/email/resetpassword/").then().extract().response().asPrettyString();
        if (response.contains("wysłana")) {
            System.out.println("Udalo sie wyslac wiadomosc");
            System.out.println(response);
            Alerts.alertSuccess("Sukces", "Login wraz z prośbą o zmianę hasła został wysłany na podany email");
            LoginController.success = 1;

        } else {
            {
                System.out.println(response);
                System.out.println("Nie udalo sie wysłac wiadomości, być może podany email nie istnieje w naszym systemie.");
                Alerts.alertError("Błąd", "Nie udalo sie wysłac wiadomości, być może podany email nie istnieje w naszym systemie.");
            }
        }
    }

    public static void changePersonalData(int id, String imie, String nazwisko) {
        Map<String, Object> lista = new HashMap<>();
        lista.put("id", id);
        lista.put("imie", imie);
        lista.put("nazwisko", nazwisko);
        String response = RestAssured.given().header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON).body(lista).
                when().post("http://localhost:3000/staff/personal_data/change/").then().extract().response().asPrettyString();
        if (response.contains("updated")) {
            System.out.println("Udalo sie zaaktualizować dane personalne");
            System.out.println(response);
            Alerts.alertSuccess("Sukces", "Dane personalne zostały zmienione. Zostaniesz wylogowany.");
            SettingsCoach.success = 1;

        } else {
            {
                System.out.println(response);
                System.out.println("Nie udalo sie zaktualizować emaila");
                Alerts.alertError("Błąd", "Wystąpił problem ze zmianą danych!");
            }
        }
    }

    public static String[] getTrainingCount(int id) throws Exception {
        String[] result = new String[3];
        var url = "http://localhost:3000/users/trainings/count/" + id;
        HttpURLConnection con = null;
        URL myurl = new URL(url);
        con = (HttpURLConnection) myurl.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Java client");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        int responseCode = con.getResponseCode();
       /* System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);*/
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONObject jsonObject;
        if (response.toString().contains("per")) {
            jsonObject = new JSONObject(response.toString());
            result[0] = jsonObject.getString("per_week");
            result[1] = jsonObject.getString("per_month");
            result[2] = jsonObject.getString("per_year");
            return result;
        } else {
            result[0] = "0";
            result[1] = "0";
            result[2] = "0";
            return result;
        }

    }

    public static JSONArray getWeekCalories(int id) throws Exception {
        var url = "http://localhost:3000/users/calories/week/" + id;
        HttpURLConnection con = null;
        URL myurl = new URL(url);
        con = (HttpURLConnection) myurl.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Java client");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        int responseCode = con.getResponseCode();
       /* System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);*/
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONArray jsonArray = new JSONArray(response.toString());
        System.out.println(jsonArray.getJSONObject(0));
        return jsonArray;

    }

    public static JSONObject getStatisticsAdmin() throws Exception {
        String response = RestAssured.given().header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON).
                when().get("http://localhost:3000/admin/statistics").then().extract().response().asPrettyString();
        if (response.contains("users_all")) {
            System.out.println("Udalo sie pobrac statystyki");
            System.out.println(response);
            JSONObject jsonObject = new JSONObject(response);
            System.out.println("array: " + jsonObject.getString("users_all"));
            return jsonObject;

        } else {
            System.out.println(response);
            System.out.println("Nie udalo sie pobrać statystyk");
            Alerts.alertError("Błąd", "Nie udalo sie pobrać statystyk.");
            return null;
        }

    }

    public static void blockUser(int id) throws Exception
    {
        var url = "http://localhost:3000/admin/blockuser/";
        var urlParameters = "id=" + id ;
        HttpURLConnection con = null;
        var myurl = new URL(url);
        try {
            con = (HttpURLConnection) myurl.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
        } catch (Exception e) {
            System.out.println("Błąd połączenia");
            Alerts.alertError("Błąd", "Błąd połączenia z serwerem");
            return;
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);

        }
        in.close();
        if (response.toString().contains("successfully")) {
            System.out.println("pomyślnie zablokowano użytkownika");
            Alerts.alertSuccess("Sukces", "Użytkownik został zablokowany");

        } else if (response.toString().contains("Problem")) {
            System.out.println("Nie udało się zablokowć użytkownika");
            Alerts.alertError("Błąd", "Nie udało się zablokować użytkownika!");
        } else {
            System.out.println("Cos poszlo nie tak");
            Alerts.alertError("Błąd", "Wystąpił nieznany problem!");
        }
    }
    public static void unblockUser(int id) throws Exception
    {        var url = "http://localhost:3000/admin/unblockuser/";
        var urlParameters = "id=" + id ;
        HttpURLConnection con = null;
        var myurl = new URL(url);
        try {
            con = (HttpURLConnection) myurl.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
        } catch (Exception e) {
            System.out.println("Błąd połączenia");
            Alerts.alertError("Błąd", "Błąd połączenia z serwerem");
            return;
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);

        }
        in.close();
        if (response.toString().contains("successfully")) {
            System.out.println("pomyślnie odblokowano użytkownika");
            Alerts.alertSuccess("Sukces", "Użytkownik został odblokowany");

        } else if (response.toString().contains("Problem")) {
            System.out.println("Nie udało się odblokować użytkownika");
            Alerts.alertError("Błąd", "Nie udało się odblokować użytkownika!");
        } else {
            System.out.println("Cos poszlo nie tak");
            Alerts.alertError("Błąd", "Wystąpił nieznany problem!");
        }}
}
