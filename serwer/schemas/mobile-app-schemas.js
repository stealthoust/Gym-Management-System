const {body} = require('express-validator');
const moment = require('moment');


let cA = moment().subtract(15, 'years').add(1, 'days').format('YYYY-MM-DD');
let cB = moment().subtract(70, 'years').add(1, 'days').format('YYYY-MM-DD');


const registerSchema = [
    body('login').isLength({
        min: 5,
        max: 30
    }).withMessage('Login must be min 5 and max 30 characters long').matches(/^[A-Za-z0-9]+$/).withMessage('Error pattern'),
    body('password').isLength({min: 8, max: 30}).withMessage('Password must be min 8 and max 30 characters long')
        .matches(/^[\s\p{L}]+$/u).withMessage('Error pattern'),
    body('imie').isLength({
        min: 3,
        max: 13
    }).withMessage('Name must be min 3 and max 13 characters long').matches(/^[\s\p{L}]+$/u).withMessage('Error pattern'),
    body('nazwisko').isLength({
        min: 2,
        max: 35
    }).withMessage('Surname must be min 3 and max 13 characters long').matches(/^[\s\p{L}]+$/u).withMessage('Error pattern'),
    body('email', 'Email is not valid').isEmail(),
    body('num_tel').custom((value) => {
        if (value.length === 0 || value.length === 9)
            return true;
    }).withMessage('Lenght of phone number must be 0 or 9').matches(/^[\\s0-9]*$/).withMessage('Error pattern'),
    body('data_ur').isBefore(cA).withMessage('You must be at least 15 years old').isAfter(cB).withMessage('You can not be more than 70 years old')

];
const loginSchema = [
    body('login').isLength({
        min: 5,
        max: 30
    }).withMessage('Login must be min 5 and max 30 characters long').matches(/^[A-Za-z0-9]+$/).withMessage('Error pattern'),
    body('password').isLength({
        min: 8,
        max: 30
    }).withMessage('Password must be min 5 and max 30 characters long').matches(/^[A-Za-z0-9]+$/).withMessage('Error pattern'),
];

const addProductSchema = [
    body('id').isInt({min: 1}).withMessage('Error pattern'),
    body('waga', 'Error pattern or value out of range ').isFloat({min: 1, max: 2500}),
    body('nazwa').isLength({min: 3, max: 40}).withMessage('Name of product must be min 3 and max 40 characters long'),
    body('kcal', 'Error pattern or value out of range ').isFloat({min: 0, max: 900}),
    body('w', 'Error pattern or value out of range ').isFloat({min: 0, max: 100}),
    body('b', 'Error pattern or value out of range ').isFloat({min: 0, max: 100}),
    body('t', 'Error pattern or value out of range ').isFloat({min: 0, max: 100}),
//przydaloby sie sprawdzic czt b+w+t <=100

];

const changePasswordSchema = [
    body('id').isInt({min: 1}).withMessage('Error pattern'),
    body('old_password').isLength({
        min: 8,
        max: 30
    }).withMessage('Password must be min 8 and max 30 characters long').matches(/^[A-Za-z0-9]+$/).withMessage('Error pattern'),
    body('new_password').isLength({
        min: 8,
        max: 30
    }).withMessage('Password must be min 8 and max 30 characters long').matches(/^[A-Za-z0-9]+$/).withMessage('Error pattern'),

]
const validEmailSchema = [
    body('email', 'Email is not valid').isEmail(),
]
const validPhoneNumberSchema = [
    body('phone_number').custom((value) => {
        if (value.length === 0 || value.length === 9)
            return true;
    }).withMessage('Lenght of phone number must be 0 or 9').matches(/^[\\s0-9]*$/).withMessage('Error pattern'),

]
const validWeightSchema = [
    body('id').isInt({min: 1}).withMessage('Error pattern'),
    body('waga', 'Error pattern or value out of range ').isFloat({min: 40, max: 210}),
]

const changeProfileDataSchema = [
    body('id', 'Error pattern').isInt({min: 1}),
    body('trainer_id').optional().isInt({min: 1}).withMessage('Error pattern'),
    body('waga', 'Error pattern or value out of range ').isFloat({min: 40, max: 210}),
    body('plec').isLength({
        min: 1,
        max: 1
    }).withMessage('Sex length must be exactly 1 character long').matches(/^[mk]+$/).withMessage('Error pattern'),
    body('wzrost', 'Error pattern or value out of range ').isFloat({min: 130, max: 251}),
    body('tryb', 'Error pattern or value out of range').isInt({min: 1, max: 6}),
    body('cel_waga', 'Error pattern or value out of range').isInt({min: 1, max: 3}),
]
const verifyProfileDataSchema = [
    body('id', 'Error pattern').isInt({min: 1}),
    body('waga', 'Error pattern or value out of range ').isFloat({min: 40, max: 210}),
    body('plec').isLength({
        min: 1,
        max: 1
    }).withMessage('Sex length must be exactly 1 character long').matches(/^[mk]+$/).withMessage('Error pattern'),
    body('wzrost', 'Error pattern or value out of range ').isFloat({min: 130, max: 251}),
    body('aktywnosc', 'Error pattern or value out of range').isInt({min: 1, max: 6}),
    body('cel', 'Error pattern or value out of range').isInt({min: 1, max: 3}),
    body('trener').optional().isInt({min: 1}).withMessage('Error pattern'),
]
module.exports = {
    registerSchema,
    loginSchema,
    addProductSchema,
    changePasswordSchema,
    validEmailSchema,
    validWeightSchema,
    verifyProfileDataSchema,
    changeProfileDataSchema,
    validPhoneNumberSchema
};