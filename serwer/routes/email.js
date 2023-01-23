const express = require('express');
const router = express.Router();
const email=require('../services/email')
const db = require("../services/db");
const valid = require("../functions/hashMethods");

router.post('/status/',async function(req,res,next){

    const odbiorca=req.body.odbiorca;
    const status=req.body.status;
    try{
        //uzupelnij argumenty
        res.json(await email.sendMessageAccountState(odbiorca,status));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});

router.get('/token_activate/:token/:id',async function(req,res,next){

    try{
        //uzupelnij argumenty
        res.json(await email.tokenActivateAccount(req.params.token,req.params.id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.get('/token_deactivate/:token/:id',async function(req,res,next){

    try{
        //uzupelnij argumenty
        res.json(await email.tokenDeactivateAccount(req.params.token,req.params.id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});

router.get('/token_activate_change_email/:token/:id',async function(req,res,next){

    try{

        res.json(await email.tokenActivateEmail(req.params.token,req.params.id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.get('/token_deactivate_change_email/:token/:id',async function(req,res,next){

    try{
        //uzupelnij argumenty
        res.json(await email.tokenDeactivateEmail(req.params.token,req.params.id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});

router.get('/delete_tokens_reset_password/:id',async function(req,res,next){

    try{
        //uzupelnij argumenty
        res.json(await email.deleteTokenResetPassword(req.params.id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.get('/delete_tokens_reset_password_coach/:id',async function(req,res,next){

    try{
        //uzupelnij argumenty
        res.json(await email.deleteTokenResetPasswordCoach(req.params.id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.get('/token_activate_reset_password/:token/:id',async function(req,res,next){

    try{
        //uzupelnij argumenty
        res.json(await email.tokenActivateResetPassword(req.params.token,req.params.id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.get('/token_deactivate_reset_password/:token/:id',async function(req,res,next){

    try{
        //uzupelnij argumenty
        res.json(await email.tokenDeactivateResetPassword(req.params.token,req.params.id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});

router.get('/token_activate_coach/:token/:id',async function(req,res,next){

    try{
        //uzupelnij argumenty
        res.json(await email.tokenActivateCoachAccount(req.params.token,req.params.id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.get('/token_deactivate_coach/:token/:id',async function(req,res,next){

    try{
        //uzupelnij argumenty
        res.json(await email.tokenDeactivateCoachAccount(req.params.token,req.params.id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.get('/token_activate_reset_password_coach/:token/:id',async function(req,res,next){

    try{
        //uzupelnij argumenty
        res.json(await email.tokenActivateResetPasswordCoach(req.params.token,req.params.id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.get('/token_deactivate_reset_password_coach/:token/:id',async function(req,res,next){

    try{
        //uzupelnij argumenty
        res.json(await email.tokenDeactivateResetPasswordCoach(req.params.token,req.params.id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.get('/token_activate_change_email_coach/:token/:id',async function(req,res,next){

    try{

        res.json(await email.tokenActivateEmailCoach(req.params.token,req.params.id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
router.get('/token_deactivate_change_email_coach/:token/:id',async function(req,res,next){

    try{
        //uzupelnij argumenty
        res.json(await email.tokenDeactivateEmailCoach(req.params.token,req.params.id));
    } catch (err)
    {
        console.error("cos tam",err.message);
        next(err);
    }
});
module.exports=router