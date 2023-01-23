package com.trener.RequestClasses;

public class UserInfo {
    public Double waga;
    public Double wzrost;
    public String aktywnosc;
    public String cel;
    public double zapotrzebowanie;


    public double getZapotrzebowanie() {
        return zapotrzebowanie;
    }

    public void setZapotrzebowanie(double zapotrzebowanie) {
        this.zapotrzebowanie = zapotrzebowanie;
    }

    public Double getWaga() {
        return waga;
    }

    public void setWaga(Double waga) {
        this.waga = waga;
    }

    public Double getWzrost() {
        return wzrost;
    }

    public void setWzrost(Double wzrost) {
        this.wzrost = wzrost;
    }

    public String getAktywnosc() {
        return aktywnosc;
    }

    public void setAktywnosc(String aktywnosc) {
        this.aktywnosc = aktywnosc;
    }

    public String getCel() {
        return cel;
    }

    public void setCel(String cel) {
        this.cel = cel;
    }
}
