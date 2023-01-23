package my.app.RequestClasses;

public class Measurement {
    String id;
    String datap;
    String waga;
    String waga_start;

    public String getWaga_start() {
        return waga_start;
    }

    public void setWaga_start(String waga_start) {
        this.waga_start = waga_start;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatap() {
        return datap;
    }

    public void setDatap(String datap) {
        this.datap = datap;
    }

    public String getWaga() {
        return waga;
    }

    public void setWaga(String waga) {
        this.waga = waga;
    }
}
