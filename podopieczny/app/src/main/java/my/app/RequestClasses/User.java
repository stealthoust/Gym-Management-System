package my.app.RequestClasses;

import java.util.Date;

public class User {


    public static Integer id;
    public static String login;
    public static String imie;
    public static String nazwisko;
    public static String numer_tel;
    public static String data_ur;
    public static String data_utw;
    public static String weryfikacja;

    public User(int id,String login,String imie,String nazwisko,String numer,String data_ur,String data_utw,String weryfikacja) {
        setId(id);
        setLogin(login);
        setImie(imie);
        setNazwisko(nazwisko);
        setNumer_tel(numer);
        setData_ur(data_ur);
        setData_utw(data_utw);
        setWeryfikacja(weryfikacja);
    }

    public static void cleanUser()
    {

        setId(null);
        setLogin(null);
        setImie(null);
        setNazwisko(null);
        setNumer_tel(null);
        setData_ur(null);
        setData_utw(null);
        setWeryfikacja(null);
    }


    public static String getLogin() {
        return login;
    }

    public static void setLogin(String login) {
        User.login = login;
    }

    public static String getImie() {
        return imie;
    }

    public static void setImie(String imie) {
        User.imie = imie;
    }

    public static String getNazwisko() {
        return nazwisko;
    }

    public static void setNazwisko(String nazwisko) {
        User.nazwisko = nazwisko;
    }



    public static String getNumer_tel() {
        return numer_tel;
    }

    public static void setNumer_tel(String numer_tel) {
        User.numer_tel = numer_tel;
    }

    public static String getData_ur() {
        return data_ur;
    }

    public static void setData_ur(String data_ur) {
        User.data_ur = data_ur;
    }

    public static String getData_utw() {
        return data_utw;
    }

    public static void setData_utw(String data_utw) {
        User.data_utw = data_utw;
    }

    public static String getWeryfikacja() {
        return weryfikacja;
    }

    public static void setWeryfikacja(String weryfikacja) {
        User.weryfikacja = weryfikacja;
    }

    public static void setId(Integer id) {
        User.id = id;
    }

    public static Integer getId() {
        return id;
    }
}
