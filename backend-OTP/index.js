require('dotenv').config();
const express = require('express');
const nodemailer = require('nodemailer');
const crypto = require('crypto');
const admin = require('firebase-admin');
const path = require('path');
const rateLimit = require('express-rate-limit');
const app = express();

app.disable('x-powered-by');

app.use(express.json({
    limit: '10kb'
}));

const serviceAccount = require(
    path.resolve(process.env.FIREBASE_KEY_PATH)
);

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    projectId: serviceAccount.project_id
});

const db = admin.firestore();

console.log('Proyecto conectado:',serviceAccount.project_id);
const PORT = process.env.PORT || 3000;
const OTP_EXPIRATION_MS = 10 * 60 * 1000;
const RESET_TOKEN_EXPIRATION_MS = 10 * 60 * 1000;
const MAX_OTP_ATTEMPTS = 5;
const MAX_RESET_TOKEN_ATTEMPTS = 5;


// Solicitud de OTP
const sendOtpLimiter = rateLimit({
    windowMs: 15 * 60 * 1000,
    max: 5,

    standardHeaders: true,
    legacyHeaders: false,

    message: {
        success: false,
        code: 'TOO_MANY_REQUESTS'
    }
});

// Verificación de OTP
const verifyOtpLimiter = rateLimit({
    windowMs: 10 * 60 * 1000,
    max: 20,

    standardHeaders: true,
    legacyHeaders: false,

    message: {
        success: false,
        code: 'TOO_MANY_REQUESTS'
    }
});

// Reset password
const resetPasswordLimiter = rateLimit({
    windowMs: 15 * 60 * 1000,
    max: 10,

    standardHeaders: true,
    legacyHeaders: false,

    message: {
        success: false,
        code: 'TOO_MANY_REQUESTS'
    }
});


const transporter = nodemailer.createTransport({

    service: 'gmail',

    auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.EMAIL_PASS
    }
});

function generateOTP() {

    return crypto
        .randomInt(0, 10000)
        .toString()
        .padStart(4, '0');
}

function hashValue(value) {

    return crypto
        .createHash('sha256')
        .update(value)
        .digest('hex');
}

/*
 * Token criptográficamente seguro.
 */
function generateResetToken() {

    return crypto
        .randomBytes(32)
        .toString('hex');
}

/*
 * Comparación resistente a timing attacks.
 */
function safeCompareHex(valueA, valueB) {

    try {

        const bufferA =
            Buffer.from(valueA, 'hex');

        const bufferB =
            Buffer.from(valueB, 'hex');

        if (
            bufferA.length !==
            bufferB.length
        ) {
            return false;
        }

        return crypto.timingSafeEqual(
            bufferA,
            bufferB
        );

    } catch (error) {

        return false;
    }
}

function normalizeEmail(email) {

    return String(email)
        .trim()
        .toLowerCase();
}

function isValidEmail(email) {

    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        .test(email);
}

function isValidOtp(otp) {

    return /^\d{4}$/.test(otp);
}

function isValidPassword(password) {

    return (
            typeof password === 'string' &&
            password.length >= 8 &&
            password.length <= 128 &&
            /[A-Z]/.test(password) &&
            /[0-9]/.test(password)
        );
}

async function saveOtp(email, otpHash) {

    const documentId = hashValue(email);

    const expiration =
        Date.now() + OTP_EXPIRATION_MS;

    await db
        .collection('password_otps')
        .doc(documentId)
        .set({email,otpHash,attempts: 0,expiration,
           createdAt:
                admin.firestore.FieldValue
                    .serverTimestamp()

        });
}

async function getOtp(email) {

    const documentId = hashValue(email);

    const doc = await db
        .collection('password_otps')
        .doc(documentId)
        .get();

    if (!doc.exists) {

        return null;
    }

    return {

        id: doc.id,

        ...doc.data()

    };
}

async function registerOtpAttempt(email) {

    const documentId = hashValue(email);

    const otpRef = db
        .collection('password_otps')
        .doc(documentId);

    return db.runTransaction(async (transaction) => {

        const doc = await transaction.get(otpRef);

        if (!doc.exists) {
            return {
                exists: false,
                attempts: 0
            };
        }

        const data = doc.data();

        const attempts = data.attempts || 0;

        if (attempts >= MAX_OTP_ATTEMPTS) {

            transaction.delete(otpRef);

            return {
                exists: true,
                blocked: true,
                attempts
            };
        }

        const newAttempts = attempts + 1;

        transaction.update(otpRef, {
            attempts: newAttempts
        });

        return {
            exists: true,
            blocked: false,
            attempts: newAttempts
        };
    });
}

async function deleteOtp(email) {

    const documentId = hashValue(email);

    await db
        .collection('password_otps')
        .doc(documentId)
        .delete();
}



async function createResetChallenge(
    userId,
    email,
    tokenHash
) {

    const challengeId =
        crypto.randomUUID();

    const expiration =
        Date.now() +
        RESET_TOKEN_EXPIRATION_MS;

    await db
        .collection('password_reset_tokens')
        .doc(challengeId)
        .set({userId,email,tokenHash,expiration,used: false,attempts: 0,
            createdAt:
                admin.firestore.FieldValue
                    .serverTimestamp()

        });

    return challengeId;
}

async function getResetChallenge(
    challengeId
) {

    const doc = await db
        .collection('password_reset_tokens')
        .doc(challengeId)
        .get();

    if (!doc.exists) {

        return null;
    }

    return {

        id: doc.id,

        ...doc.data()

    };
}

async function deleteResetChallenge(
    challengeId
) {

    await db
        .collection('password_reset_tokens')
        .doc(challengeId)
        .delete();
}

app.post(
    '/send-otp',
    sendOtpLimiter,
    async (req, res) => {

        try {

            let { email } = req.body;

            if (
                !email ||
                typeof email !== 'string'
            ) {

                return res.status(400).json({

                    success: false,

                    code: 'INVALID_REQUEST'

                });
            }

            email = normalizeEmail(email);

            if (!isValidEmail(email)) {

                return res.status(400).json({

                    success: false,

                    code: 'INVALID_REQUEST'

                });
            }

            /*
             * Primero comprobamos si existe el usuario.
             *
             * IMPORTANTE:
             * Externamente no revelaremos si existe.
             */

            let userExists = true;

            try {

                await admin
                    .auth()
                    .getUserByEmail(email);

            } catch (error) {

                if (
                    error.code ===
                    'auth/user-not-found'
                ) {

                    userExists = false;

                } else {

                    throw error;
                }
            }

            /*
             * Para evitar enumeración de usuarios:
             *
             * si no existe, respondemos igual.
             */

            if (!userExists) {

                return res.status(200).json({

                    success: true,

                    message:
                        'Si el correo está registrado, recibirás un código.'

                });
            }

            /*
             * Generar OTP.
             */

            const otp =
                generateOTP();

            const otpHash =
                hashValue(otp);

            /*
             * Un único OTP activo.
             */

            await saveOtp(
                email,
                otpHash
            );

            /*
             * Enviar correo.
             */

            await transporter.sendMail({

                from:
                    process.env.EMAIL_USER,

                to: email,

                subject:
                    'Código de recuperación de contraseña',

                text:
                    `Tu código de recuperación es: ${otp}. ` +
                    `Es válido durante 10 minutos. ` +
                    `Si tú no solicitaste este código, ignora este mensaje.`

            });

            return res.status(200).json({

                success: true,

                message:
                    'Si el correo está registrado, recibirás un código.'

            });

        } catch (error) {

            console.error(
                'SEND_OTP_ERROR:',
                error?.code || 'UNKNOWN_ERROR'
            );


            return res.status(500).json({

                success: false,

                code: 'INTERNAL_SERVER_ERROR'

            });
        }
    }
);

async function registerResetTokenAttempt(challengeId) {

    const challengeRef = db
        .collection('password_reset_tokens')
        .doc(challengeId);

    return db.runTransaction(async (transaction) => {

        const doc = await transaction.get(challengeRef);

        if (!doc.exists) {

            return {
                exists: false
            };
        }

        const data = doc.data();

        const attempts =
            data.attempts || 0;

        if (
            attempts >=
            MAX_RESET_TOKEN_ATTEMPTS
        ) {

            transaction.delete(challengeRef);

            return {
                exists: true,
                blocked: true
            };
        }

        const newAttempts =
            attempts + 1;

        transaction.update(
            challengeRef,
            {
                attempts: newAttempts
            }
        );

        return {
            exists: true,
            blocked: false,
            attempts: newAttempts
        };
    });
}

app.post(
    '/verify-otp',
    verifyOtpLimiter,
    async (req, res) => {

        try {

            let {email,
                otp
            } = req.body;

            if (
                !email ||
                typeof email !== 'string' ||
                !otp
            ) {

                return res.status(400).json({

                    success: false,

                    code: 'INVALID_REQUEST'

                });
            }

            email =
                normalizeEmail(email);

            otp =
                String(otp).trim();

            if (
                !isValidEmail(email) ||
                !isValidOtp(otp)
            ) {

                return res.status(400).json({

                    success: false,

                    code: 'INVALID_REQUEST'

                });
            }

            /*
             * Buscar OTP.
             */

            const record =
                await getOtp(email);

            if (!record) {

                return res.status(400).json({

                    success: false,

                    code: 'OTP_INVALID'

                });
            }

            /*
             * Comprobar expiración.
             */

            if (
                Date.now() >
                record.expiration
            ) {

                await deleteOtp(email);

                return res.status(400).json({

                    success: false,

                    code: 'OTP_EXPIRED'

                });
            }

            const attemptResult =
                await registerOtpAttempt(email);

            if (!attemptResult.exists) {

                return res.status(400).json({

                    success: false,

                    code: 'OTP_INVALID'
                });
            }

            if (attemptResult.blocked) {

                return res.status(429).json({

                    success: false,

                    code: 'OTP_TOO_MANY_ATTEMPTS'
                });
            }

            /*
             * Hash del OTP recibido.
             */

            const inputHash = hashValue(otp);

            /*
             * Comparación segura.
             */

            const valid =
                safeCompareHex(
                    inputHash,
                    record.otpHash
                );

            /*
             * OTP incorrecto.
             */

            if (!valid) {

                return res.status(400).json({

                    success: false,

                    code: 'OTP_INVALID'

                });
            }

            /*
             * OTP correcto
             * Primero obtenemos el usuario.
             */

            let user;

            try {

                user =
                    await admin
                        .auth()
                        .getUserByEmail(email);

            } catch (error) {

                console.error(
                    'GET_USER_AFTER_OTP_ERROR:',
                    error?.code || 'UNKNOWN_ERROR'
                );


                await deleteOtp(email);

                return res.status(400).json({

                    success: false,

                    code: 'OTP_INVALID'

                });
            }

            /*
             * OTP de un solo uso.
             */

            await deleteOtp(email);

            /*
             * Crear token de recuperación.
             */

            const resetToken =
                generateResetToken();

            const resetTokenHash =
                hashValue(resetToken);

            const challengeId =
                await createResetChallenge(
                    user.uid,
                    email,
                    resetTokenHash
                );

            /*
             * IMPORTANTE:
             *
             * El challengeId y resetToken juntos
             * permiten autorizar el siguiente paso.
             */

            return res.status(200).json({

                success: true,

                verified: true,

                resetToken,

                challengeId

            });

        } catch (error) {

            console.error(
                'VERIFY_OTP_ERROR:',
                error?.code || 'UNKNOWN_ERROR'
            );


            return res.status(500).json({

                success: false,

                code: 'INTERNAL_SERVER_ERROR'

            });
        }
    }
);

app.post('/reset-password',resetPasswordLimiter,
    async (req, res) => {

        try {

            const {
                challengeId,
                resetToken,
                password
            } = req.body;

            /*
             * Validación básica.
             */

            if (
                !challengeId ||
                typeof challengeId !== 'string' ||
                !resetToken ||
                typeof resetToken !== 'string' ||
                !password ||
                typeof password !== 'string'
            ) {

                return res.status(400).json({

                    success: false,

                    code: 'INVALID_REQUEST'

                });
            }

            /*
             * Validar contraseña en servidor.
             */

            if (!isValidPassword(password)) {

                return res.status(400).json({

                    success: false,

                    code: 'INVALID_PASSWORD'

                });
            }

            /*
             * Buscar challenge.
             */

            const challenge = await getResetChallenge(
                    challengeId
                );

            if (!challenge) {

                return res.status(401).json({

                    success: false,

                    code:
                        'INVALID_RESET_TOKEN'
                });
            }

            if (challenge.used === true) {

                return res.status(401).json({

                    success: false,

                    code:
                        'RESET_TOKEN_ALREADY_USED'
                });
            }

            if (
                Date.now() >
                challenge.expiration
            ) {

                await deleteResetChallenge(
                    challengeId
                );

                return res.status(401).json({

                    success: false,

                    code:
                        'RESET_TOKEN_EXPIRED'
                });
            }

            const attemptResult =
                await registerResetTokenAttempt(
                    challengeId
                );

            if (!attemptResult.exists) {

                return res.status(401).json({

                    success: false,

                    code:
                        'INVALID_RESET_TOKEN'
                });
            }

            if (attemptResult.blocked) {

                return res.status(429).json({

                    success: false,

                    code:
                        'RESET_TOKEN_TOO_MANY_ATTEMPTS'
                });
            }

            const tokenHash =
                hashValue(resetToken);

            const validToken =
                safeCompareHex(
                    tokenHash,
                    challenge.tokenHash
                );

            if (!validToken) {

                return res.status(401).json({

                    success: false,

                    code:
                        'INVALID_RESET_TOKEN'
                });
            }


            await admin.auth().updateUser(
                challenge.userId,
                    {
                        password
                    }
                );

            await deleteResetChallenge(
                challengeId
            );

            /*
             * Respuesta.
             */

            return res.status(200).json({

                success: true,

                message:
                    'Contraseña actualizada correctamente'

            });

        } catch (error) {

            console.error(
                'RESET_PASSWORD_ERROR:',
                error?.code || 'UNKNOWN_ERROR'
            );


            return res.status(500).json({

                success: false,

                code:
                    'PASSWORD_RESET_ERROR'

            });
        }
    }
);

app.use(
    (error, req, res, next) => {

        if (
            error instanceof SyntaxError &&
            error.status === 400 &&
            'body' in error
        ) {

            return res.status(400).json({

                success: false,

                code: 'INVALID_JSON'

            });
        }

        console.error(
            'UNHANDLED_ERROR:',
            error?.code || 'UNKNOWN_ERROR'
        );

        return res.status(500).json({

            success: false,

            code:
                'INTERNAL_SERVER_ERROR'

        });
    }
);

app.listen(PORT,'0.0.0.0',() => {

    console.log(
            `Servidor escuchando en puerto ${PORT}`
        );

    }
);
