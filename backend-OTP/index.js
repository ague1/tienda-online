require('dotenv').config();
const express = require('express');
const bodyParser = require('body-parser');
const nodemailer = require('nodemailer');
const cors = require('cors');

const app = express();
app.use(bodyParser.json());
app.use(cors());

const OTP_STORE = {}; // guardaremos OTPs temporalmente

function generateOTP(length = 6) {
    let otp = '';
    for (let i = 0; i < length; i++) {
        otp += Math.floor(Math.random() * 10);
    }
    return otp;
}

// Configura tu correo (Gmail o proveedor SMTP)
const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.EMAIL_PASS
    }
});

// Endpoint para enviar OTP
app.post('/send-otp', (req, res) => {
    const { email } = req.body;
    if (!email) return res.status(400).json({ error: 'Email requerido' });

    const otp = generateOTP();
    const expiresAt = Date.now() + 10 * 60 * 1000; // 10 minutos

    OTP_STORE[email] = { otp, expiresAt };

    const mailOptions = {
        from: process.env.EMAIL_USER,
        to: email,
        subject: 'Tu código OTP',
        text: `Tu código OTP es: ${otp}. Válido por 10 minutos.`
    };

    transporter.sendMail(mailOptions, (err, info) => {
        if (err) return res.status(500).json({ error: 'Error enviando correo' });
        res.json({ success: true, message: 'OTP enviado correctamente' });
    });
});

// Endpoint para verificar OTP
app.post('/verify-otp', (req, res) => {
    const { email, otp } = req.body;
    if (!email || !otp) return res.status(400).json({ error: 'Email y OTP requeridos' });

    const record = OTP_STORE[email];
    if (!record) return res.status(400).json({ valid: false, message: 'No se encontró OTP' });

    if (Date.now() > record.expiresAt) {
        delete OTP_STORE[email];
        return res.status(400).json({ valid: false, message: 'OTP expirado' });
    }

    if (otp === record.otp) {
        delete OTP_STORE[email];
        return res.json({ valid: true });
    } else {
        return res.status(400).json({ valid: false, message: 'OTP incorrecto' });
    }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Servidor escuchando en puerto ${PORT}`));