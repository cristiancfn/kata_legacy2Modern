\# Cloud | Legacy2Modern - Motor de Migración de Código Legacy



\## 🎯 Objetivo del Proyecto

Este proyecto implementa una prueba de concepto (PoC) funcional de un motor de migración asistida diseñado para transformar código legacy (ej. COBOL) a tecnologías modernas (Java, Node JS). El desarrollo se centra fuertemente en la mantenibilidad arquitectónica, la escalabilidad y la seguridad, cumpliendo con las mejores prácticas de la Ingeniería de Software.



---



\## 🏗️ Arquitectura de Software y Patrones de Diseño



La solución backend ha sido diseñada siguiendo los principios de la \*\*Arquitectura Limpia (Clean Architecture)\*\* o de Puertos y Adaptadores (Hexagonal), aislando completamente la lógica de dominio (el motor de traducción) del framework (Spring Boot) y de la capa de presentación (Angular).



\### 1. Patrón Strategy (El Core del Motor)

Para evitar un acoplamiento rígido y cumplir con el Principio Abierto/Cerrado (OCP) de SOLID, se implementó el patrón Strategy.

\* \*\*`LegacyParser` (Puerto de Entrada):\*\* Define el contrato para leer código fuente. Actualmente implementado por `CobolParser`. Si en el futuro se requiere soportar Delphi, basta con crear un `DelphiParser` sin modificar el motor principal.

\* \*\*`ModernGenerator` (Puerto de Salida):\*\* Define el contrato para emitir código moderno. Actualmente implementado por `JavaGenerator` y por `NodeJsGenerator`. Permite la extensión natural hacia `CGenerator` o `PythonGenerator`, entre otros según se necesite.



\### 2. Árbol de Sintaxis Abstracta (AST) Simplificado

El motor \*\*no\*\* realiza traducciones literales (texto a texto). El `LegacyParser` actúa como un analizador léxico/sintáctico que transforma el código COBOL en un modelo intermedio neutral (Nodos AST como `IfStatement`, `VariableDeclarationNode`). El `ModernGenerator` lee este AST y genera el código destino. Esto garantiza que la lógica de lectura y la de escritura estén 100% desacopladas.



---



\## 📊 Estándares de Calidad (ISO/IEC 25010)



El diseño arquitectónico responde directamente a los atributos de calidad del estándar ISO 25010:

\* \*\*Mantenibilidad (Modularidad):\*\* La separación estricta entre Frontend (CSR en Angular) y Backend (API REST en Spring Boot) permite evolucionar y escalar cada componente de forma independiente.

\* \*\*Fiabilidad (Madurez y Tolerancia a fallos):\*\* El motor implementa un manejador global de excepciones (`GlobalExceptionHandler`) que captura errores de validación o lenguajes no soportados, devolviendo respuestas HTTP semánticamente correctas sin exponer la traza de la pila al usuario.



---



\## 🔒 Seguridad y Mitigación de Riesgos



Atendiendo a las políticas de seguridad de la organización y a las lecciones aprendidas de incidentes previos, se han aplicado las siguientes directrices:



1\. \*\*Prevención de Fuga de Credenciales (Secrets Leakage):\*\*

&nbsp;  \* \*\*Mitigación:\*\* El proyecto se aloja en un repositorio personal aislado. Se implementó un archivo `.gitignore` estricto desde el inicio para evitar la subida de archivos `.env`, carpetas de compilación o cualquier token. No existen credenciales "quemadas" (hardcoded) en el código fuente.

2\. \*\*Prevención de Ataques de Denegación de Servicio (DoS) a nivel de Aplicación:\*\*

&nbsp;  \* \*\*Riesgo:\*\* Un actor malintencionado podría enviar un payload gigantesco de código fuente para saturar la memoria del analizador léxico.

&nbsp;  \* \*\*Mitigación:\*\* Se implementó validación de entrada estricta mediante `jakarta.validation.constraints`. El DTO de entrada limita el tamaño del código fuente (ej. `@Size(max = 10000)`), truncando peticiones masivas antes de que alcancen la capa de dominio.

3\. \*\*Prevención de Inyección de Código (Code Injection):\*\*

&nbsp;  \* \*\*Riesgo:\*\* Al procesar texto introducido por el usuario, existe el riesgo de ejecución de comandos.

&nbsp;  \* \*\*Mitigación:\*\* El motor está diseñado para tratar la entrada estrictamente como datos para el escáner léxico. El AST actúa como una capa de sanitización; cualquier instrucción no reconocida por las expresiones regulares seguras es catalogada y reportada como un \*Warning\*, sin ser ejecutada jamás por el servidor.



---



\## ☁️ Arquitectura de Despliegue Cloud



La solución está preparada para despliegue en entornos Cloud (AWS, Azure, GCP) utilizando un enfoque Cloud-Native:

\* \*\*Contenerización:\*\* Tanto el frontend (servido estáticamente vía Nginx) como el backend (Spring Boot) están empaquetados en contenedores Docker inmutables.

\* \*\*Orquestación Básica:\*\* Se provee un `docker-compose.yml` para levantar todo el stack de manera determinista en cualquier instancia de cómputo (ej. AWS EC2).

\* \*\*Escalabilidad:\*\* El backend es completamente \*stateless\* (sin estado), lo que permite desplegar múltiples réplicas detrás de un balanceador de carga según la demanda.



---



\## 🚀 Instrucciones de Ejecución (Local)



\*(Nota: Esta sección se completará tras configurar los contenedores)\*



Para desplegar la aplicación utilizando Docker Compose, ejecuta en la raíz del proyecto:

```bash

docker-compose up -d --build

