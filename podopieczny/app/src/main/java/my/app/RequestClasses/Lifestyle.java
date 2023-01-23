package my.app.RequestClasses;

import androidx.annotation.NonNull;

public class Lifestyle {
    int id;
    String opis;
    double wartosc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    @NonNull
    @Override
    public String toString() {
        return opis;
    }

    public double getWartosc() {
        return wartosc;
    }

    public void setWartosc(double wartosc) {
        this.wartosc = wartosc;
    }
}
