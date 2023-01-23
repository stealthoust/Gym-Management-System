package my.app.RequestClasses;

import com.google.gson.annotations.SerializedName;

public class Exercise {
    @SerializedName("id")
    int id;
    @SerializedName("nazwa")
    String nazwa;
    @SerializedName("pozycja_wyjsciowa")
    String pozycja_wyjsciowa;
    @SerializedName("ruch")
    String ruch;
    @SerializedName("wskazowki")
    String wskazowki;
    @SerializedName("video_url")
    String video_url;
    @SerializedName("kategoria")
    String kategoria;

    public Exercise(int id, String nazwa, String pozycja_wyjsciowa, String ruch, String wskazowki, String video_url, String kategoria) {
        this.id = id;
        this.nazwa = nazwa;
        this.pozycja_wyjsciowa = pozycja_wyjsciowa;
        this.ruch = ruch;
        this.wskazowki = wskazowki;
        this.video_url = video_url;
        this.kategoria = kategoria;
    }

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

    public String getKategoria() {
        return kategoria;
    }

    public void setKategoria(String kategoria) {
        this.kategoria = kategoria;
    }
}
