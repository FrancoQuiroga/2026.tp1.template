# Documentación de Arquitectura y Decisiones de Diseño - BiblioTech

Este documento explica las decisiones técnicas tomadas durante el desarrollo del núcleo del sistema BiblioTech, fundamentadas en principios SOLID, el uso de Java moderno y las mejores prácticas de la industria.

## 1. Arquitectura General del Proyecto

Para garantizar el bajo acoplamiento y facilitar el mantenimiento, el sistema fue diseñado bajo una arquitectura de capas estricta. El flujo de dependencias sigue este modelo unidireccional:

    +----------------+      +-------------------+      +----------------------+      +----------------+
    |                |      |                   |      |                      |      |                |
    |   Main (CLI)   | ---> |     Services      | ---> |     Repositories     | ---> |     Models     |
    | (Presentación) |      | (Reglas de Neg.)  |      |  (Acceso a Datos)    |      | (Entidades)    |
    |                |      |                   |      |                      |      |                |
    +----------------+      +-------------------+      +----------------------+      +----------------+
            ^                                                      |
            |               +-------------------+                  |
            +-------------> |  InMemory Impls   | -----------------+
           (Inyección)      | (Base de Datos)   |     (Implementación de Interfaz)
                            +-------------------+

* **Main:** Actúa como punto de entrada y orquestador. Es el único componente que conoce las implementaciones concretas en memoria para poder inyectarlas.
* **Services:** Contienen la lógica pura. Solo conocen las *interfaces* de los repositorios, nunca sus implementaciones concretas.
* **Repositories:** Definen los contratos de almacenamiento. Solo conocen los *Models* que deben guardar o buscar.
* **Models:** Son el núcleo puro de los datos y no dependen de ninguna otra capa.

## 2. Capa de Modelado (Models)

El objetivo en esta capa fue representar las entidades del dominio de forma segura, inmutable y aprovechando las características recientes de Java.

* **Uso de Interfaces para Polimorfismo:** Se crearon las interfaces `Recurso` y `Socio` para agrupar bajo un mismo contrato a los distintos tipos de libros y usuarios.
* **Implementación con Records:** Se utilizaron `records` (`LibroFisico`, `Ebook`, `Estudiante`, `Docente`) porque representan un estado inmutable de los datos (DTOs). Garantizan que los datos no sean alterados accidentalmente.
* **Validación en Constructores Compactos:** Las validaciones inherentes a los datos, como asegurar un DNI único o un email con formato correcto, se aislaron dentro de los constructores de los `records`.

## 3. Capa de Acceso a Datos (Repositories)

El diseño se centró fuertemente en el Desacoplamiento y la Segregación de Interfaces (SOLID).

### Estructura de Archivos
La jerarquía de esta capa se organizó separando estrictamente los contratos (interfaces) de sus implementaciones físicas:

    src/main/java/com/bibliotech/repository/
    ├── (interfaces)
    │   ├── Repository.java                 # Interfaz genérica base (CRUD)
    │   ├── RecursoRepository.java          # Contrato específico para inventario
    │   ├── SocioRepository.java            # Contrato específico para usuarios
    │   └── PrestamoRepository.java         # Contrato específico para transacciones
    └── (InMemory/Implementaciones Particulares)
        ├── InMemoryRecursoRepository.java  # Implementación concreta (Map)
        ├── InMemorySocioRepository.java    # Implementación concreta (Map)
        └── InMemoryPrestamoRepository.java # Implementación concreta (Map)

* **Interfaz Genérica `Repository<T, ID>`:** Se creó un contrato base para centralizar las operaciones CRUD, evitando duplicar código en las interfaces particulares.
* **Uso de Optional:** Para métodos de búsqueda, se adoptó el uso de `Optional`, previniendo explícitamente los `NullPointerException`.
* **Diccionarios Concurrentes (`ConcurrentHashMap`):** Garantizan búsquedas inmediatas por clave (ID/DNI) y preparan el sistema para entornos multi-hilo.
* **Generación de IDs de Inventario:** El repositorio asigna un `Integer` auto-incremental a los `Recursos` físicos. Esto resolvió el problema de tener múltiples copias físicas idénticas de un mismo libro (mismo ISBN).

## 4. Capa de Lógica de Negocio (Services)

La capa de servicios actúa como el cerebro del sistema, aplicando las reglas de la biblioteca.

* **Inyección de Dependencias:** Los servicios exigen por constructor las *interfaces* de los repositorios. Esto cumple con el Principio de Inversión de Dependencias (DIP). Si a futuro se requiere una persistencia distinta (ej: base de datos SQL o JSON), la lógica del servicio no sufrirá ninguna modificación.
* **Separación de Responsabilidades:** No se utilizó una interfaz genérica de "Servicio". Cada servicio (`SocioService`, `PrestamoService`) define sus propios métodos acordes a sus reglas de negocio particulares, respetando los principios SOLID.

## 5. Manejo de Errores y Excepciones

Se abandonó el uso de excepciones genéricas (`RuntimeException`) para adoptar un enfoque estructurado y específico.

* **Jerarquía Personalizada:** Se implementó una excepción raíz de negocio, de la cual derivan excepciones específicas como `SocioException`, `RecursoException` y `PrestamoException`.
* **Delegación de Errores:** El Modelo lanza errores de formato; el Servicio los transforma, junto con las infracciones de reglas de negocio, en excepciones de la jerarquía personalizada; el `Main` (Presentación) es el único que utiliza `try-catch` para interactuar con el usuario sin romper el flujo de ejecución.