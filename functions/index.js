/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {setGlobalOptions} = require("firebase-functions");
const {onDocumentWritten} = require("firebase-functions/v2/firestore");
const {onRequest} = require("firebase-functions/https");
const logger = require("firebase-functions/logger");
const admin = require("firebase-admin");


// For cost control, you can set the maximum number of containers that can be
// running at the same time. This helps mitigate the impact of unexpected
// traffic spikes by instead downgrading performance. This limit is a
// per-function limit. You can override the limit for each function using the
// `maxInstances` option in the function's options, e.g.
// `onRequest({ maxInstances: 5 }, (req, res) => { ... })`.
// NOTE: setGlobalOptions does not apply to functions using the v1 API. V1
// functions should each use functions.runWith({ maxInstances: 10 }) instead.
// In the v1 API, each function can only serve one request per container, so
// this will be the maximum concurrent request count.
setGlobalOptions({maxInstances: 10});
admin.initializeApp();

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

exports.normalizeProductName = onDocumentWritten(
    "productos/{productId}",
    async (event) => {
      const after = event.data.after;

      if (!after.exists) {
        return;
      }

      const data = after.data();

      if (!data.nombre) {
        return;
      }

      const keySearch = data.nombre
          .normalize("NFD")
          .replace(/[\u0300-\u036f]/g, "")
          .toLowerCase()
          .trim();

      // Evita escribir nuevamente si ya está correcto.
      if (data.keySearch === keySearch) {
        return;
      }

      await after.ref.update({
        keySearch: keySearch,
      });

      logger.info("Producto normalizado", {
        productId: event.params.productId,
        nombre: data.nombre,
        keySearch: keySearch,
      });
    },
);

exports.populateKeySearch = onRequest(
    async (req, res) => {
      try {
        const snapshot = await admin
            .firestore()
            .collection("productos")
            .get();

        const batch = admin.firestore().batch();

        let updated = 0;

        snapshot.forEach((doc) => {
          const data = doc.data();

          if (!data.nombre) {
            return;
          }

          const keySearch = data.nombre
              .normalize("NFD")
              .replace(/[\u0300-\u036f]/g, "")
              .toLowerCase()
              .trim();

          batch.update(doc.ref, {
            keySearch: keySearch,
          });

          updated++;
        });

        await batch.commit();

        logger.info("keySearch actualizado", {
          productos: updated,
        });

        res.json({
          success: true,
          productosActualizados: updated,
        });
      } catch (error) {
        logger.error("Error actualizando keySearch", error);

        res.status(500).json({
          success: false,
          error: error.message,
        });
      }
    },
);
