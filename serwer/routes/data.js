const express = require('express');
const router = express.Router();
const data=require('../services/data')
const users = require("../services/users");
const staff = require("../services/staff");


router.get('/lifestyle', async function(req, res, next) {
    try {
        res.json(await data.getAllLifestyles());
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.get('/weight', async function(req, res, next) {
    try {
        res.json(await data.getAllWeightGoals());
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.get('/categories', async function(req, res, next) {
    try {
        res.json(await data.getAllCategories());
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.get('/exercises_category/:id', async function(req, res, next) {
    try {
        res.json(await data.getAllExercisesFromCategory(req.params.id));
    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);
        next(err);
    }
});

router.post('/exercise/add',async function(req,res,next) {

    const nazwa=req.body.nazwa;
    const pozycja=req.body.pozycja;
    const ruch=req.body.ruch;
    const wskazowki=req.body.wskazowki;
    const url=req.body.url;
    const kategoria=req.body.kategoria;

    try{
        res.json(await data.addNewExercise(nazwa,pozycja,ruch,wskazowki,url,kategoria));
    } catch (err)
    {
        console.error("error",err.message);
        next(err);
    }
});

router.post('/exercise/update',async function(req,res,next) {

    const nazwa=req.body.nazwa;
    const pozycja=req.body.pozycja;
    const ruch=req.body.ruch;
    const wskazowki=req.body.wskazowki;
    const url=req.body.url;
    const id=req.body.id;

    try{
        res.json(await data.updateExercise(nazwa,pozycja,ruch,wskazowki,url,id));
    } catch (err)
    {
        console.error("error",err.message);
        next(err);
    }
});


router.delete('/exercise/delete/:id', async function (req, res) {


    try {

        res.json(await data.deleteExercise(req.params.id));

    } catch (err) {
        console.error(`Coś poszło nie tak `, err.message);

    }

});
module.exports=router