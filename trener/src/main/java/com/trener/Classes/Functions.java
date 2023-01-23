package com.trener.Classes;

import com.trener.ControllersCoach.RegisterController;
import org.apache.commons.validator.routines.EmailValidator;

public class Functions {
    public static final EmailValidator validator = EmailValidator.getInstance();

    public static boolean validatePassword(String old, String newpassword, String repeat) {
        if (old.isEmpty() || newpassword.isEmpty() || repeat.isEmpty()) {
            Alerts.alertError("Błąd", "Wszystkie pola muszą być wypełnione");
            return false;
        } else if (!newpassword.matches("[a-zA-Z0-9]+") || !old.matches("[a-zA-Z0-9]+")) {
            Alerts.alertError("Błąd", "Hasło musi składać się tylko z liter i cyfr");
            return false;
        } else if (newpassword.length() < 8 || newpassword.length() > 30) {
            Alerts.alertError("Błąd", "Hasło musi zawierać pomiędzy 8 a 30 znaków");
            return false;

        } else if (!newpassword.equals(repeat)) {
            Alerts.alertError("Błąd", "Podane hasła nie są identyczne");
            return false;

        }
        return true;

    }

    public static boolean validateEmailMessage(String email) {
        if (!validator.isValid(email)) {
            Alerts.alertError("Błąd", "Niepoprawny adres email");
            return false;
        } else return true;
    }

    public static boolean validateEmail(String email) {
        if (!validator.isValid(email)) {
            return false;
        } else return true;
    }

    public static boolean validatePersonalData(String imie, String nazwisko) {
        if (!imie.matches("[\\p{IsAlphabetic}]+")) {
            Alerts.alertError("Błąd", "Imię musi się składać tylko z liter");
            return false;
        } else if (imie.length() < 3 || imie.length() > 13) {
            Alerts.alertError("Błąd", "Imię musi się zawierać pomiędzy 3 a 13 znaków");
            return false;
        } else if (!nazwisko.matches("[\\p{IsAlphabetic}]+")) {
            Alerts.alertError("Błąd", "Nazwisko musi się składać tylko z liter");
            return false;
        } else if (nazwisko.length() < 2 || nazwisko.length() > 35) {
            Alerts.alertError("Błąd", "Imię musi się zawierać pomiędzy 2 a 35 znaków");
            return false;
        } else return true;
    }

    public static boolean validateLoginData(String login, String password) {
        if (login.length() < 5 || login.length() > 30) {
            Alerts.alertError("Błąd", "Login musi zawierać pomiędzy 5 a 30 znaków");
            return false;
        } else if (password.length() < 8 || password.length() > 30) {
            Alerts.alertError("Błąd", "Hasło musi zawierać pomiędzy 8 a 30 znaków");
            return false;
        } else if (!login.matches("[a-zA-Z0-9]+") || !password.matches("[a-zA-Z0-9]+")) {
            Alerts.alertError("Błąd", "Login i hasło musi składać się tylko z liter i cyfr!");
            return false;
        }
        return true;
    }

}
