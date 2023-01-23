const db = require('./db');
const express = require("express");
const config = require('../config');
const crypto = require("crypto");
const valid = require('../functions/hashMethods');
const mariadb = require("mariadb");
const moment = require("moment");
const uuid = require("uuid");
const emailSender = require("./email");


async function registerCoach(login, email, password, salt, imie, nazwisko) {
    let conn;
    try {
        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const res = await conn.execute('SELECT EXISTS (SELECT * FROM token_personel WHERE opis = ?) AS token, ' +
                'EXISTS (SELECT * FROM personel WHERE email = ? or login=?) AS trener', [email, email, login]);
            if (res[0].token == 1 || res[0].trener == 1) {
                console.log('Email Exists')
                return JSON.stringify('Coach Exists')
            } else {
                const data = moment().format("YYYY-MM-DD")
                await conn.execute('INSERT INTO `personel` (`login`, `password`, `salt`, `imie`, `nazwisko`,  `email`, `rola`, `weryfikacja`) ' +
                    'VALUES (?,?,?,?,?,?,"trener","e")', [login, password, salt, imie, nazwisko, email]);
                const coach_inserted_id = await conn.execute('SELECT MAX(id) as id FROM `personel`');
                const token_unique_id = uuid.v4();
                await conn.execute('INSERT INTO `token_personel` (`unique_id`,`opis`, `data`, `personel_id`) ' +
                    'VALUES (?,"register",?,?)', [token_unique_id, data, coach_inserted_id[0].id]);
                const url_activate = "http://localhost:3000/email/token_activate_coach/" + token_unique_id + "/" + coach_inserted_id[0].id;
                const url_deactivate = "http://localhost:3000/email/token_deactivate_coach/" + token_unique_id + "/" + coach_inserted_id[0].id;
                await emailSender.sendMailVerificationAccount(email, url_activate, url_deactivate);
                console.log('Coach registered')
                await conn.commit();
                return JSON.stringify('Coach registered')
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

async function loginCoach(login, password) {
    const result = await db.query(
        'SELECT * FROM personel where login=?', [login],
        function (err, result, fields) {
            result.on('error', function (err) {
                console.log('[MySQL ERROR]', err);
            });
        });
    if (result && result.length) {
        const salt = result[0].salt;
        const pass = result[0].password;
        const hashed_password = valid.checkHashPassword(password, salt).passwordHash
        if (pass === hashed_password)
            return result[0]
        else
            return "Wrong password"
    } else return "Coach doesnt exists"
}

async function getAllCoaches() {
    const rows = await db.query(
        'SELECT id,imie,nazwisko FROM `personel` where weryfikacja="a" and rola="trener";'
    );

    return rows;
}

async function getCountAllUnverifiedUsers(personel_id) {
    const rows = await db.query(
        'SELECT count(`id`) as liczba from uzytkownicy where trener=? and weryfikacja="o";', [personel_id]
    );

    return rows;
}

async function getAllUnverifiedUsers(personel_id) {
    const rows = await db.query(
        'SELECT id,imie,nazwisko from uzytkownicy where trener=? and weryfikacja="o";', [personel_id]
    );

    return rows;
}

async function getAllVerifiedUsers(personel_id) {
    const rows = await db.query(
        'SELECT id,imie,nazwisko from uzytkownicy where trener=? and weryfikacja="a";', [personel_id]
    );

    return rows;
}

async function addPlan(nazwa, opis, user_id, lista) {
    let conn;
    try {
        conn = await mariadb.createConnection({
            host: "localhost",
            user: "root",
            password: "",
            database: "silownia"
        });
        await conn.execute('SET TRANSACTION ISOLATION LEVEL READ COMMITTED')
        await conn.beginTransaction();
        try {
            await conn.execute('INSERT INTO plan_treningowy (tytul,opis,uzytkownik_id) VALUES (?,?,?); ',
                [nazwa, opis, user_id]);

            const plan = await conn.execute('SELECT MAX(id) as idx from plan_treningowy');
            console.log(plan[0].idx);
            let queryExercises = "INSERT INTO cwiczenia_plan_treningowy (cwiczenia, plan_treningowy,pos) " +
                "VALUES ";
            for (let i = 0; i < lista.length; i++) {
                queryExercises += "(" + lista[i] + "," + plan[0].idx + "," + (i + 1) + ")";
                if (i === lista.length - 1)
                    queryExercises += ";";
                else
                    queryExercises += ",";
            }
            await conn.execute(queryExercises);
            await conn.commit();
            console.log('Plan added successfully');
            return "Plan added successfully";
        } catch (err) {
            await conn.rollback();
            console.log("Problem z dodawaniem planu lub cwiczen");
            console.error(err);
            return "Problem with adding plan";
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return "Problem with adding plan";
    }
}

async function updatePlan(nazwa, opis, plan_id, lista) {
    //delete from cwiczenia_plan_treningowy where plan_treningowy=68

    let conn;
    try {
        conn = await mariadb.createConnection({
            host: "localhost",
            user: "root",
            password: "",
            database: "silownia"
        });
        await conn.execute('SET TRANSACTION ISOLATION LEVEL READ COMMITTED')
        await conn.beginTransaction();
        try {
            await conn.execute('delete from cwiczenia_plan_treningowy where plan_treningowy=?; ', [plan_id]);
            await conn.execute('UPDATE plan_treningowy SET tytul = ?, opis = ? WHERE id = ?; ', [nazwa, opis, plan_id]);

            let queryExercises = "INSERT INTO cwiczenia_plan_treningowy (cwiczenia, plan_treningowy, pos) " +
                "VALUES ";
            for (let i = 0; i < lista.length; i++) {
                queryExercises += "(" + lista[i] + "," + plan_id + "," + (i + 1) + ")";
                if (i === lista.length - 1)
                    queryExercises += ";";
                else
                    queryExercises += ",";
            }
            await conn.execute(queryExercises);
            await conn.commit();
            console.log('Plan updated successfully');
            //return JSON.stringify('Plan added successfully')
            return "Plan updated successfully";
        } catch (err) {
            await conn.rollback();
            console.log("Problem z aktualizacja planu lub cwiczen");
            console.error(err);
            //return JSON.stringify('Problem with adding plan');
            return "Problem with updating plan";
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        //return JSON.stringify('Problem with adding plan');
        return "Problem with internet connection";
    }

}

async function deletePlan(id) {
    const result = await db.query('DELETE FROM plan_treningowy where id=?', [id])

    if (result.affectedRows) {
        console.log('Plan treningowy usuniety');
        return JSON.stringify("Training plan deleted successfully");
    } else {
        console.log('Plan treningowy nie zostal usuniety');
        return JSON.stringify("Training plan deletion failed");
    }
}

async function verifyUser(id) {


    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {


            const user = await conn.execute('SELECT email from uzytkownicy where id=?', [id]);
            await db.query('UPDATE uzytkownicy SET weryfikacja="a" WHERE id=?;', [id]);

            await emailSender.sendMessageAccountState(user[0].email, "a");
            return "User verified successfully";


        } catch (err) {
            await conn.rollback();
            console.log("Nie udalo sie aktywowac konta");
            console.error(err);
            return "Problem with verifying user";
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return "Problem with verifying user";
    }
}

async function dismissUser(id) {
    let conn;
    try {
        conn = await mariadb.createConnection({
            host: "localhost",
            user: "root",
            password: "",
            database: "silownia"
        });
        await conn.beginTransaction();
        try {
            await conn.batch('UPDATE uzytkownicy SET weryfikacja ="n" WHERE id=? ', [id]);
            await conn.batch('DELETE FROM dieta_inf where uzytkownik_id=?; ', [id]);

            await conn.commit();
            console.log('User dismissed successfully');
            //return JSON.stringify('Plan added successfully')
            return "User dismissed successfully";
        } catch (err) {
            await conn.rollback();
            console.log("Problem z usunieciem uzytkownika");
            console.error(err);
            //return JSON.stringify('Problem with adding plan');
            return "Problem with dismissing user";
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        //return JSON.stringify('Problem with adding plan');
        return "Problem with internet connection";
    }
}

async function changeEmail(id, email) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const res = await conn.execute('SELECT EXISTS (SELECT * FROM token_personel WHERE opis = ?) AS token, ' +
                'EXISTS (SELECT * FROM personel WHERE email = ?) AS coach', [email, email]);
            if (res[0].token == 1 || res[0].coach == 1) {
                console.log("Email zajety");
                return "Email is already in use";

            } else {
                const old_email = await conn.execute('SELECT email from personel where id=?', [id]);
                const token_unique_id = uuid.v4();
                const data = moment().format("YYYY-MM-DD")
                await conn.execute('INSERT INTO `token_personel` (`unique_id`,`opis`, `data`, `personel_id`) ' +
                    'VALUES (?,?,?,?)', [token_unique_id, old_email[0].email, data, id]);
                await conn.execute('UPDATE personel set email=?,weryfikacja="ez" where id=?', [email, id]);
                const url_activate = "http://localhost:3000/email/token_activate_change_email_coach/" + token_unique_id + "/" + id;
                const url_deactivate = "http://localhost:3000/email/token_deactivate_change_email_coach/" + token_unique_id + "/" + id;
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

async function changePassword(id, old_haslo, new_haslo) {
    const result = await db.query('SELECT * FROM personel where id=?', [id], function (err, result, fields) {
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
            const res = await db.query('UPDATE personel set password=? where id=?', [new_pass_hash, id], function (err, result, fields) {
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
    } else return "Trainer not found";

}

async function getMeasurementsUser(user_id) {
    const rows = await db.query(
        'SELECT id,waga,data_pomiaru as data FROM `pomiar` WHERE uzytkownik_id=? order by data;', [user_id]
    );

    if (rows && rows.length) {
        for (let i = 0; i < rows.length; i++) {
            rows[i].data = moment(rows[i].data).format("DD-MM-YYYY")

        }
        return rows;
    } else return [];
}

async function updatePersonalData(id, imie, nazwisko) {
    const result = await db.query('UPDATE personel set imie=?,nazwisko=? where id=?', [imie, nazwisko, id], function (err, result, fields) {
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

async function resetPassword(email) {

    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const personel = await conn.execute('SELECT id,login FROM `personel` where email=?', [email]);
            await emailSender.deleteTokenResetPasswordCoach(personel[0].id);
            const token_unique_id = uuid.v4();
            const data = moment().format("YYYY-MM-DD")
            await conn.execute('INSERT INTO `token_personel` (`unique_id`,`opis`, `data`, `personel_id`) ' +
                'VALUES (?,?,?,?)', [token_unique_id, "reset_password_token", data, personel[0].id]);
            const url_activate = "http://localhost:3000/email/token_activate_reset_password_coach/" + token_unique_id + "/" + personel[0].id;
            const url_deactivate = "http://localhost:3000/email/token_deactivate_reset_password_coach/" + token_unique_id + "/" + personel[0].id;
            await emailSender.sendMailResetPasswordCoach(email, personel[0].login, url_activate, url_deactivate);

            await conn.commit();
            console.log('Prośba o zmiane hasła wysłana');
            return 'Prośba o zmiane hasła wysłana';


        } catch (err) {
            await conn.rollback();
            console.log('Nie udało się wyśłać prośby o zmianę hasła');
            return JSON.stringify('Email not found');
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }


}

module.exports = {
    registerCoach,
    loginCoach,
    getAllCoaches,
    getCountAllUnverifiedUsers,
    getAllUnverifiedUsers,
    getAllVerifiedUsers,
    addPlan,
    verifyUser,
    dismissUser,
    updatePlan,
    deletePlan,
    changeEmail,
    changePassword,
    getMeasurementsUser,
    updatePersonalData,
    resetPassword
}