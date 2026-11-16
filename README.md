
Cómo ejecutarlo:

    mvn clean spring-boot:run (H2 en memoria por defecto).

    La API estará en http://localhost:8080.

    Credenciales Basic Auth (ejemplo): admin / admin123.

    Documentación: http://localhost:8080/swagger-ui/index.html

Decisiones tomadas:

    Base de datos: Usé H2 para facilitar la ejecución y pruebas sin dependencias externas, pero la configuración de Flyway está lista para cambiar a PostgreSQL solo cambiando el application.properties y la dependencia del driver.

    Seguridad: Opté por Basic Auth por simplicidad, cumpliendo el requisito de proteger escritura.

    Concurrencia: La comprobación de solapamiento se hace en la capa de servicio dentro de una transacción. Para un entorno de alta concurrencia, se podría añadir un bloqueo pesimista (@Lock) u optimista en dependencia.

    Fechas: Usé Instant para manejar zonas horarias de forma consistente.

Qué haría con más tiempo:

    Implementar JWT con refresh tokens.

    Notificaciones (kafka, nats, websocket, etc)

    Auditoría (createdAt, updatedAt).

Cómo usé la IA:

    Qué le pedí: "Genera la configuración de Spring Security para permitir GET público y POST autenticado", "Ayúdame con la consulta JPQL para detectar solapamiento de fechas".

    Qué acepté: La sintaxis de la consulta de solapamiento (start < end AND end > start) y la configuración de SecurityFilterChain.

    Qué corregí: La IA inicialmente sugirió usar @PreAuthorize en los controladores, pero lo cambié a configuración centralizada en SecurityConfig para mayor claridad. También ajusté el manejo de excepciones para que devolviera el formato JSON exacto que necesitaba en lugar de un texto plano.