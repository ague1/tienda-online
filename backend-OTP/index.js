require('dotenv').config();
const express = require('express');
const bodyParser = require('body-parser');
const nodemailer = require('nodemailer');
const cors = require('cors');
const crypto = require('crypto');
const admin = require('firebase-admin');
const path = require('path');

const app = express();
app.use(express.json());

const serviceAccount = require(
  path.resolve(process.env.FIREBASE_KEY_PATH)
);

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  projectId: serviceAccount.project_id
});

const db = admin.firestore();
console.log("🔥 Proyecto conectado:", serviceAccount.project_id);

// Función para generar OTP de 4 dígitos
function generateOTP(length = 4) {
  let otp = '';
  for (let i = 0; i < length; i++) {
    otp += Math.floor(Math.random() * 10);
  }
  return otp;
}

function hashOtp(otp) {
  return crypto.createHash('sha256').update(otp).digest('hex');
}

// Configurar Nodemailer (Gmail o cualquier SMTP)
const transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: process.env.EMAIL_USER,  //email configurado en .env.example
    pass: process.env.EMAIL_PASS, // contraseña
  },
});


// --- Funciones para Firestore ---
async function saveOtp(email, otp) {
  const expiresAt = Date.now() + 10 * 60 * 1000;

  await db.collection('password_otps').add({
    email,
    otp,
    expiration: expiresAt,
    createdAt: admin.firestore.FieldValue.serverTimestamp()
  });
}

async function getOtp(email) {
  const snap = await db.collection('password_otps')
    .where('email', '==', email)
    .orderBy('createdAt', 'desc')
    .limit(1)
    .get();

  if (snap.empty) return null;

  return { id: snap.docs[0].id, ...snap.docs[0].data() };
}

async function deleteOtp(id) {
  await db.collection('password_otps').doc(id).delete();
}

// --- Endpoint para enviar OTP ---
app.post('/send-otp', async (req, res) => {
  try {
    const { email } = req.body;
    if (!email) return res.status(400).json({ error: 'Email requerido' });

    const otp = generateOTP();
    const hashedOtp = hashOtp(otp);

    await saveOtp(email, hashedOtp);

    const mailOptions = {
      from: process.env.EMAIL_USER,
      to: email,
      subject: 'Tu código de Verificacion',
      text: `Tu código OTP es: ${otp}. Válido por 10 minutos.`,
    };

    await transporter.sendMail(mailOptions);
    res.json({ success: true, message: 'OTP enviado correctamente' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Error enviando OTP' });
  }
});

// --- Endpoint para verificar OTP ---
app.post('/verify-otp', async (req, res) => {
  try {
    const { email, otp } = req.body;
    if (!email || !otp) return res.status(400).json({ error: 'Email y OTP requeridos' });

    const record = await getOtp(email);
    if (!record) return res.status(400).json({ valid: false, message: 'No se encontró OTP' });

    if (Date.now() > record.expiration) {
      await deleteOtp(record.id);
      return res.status(400).json({ valid: false, message: 'OTP expirado' });

    }
    const hashedInput = hashOtp(otp);

    if (hashedInput === record.otp) {
      await deleteOtp(record.id); // OTP usado, borrarlo
      return res.json({ valid: true, message: 'OTP verificado correctamente' });
    } else {
      return res.status(400).json({ valid: false, message: 'OTP incorrecto' });
    }
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Error verificando OTP' });
  }
});

// --- Iniciar servidor ---
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Servidor escuchando en puerto ${PORT}`));