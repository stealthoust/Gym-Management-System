const db = require('./db');
const nodemailer = require("nodemailer");
const mariadb = require("mariadb");
const uuid = require("uuid");
const valid = require("../functions/hashMethods");
require('dotenv').config();

const transporter = nodemailer.createTransport({
    host: 'smtp.gmail.com',
    service: 'gmail',
    secure: true,
    auth: {
        user: process.env.EMAIL,
        pass: process.env.SECRET_KEY
    },
    tls: {
        rejectUnauthorized: false,
    },
})


async function sendMessageAccountState(odbiorca,status) {
    let tresc="";
    let mailOptions = {
        from: process.env.EMAIL,
        to: odbiorca,
        subject: "Zmiana statusu konta w systemie HealthBoost.",
        text: tresc
    }

    if(status=="n"){
        tresc="Witaj!\n\nTwoje konto zostało aktywowane. Od teraz możesz zalogować się w aplikacji, aby przejść wstępny formularz!";
    }
    else if(status=="o")
    {
        tresc="Witaj!\n\nTwoje konto czeka na aktywację przez wybranego przez Ciebie trenera.\n" +
            "Jak tylko status twojego konta się zmieni dostaniesz powiadomienie.\nMimo wszystko zalecamy sprawdzać status konta co kilka godzin.";
    }
    else if(status=="a")
    {
        tresc="Witaj!\n\nTwoje konto zostało aktywowane.\nOd teraz możesz korzystać pełnoprawnie z aplikacji! Prosimy o zapoznanie się z regulaminem aplikacji dołączonym w załączniku.";
         mailOptions = {
            from: process.env.EMAIL,
            to: odbiorca,
            subject: "Zmiana statusu konta w systemie HealthBoost.",
            text: tresc,
             attachments: [
                 {filename:'regulamin.pdf',path:'files/regulamin_uzytkownik.pdf'}
             ]
        }
    }
    else if(status=="at")
    {
        tresc="Witaj!\n\nTwoje konto zostało aktywowane.\nOd teraz możesz korzystać pełnoprawnie z aplikacji! Prosimy o zapoznanie się z regulaminem aplikacji dołączonym w załączniku.";
        mailOptions = {
            from: process.env.EMAIL,
            to: odbiorca,
            subject: "Zmiana statusu konta w systemie HealthBoost.",
            text: tresc,
            attachments: [
                {filename:'regulamin.pdf',path:'files/regulamin_personel.pdf'}
            ]
        }
    }

    await transporter.sendMail(mailOptions, function (error, success) {
        if (error) {
            console.log(error);
        } else {
            console.log("Email sent");
        }
    });
}
async function sendMessagePasswordReset(odbiorca,pass) {


    let mailOptions = {
        from: process.env.EMAIL,
        to: odbiorca,
        subject: "Reset hasła w systemie HealthBoost.",
        text: "Witaj!\n\nTwoje hasło zostało zresetowane.\nTwoje nowe hasło to: "+pass
    }
    await transporter.sendMail(mailOptions, function (error, success) {
        if (error) {
            console.log(error);
        } else {
            console.log("Email sent");
        }
    });
}
async function tokenActivateAccount(token_id, user_id) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const token = await conn.execute('SELECT IF( EXISTS( SELECT * FROM token_user WHERE `unique_id` =  ?), 1, 0) as number', [token_id]);

            if (token[0].number === 1) {
                await conn.execute('UPDATE uzytkownicy set weryfikacja="n" where id=?', [user_id]);
                await conn.execute('DELETE FROM token_user WHERE unique_id=?;', [token_id]);
                await conn.commit();
                console.log("Pomyślnie potwierdzono email");
                return JSON.stringify('Email potwierdzony pomyślnie');
            } else {

                console.log("Token nie istnieje");
                return 'Token nie istnieje. Być może twoje konto jest już aktywne!';

            }


        } catch (err) {
            await conn.rollback();
            console.log("Nie udalo sie zweryfikować emaila");
            console.error(err);
            return 'Wystąpił problem z aktywacją konta.';
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }
}
async function tokenDeactivateAccount(token_id, user_id) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {


                await conn.execute('DELETE FROM token_user WHERE unique_id=?;', [token_id]);
                await conn.execute('DELETE FROM uzytkownicy WHERE id=?;', [user_id]);
                await conn.commit();
                console.log("Pomyślnie usunięto konto");
                return JSON.stringify('Konto zostało zdezaktywowane');



        } catch (err) {
            await conn.rollback();
            console.log("Nie udalo sie zdezaktywować konta");
            console.error(err);
            return JSON.stringify('Nie udalo sie zdezaktywować konta');
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }
}
async function sendMailVerificationAccount(email, routerActivateAccount,routerDeactivateAccount) {

    let mailOptions = {
        from: process.env.EMAIL,
        to: email,
        subject: "Potwierdzenie konta w systemie HealthBoost",
        html: `<p>Witaj!<br><br>Twój adres email został wykorzystany do założenia konta w systemie HealthBoost. Jeśli to ty założyłeś konto, kliknij w poniższy przycisk,
            aby je aktywować.<br><br> 
            <form action="${routerActivateAccount}">
         <input style="background-color: green; color: white" type="submit" value="Aktywuj konto" />
        </form>
            <br><br>
            Jeśli to nie ty, poniższy przycisk usunie konto. <br><br>
            <form action="${routerDeactivateAccount}">
         <input style="background-color: red; color: white" type="submit" value="Usuń konto" />
        </form>
            </p>
        `
    }

    await transporter.sendMail(mailOptions, function (error, success) {
        if (error) {
            console.log(error);
        } else {
            console.log("Email sent");
        }
    });
}
async function tokenActivateEmail(token_id, user_id) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const token = await conn.execute('SELECT IF( EXISTS( SELECT * FROM token_user WHERE `unique_id` =  ?), 1, 0) as number', [token_id]);

            if (token[0].number === 1) {
                await conn.execute('UPDATE uzytkownicy set weryfikacja="a" where id=?', [user_id]);
                await conn.execute('DELETE FROM token_user WHERE unique_id=?;', [token_id]);
                await conn.commit();
                console.log("Pomyślnie zmieniono email");
                return 'Email zmieniony pomyślnie';
            } else {

                console.log("Token nie istnieje");
                return 'Token wygasł. Być może zmieniłeś już email lub usunąłeś żądanie zmiany emaila!';

            }


        } catch (err) {
            await conn.rollback();
            console.log("Nie udalo sie zmienić emaila");
            console.error(err);
            return "Nie udalo sie zmienić emaila";
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }
}
async function tokenDeactivateEmail(token_id, user_id) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {

            const res=await conn.execute('SELECT opis FROM token_user WHERE unique_id=?;', [token_id]);

            await conn.execute('UPDATE uzytkownicy set email=?,weryfikacja="a" where id=?;', [res[0].opis,user_id]);
            await conn.execute('DELETE FROM token_user WHERE unique_id=?;', [token_id]);
            await conn.commit();
            console.log("Pomyślnie usunięto token zmiany email");
            return 'Zmiana adresu email została odrzucona!';



        } catch (err) {
            await conn.rollback();
            console.log("Nie udalo sie usunąć prośby o zmianę emaila");
            console.error(err);
            return 'Nie udalo sie usunąć prośby o zmianę emaila. Być może zrobiłeś już to wcześniej';
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }
}
async function deleteTokenResetPassword( user_id) {
    await db.query('DELETE FROM token_user where uzytkownik_id=? and opis="reset_password_token"', [user_id], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });

    });

}
async function deleteTokenResetPasswordCoach( personel_id) {
    await db.query('DELETE FROM token_personel where personel_id=? and opis="reset_password_token"', [personel_id], function (err, result, fields) {
        result.on('error', function (err) {
            console.log('[MySQL ERROR]', err);
        });

    });

}
async function tokenActivateResetPassword(token_id, user_id) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const token = await conn.execute('SELECT IF( EXISTS( SELECT * FROM token_user WHERE `unique_id` =  ?), 1, 0) as number', [token_id]);


            if (token[0].number === 1) {
                const user = await conn.execute('SELECT * from uzytkownicy where id=?', [user_id]);
                const new_pass=(Math.random() + 1).toString(36).substring(2,14);

                const new_pass_hash = valid.sha512(new_pass, user[0].salt).passwordHash;
                await conn.execute('UPDATE uzytkownicy set password=? where id=?', [new_pass_hash,user_id]);
                await conn.execute('DELETE FROM token_user WHERE unique_id=?;', [token_id]);
                await sendMessagePasswordReset(user[0].email,new_pass);
                await conn.commit();
                console.log("Pomyślnie zmieniono email");
                return 'Pomyślnie zresetowano hasło! Wkrótce otrzymasz wiadomość z nowym hasłem.';
            } else {

                console.log("Token nie istnieje");
                return 'Token wygasł. Być może zmieniłeś już hasło lub usunąłeś żądanie zmiany emaila!';

            }


        } catch (err) {
            await conn.rollback();
            console.log("Nie udalo sie zresetować hasła");
            console.error(err);
            return "Nie udalo sie zresetować hasła";
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }
}
async function tokenDeactivateResetPassword(token_id, user_id) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const token = await conn.execute('SELECT IF( EXISTS( SELECT * FROM token_user WHERE `unique_id` =  ?), 1, 0) as number', [token_id]);

            if (token[0].number === 1) {

                await conn.execute('DELETE FROM token_user WHERE unique_id=? and uzytkownik_id=?;', [token_id,user_id]);
                await conn.commit();
                console.log("Pomyślnie usunieto token zmiany hasła");
                return 'Pomyślnie usunieto token zmiany hasła';
            } else {

                console.log("Token nie istnieje");
                return 'Token wygasł. Być może już wcześniej usunąłeś token!';

            }


        } catch (err) {
            await conn.rollback();
            console.log("Nie udalo sie usunąc tokenu resetu hasła");
            console.error(err);
            return "Nie udalo sie zresetować hasła";
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }
}
async function sendMailChangeEmail(email, routerActivateEmail,routerDeactivateEmail) {

    let mailOptions = {
        from: process.env.EMAIL,
        to: email,
        subject: "Zmiana adresu w systemie HealthBoost",
        html: `<p>Witaj!<br><br>Jeśli chcesz zmienić email swojego konta kliknij w poniższy przycisk.
            <br><br>
            <form action="${routerActivateEmail}">
         <input style="background-color: green; color: white" type="submit" value="Potwierdź zmianę adresu email" />
        </form>
            <br><br>
            Jeśli to nie ty poprosiłeś o zmianę adresu email, kliknij w poniższy przycisk. <br><br>
            <form action="${routerDeactivateEmail}">
         <input style="background-color: red; color: white" type="submit" value="Usuń prośbę zmiany adresu email" />
        </form>
            </p>
        `
    }
    await transporter.sendMail(mailOptions, function (error, success) {
        if (error) {
            console.log(error);
        } else {
            console.log("Email sent");
        }
    });
}
async function sendMailResetPassword(email, routerActivateResetPassword,routerDeactivateResetPassword) {

    let mailOptions = {
        from: process.env.EMAIL,
        to: email,
        subject: "Próba zresetowania hasła w systemie HealthBoost",
        html: `<p>Witaj!<br><br>Jeśli zapomniałeś swojego hasła i chcesz wygenerować nowe, kliknij w poniższy przycisk.
            <br> <br>
            <form action="${routerActivateResetPassword}">
         <input style="background-color: green; color: white" type="submit" value="Wygeneruj nowe hasło" />
        </form>
            <br><br>
            Jeśli to nie ty wysłałeś żądanie zresetowania hasła, kliknij w poniższy przycisk. <br><br>
            <form action="${routerDeactivateResetPassword}">
         <input style="background-color: red; color: white" type="submit" value="Usuń prośbę zmiany hasła" />
        </form>
            </p>
        `

    }
    await transporter.sendMail(mailOptions, function (error, success) {
        if (error) {
            console.log(error);
        } else {
            console.log("Email sent");
        }
    });
}
async function sendMailForgetLogin(email,login) {

    let mailOptions = {
        from: process.env.EMAIL,
        to: email,
        subject: "Przypomnienie loginu w systemie HealthBoost",
        text: "Witaj!\n\nTwój login to: "+login
    }
    await transporter.sendMail(mailOptions, function (error, success) {
        if (error) {
            console.log(error);
        } else {
            console.log("Email sent");
        }
    });
}






async function tokenActivateCoachAccount(token_id, personel_id) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const token = await conn.execute('SELECT IF( EXISTS( SELECT * FROM token_personel WHERE `unique_id` =  ?), 1, 0) as number', [token_id]);

            if (token[0].number === 1) {
                await conn.execute('UPDATE personel set weryfikacja="n" where id=?', [personel_id]);
                await conn.execute('DELETE FROM token_personel WHERE unique_id=? and personel_id=?;', [token_id,personel_id]);
                await conn.commit();
                console.log("Pomyślnie potwierdzono email");
                return JSON.stringify('Email potwierdzony pomyślnie');
            } else {

                console.log("Token nie istnieje");
                return 'Token nie istnieje. Być może twoje konto jest już aktywne!';

            }


        } catch (err) {
            await conn.rollback();
            console.log("Nie udalo sie zweryfikować emaila");
            console.error(err);
            return 'Wystąpił problem z aktywacją konta.';
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }
}
async function tokenDeactivateCoachAccount(token_id, personel_id) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {


            await conn.execute('DELETE FROM token_personel WHERE unique_id=? and personel_id=?;', [token_id,personel_id]);
            await conn.execute('DELETE FROM personel WHERE id=?;', [personel_id]);
            await conn.commit();
            console.log("Pomyślnie usunięto konto");
            return JSON.stringify('Konto zostało zdezaktywowane');



        } catch (err) {
            await conn.rollback();
            console.log("Nie udalo sie zdezaktywować konta");
            console.error(err);
            return JSON.stringify('Nie udalo sie zdezaktywować konta');
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }
}
async function sendMailResetPasswordCoach(email,login, routerActivateResetPassword,routerDeactivateResetPassword) {

    let mailOptions = {
        from: process.env.EMAIL,
        to: email,
        subject: "Próba zresetowania hasła w systemie HealthBoost",
        html: `<p>Witaj!<br><br>Jeśli zapomniałeś swojego hasła i chcesz wygenerować nowe, kliknij w poniższy przycisk.
            <br> <br>
            <form action="${routerActivateResetPassword}">
         <input style="background-color: green; color: white" type="submit" value="Wygeneruj nowe hasło" />
        </form>
            <br><br>
            Jeśli to nie ty wysłałeś żądanie zresetowania hasła, kliknij w poniższy przycisk. <br><br>
            <form action="${routerDeactivateResetPassword}">
         <input style="background-color: red; color: white" type="submit" value="Usuń prośbę zmiany hasła" />
        </form>
            </p>
        `
    }
    await transporter.sendMail(mailOptions, function (error, success) {
        if (error) {
            console.log(error);
        } else {
            console.log("Email sent");
        }
    });
}
async function tokenActivateResetPasswordCoach(token_id, personel_id) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const token = await conn.execute('SELECT IF( EXISTS( SELECT * FROM token_personel WHERE `unique_id` =  ?), 1, 0) as number', [token_id]);


            if (token[0].number === 1) {
                const personel = await conn.execute('SELECT * from personel where id=?', [personel_id]);
                const new_pass=(Math.random() + 1).toString(36).substring(2,14);

                const new_pass_hash = valid.sha512(new_pass, personel[0].salt).passwordHash;
                await conn.execute('UPDATE personel set password=? where id=?', [new_pass_hash,personel_id]);
                await conn.execute('DELETE FROM token_personel WHERE unique_id=?;', [token_id]);
                await sendMessagePasswordReset(personel[0].email,new_pass);
                await conn.commit();
                console.log("Pomyślnie zmieniono email");
                return 'Pomyślnie zresetowano hasło! Wkrótce otrzymasz wiadomość z nowym hasłem.';
            } else {

                console.log("Token nie istnieje");
                return 'Token wygasł. Być może zmieniłeś już hasło lub usunąłeś żądanie zmiany emaila!';

            }


        } catch (err) {
            await conn.rollback();
            console.log("Nie udalo sie zresetować hasła");
            console.error(err);
            return "Nie udalo sie zresetować hasła";
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }
}
async function tokenDeactivateResetPasswordCoach(token_id, personel_id) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const token = await conn.execute('SELECT IF( EXISTS( SELECT * FROM token_personel WHERE `unique_id` =  ?), 1, 0) as number', [token_id]);

            if (token[0].number === 1) {

                await conn.execute('DELETE FROM token_personel WHERE unique_id=? and personel_id=?;', [token_id,personel_id]);
                await conn.commit();
                console.log("Pomyślnie usunieto token zmiany hasła");
                return 'Pomyślnie usunieto token zmiany hasła';
            } else {

                console.log("Token nie istnieje");
                return 'Token wygasł. Być może już wcześniej usunąłeś token!';

            }


        } catch (err) {
            await conn.rollback();
            console.log("Nie udalo sie usunąc tokenu resetu hasła");
            console.error(err);
            return "Nie udalo sie zresetować hasła";
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }
}
async function tokenActivateEmailCoach(token_id, personel_id) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {
            const token = await conn.execute('SELECT IF( EXISTS( SELECT * FROM token_personel WHERE `unique_id` =  ?), 1, 0) as number', [token_id]);

            if (token[0].number === 1) {
                await conn.execute('UPDATE personel set weryfikacja="a" where id=?', [personel_id]);
                await conn.execute('DELETE FROM token_personel WHERE unique_id=?;', [token_id]);
                await conn.commit();
                console.log("Pomyślnie zmieniono email");
                return 'Email zmieniony pomyślnie';
            } else {

                console.log("Token nie istnieje");
                return 'Token wygasł. Być może zmieniłeś już email lub usunąłeś żądanie zmiany emaila!';

            }


        } catch (err) {
            await conn.rollback();
            console.log("Nie udalo sie zmienić emaila");
            console.error(err);
            return "Nie udalo sie zmienić emaila";
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }
}
async function tokenDeactivateEmailCoach(token_id, personel_id) {
    let conn;

    try {

        conn = await mariadb.createConnection({
            host: "localhost", user: "root", password: "", database: "silownia"
        });
        await conn.beginTransaction();
        try {

            const res=await conn.execute('SELECT opis FROM token_personel WHERE unique_id=?;', [token_id]);

            await conn.execute('UPDATE personel set email=?,weryfikacja="a" where id=?;', [res[0].opis,personel_id]);
            await conn.execute('DELETE FROM token_personel WHERE unique_id=?;', [token_id]);
            await conn.commit();
            console.log("Pomyślnie usunięto token zmiany email");
            return 'Zmiana adresu email została odrzucona!';



        } catch (err) {
            await conn.rollback();
            console.log("Nie udalo sie usunąć prośby o zmianę emaila");
            console.error(err);
            return 'Nie udalo sie usunąć prośby o zmianę emaila. Być może zrobiłeś już to wcześniej';
        }
    } catch (err) {
        console.error("Error starting a transaction: ", err);
        return JSON.stringify('Error with connection or begin transaction');
    }
}
module.exports = {
    sendMessageAccountState,
    tokenActivateAccount,
    sendMailVerificationAccount,
    tokenDeactivateAccount,
    sendMailChangeEmail,
    tokenActivateEmail,
    tokenDeactivateEmail,
    deleteTokenResetPassword,
    tokenActivateResetPassword,
    tokenDeactivateResetPassword,
    sendMailResetPassword,
    sendMailResetPasswordCoach,
    sendMailForgetLogin,
    tokenActivateCoachAccount,
    tokenDeactivateCoachAccount,
    tokenActivateResetPasswordCoach,
    tokenDeactivateResetPasswordCoach,
    deleteTokenResetPasswordCoach,
    tokenActivateEmailCoach,
    tokenDeactivateEmailCoach
}