package my.app.RequestClasses;

import androidx.annotation.NonNull;

public class WeightGoals {

    int id;
    String nazwa;
    double wartosc;

    @NonNull
    @Override
    public String toString() {
        return nazwa;
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

    public double getWartosc() {
        return wartosc;
    }

    public void setWartosc(double wartosc) {
        this.wartosc = wartosc;
    }
}
