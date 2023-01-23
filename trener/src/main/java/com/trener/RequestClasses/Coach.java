package com.trener.RequestClasses;

public class Coach {
    public int id;
    public String imie;
    public String nazwisko;
    public String email;
    public String weryfikacja;
    public String rola;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeryfikacja() {
        return weryfikacja;
    }

    public void setWeryfikacja(String weryfikacja) {
        this.weryfikacja = weryfikacja;
    }

    public String getRola() {
        return rola;
    }

    public void setRola(String rola) {
        this.rola = rola;
    }
}
