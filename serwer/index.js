const express = require('express');
const crypto=require('crypto')
const uuid= require('uuid')
const bodyParser= require('body-parser')
const mysql= require('mysql')
const db = require('./services/db');
const app = express();

const usersRouter=require('./routes/users');
const staffRouter=require('./routes/staff');
const dataRouter=require('./routes/data');
const adminRouter=require('./routes/admin');
const emailRouter=require('./routes/email');
const port = 3000;


app.use(express.urlencoded({extended: true}));
app.use(express.json());
app.use("/users",usersRouter);
app.use("/staff",staffRouter);
app.use("/data",dataRouter);
app.use("/admin",adminRouter);
app.use("/email",emailRouter);




app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)

})
