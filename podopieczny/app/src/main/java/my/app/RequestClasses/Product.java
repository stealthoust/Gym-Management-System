package my.app.RequestClasses;

public class Product {

    String id;
    String uzytkownik_id;
    String nazwa;
    Double kcal;
    Double weglowodany;
    Double bialko;
    Double tluszcz;
    Double kcal_na_100g;
    Double weglowodany_na_100g;
    Double bialko_na_100g;
    Double tluszcz_na_100g;
    Double waga;


    public Double getKcal_na_100g() {
        return kcal_na_100g;
    }

    public void setKcal_na_100g(Double kcal_na_100g) {
        this.kcal_na_100g = kcal_na_100g;
    }

    public Double getWeglowodany_na_100g() {
        return weglowodany_na_100g;
    }

    public void setWeglowodany_na_100g(Double weglowodany_na_100g) {
        this.weglowodany_na_100g = weglowodany_na_100g;
    }

    public Double getBialko_na_100g() {
        return bialko_na_100g;
    }

    public void setBialko_na_100g(Double bialko_na_100g) {
        this.bialko_na_100g = bialko_na_100g;
    }

    public Double getTluszcz_na_100g() {
        return tluszcz_na_100g;
    }

    public void setTluszcz_na_100g(Double tluszcz_na_100g) {
        this.tluszcz_na_100g = tluszcz_na_100g;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUzytkownik_id() {
        return uzytkownik_id;
    }

    public void setUzytkownik_id(String uzytkownik_id) {
        this.uzytkownik_id = uzytkownik_id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public Double getKcal() {
        return kcal;
    }

    public void setKcal(Double kcal) {
        this.kcal = kcal;
    }

    public Double getWeglowodany() {
        return weglowodany;
    }

    public void setWeglowodany(Double weglowodany) {
        this.weglowodany = weglowodany;
    }

    public Double getBialko() {
        return bialko;
    }

    public void setBialko(Double bialko) {
        this.bialko = bialko;
    }

    public Double getTluszcz() {
        return tluszcz;
    }

    public void setTluszcz(Double tluszcz) {
        this.tluszcz = tluszcz;
    }

    public Double getWaga() {
        return waga;
    }

    public void setWaga(Double waga) {
        this.waga = waga;
    }
}
