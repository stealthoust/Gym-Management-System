package com.trener.RequestClasses;

public class Cwiczenie {
    public int id;
    public String nazwa;
    public String pozycja_wyjsciowa;
    public String ruch;
    public String wskazowki;
    public String video_url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getPozycja_wyjsciowa() {
        return pozycja_wyjsciowa;
    }

    public void setPozycja_wyjsciowa(String pozycja_wyjsciowa) {
        this.pozycja_wyjsciowa = pozycja_wyjsciowa;
    }

    public String getRuch() {
        return ruch;
    }

    public void setRuch(String ruch) {
        this.ruch = ruch;
    }

    public String getWskazowki() {
        return wskazowki;
    }

    public void setWskazowki(String wskazowki) {
        this.wskazowki = wskazowki;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
}
