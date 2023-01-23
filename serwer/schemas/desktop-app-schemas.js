const {body} = require("express-validator");
const loginSchema=[
    body('login').isLength({min:5,max:30}).withMessage('Login must be min 5 and max 30 characters long').matches(/^[A-Za-z0-9]+$/).withMessage('Error pattern'),
    body('password').isLength({min:8,max:30}).withMessage('Password must be min 5 and max 30 characters long').matches(/^[A-Za-z0-9]+$/).withMessage('Error pattern'),
];

const registerSchema=[
    body('login').isLength({min:5,max:30}).withMessage('Login must be min 5 and max 30 characters long').matches(/^[A-Za-z0-9]+$/).withMessage('Error pattern'),
    body('password').isLength({min:8,max:30}).withMessage('Password must be min 8 and max 30 characters long').matches(/^[A-Za-z0-9]+$/).withMessage('Error pattern'),
    body('imie').isLength({min:3,max:13}).withMessage('Name must be min 3 and max 13 characters long').matches(/^[A-Za-z]+$/).withMessage('Error pattern'),
    body('nazwisko').isLength({min:2,max:35}).withMessage('Surname must be min 3 and max 13 characters long').matches(/^[A-Za-z]+$/).withMessage('Error pattern'),
    body('email','Email is not valid').isEmail(),

];

module.exports = {
    registerSchema,
    loginSchema,
};

