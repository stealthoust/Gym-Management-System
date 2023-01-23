const express = require('express');
const router = express.Router();
const users=require('../services/users')
const uuid= require('uuid')
const valid=require('../functions/hashMethods');
const {  validationResult } = require('express-validator');

const schemat=require("../schemas/mobile-app-schemas");


router.post('/register',schemat.registerSchema,async function(req,res,next){
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        console.log(errors.array());
        res.send("Problem with validation on server-side"); //wyświetla wszystkie błędy
    }
    else
    {
        const uid=uuid.v4();
        const plaint_password=req.body.password;
        const hash_data=valid.saltHashPassword(plaint_password);
        const password=hash_data.passwordHash;
        const salt=hash_data.salt;

        const login=req.body.login;
        const imie=req.body.imie;
        const nazwisko=req.body.nazwisko;
        const email=req.body.email;
        const numer=req.body.num_tel;
        let data=req.body.data_ur;

        try{
            res.json(await users.registerUser(uid,login,email,password,salt,imie,nazwisko,numer,data));
        } catch (err)
        {
            console.error("cos tam",err.message);
            next(err);
        }
    }
});

router.post('/login',schemat.loginSchema,async function(req,res,next) {
    const errors = validationResult(req);

    if (!errors.isEmpty()) {

        console.log(errors.array());
        res.send("Problem with validation on server-side"); //wyświetla wszystkie błędy

    }
    else {
    const login=req.body.login;
    const pass=req.body.password;

    try{
        res.json(await users.loginUser(login,pass));
    } catch (err)
    {
        console.error("error",err.message);
        next(err);
    }
    }
});

router.get('/calories/:id', async function(req, res, next) {
    try {
        res.json(await users.userCalories(req.params.id));

    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.post('/verify',schemat.verifyProfileDataSchema,async function(req,res,next) {
    const errors = validationResult(req);

    if (!errors.isEmpty()) {

        console.log(errors.array());
        res.send("Problem with validation on server-side"); //wyświetla wszystkie błędy

    }
    else {
        const id = req.body.id;
        const waga = req.body.waga;
        const plec = req.body.plec;
        const wzrost = req.body.wzrost;
        const aktywnosc = req.body.aktywnosc;
        const cel = req.body.cel;
        const trener_id = req.body.trener;


        try {
            res.json(await users.verifyUser(id, waga, plec, wzrost, aktywnosc, cel, trener_id));
        } catch (err) {
            console.error("error", err.message);
            next(err);
        }
    }
});

router.get('/measurements/:id', async function(req, res, next) {
    try {
        res.json(await users.getMeasurements(req.params.id));
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.post('/measurements/add',async function(req,res,next) {

    const user_id=req.body.id;
    const waga=req.body.waga;

    try{
        res.json(await users.addMeasurement(user_id,waga));
    } catch (err)
    {
        console.error("error",err.message);
        next(err);
    }
});

router.delete('/measurements/delete/:id', async function (req, res) {


    try {

        res.json(await users.deleteMeasurement(req.params.id));

    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);

    }

});

router.post('/product/add',schemat.addProductSchema,async function(req,res,next) {

    const errors = validationResult(req);

    if (!errors.isEmpty()) {

        console.log(errors.array());
        res.send("Problem with validation on server-side"); //wyświetla wszystkie błędy

    }
    else {
        const user_id = req.body.id;
        const waga = req.body.waga;
        const nazwa = req.body.nazwa;
        const kcal = req.body.kcal;
        const w = req.body.w;
        const b = req.body.b;
        const t = req.body.t;

        try {
            res.json(await users.addProduct(user_id, nazwa, kcal, w, b, t, waga));
        } catch (err) {
            console.error("error", err.message);
            next(err);
        }
    }
});

router.delete('/product/delete/:id', async function (req, res) {


    try {

        res.json(await users.deleteProduct(req.params.id));

    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);

    }

});

router.get('/product/:id', async function(req, res, next) {
    try {
        res.json(await users.getProducts(req.params.id));
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.post('/weight/update',schemat.validWeightSchema,async function(req,res,next) {

    const errors = validationResult(req);

    if (!errors.isEmpty()) {

        console.log(errors.array());
        res.send("Problem with validation on server-side"); //wyświetla wszystkie błędy

    }
    else {

        const user_id = req.body.id;
        const waga = req.body.waga;

        try {
            res.json(await users.updateWeight(user_id, waga));
        } catch (err) {
            console.error("error", err.message);
            next(err);
        }
    }
});

router.get('/plans/:id', async function(req, res, next) {
    try {
        res.json(await users.getTrainingPlans(req.params.id));
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.get('/plan/exercises/:id', async function(req, res, next) {
    try {
        res.json(await users.getExercisesFromTrainingPlan(req.params.id));
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.post('/training/add',async function(req,res,next) {

    const nazwa=req.body.nazwa;
    const opis=req.body.opis;
    const user_id=req.body.id;
    const id_planu=req.body.id_planu;

    try{
        res.json(await users.addTraining(id_planu,nazwa,opis,user_id));
    } catch (err)
    {
        console.error("error",err.message);
        next(err);
    }
});

router.delete('/training/delete/:id', async function (req, res) {


    try {

        res.json(await users.deleteTraining(req.params.id));

    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);

    }

});

router.get('/trainings/:id', async function(req, res, next) {
    try {
        res.json(await users.getTrainings(req.params.id));
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.get('/trainings/count/:id', async function(req, res, next) {
    try {
        res.json(await users.getTrainingCount(req.params.id));
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.get('/plan_trainings/count/:id', async function(req, res, next) {
    try {
        res.json(await users.getPlanAndTrainingCount(req.params.id));
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.get('/profile/:id', async function(req, res, next) {
    try {
        res.json(await users.getUserData(req.params.id));
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.post('/data/update',schemat.changeProfileDataSchema,async function(req,res,next) {
    const errors = validationResult(req);

    if (!errors.isEmpty()) {

        console.log(errors.array());
        res.send("Problem with validation on server-side"); //wyświetla wszystkie błędy

    }
    else {

        const waga = req.body.waga;
        const plec = req.body.plec;
        const wzrost = req.body.wzrost;
        const tryb = req.body.tryb;
        const cel_waga = req.body.cel_waga;
        const user_id = req.body.id;

        try {
            res.json(await users.updateUserDataWithoutTrainer(waga, plec, wzrost, tryb, cel_waga, user_id));
        } catch (err) {
            console.error("error", err.message);
            next(err);
        }
    }
});

router.post('/data/updatetrainer',schemat.changeProfileDataSchema,async function(req,res,next) {
    const errors = validationResult(req);

    if (!errors.isEmpty()) {

        console.log(errors.array());
        res.send("Problem with validation on server-side"); //wyświetla wszystkie błędy

    }
    else {
        const waga = req.body.waga;
        const plec = req.body.plec;
        const wzrost = req.body.wzrost;
        const tryb = req.body.tryb;
        const cel_waga = req.body.cel_waga;
        const user_id = req.body.id;
        const trainer_id = req.body.trainer_id;

        try {
            res.json(await users.updateUserDataWithTrainer(waga, plec, wzrost, tryb, cel_waga, trainer_id, user_id));
        } catch (err) {
            console.error("error", err.message);
            next(err);
        }
    }
});

router.get('/statistics/:id', async function(req, res, next) {
    try {
        res.json(await users.getUserStatistics(req.params.id));
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.get('/calories/week/:id', async function(req, res, next) {
    try {
        res.json(await users.getWeekCalories(req.params.id));
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.get('/settings/:id', async function(req, res, next) {
    try {
        res.json(await users.getUserPersonalData(req.params.id));
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.post('/settings/updateemail',schemat.validEmailSchema,async function(req,res,next) {
    const errors = validationResult(req);

    if (!errors.isEmpty()) {

        console.log(errors.array());
        res.send("Problem with validation on server-side"); //wyświetla wszystkie błędy

    }
    else {
        const id = req.body.id;
        const email = req.body.email;

        try {
            res.json(await users.changeEmail(id, email));
        } catch (err) {
            console.error("error", err.message);
            next(err);
        }
    }
});

router.post('/settings/updatephone',schemat.validPhoneNumberSchema,async function(req,res,next) {


    const errors = validationResult(req);

    if (!errors.isEmpty()) {

        console.log(errors.array());
        res.send("Problem with validation on server-side"); //wyświetla wszystkie błędy

    }
    else {
        const id = req.body.id;
        const phoneNumber = req.body.phone_number;

        try {
            res.json(await users.changePhoneNumber(id, phoneNumber));
        } catch (err) {
            console.error("error", err.message);
            next(err);
        }
    }
});

router.post('/settings/updatePassword',schemat.changePasswordSchema,async function(req,res,next) {

    const errors = validationResult(req);

    if (!errors.isEmpty()) {

        console.log(errors.array());    //wyświetla wszystkie błędy
        res.send("Problem with validation on server-side");

    }
    else {
        const id = req.body.id;
        const old_haslo = req.body.old_password;
        const new_haslo = req.body.new_password;

        try {
            res.json(await users.changePassword(id, old_haslo, new_haslo));
        } catch (err) {
            console.error("error", err.message);
            next(err);
        }
    }
});

router.post('/email/resetpassword',schemat.validEmailSchema,async function(req,res,next) {
    const errors = validationResult(req);

    if (!errors.isEmpty()) {

        console.log(errors.array());
        res.send("Problem with validation on server-side"); //wyświetla wszystkie błędy

    }
    else {
        const email = req.body.email;


        try {
            res.json(await users.resetPassword(email));
        } catch (err) {
            console.error("error", err.message);
            next(err);
        }
    }
});
router.post('/email/forgetlogin',schemat.validEmailSchema,async function(req,res,next) {


    const errors = validationResult(req);

    if (!errors.isEmpty()) {

        console.log(errors.array());
        res.send("Problem with validation on server-side"); //wyświetla wszystkie błędy

    }
    else {
        const email = req.body.email;


        try {
            res.json(await users.resetLogin(email));
        } catch (err) {
            console.error("error", err.message);
            next(err);
        }
    }
});
router.get('/email_exsists/:email',schemat.validEmailSchema, async function(req, res, next) {
    const errors = validationResult(req);

    if (!errors.isEmpty()) {

        console.log(errors.array());
        res.send("Problem with validation on server-side"); //wyświetla wszystkie błędy

    }
    else {
        try {
            res.json(await users.checkEmail(req.params.email));
        } catch (err) {
            console.error(`Coś poszło nie tak `, err.message);
            next(err);
        }
    }
});

module.exports=router








