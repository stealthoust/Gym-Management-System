const express = require('express');
const router = express.Router();
const admin=require('../services/admin')
const db = require("../services/db");
const valid = require("../functions/hashMethods");
const email = require("../services/email");

router.get('/allusers',async function(req,res,next){

    try{

        res.json(await admin.getAllUsers());
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.get('/allcoaches',async function(req,res,next){

    try{

        res.json(await admin.getAllCoaches());
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.post('/updateuser',async function(req,res,next){

    try{

        const id=req.body.id;
        const imie=req.body.imie;
        const nazwisko=req.body.nazwisko;
        const dataUrodzenia=req.body.dataUrodzenia;


        res.json(await admin.updateUser(id,imie,nazwisko,dataUrodzenia));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.post('/deleteuser',async function(req,res,next){

    try{

        const id=req.body.id;



        res.json(await admin.deleteUser(id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.post('/updatecoach',async function(req,res,next){

    try{

        const id=req.body.id;
        const imie=req.body.imie;
        const nazwisko=req.body.nazwisko;
        const rola=req.body.rola;
        const weryfikacja=req.body.weryfikacja;



        res.json(await admin.updateCoach(id,imie,nazwisko,rola,weryfikacja));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.post('/deletecoach',async function(req,res,next){

    try{

        const id=req.body.id;


        res.json(await admin.deleteCoach(id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.get('/statistics',async function(req,res,next){

    try{

        res.json(await admin.getStatisticsData());
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.post('/blockuser',async function(req,res,next){

    try{

        const id=req.body.id;

        res.json(await admin.blockUser(id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.post('/unblockuser',async function(req,res,next){

    try{
        const id=req.body.id;
        res.json(await admin.unblockUser(id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
module.exports=router