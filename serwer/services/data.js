const db = require('./db');
const express = require("express");
const config = require('../config');
const moment = require("moment");
const mariadb = require("mariadb");


async function getAllLifestyles() {
    const rows = await db.query(
        'SELECT * FROM `tryb_zycia`'
    );

    return rows;

}

async function getAllWeightGoals() {
    const rows = await db.query(
        'SELECT * FROM `cel_waga`'
    );

    return rows;

}

async function getAllCategories() {
    const rows = await db.query(
        'SELECT * FROM `kategoria`'
    );

    return rows;

}

async function getAllExercisesFromCategory(kategoria) {
    const rows = await db.query(
        'SELECT * from `cwiczenia` where kategoria_id=? order by id asc;', [kategoria], function (err, result, fields) {
            result.on('error', function (err) {
                console.log('[MySQL ERROR]', err);
            });
        }
    );

    return rows;

}

async function addNewExercise(nazwa, pozycja, ruch, wskazowki, url, kategoria) {
    const rows = await db.query(
        'INSERT INTO `cwiczenia` (`nazwa`,`pozycja_wyjsciowa`,`ruch`,`wskazowki`,`video_url`,`kategoria_id`) VALUES (?,?,?,?,?,?);', [nazwa, pozycja, ruch, wskazowki, url, kategoria], function (err, result, fields) {
            result.on('error', function (err) {
                console.log('[MySQL ERROR]', err);
            });
        }
    );
    if (rows.affectedRows) return "Pomyślnie dodano ćwiczenie";
    else return "Nie udało się dodać ćwiczenia";

}

async function updateExercise(nazwa, pozycja, ruch, wskazowki, url, id) {
    const rows = await db.query(
        'UPDATE `cwiczenia` SET `nazwa`=?,`pozycja_wyjsciowa`=?,`ruch`=?,`wskazowki`=?,`video_url`=? WHERE `id`=?;', [nazwa, pozycja, ruch, wskazowki, url, id], function (err, result, fields) {
            result.on('error', function (err) {
                console.log('[MySQL ERROR]', err);
            });
        }
    );
    if (rows.affectedRows) return "Pomyślnie zaktualizowano ćwiczenie";
    else return "Nie udało się zaktualizować ćwiczenia";

}

//trzeba zrobic transakcje!
async function deleteExercise(id) {
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
            await conn.execute('DELETE FROM cwiczenia_plan_treningowy where cwiczenia=? ', [id]);
            await conn.execute('DELETE FROM cwiczenia where id=? ', [id]);

            await conn.commit();
            console.log('Exercise deleted successfully');
            //return JSON.stringify('Plan added successfully')
            return "Exercise deleted successfully";
        } catch (err) {
            await conn.rollback();
            console.log("Problem z usuwaniem cwiczenia");
            console.error(err);
            //return JSON.stringify('Problem with adding plan');
            return "Problem with deleting exercise";
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        //return JSON.stringify('Problem with adding plan');
        return "Problem with deleting exercise";
    }
}

module.exports = {

    getAllLifestyles,
    getAllWeightGoals,
    getAllCategories,
    getAllExercisesFromCategory,
    addNewExercise,
    updateExercise,
    deleteExercise

}


