const db = require('./db');
const express = require("express");
const config = require('../config');
const crypto = require("crypto");
const valid = require('../functions/hashMethods');
const emailSender = require('../services/email');
const moment = require('moment');
const mariadb = require('mariadb');
const uuid = require("uuid");


async function registerUser(id, login, email, password, salt, imie, nazwisko, num_tel, data_ur) {

    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const res = await conn.execute('SELECT EXISTS (SELECT * FROM token_user WHERE opis = ?) AS token, ' +
                'EXISTS (SELECT * FROM uzytkownicy WHERE email = ? or login=?) AS user', [email, email, login]);
            if (res[0].token == 1 || res[0].user == 1) {
                console.log('User Exists')
                return JSON.stringify('User Exists')
            } else {
                const data = moment().format("YYYY-MM-DD")

                await conn.execute('INSERT INTO `uzytkownicy` (`unique_id`, `login`, `password`, `salt`, `imie`, `nazwisko`, `email`, `numer_tel`, `data_ur`, `data_utw`, `weryfikacja`) ' +
                    'VALUES (?,?,?,?,?,?,?,?,?,?,"e")', [id, login, password, salt, imie, nazwisko, email, num_tel, data_ur, data]);

                const user_inserted_id = await conn.execute('SELECT MAX(id) as id FROM `uzytkownicy`');
                const token_unique_id = uuid.v4();

                await conn.execute('INSERT INTO `token_user` (`unique_id`,`opis`, `data`, `uzytkownik_id`) ' +
                    'VALUES (?,"register",?,?)', [token_unique_id, data, user_inserted_id[0].id]);
                const url_activate = "http://localhost:3000/email/token_activate/" + token_unique_id + "/" + user_inserted_id[0].id;
                const url_deactivate = "http://localhost:3000/email/token_deactivate/" + token_unique_id + "/" + user_inserted_id[0].id;
                await emailSender.sendMailVerificationAccount(email, url_activate, url_deactivate);

                console.log('User registered')
                await conn.commit();
                return JSON.stringify('User registered')

            }

        } catch (err) {
            await conn.rollback();
            console.log("Nie udalo sie zarejestrowac konta");
            console.error(err);
            return JSON.stringify('Problem with register');
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }


}

async function loginUser(login, password) {
    const result = await db.query('SELECT * FROM uzytkownicy where login=?', [login], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });
    if (result && result.length) {
        const salt = result[0].salt;
        const pass = result[0].password;
        const hashed_password = valid.checkHashPassword(password, salt).passwordHash // checkHashPassword(password,salt).passwordHash

        if (pass === hashed_password) {
            result[0].data_ur = moment(result[0].data_ur).format("DD-MM-YYYY");
            result[0].data_utw = moment(result[0].data_utw).format("DD-MM-YYYY");
            //result[0].data_utw=  moment().diff(moment(result[0].data_utw),'years');
            return result[0]
        } else

            return JSON.stringify('Wrong password')
    } else return null

}

async function userCalories(id) {
    const result = await db.query('select d.waga_obecna as waga,d.wzrost as wzrost,u.data_ur as data ,tz.wartosc as aktywnosc,cw.wartosc as cel from uzytkownicy as u join ' + 'dieta_inf as d on d.uzytkownik_id=u.id ' + 'JOIN cel_waga as cw on d.cel_waga=cw.id ' + 'join tryb_zycia as tz on d.tryb_zycia=tz.id ' + 'where u.id=?', [id], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });

    const rows = await db.query('SELECT kcal as kcal,wielkosc_porcji as waga from jadlospis where uzytkownik_id=? and data=CURDATE()', [id], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });
    let ilosc = 0;
    for (let i = 0; i < rows.length; i++) {
        rows[i].data = moment(rows[i].data).format("DD-MM-YYYY")
        ilosc += (rows[i].kcal * rows[i].waga) / 100


    }
    if (result && result.length) {
        const masa = result[0].waga;
        const wzrost = result[0].wzrost;
        const wiek = moment().diff(moment(result[0].data), 'years');
        const aktywnosc = result[0].aktywnosc;
        const cel = result[0].cel;
        let ppm = 0;
        if (result[0].plec === 'm') ppm = (10 * masa) + (6.25 * wzrost) - (5 * wiek) + 5; else ppm = (10 * masa) + (6.25 * wzrost) - (5 * wiek) - 161;
        let cpm = ppm * aktywnosc;
        cpm = cpm + cpm * cel;
        if (wiek >= 50) cpm = cpm * 0.9;
        return {
            'cpm': cpm.toFixed(2), 'kcal_today': ilosc.toFixed(2), 'roznica': (cpm - ilosc).toFixed(2)
        }
    } else return JSON.stringify("Błąd podczas liczenia kalorii")


}

async function verifyUser(user_id, waga, plec, wzrost, aktywnosc, cel, trener_id) {

    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            await conn.batch('INSERT INTO dieta_inf (waga_obecna, plec, wzrost, uzytkownik_id, tryb_zycia, cel_waga) VALUES (?,?,?,?,?,?);', [waga, plec, wzrost, user_id, aktywnosc, cel, user_id])
            await conn.batch(' UPDATE uzytkownicy SET weryfikacja = "o",trener=? WHERE uzytkownicy.id = ?;', [trener_id, user_id])
            await conn.commit();
            return JSON.stringify('User verified successfully');


        } catch (err) {
            await conn.rollback();
            console.log("Uzytkownik moze miec juz rekord w bazie");
            console.error(err);
            return JSON.stringify('Problem with verifying');
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }

    //return Promise.resolve(1);


}

async function getMeasurements(id) {
    const rows = await db.query('SELECT p.id,p.data_pomiaru as datap,p.waga,d.waga_obecna as waga_start FROM `pomiar` as p join uzytkownicy as u on' + ' p.uzytkownik_id=u.id  right join dieta_inf as d on d.uzytkownik_id=u.id ' + 'WHERE d.uzytkownik_id=? order by id desc;', [id], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });

    if (rows && rows.length) {

        for (let i = 0; i < rows.length; i++) {
            rows[i].datap = moment(rows[i].datap).format("DD-MM-YYYY")

        }
        return rows
    } else return null//JSON.stringify('No measurements');
}

async function addMeasurement(user_id, waga) {
    await db.query('insert INTO pomiar (uzytkownik_id,waga,data_pomiaru) VALUES(?,?,?)', [user_id, waga, moment().format("YYYY-MM-DD")], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });
    console.log('Pomiar dodany');
    return JSON.stringify('Measurement added');
}

async function deleteMeasurement(id) {

    const result = await db.query('DELETE FROM pomiar where id=?', [id])

    if (result.affectedRows) {
        console.log('Pomiar usuniety');
        return JSON.stringify("Pomiar usunięty pomyślnie");
    } else {
        console.log('Pomiar nie zostal usuniety');
        return JSON.stringify("Błąd podczas usuwania");
    }


}

async function addProduct(user_id, nazwa, kcal, w, b, t, waga) {
    const res = await db.query('insert INTO jadlospis (uzytkownik_id,nazwa_produktu,kcal,weglowodany_na_100g,bialko_na_100g,tluszcz_na_100g,wielkosc_porcji,data) VALUES(?,?,?,?,?,?,?,?)', [user_id, nazwa, kcal, w, b, t, waga, moment().format("YYYY-MM-DD")], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });
    if (res.affectedRows) {
        console.log('Produkt dodany');
        return JSON.stringify('Product added');
    } else return JSON.stringify("Error while adding product")
}

async function deleteProduct(id) {

    const result = await db.query('DELETE FROM jadlospis where id=?', [id])


    if (result.affectedRows) {
        console.log('Produkt usuniety');
        return JSON.stringify("Produkt usunięty pomyślnie");
    } else {
        console.log('Produkt nie zostal usuniety');
        return JSON.stringify("Błąd podczas usuwania");
    }


}

async function getProducts(id) {
    const rows = await db.query('SELECT id,uzytkownik_id,nazwa_produktu as nazwa,kcal,weglowodany_na_100g,bialko_na_100g, ' +
        'tluszcz_na_100g,wielkosc_porcji as waga,data  from jadlospis where uzytkownik_id=? and data=CURDATE()', [id], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });
    if (rows && rows.length) {

        for (let i = 0; i < rows.length; i++) {
            const mnoznik = rows[i].waga / 100;
            rows[i].data = moment(rows[i].data).format("DD-MM-YYYY");
            rows[i].kcal_na_100g = (rows[i].kcal).toFixed(2);
            rows[i].kcal = (mnoznik * rows[i].kcal).toFixed(2);
            rows[i].weglowodany = (mnoznik * rows[i].weglowodany_na_100g).toFixed(2);
            rows[i].bialko = (mnoznik * rows[i].bialko_na_100g).toFixed(2);
            rows[i].tluszcz = (mnoznik * rows[i].tluszcz_na_100g).toFixed(2);


        }
        return rows
    } else return null//JSON.stringify('No measurements');
}

async function updateWeight(user_id, waga) {
    const res = await db.query('UPDATE dieta_inf SET waga_obecna=? where uzytkownik_id=?', [waga, user_id], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });
    if (res.affectedRows) {
        console.log('Waga zmieniona');
        return JSON.stringify('Weight updated');

    } else return JSON.stringify('Error');
}

async function getTrainingPlans(id) {
    const rows = await db.query('SELECT id,tytul,opis FROM plan_treningowy WHERE uzytkownik_id=?;', [id], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });

    if (rows && rows.length) {

        return rows
    } else return null
}

async function getExercisesFromTrainingPlan(id_planu) {
    const rows = await db.query('SELECT c.id,c.nazwa,c.pozycja_wyjsciowa,c.ruch,c.wskazowki,c.video_url,k.nazwa as kategoria \n' + 'FROM cwiczenia_plan_treningowy as cpt \n' + 'join cwiczenia as c on cpt.cwiczenia=c.id \n' + 'join kategoria as k on c.kategoria_id=k.id\n' + 'WHERE cpt.plan_treningowy=? order by pos;', [id_planu], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });
    if (rows && rows.length) {

        return rows
    } else return null//JSON.stringify('No measurements');
}

async function addTraining(id_planu, nazwa, opis, uzytkownik_id) {
    await db.query('insert INTO trening (id_planu,nazwa,opis,data,uzytkownik_id) VALUES(?,?,?,?,?)', [id_planu, nazwa, opis, moment().format("YYYY-MM-DD"), uzytkownik_id], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });
    console.log('Trening dodany');
    return JSON.stringify('Training added');
}

async function deleteTraining(id) {

    const result = await db.query('DELETE FROM trening where id=?', [id])

    if (result.affectedRows) {
        console.log('Trening usuniety');
        return JSON.stringify("Training deleted successfully");
    } else {
        console.log('Trening nie zostal usuniety');
        return JSON.stringify("Training deletion failed");
    }


}

async function getTrainings(user_id) {
    const month = moment().format("MM");

    const rows = await db.query('SELECT id,nazwa,opis,data from trening where uzytkownik_id=? and MONTH(data)=? order by id desc', [user_id, month], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });
    if (rows && rows.length) {

        for (let i = 0; i < rows.length; i++) {
            rows[i].data = moment(rows[i].data).format("DD-MM-YYYY")

        }
        return rows
    }
    return rows;
}

async function getTrainingCount(user_id) {
    const month = moment().format("MM");
    const year = moment().format("YYYY");

    const rows = await db.query('SELECT (SELECT COUNT(id) from trening where uzytkownik_id=? and WEEK(data)=WEEK(CURDATE())) as per_week,' +
        ' (SELECT COUNT(id) from trening where uzytkownik_id=? and MONTH(data)=?) as per_month,' +
        '(SELECT COUNT(id) from trening where uzytkownik_id=? and YEAR(data)=?) as per_year', [user_id, user_id, month, user_id, year], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });


    return {
        'per_week': rows[0].per_week, 'per_month': rows[0].per_month, 'per_year': rows[0].per_year
    }
}

async function getPlanAndTrainingCount(user_id) {


    const rows = await db.query('select pt.tytul as nazwa, COUNT(t.id) as liczba ' +
        'from uzytkownicy as u JOIN plan_treningowy as pt on pt.uzytkownik_id=u.id ' +
        'join trening as t on pt.id=t.id_planu where u.id=? group by pt.id order by pt.id;', [user_id], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });

    if (rows && rows.length) {

        return rows
    } else return [];
}

async function getUserData(user_id) {
    const result = await db.query('SELECT d.waga_obecna as waga,d.plec as plec,d.wzrost,d.tryb_zycia as tryb, tz.opis as aktywnosc,' +
        'd.cel_waga,cw.nazwa as cel,u.trener FROM uzytkownicy as u join dieta_inf as d on d.uzytkownik_id=u.id join tryb_zycia as tz on d.tryb_zycia=tz.id ' +
        'join cel_waga as cw on d.cel_waga= cw.id where u.id=?'
        , [user_id], function (err, result, fields) {
            result.on('error', function (err) {
                console.log('[MySQL ERROR]', err);
            });
        });
    const resultCalories = await userCalories(user_id);
    if (result && result.length) {
        result[0].tryb = result[0].tryb - 1;
        result[0].cel_waga = result[0].cel_waga - 1;
        result[0].trener = result[0].trener - 1;
        if (result[0].plec === "m") result[0].plec = 0; else result[0].plec = 1;
    }
    return {
        'waga': result[0].waga,
        'plec': result[0].plec,
        'wzrost': result[0].wzrost,
        'tryb': result[0].tryb,
        'aktywnosc': result[0].aktywnosc,
        'zapotrzebowanie': resultCalories.cpm,
        'cel_waga': result[0].cel_waga,
        'cel': result[0].cel,
        'trener': result[0].trener
    };
}

async function updateUserDataWithoutTrainer(waga, plec, wzrost, tryb, cel_waga, user_id) {
    const result = await db.query('UPDATE dieta_inf set waga_obecna=?,plec=?,wzrost=?,tryb_zycia=?,cel_waga=? where uzytkownik_id=?', [waga, plec, wzrost, tryb, cel_waga, user_id], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });
    if (result.affectedRows) {
        console.log('Dane zaaktualizowane');
        return JSON.stringify('Data updated');
    } else {
        console.log('Nie udało się zaktualizowac danych');
        return JSON.stringify('Error updating data');
    }

}

async function updateUserDataWithTrainer(waga, plec, wzrost, tryb, cel_waga, trener, user_id) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            await conn.batch('UPDATE dieta_inf set waga_obecna=?,plec=?,wzrost=?,tryb_zycia=?,cel_waga=? where uzytkownik_id=?;', [waga, plec, wzrost, tryb, cel_waga, user_id])
            await conn.batch('UPDATE uzytkownicy set trener=?,weryfikacja="o" where id=?;', [trener, user_id])
            await conn.commit();
            return JSON.stringify('Data updated successfully');


        } catch (err) {
            await conn.rollback();
            console.log("Nie udalo sie zaaktualizowac danych");
            console.error(err);
            return JSON.stringify('Problem with updating');
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }
}

async function getUserStatistics(id) {
    const data = moment().format("YYYY-MM-DD");
    const rok = moment().format("YYYY");
    console.log(data);
    const kcal = userCalories(id);
    const statictics = await db.query(`SELECT (SELECT COUNT(id) from trening where YEAR(data)=? and uzytkownik_id=?) as rok,
    (SELECT COUNT(id) from trening where DATE(data)=? and uzytkownik_id=?) as today,(SELECT data from trening WHERE uzytkownik_id=?  and DATE(data)<= ? order by data desc limit 1) as ostatni_trening, (SELECT COUNT(id) from plan_treningowy where
     uzytkownik_id=?) as liczba_planow`, [rok, id, data, id, id, data, id]);
    if (statictics[0].ostatni_trening != null)
        statictics[0].ostatni_trening = moment(statictics[0].ostatni_trening).format("DD-MM-YYYY");
    else statictics[0].ostatni_trening = "Brak treningów";
    return {
        'kcal': (await kcal).roznica,
        'today': statictics[0].today,
        'data': statictics[0].ostatni_trening,
        'rok': statictics[0].rok,
        'liczba_planow': statictics[0].liczba_planow
    }
}

async function getUserPersonalData(id) {
    const result = await db.query('SELECT email,numer_tel from uzytkownicy where id=?', [id], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });
    if (result && result.length) {
        return {
            'email': result[0].email,
            'numer_tel': result[0].numer_tel
        }
    } else return null;
}

async function changeEmail(id, email) {

    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const res = await conn.execute('SELECT EXISTS (SELECT * FROM token_user WHERE opis = ?) AS token, ' +
                'EXISTS (SELECT * FROM uzytkownicy WHERE email = ?) AS user', [email, email]);
            if (res[0].token == 1 || res[0].user == 1) {
                console.log("Email zajety");
                return "Email is already in use";

            } else {
                const old_email = await conn.execute('SELECT email from uzytkownicy where id=?', [id]);
                const token_unique_id = uuid.v4();
                const data = moment().format("YYYY-MM-DD")
                await conn.execute('INSERT INTO `token_user` (`unique_id`,`opis`, `data`, `uzytkownik_id`) ' +
                    'VALUES (?,?,?,?)', [token_unique_id, old_email[0].email, data, id]);
                await conn.execute('UPDATE uzytkownicy set email=?,weryfikacja="ez" where id=?', [email, id]);
                const url_activate = "http://localhost:3000/email/token_activate_change_email/" + token_unique_id + "/" + id;
                const url_deactivate = "http://localhost:3000/email/token_deactivate_change_email/" + token_unique_id + "/" + id;
                await emailSender.sendMailChangeEmail(email, url_activate, url_deactivate);
                //send email
                await conn.commit();
                console.log('Email zaktualizowany');
                return 'Email updated';

            }


        } catch (err) {
            await conn.rollback();
            console.log('Nie udało się zaktualizowac emaila');
            return JSON.stringify('Error updating email');
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }


}

async function changePhoneNumber(id, numer_tel) {


    const result = await db.query('UPDATE uzytkownicy set numer_tel=? where id=?', [numer_tel, id], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });
    if (result.affectedRows) {
        console.log('Numer telefonu zaktualizowany');
        return JSON.stringify('Phone number updated');
    } else {
        console.log('Nie udało się zaktualizowac numeru telefonu');
        return JSON.stringify('Error updating phone number');
    }
}

async function changePassword(id, old_haslo, new_haslo) {
    const result = await db.query('SELECT * FROM uzytkownicy where id=?', [id], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });

    if (result && result.length) {
        const salt = result[0].salt;
        const pass = result[0].password;
        const hashed_password = valid.checkHashPassword(old_haslo, salt).passwordHash // checkHashPassword(password,salt).passwordHash


        if (pass === hashed_password) {
            const new_pass_hash = valid.sha512(new_haslo, salt).passwordHash;
            const res = await db.query('UPDATE uzytkownicy set password=? where id=?', [new_pass_hash, id], function (err, result, fields) {
                result.on('error', function (err) {
                    console.log('[MySQL ERROR]', err);
                });
            });
            if (res.affectedRows) {
                console.log('Hasło zaktualizowane');
                return JSON.stringify('Password updated');
            } else {
                console.log('Nie udało się zaktualizowac hasła');
                return JSON.stringify('Error updating password');
            }
        } else return "Wrong password";
    } else return "User not found";

}

async function resetPassword(email) {

    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const user_id = await conn.execute('SELECT id FROM `uzytkownicy` where email=?', [email]);
            await emailSender.deleteTokenResetPassword(user_id[0].id);
            const token_unique_id = uuid.v4();
            const data = moment().format("YYYY-MM-DD")
            await conn.execute('INSERT INTO `token_user` (`unique_id`,`opis`, `data`, `uzytkownik_id`) ' +
                'VALUES (?,?,?,?)', [token_unique_id, "reset_password_token", data, user_id[0].id]);
            const url_activate = "http://localhost:3000/email/token_activate_reset_password/" + token_unique_id + "/" + user_id[0].id;
            const url_deactivate = "http://localhost:3000/email/token_deactivate_reset_password/" + token_unique_id + "/" + user_id[0].id;
            await emailSender.sendMailResetPassword(email, url_activate, url_deactivate);

            await conn.commit();
            console.log('Prośba o zmiane hasła wysłana');
            return 'Prośba o zmiane hasła wysłana';


        } catch (err) {
            await conn.rollback();
            console.log('Nie udało się wyśłać prośby o zmianę hasła');
            return JSON.stringify('Error updating email');
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }


}

async function getWeekCalories(id) {

    let data = moment().add(-6, 'days').format("YYYY-MM-DD");

    let days = [];
    days[6] = moment().add(-6, 'days').locale("pl").format("dddd");
    let query = ' (SELECT ifnull( (SELECT SUM(kcal*wielkosc_porcji/100) from jadlospis WHERE uzytkownik_id=' + id + ' and data ="' + data + '" GROUP BY DAY(data)),0) as kcal) ';
    for (let i = 5; i >= 0; i--) {
        days[i] = moment().add(-i, 'days').locale("pl").format("dddd");
        let data = moment().add(-i, 'days').format("YYYY-MM-DD");
        query += data = 'UNION ALL (SELECT ifnull( (SELECT SUM(kcal*wielkosc_porcji/100) from jadlospis WHERE uzytkownik_id=' + id + ' and data ="' + data + '" GROUP BY DAY(data)),0)) ';

    }

    const result = await db.query(query);

    if (result && result.length) {
        for (let i = 0, j = result.length - 1; i < result.length; i++, j--) {
            result[i].day = days[j];
            result[i].kcal = result[i].kcal.toFixed(2);
        }
        return result;
    } else return null;

}

async function checkEmail(email) {


    const rows = await db.query(' SELECT EXISTS(SELECT * FROM uzytkownicy WHERE email =  ?) as number;', [email], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });
    });

    if (rows[0].number == 1) {

        return 1;
    } else return 0;

}

async function resetLogin(email) {

    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const user = await conn.execute('SELECT login FROM `uzytkownicy` where email=?', [email]);
            await emailSender.sendMailForgetLogin(email, user[0].login);

            await conn.commit();
            console.log('Wysłano wiadomość email z loginem');
            return 'Login został wysłany na adres email';


        } catch (err) {
            await conn.rollback();
            console.log('Nie udało się wyśłać loginu');
            return 'Nie udało się wyśłać loginu';
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }


}

module.exports = {
    registerUser,
    loginUser,
    userCalories,
    verifyUser,
    getMeasurements,
    addMeasurement,
    deleteMeasurement,
    addProduct,
    deleteProduct,
    getProducts,
    updateWeight,
    getTrainingPlans,
    getExercisesFromTrainingPlan,
    addTraining,
    deleteTraining,
    getTrainings,
    getTrainingCount,
    getUserData,
    updateUserDataWithoutTrainer,
    updateUserDataWithTrainer,
    getUserStatistics,
    getUserPersonalData,
    changeEmail,
    changePhoneNumber,
    changePassword,
    getPlanAndTrainingCount,
    getWeekCalories,
    resetPassword,
    checkEmail,
    resetLogin

}
