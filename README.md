LashBook v1.0.0
LashBook es una plataforma para la gestión de citas de servicios de pestañas. El proyecto integra un sitio web, un backend con API REST, un buscador independiente, un dashboard administrativo, un widget desarrollado con Flutter Web y una aplicación para smartwatch con Wear OS.
La versión 1.0.0 corresponde a la versión funcional validada del proyecto.

Tecnologías utilizadas
Frontend
•	React
•	Vite
•	React Router
•	JavaScript
•	HTML5
•	CSS3
Backend
•	Java 21
•	Spring Boot
•	Spring Security
•	Spring Data JPA
•	Maven
•	JWT
•	PostgreSQL
Base de datos y almacenamiento
•	Supabase
•	PostgreSQL
•	Supabase Storage
Buscador
•	Elasticsearch 9
Widget
•	Flutter
•	Dart
•	Flutter Web
Wearable
•	Kotlin
•	Wear OS
•	Jetpack Compose
•	Firebase Cloud Messaging
•	Android Studio
________________________________________
Arquitectura general
LashBook está dividido en cuatro módulos principales:
LashBook/
│
├── Back-End/
│   └── lashbook-api/
│
├── Front-End/
│   └── lashbook-web/
│
├── Wearable/
│   └── lashbook-wearable/
│
├── Widgets/
│
└── README.md
El flujo principal del sistema es:
React
   ↓
Spring Boot API
   ↓
PostgreSQL / Supabase
   ↓
Elasticsearch
   ↓
Firebase Cloud Messaging
   ↓
Wear OS
________________________________________
Sitio web
El frontend de LashBook contiene un sitio público y áreas protegidas según el rol del usuario.
Página pública
Incluye:
•	Página de inicio.
•	Navegación.
•	Presentación de LashBook.
•	Carrusel de imágenes.
•	Servicios disponibles.
•	Búsqueda predictiva de servicios.
•	Información de contacto.
•	Formulario de contacto.
•	Redes sociales.
•	WhatsApp.
•	Google Maps.
•	Login.
•	Registro de nuevas clientas.
•	Diseño responsive.
________________________________________
Administración de usuarios
LashBook utiliza autenticación basada en JWT.
Los roles disponibles son:
•	CLIENTA
•	LASHISTA
•	ADMIN
Cada rol tiene acceso únicamente a las funciones autorizadas mediante Spring Security.
El sistema permite:
•	Crear cuentas de clientas.
•	Iniciar sesión.
•	Consultar el perfil autenticado.
•	Proteger rutas del frontend.
•	Proteger endpoints del backend.
•	Administrar sesiones mediante JWT.
________________________________________
Gestión de citas
Las clientas pueden:
•	Reservar citas.
•	Consultar sus citas.
•	Confirmar citas.
•	Cancelar citas.
•	Solicitar reagendamiento.
•	Consultar el historial de movimientos.
Las citas pueden utilizar los siguientes estados:
PENDIENTE
CONFIRMADA
REAGENDAR
CANCELADA
COMPLETADA
Las citas que ya pasaron no pueden ser modificadas por la clienta ni desde el smartwatch.
________________________________________
Historial de citas
Cada cambio realizado sobre una cita queda registrado.
Los posibles orígenes de un cambio incluyen:
WEB_CLIENTA
WEB_ADMIN
WEARABLE
El historial permite conocer:
•	Estado anterior.
•	Estado nuevo.
•	Usuario que realizó el cambio.
•	Fecha y hora.
•	Origen del cambio.
•	Descripción del movimiento.
________________________________________
Panel administrativo
LashBook cuenta con un dashboard para administración.
Desde el panel es posible:
•	Consultar citas.
•	Administrar servicios.
•	Cambiar estados.
•	Reagendar citas.
•	Consultar historiales.
•	Consultar estadísticas.
•	Visualizar el widget Flutter.
________________________________________
Gestión de servicios
La administración puede:
•	Crear servicios.
•	Modificar servicios.
•	Desactivar servicios.
•	Subir imágenes.
•	Consultar servicios.
Las imágenes se almacenan mediante Supabase Storage.
________________________________________
Buscador independiente
LashBook integra Elasticsearch como motor de búsqueda independiente.
El buscador permite realizar búsquedas predictivas de servicios utilizando:
•	Nombre.
•	Descripción.
•	Prefijos de búsqueda.
Ejemplo:
ex
ext
exten
El servicio puede devolver resultados relacionados con extensiones de pestañas.
El índice utilizado es:
servicios_busqueda
________________________________________
Formulario de contacto
El sitio público incluye un formulario de contacto.
Los mensajes enviados desde la web son almacenados en PostgreSQL/Supabase.
La información registrada incluye:
•	Nombre.
•	Correo electrónico.
•	Mensaje.
•	Fecha de creación.
•	Estado de lectura.
________________________________________
Widget Flutter Web
LashBook incorpora un widget interactivo desarrollado con Flutter Web.
El widget está integrado dentro del panel administrativo y obtiene estadísticas reales desde el backend.
Endpoint utilizado:
GET /api/admin/estadisticas
El widget muestra:
•	Total de citas.
•	Citas pendientes.
•	Citas confirmadas.
•	Citas completadas.
•	Citas canceladas.
•	Citas por reagendar.
•	Ingresos acumulados.
•	Distribución gráfica por estado.
También cuenta con actualización interactiva de datos y una sección que explica la integración con el módulo Wear OS.
________________________________________
Wear OS
LashBook cuenta con una aplicación para smartwatch desarrollada con Kotlin y Jetpack Compose.
La aplicación permite:
•	Iniciar sesión.
•	Crear un NIP de seguridad.
•	Desbloquear acciones mediante NIP.
•	Consultar la próxima cita.
•	Recibir notificaciones.
•	Confirmar una cita.
•	Solicitar reagendamiento.
•	Cancelar una cita.
•	Actualizar información.
•	Cerrar sesión.
________________________________________
Seguridad del Wearable
Las acciones realizadas desde el smartwatch requieren un NIP de cuatro dígitos.
El NIP se utiliza para proteger las operaciones realizadas desde el dispositivo.
Las acciones del reloj son registradas en el historial con:
Origen: WEARABLE
________________________________________
Firebase Cloud Messaging
LashBook utiliza Firebase Cloud Messaging para enviar recordatorios de citas al smartwatch.
Flujo:
Cita
   ↓
Scheduler Spring Boot
   ↓
Firebase Cloud Messaging
   ↓
Wear OS
   ↓
Notificación
   ↓
Confirmar / Reagendar / Cancelar
   ↓
NIP
   ↓
Backend
   ↓
Historial WEARABLE
________________________________________
Recordatorios automáticos
En funcionamiento normal, LashBook envía el recordatorio hasta 24 horas antes de la cita.
Configuración normal:
lashbook.wearable.modo-demo=false
Para fines de demostración académica se incorporó un modo configurable.
Ejemplo:
lashbook.wearable.modo-demo=true
lashbook.wearable.anticipacion-demo-minutos=5
En este modo se conserva exactamente el mismo flujo de:
•	Scheduler.
•	Firebase.
•	Wear OS.
•	NIP.
•	Backend.
•	Historial.
La única diferencia es la anticipación utilizada para facilitar una demostración en tiempo real.
________________________________________
Zona horaria
Para evitar diferencias entre servidores, navegador y smartwatch, LashBook utiliza como referencia:
America/Mexico_City
Esto permite comparar correctamente las fechas y horas de las citas incluso cuando el backend sea desplegado en infraestructura con otra zona horaria.
________________________________________
Pruebas realizadas
Durante el desarrollo se realizaron pruebas funcionales del sistema.
Registro y autenticación
Resultado:
APROBADA
Se comprobó el registro de clientas, inicio de sesión y control por roles.
________________________________________
Creación de citas
Resultado:
APROBADA
Se comprobó la creación de citas y la validación de horarios ocupados.
________________________________________
Historial de citas
Resultado:
APROBADA
Los cambios realizados sobre una cita quedan registrados correctamente.
________________________________________
Buscador Elasticsearch
Resultado:
APROBADA
Se comprobaron búsquedas predictivas utilizando prefijos de nombres y descripciones de servicios.
________________________________________
Formulario de contacto
Resultado:
APROBADA
Se comprobó que los mensajes enviados desde el frontend son almacenados en Supabase.
________________________________________
Widget Flutter
Resultado:
APROBADA
Se comprobó la visualización de estadísticas reales dentro del panel administrativo.
________________________________________
Wear OS
Resultado:
APROBADA
La aplicación fue ejecutada y probada mediante un emulador Wear OS.
________________________________________
Notificación automática Wearable
Resultado:
APROBADA
Se realizó una prueba utilizando el modo demostración de cinco minutos.
Flujo comprobado:
Cita programada
   ↓
5 minutos antes
   ↓
Scheduler
   ↓
Firebase Cloud Messaging
   ↓
Notificación recibida en Wear OS
________________________________________
Acción desde Wear OS
Resultado:
APROBADA
Se comprobó el siguiente flujo:
Notificación
   ↓
Confirmar cita
   ↓
Validación mediante NIP
   ↓
Spring Boot
   ↓
PostgreSQL
   ↓
Historial
El historial registró correctamente:
PENDIENTE → CONFIRMADA
Origen: WEARABLE
________________________________________
Ejecución local
Requisitos
Se requiere:
•	Java 21.
•	Maven.
•	Node.js.
•	npm.
•	Docker.
•	Elasticsearch.
•	Flutter.
•	Android Studio.
•	Emulador Wear OS.
•	PostgreSQL/Supabase.
•	Proyecto Firebase configurado.
________________________________________
Backend
Ubicación:
Back-End/lashbook-api/lashbook-api
Antes de iniciar el backend deben configurarse las variables de entorno necesarias.
Entre ellas:
DB_PASSWORD
JWT_SECRET
SUPABASE_URL
SUPABASE_SECRET_KEY
GOOGLE_APPLICATION_CREDENTIALS
Las credenciales privadas no se almacenan en el repositorio.
Para ejecutar:
mvnw.cmd spring-boot:run
El backend local utiliza:
http://localhost:8080
________________________________________
Elasticsearch
En desarrollo local Elasticsearch se ejecuta mediante Docker.
Puerto:
9200
Comprobación:
http://localhost:9200
________________________________________
Frontend
Ubicación:
Front-End/lashbook-web
Instalar dependencias:
npm install
Ejecutar:
npm run dev
Por defecto:
http://localhost:5173
________________________________________
Widget Flutter
Ubicación:
Widgets
Para ejecutar en Chrome:
flutter run -d chrome
La versión compilada utilizada por React se encuentra dentro de:
Front-End/lashbook-web/public/flutter-widget
________________________________________
Wearable
Ubicación:
Wearable/lashbook-wearable
La aplicación se ejecuta desde Android Studio utilizando un dispositivo o emulador compatible con Wear OS.
El archivo privado:
google-services.json
no se incluye en Git y debe configurarse localmente desde Firebase.
________________________________________
Seguridad y secretos
El repositorio no debe contener:
•	Contraseñas de base de datos.
•	Secretos JWT.
•	Credenciales de Firebase Admin.
•	google-services.json.
•	Archivos .env.
•	Llaves privadas.
•	Tokens FCM.
•	Keystores.
Estos elementos son excluidos mediante .gitignore o configurados usando variables de entorno.
________________________________________
Endpoint de prueba Wearable
Durante el desarrollo se conserva temporalmente un endpoint para realizar pruebas manuales de notificaciones Wearable.
Este endpoint es únicamente para desarrollo y demostración y deberá eliminarse o deshabilitarse antes de una publicación definitiva del sistema.
________________________________________
Control de versiones
Repositorio del proyecto:
https://github.com/pao200/lashbook
La versión estable de entrega se identifica como:
v1.0.0
Incluye:
Back-End/
Front-End/
Wearable/
Widgets/
README.md
________________________________________
________________________________________
Proyecto 
LashBook fue desarrollado como una solución integral que demuestra la integración de tecnologías web, backend, búsqueda independiente, widgets y dispositivos wearable dentro de una misma aplicación.
