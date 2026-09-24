


[![Android](https://img.shields.io/badge/Platform-Android-green)](https://developer.android.com/) 
[![Status](https://img.shields.io/badge/Status-Desarrollo-yellow)] 
[![GitHub stars](https://img.shields.io/github/stars/ague1/tienda-online?style=social)](https://github.com/ague1/tienda-online/stargazers)


https://github.com/user-attachments/assets/bdf70a65-4fbc-4cc9-b9f6-2b59ef3da7d3


*Tu mercadito digital en la palma de tu mano*

---

# Aplicación Android de Compras

Aplicación móvil Android para gestionar un flujo completo de compra, desde la autenticación del usuario y exploración de productos hasta el carrito, checkout y seguimiento de pedidos.

El proyecto utiliza Firebase, Hilt, ViewModel, LiveData y comunicación HTTP para integrar los diferentes servicios de la aplicación.La solución está organizada como un monolito modular, aplicando principios de Clean Architecture y Hexagonal Architecture para mantener separadas las responsabilidades de dominio, aplicación, infraestructura y presentación.

---

## Objetivo

El objetivo del proyecto es construir una aplicación de comercio electrónico para Android que permita a los usuarios:

* Crear una cuenta e iniciar sesión.
* Recuperar el acceso mediante un flujo de OTP.
* Consultar productos y categorías.
* Buscar productos.
* Consultar promociones.
* Agregar productos al carrito.
* Modificar cantidades y eliminar productos.
* Gestionar información de perfil.
* Realizar un proceso de checkout.
* Crear y consultar pedidos.
* Consultar el estado y la evolución de un pedido.

La prioridad del proyecto es combinar una experiencia de compra funcional con una base de código preparada para continuar creciendo y mantenerse a largo plazo.

---

##  Funcionalidades

### Autenticación

* Inicio de sesión.
* Registro de usuarios.
* Cierre de sesión.
* Validación de credenciales.
* Manejo de errores de autenticación.
* Recuperación de contraseña mediante OTP.
* Verificación de código OTP.
* Restablecimiento de contraseña.

La autenticación principal utiliza Firebase Authentication.

---

###  Productos

* Listado de productos.
* Consulta por categorías.
* Búsqueda de productos.
* Visualización de promociones.
* Actualización de precios promocionales.
* Paginación y navegación de productos.
* Integración con Firestore.

---

###  Carrito

El carrito mantiene el estado de los productos seleccionados y permite:

* Agregar productos.
* Aumentar cantidades.
* Disminuir cantidades.
* Establecer cantidades específicas.
* Eliminar productos.
* Vaciar el carrito.
* Calcular el total.
* Sincronizar el carrito con Firestore.

El carrito está diseñado para mantener el estado independientemente del ciclo de vida de cada `Fragment`, evitando perder información al navegar entre pantallas.

---

### Pedidos

El flujo de pedidos permite:

* Crear pedidos desde el checkout.
* Asociar productos y cantidades al pedido.
* Registrar información de entrega.
* Consultar pedidos por estado.
* Escuchar cambios en tiempo real.
* Consultar el detalle de un pedido.
* Visualizar la línea temporal del pedido.

Estados utilizados actualmente:

```text
pending
processing
confirmed
delivered
cancelled
```

La aplicación también mantiene información temporal del progreso del pedido, por ejemplo:

```text
placed
pending
confirmed
processing
delivered
```

---

###  Perfil

El usuario puede gestionar la información asociada a su perfil, incluyendo los datos utilizados durante el proceso de compra.

---

##  Arquitectura

El proyecto está basado en: **Clean Architecture + Hexagonal Architecture + Monolito Modular**

La intención es mantener separadas las responsabilidades de cada parte del sistema y evitar que las reglas de negocio dependan directamente de Android, Firebase, Firestore u otras tecnologías externas.

La organización principal de cada funcionalidad sigue el siguiente esquema:

```text
feature/
│
├── domain/
│   ├── model/
│   └── port/
│
├── application/
│   └── usecase/
│
├── infrastructure/
│   ├── datasource/
│   └── repository/
│
└── presentation/
    ├── activity/
    ├── fragment/
    ├── adapter/
    └── viewmodel/
```

### Domain

Contiene las reglas y conceptos principales del negocio.

```text
domain/
├── model/
└── port/
```

Esta capa no debería depender de:

* Android.
* Firebase.
* Firestore.
* OkHttp.
* Hilt.
* APIs específicas de infraestructura.

---

### Application

Contiene los casos de uso de la aplicación.

```text
application/
└── usecase/
```

Ejemplos:

```text
LoginUseCase
SignupUseCase
SendOtpUseCase
VerifyOtpUseCase
ResetPasswordUseCase

CreateOrderUseCase
ListenOrderUseCase
ProcessPaymentUseCase
```

Los casos de uso coordinan las operaciones necesarias para ejecutar una acción de negocio.

---

### Infrastructure

Contiene las implementaciones técnicas necesarias para comunicarse con servicios externos.

```text
infrastructure/
├── datasource/
└── repository/
```

Actualmente incluye integraciones con:

* Firebase Authentication.
* Firebase Firestore.
* APIs HTTP.
* Persistencia remota.
* Mapeadores y documentos específicos de infraestructura.

La infraestructura implementa los contratos definidos por el dominio.

---

### Presentation

Contiene la interfaz de usuario y su lógica de presentación.

```text
presentation/
├── activity/
├── fragment/
├── adapter/
├── listener/
└── viewmodel/
```

Los `ViewModel` coordinan el estado de la interfaz y consumen los casos de uso correspondientes.

---

## Principio Hexagonal

La comunicación entre el dominio y la infraestructura se realiza mediante puertos y adaptadores.

Por ejemplo:

```text
                DOMAIN
                  │
                  │
          CartRepository
                  │
                 ▲
                 │ implements
                 │
        CartRepositoryImpl
                  │
                  ▼
          CartDataSource
                  │
                  ▼
              Firestore
```

Esto permite cambiar una implementación de infraestructura sin modificar las reglas principales del negocio.

---

## Estructura del proyecto

La estructura actual se organiza por funcionalidades:

```text
com.example.myapplication
│
├── MyApplication.java
│
├── core/
│   ├── network/
│   ├── scheduler/
│   └── ui/
│
├── di/
│   ├── FirebaseModule.java
│   ├── NetworkModule.java
│   ├── RepositoryModule.java
│   ├── DataSourceModule.java
│   └── SchedulerModule.java
│
└── features/
    │
    ├── main/
    │
    ├── auth/
    │
    ├── cart/
    │
    ├── product/
    │
    ├── order/
    │
    └── profile/
```

Esta organización permite que cada funcionalidad tenga sus propias responsabilidades y facilita una futura separación en módulos Gradle cuando el proyecto lo requiera.

---

## Tecnologías utilizadas

### Android

* Java
* Android SDK
* AndroidX
* AppCompat
* Fragments
* ViewModel
* LiveData
* ViewPager2
* Navigation Component

### Arquitectura y dependencias

* Clean Architecture
* Hexagonal Architecture
* Monolito Modular
* Hilt / Dagger
* Repository Pattern
* Use Case Pattern

### Backend y persistencia

* Firebase Authentication
* Firebase Firestore
* OkHttp
* API HTTP para recuperación de contraseña mediante OTP

### Herramientas

* Android Studio
* Gradle
* Git / GitHub

---

## Inyección de dependencias

La aplicación utiliza Hilt para administrar dependencias.

Los módulos principales se concentran en:

```text
di/
├── FirebaseModule
├── NetworkModule
├── RepositoryModule
├── DataSourceModule
└── SchedulerModule
```

Las interfaces de dominio se conectan con sus implementaciones de infraestructura mediante estos módulos.

Ejemplo:

```text
CartRepository
      ↓
CartRepositoryImpl
```

y:

```text
CartDataSource
      ↓
CartDataSourceImpl
```

---

## Comunicación HTTP

El flujo de recuperación de contraseña utiliza una API externa para el envío y validación del OTP.

Conceptualmente:

```text
ForgotPassword
       ↓
SendOtpUseCase
       ↓
OtpRepository
       ↓
OtpRepositoryImpl
       ↓
OtpDataSource
       ↓
HTTP API
```

Los endpoints se mantienen centralizados para evitar URLs dispersas dentro de la aplicación.

---

## ⚙️ Configuración

Antes de ejecutar el proyecto es necesario configurar los servicios externos utilizados por la aplicación.

### Firebase

Configurar el proyecto de Firebase y agregar:

```text
google-services.json
```

en el módulo correspondiente de Android.

Los servicios utilizados actualmente incluyen:

```text
Firebase Authentication
Firebase Firestore
```

### API de OTP

La URL de la API se configura mediante la configuración de red de la aplicación.

La aplicación utiliza una constante centralizada para el endpoint base:

```text
BuildConfig.BASE_URL
```

Esto evita colocar directamente URLs de backend dentro de las clases de negocio.

---

## Ejecución

### 1. Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
```

### 2. Abrir el proyecto

Abrir el proyecto desde Android Studio.

### 3. Configurar Firebase

Agregar la configuración de Firebase correspondiente al proyecto.

### 4. Configurar la API

Configurar la URL utilizada por la API de recuperación de contraseña.

### 5. Ejecutar

Sincronizar Gradle y ejecutar la aplicación en un dispositivo físico o emulador Android.

---

## Estado actual

El proyecto actualmente cuenta con un MVP funcional y continúa en proceso de evolución.

### Implementado

* [x] Registro de usuarios
* [x] Inicio de sesión
* [x] Cierre de sesión
* [x] Recuperación de contraseña mediante OTP
* [x] Consulta de productos
* [x] Categorías
* [x] Búsqueda
* [x] Promociones
* [x] Carrito persistente
* [x] Checkout
* [x] Creación de pedidos
* [x] Consulta de pedidos
* [x] Seguimiento del estado del pedido
* [x] Perfil de usuario
* [x] Integración con Firebase
* [x] Inyección de dependencias con Hilt

### En desarrollo

* [ ] Centralizar manejo de errores.
* [ ] Incorporar pruebas automatizadas para los casos críticos.
* [ ] Completar la separación de responsabilidades entre autenticación y recuperación de contraseña.
* [ ] Evaluar la conversión a módulos Gradle independientes.

---

## Decisiones arquitectónicas

### El dominio no conoce Firebase

Firebase pertenece a infraestructura:

```text
Domain
   ✕ Firebase

Infrastructure
   ✓ Firebase
```

### Los repositorios son contratos

Por ejemplo:

```java
public interface CartRepository {
    ...
}
```

representa un puerto del dominio.

La implementación concreta pertenece a infraestructura:

```text
CartRepositoryImpl
```

### La UI no accede directamente a Firestore

El flujo esperado es:

```text
Fragment
   ↓
ViewModel
   ↓
UseCase
   ↓
Repository
   ↓
DataSource
   ↓
Firebase / API
```

Esto permite mantener una separación clara entre interfaz, lógica de aplicación y tecnología externa.

---

##  Consideraciones de seguridad

La aplicación está diseñada para evitar colocar lógica sensible directamente en la interfaz de usuario.

Entre las medidas aplicadas se consideran:

* Autenticación mediante Firebase Authentication.
* Separación entre UI y acceso a datos.
* Centralización de endpoints.
* Uso de repositorios para acceso a servicios externos.
* Validación de datos antes de procesarlos.
* Separación del flujo de recuperación de contraseña respecto a la autenticación normal.

Las credenciales, claves y configuraciones sensibles deben mantenerse fuera del código fuente y gestionarse mediante la configuración correspondiente de cada entorno.

---

##  Evolución del proyecto

El proyecto comenzó como una aplicación Android funcional y está evolucionando progresivamente hacia una estructura más mantenible.

La reorganización actual no busca reemplazar todo el código existente, sino establecer límites claros para que nuevas funcionalidades puedan desarrollarse sin aumentar innecesariamente el acoplamiento.

La meta es mantener una aplicación funcional mientras se mejora progresivamente:

```text
Mantenibilidad
      ↓
Testabilidad
      ↓
Escalabilidad
      ↓
Separación de responsabilidades
```

---

Author:
Developed by <b/>Agueda Talavera<b/>.
