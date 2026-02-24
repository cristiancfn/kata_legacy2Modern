# Cloud | Legacy2Modern - Motor de Migración de Código Legacy

## 🎯 Objetivo del Proyecto
Este proyecto implementa una prueba de concepto (PoC) funcional de un motor de migración asistida diseñado para transformar código legacy (ej. COBOL) a tecnologías modernas (Java 24 / Node.js). El desarrollo se centra fuertemente en la mantenibilidad arquitectónica, la escalabilidad y la seguridad, cumpliendo con las mejores prácticas de la Ingeniería de Software.

---

## 🏗️ Arquitectura de Software y Patrones de Diseño

La solución backend ha sido diseñada siguiendo los principios de la **Arquitectura Limpia (Clean Architecture)** o de Puertos y Adaptadores (Hexagonal), aislando completamente la lógica de dominio (el motor de traducción) del framework (Spring Boot) y de la capa de presentación (Angular 17+).

### 1. Patrón Strategy (El Core del Motor)
Para evitar un acoplamiento rígido y cumplir con el Principio Abierto/Cerrado (OCP) de SOLID, se implementó el patrón Strategy.
* **`LegacyParser` (Puerto de Entrada):** Define el contrato para leer código fuente. Actualmente implementado por `CobolParser`. 
* **`ModernGenerator` (Puerto de Salida):** Define el contrato para emitir código moderno. Actualmente implementado por `JavaGenerator` y `NodeJsGenerator`. La adición de Node.js se realizó sin modificar el núcleo del motor, demostrando la alta extensibilidad del sistema.

### 2. Árbol de Sintaxis Abstracta (AST) Simplificado
El motor **no** realiza traducciones literales (texto a texto). El `LegacyParser` actúa como un analizador léxico/sintáctico que transforma el código COBOL en un modelo intermedio neutral (Nodos AST como `IfStatement`, `VariableDeclarationNode`). Los generadores modernos leen este AST y emiten el código destino. 

---

## 📊 Estándares de Calidad (ISO/IEC 25010)



El diseño arquitectónico responde directamente a los atributos de calidad del estándar ISO 25010:

* **Mantenibilidad (Modularidad):** La separación estricta entre Frontend (CSR en Angular) y Backend (API REST en Spring Boot) permite evolucionar y escalar cada componente de forma independiente.

* **Fiabilidad (Madurez y Tolerancia a fallos):** El motor implementa un manejador global de excepciones (`GlobalExceptionHandler`) que captura errores de validación o lenguajes no soportados, devolviendo respuestas HTTP semánticamente correctas sin exponer el stack trace al usuario.



---

## 🔒 Seguridad y Mitigación de Riesgos

Atendiendo a las políticas de seguridad de la organización y a lecciones aprendidas de incidentes de la industria, se aplicaron las siguientes directrices:

1. **Prevención de Fuga de Credenciales (Secrets Leakage):**
   * El proyecto mantiene una política de *Zero Secrets*. Se implementó un `.gitignore` estricto y no existen credenciales ni IPs quemadas (hardcoded) en el código fuente. El enrutamiento se maneja dinámicamente mediante variables de entorno.
2. **Prevención de Ataques DoS a nivel de Aplicación:**
   * **Riesgo:** Un actor malintencionado podría enviar un payload gigantesco de código fuente para saturar la memoria del analizador léxico.
   * **Mitigación:** Se implementó validación de entrada estricta mediante `jakarta.validation.constraints` para truncar payloads masivos antes de que alcancen la capa de dominio.
3. **Prevención de Inyección de Código (Code Injection):**
   * El AST actúa como una capa de sanitización; cualquier instrucción no reconocida por las expresiones regulares seguras es catalogada y reportada como un *Warning*, sin ser ejecutada jamás por el servidor.

---

## ☁️ Arquitectura de Despliegue Cloud (PaaS Desacoplado)

Para superar restricciones de firewalls corporativos y garantizar la escalabilidad asimétrica, se optó por una arquitectura *Cloud-Native* distribuida:

* **Frontend (CDN):** Desplegado en **Vercel**. Se sirven únicamente archivos estáticos. Se configuró un proxy inverso (`vercel.json`) que intercepta las peticiones `/api` y las redirige de forma transparente al backend. Esto elimina problemas de CORS y oculta la URL real de la API.
* **Backend (Compute):** Desplegado en **Render**. Se utiliza un *Multi-stage build* de Docker (con Java 24) para compilar el artefacto y ejecutarlo en una imagen JRE ultraligera bajo un usuario sin privilegios.



---

## 🚀 Instrucciones de Ejecución

El proyecto está configurado con **Variables de Entorno Dinámicas**, lo que permite ejecutarlo localmente o en la nube sin necesidad de modificar el código fuente.

### Opción A: Despliegue en Vivo (Demo Cloud)
Puedes acceder a la plataforma funcional directamente desde cualquier red corporativa a través del siguiente enlace:
* **UI (Vercel):** `https://kata-legacy2-modern.vercel.app/`
*(Nota: Al estar el backend en la capa gratuita de Render, la primera petición puede tardar hasta 50 segundos en despertar el contenedor. Las peticiones subsecuentes serán instantáneas).*

### Opción B: Ejecución Local (Entorno de Desarrollo)

**Requisitos previos:** Java 24, Maven, Node.js (v20+) y Angular CLI.

**1. Levantar el Backend (Spring Boot):**
```bash
cd migration-engine
mvn clean install
mvn spring-boot:run
```

**2. Levantar el Frontend (Angular):**
```bash
cd frontend-ui
npm install
ng serve
```

El frontend estará disponible en `http://localhost:4200`

**Nota Arquitectónica:** Al ejecutar ng serve, Angular utiliza environment.development.ts y el archivo proxy.conf.json para enrutar internamente las peticiones hacia el puerto 8080 local, replicando el comportamiento del proxy de Vercel en la nube.

**Documentación generada de arquitectura para este reto: ** https://drive.google.com/drive/folders/1P94ZeQrY37Fy0WBWpjlOgSqrQvpmqajh?usp=sharing