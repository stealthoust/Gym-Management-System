const express = require('express');
const router = express.Router();
const staff=require('../services/staff')
const uuid= require('uuid')
const valid=require('../functions/hashMethods');
const users = require("../services/users");
const schemat=require("../schemas/desktop-app-schemas");
const {validationResult} = require("express-validator");
router.post('/register',schemat.registerSchema,async function(req,res,next){

    const errors = validationResult(req);

    if (!errors.isEmpty()) {

        console.log(errors.array());
        res.send("Problem with validation on server-side"); //wyświetla wszystkie błędy

    }
    else {

        const plaint_password = req.body.password;
        const hash_data = valid.saltHashPassword(plaint_password);
        const password = hash_data.passwordHash;
        const salt = hash_data.salt;

        const login = req.body.login;
        const imie = req.body.imie;
        const nazwisko = req.body.nazwisko;
        const email = req.body.email;


        try {
            res.json(await staff.registerCoach(login, email, password, salt, imie, nazwisko));
        } catch (err) {
            console.error("cos tam", err.message);
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
        const login = req.body.login;
        const pass = req.body.password;

        try {
            res.json(await staff.loginCoach(login, pass));
        } catch (err) {
            console.error("error", err.message);
            next(err);
        }
    }
});

router.get('/', async function(req, res, next) {
    try {
        res.json(await staff.getAllCoaches());
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.get('/count_unverified/:id', async function(req, res, next) {
    try {
        res.json(await staff.getCountAllUnverifiedUsers(req.params.id));
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.get('/unverified/:id', async function(req, res, next) {
    try {
        res.json(await staff.getAllUnverifiedUsers(req.params.id));
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.get('/verified/:id', async function(req, res, next) {
    try {
        res.json(await staff.getAllVerifiedUsers(req.params.id));
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.post('/addplan',async function(req,res,next) {

    const nazwa=req.body.nazwa;
    const opis=req.body.opis;
    const user_id=req.body.user;
    const lista=req.body.lista;


    try{
        res.json(await staff.addPlan(nazwa,opis,user_id,lista));
    } catch (err)
    {
        console.error("error",err.message);
        next(err);
    }
});

router.post('/updateplan',async function(req,res,next) {

    const nazwa=req.body.nazwa;
    const opis=req.body.opis;
    const plan_id=req.body.plan;
    const lista=req.body.lista;


    try{
        res.json(await staff.updatePlan(nazwa,opis,plan_id,lista));
    } catch (err)
    {
        console.error("error",err.message);
        next(err);
    }
});

router.delete('/plan/delete/:id', async function (req, res) {


    try {

        res.json(await staff.deletePlan(req.params.id));

    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);

    }

});

router.post('/verifyuser',async function(req,res,next) {

    const id=req.body.id;
    try{
        res.json(await staff.verifyUser(id));
    } catch (err)
    {
        console.error("error",err.message);
        next(err);
    }
});

router.post('/dismissuser',async function(req,res,next) {

    const id=req.body.id;
    try{
        res.json(await staff.dismissUser(id));
    } catch (err)
    {
        console.error("error",err.message);
        next(err);
    }
});

router.post('/password/change',async function(req,res,next) {

    const id=req.body.id;
    const old_pass=req.body.old_password;
    const new_pass=req.body.new_password;
    try{
        res.json(await staff.changePassword(id,old_pass,new_pass));
    } catch (err)
    {
        console.error("error",err.message);
        next(err);
    }
});

router.post('/email/change',async function(req,res,next) {

    const id=req.body.id;
    const email=req.body.email;

    try{
        res.json(await staff.changeEmail(id,email));
    } catch (err)
    {
        console.error("error",err.message);
        next(err);
    }
});

router.get('/measurements_user/:id', async function(req, res, next) {
    try {
        res.json(await staff.getMeasurementsUser(req.params.id));
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.post('/personal_data/change',async function(req,res,next) {

    const id=req.body.id;
    const imie=req.body.imie;
    const nazwisko=req.body.nazwisko;

    try{
        res.json(await staff.updatePersonalData(id,imie,nazwisko));
    } catch (err)
    {
        console.error("error",err.message);
        next(err);
    }
});
router.post('/email/resetpassword',async function(req,res,next) {

    const email=req.body.email;


    try{
        res.json(await staff.resetPassword(email));
    } catch (err)
    {
        console.error("error",err.message);
        next(err);
    }
});
module.exports=router