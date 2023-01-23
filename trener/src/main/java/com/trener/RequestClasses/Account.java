package com.trener.RequestClasses;

public class Account {
    public static int id;
    public static String login;
    public static String imie;
    public static String nazwisko;
    public static String email;
    public static String rola;
    public static String weryfikacja;

    public Account(int id, String login, String imie, String nazwisko, String email, String rola, String weryfikacja) {
        Account.id = id;
        Account.login = login;
        Account.imie = imie;
        Account.nazwisko = nazwisko;
        Account.email = email;
        Account.rola = rola;
        Account.weryfikacja = weryfikacja;
    }


    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        Account.id = id;
    }

    public static String getLogin() {
        return login;
    }

    public static void setLogin(String login) {
        Account.login = login;
    }

    public static String getImie() {
        return imie;
    }

    public static void setImie(String imie) {
        Account.imie = imie;
    }

    public static String getNazwisko() {
        return nazwisko;
    }

    public static void setNazwisko(String nazwisko) {
        Account.nazwisko = nazwisko;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        Account.email = email;
    }

    public static String getWeryfikacja() {
        return weryfikacja;
    }

    public static void setWeryfikacja(String weryfikacja) {
        Account.weryfikacja = weryfikacja;
    }

    public static void clearFields() {
        id = -1;
        login = null;
        imie = null;
        nazwisko = null;
        email = null;
        weryfikacja = null;
        rola = null;
    }

}
