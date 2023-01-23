const db = require('./db');
const nodemailer = require("nodemailer");
const moment = require("moment");
const mariadb = require("mariadb");
const uuid = require("uuid");
const emailSender = require("./email");

BigInt.prototype.toJSON = function () {
    return this.toString()
}


async function getAllUsers() {
    const result = await db.query('SELECT u.id,u.imie,u.nazwisko, u.email,u.data_ur as dataUrodzenia,u.data_utw as dataUtworzenia,u.weryfikacja,CONCAT(t.imie," ",t.nazwisko) as trener\n' +
        'FROM uzytkownicy as u left join personel as t on t.id=u.trener');
    if (result && result.length) {
        for (let i = 0; i < result.length; i++) {
            result[i].dataUrodzenia = moment(result[i].dataUrodzenia).format("YYYY-MM-DD");
            result[i].dataUtworzenia = moment(result[i].dataUtworzenia).format("YYYY-MM-DD");
            if (result[i].trener == null) result[i].trener = "Brak";

            switch (result[i].weryfikacja) {
                case 'a':
                    result[i].weryfikacja = 'Aktywny';
                    break;
                case 'o':
                    result[i].weryfikacja = 'Oczekujący-aktywacja';
                    break;
                case 'e':
                    result[i].weryfikacja = 'Oczekujący-weryfikacja';
                    break;
                case 'ez':
                    result[i].weryfikacja = 'Zmienia-email';
                    break;
                case 'n':
                    result[i].weryfikacja = 'Nieaktywny';
                    break;
                case 'z':
                    result[i].weryfikacja = 'Zablokowany';
                    break;
                default:
                    result[i].weryfikacja = 'Brak';
                    break;
            }
        }
        return result;
    }
    return null;
}

async function getAllCoaches() {
    const result = await db.query('SELECT id,imie,nazwisko,email,weryfikacja,rola from personel WHERE rola!="admin" ');
    if (result && result.length) {
        for (let i = 0; i < result.length; i++) {

            switch (result[i].weryfikacja) {
                case 'a':
                    result[i].weryfikacja = 'Aktywny';
                    break;
                case 'n':
                    result[i].weryfikacja = 'Nieaktywny';
                    break;
                case 'e':
                    result[i].weryfikacja = 'Oczekujący-weryfikacja';
                    break;
                case 'ez':
                    result[i].weryfikacja = 'Oczekujący-zmiana email';
                    break;
                default:
                    result[i].weryfikacja = 'Brak';
                    break;
            }
        }
        return result;
    }
    return null;
}

async function updateUser(id, imie, nazwisko, dataUrodzenia) {
    if (!moment(dataUrodzenia, "YYYY-MM-DD", true).isValid()) return "Invalid date";
    else {
        console.log(dataUrodzenia);
        console.log(!moment("2007-07-25A", "YYYY-MM-DD").isValid());
        const result = await db.query('UPDATE uzytkownicy set imie=?, nazwisko=?, data_ur=? where id=?',
            [imie, nazwisko, dataUrodzenia, id], function (err, result, fields) {
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

}

async function deleteUser(id) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const plan = await conn.execute('SELECT id FROM plan_treningowy where uzytkownik_id=?', [id]);
            if (plan && plan.length) {

                for (let i = 0; i < plan.length; i++) {
                    await conn.execute('DELETE FROM cwiczenia_plan_treningowy where plan_treningowy=?', [plan[i].id]);

                }
            }
            await conn.execute('DELETE FROM plan_treningowy where uzytkownik_id=?', [id]);
            await conn.execute('DELETE FROM dieta_inf where uzytkownik_id=?', [id]);
            await conn.execute('DELETE FROM jadlospis where uzytkownik_id=?', [id]);
            await conn.execute('DELETE FROM token_user where uzytkownik_id=?', [id]);
            await conn.execute('DELETE FROM trening where uzytkownik_id=?', [id]);
            await conn.execute('DELETE FROM pomiar where uzytkownik_id=?', [id]);
            await conn.execute('DELETE FROM uzytkownicy where id=?', [id]);

            await conn.commit();
            return JSON.stringify('User deleted successfully');


        } catch (err) {
            await conn.rollback();
            console.log("Uzytkownik nie zostal usuniety");
            console.error(err);
            return JSON.stringify('Problem with deleting');
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }

}

async function updateCoach(id, imie, nazwisko, rola, weryfikacja) {


    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const coach = await conn.execute('SELECT email FROM personel where id=?', [id]);
            const result = await conn.execute('UPDATE personel set imie=?, nazwisko=?, rola=?, weryfikacja=? where id=?', [imie, nazwisko, rola, weryfikacja, id]);

            if (result.affectedRows && weryfikacja === 'a')
                await emailSender.sendMessageAccountState(coach[0].email, 'at')


            await conn.commit();
            console.log('Dane zaaktualizowane');
            return JSON.stringify('Data updated');


        } catch (err) {
            await conn.rollback();
            console.log('Nie udało się zaktualizowac danych');
            return JSON.stringify('Error updating data');
        }
    } catch (err) {
        console.log('Nie udało się zaktualizowac danych');
        return JSON.stringify('Error updating data');
    }

}

async function deleteCoach(id) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {

            await conn.execute('UPDATE uzytkownicy set trener=NULL where trener=?', [id]);
            await conn.execute('DELETE FROM token_personel where personel_id=?', [id]);
            await conn.execute('DELETE FROM personel where id=?', [id]);

            await conn.commit();
            return JSON.stringify('User deleted successfully');


        } catch (err) {
            await conn.rollback();
            console.log("Uzytkownik nie zostal usuniety");
            console.error(err);
            return JSON.stringify('Problem with deleting');
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }

}

async function getStatisticsData() {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const data = moment().format('YYYY-MM-DD');
            const users = await conn.execute('SELECT COUNT(id) AS all_users , ' +
                'SUM(CASE ' +
                'WHEN weryfikacja="a" THEN 1 ' +
                'ELSE 0 ' +
                'END) AS active_users ' +
                'FROM uzytkownicy');
            const personel = await conn.execute('SELECT SUM(CASE WHEN weryfikacja = "a" or ' +
                'weryfikacja = "ez" THEN 1 ELSE 0 END) AS verified, ' +
                'SUM(CASE WHEN weryfikacja = "n" or weryfikacja = "e" ' +
                'THEN 1 ELSE 0 END) AS unverified FROM personel WHERE rola="trener";');
            const trening = await conn.execute('SELECT COUNT(id) as number FROM `trening` WHERE data=?;', [data]);
            const pomiar = await conn.execute('SELECT COUNT(id) as number FROM `pomiar` WHERE data_pomiaru=?;', [data]);
            await conn.commit();
            console.log('Pomiary udane');
            return {
                /*                users_all: users[0].all_users,
                                users_active: users[0].active_users,
                                personel_verified: personel[0].verified,
                                personel_unverified: personel[0].unverified,
                                trening: trening[0].number,
                                pomiar: pomiar[0].number    */

                users_all: users[0].all_users,
                users_active: users[0].active_users,
                personel_verified: personel[0].verified,
                personel_unverified: personel[0].unverified,
                trening: trening[0].number,
                pomiar: pomiar[0].number


            }


        } catch (err) {
            await conn.rollback();
            console.log('Nie udało się zrobic pomiarow');
            console.log(err);
            return 'Nie udało się zrobic pomiarow';
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }
}

async function blockUser(id) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            await conn.execute('UPDATE uzytkownicy set weryfikacja="z" where id=?', [id]);
            await conn.commit();
            return 'User blocked successfully';

        } catch (err) {
            await conn.rollback();
            console.log("Uzytkownik nie zostal zablokowany");
            console.error(err);
            return 'Problem with blocking';
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }
}

async function unblockUser(id) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const result=await conn.execute('SELECT COUNT(id) as number FROM `dieta_inf` WHERE uzytkownik_id=?', [id]);
            if(result[0].number==1){
                await conn.execute('UPDATE uzytkownicy set weryfikacja="o" where id=?', [id]);
            }
            else  {await conn.execute('UPDATE uzytkownicy set weryfikacja="n" where id=?', [id]);}

            await conn.commit();
            return 'User unblocked successfully';

        } catch (err) {
            await conn.rollback();
            console.log("Uzytkownik nie zostal odblokowany");
            console.error(err);
            return 'Problem with unblocking';
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }
}
module.exports = {
    getAllUsers,
    getAllCoaches,
    updateUser,
    deleteUser,
    updateCoach,
    deleteCoach,
    getStatisticsData,
    blockUser,
    unblockUser

}