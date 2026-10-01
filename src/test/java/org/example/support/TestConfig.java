package org.example.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Properties;

/**
 * Configuración de los tests E2E. Cada valor se busca en este orden:
 *   1. propiedad de sistema     mvn test -Dbrowser=edge
 *   2. variable de entorno      BROWSER=edge
 *   3. archivo                  src/test/resources/test.properties
 *   4. valor por defecto
 */
public final class TestConfig {

    private static final Properties ARCHIVO = cargarArchivo();

    private TestConfig() {
    }

    /** URL del frontend bajo prueba. */
    public static String baseUrl() {
        return get("base.url", "BASE_URL", "http://localhost:3001");
    }

    /** Navegador configurado: auto | chrome | edge | firefox. */
    public static String browser() {
        return get("browser", "BROWSER", "auto").toLowerCase();
    }

    /** true para correr sin ventana (obligatorio en servidores sin pantalla). */
    public static boolean headless() {
        return Boolean.parseBoolean(get("headless", "HEADLESS", "false"));
    }

    /** URL de un Selenium Grid / standalone (ej: http://localhost:4444). Vacío = navegador local. */
    public static String remoteUrl() {
        return get("selenium.remote.url", "SELENIUM_REMOTE_URL", "");
    }

    /** Timeout de las esperas explícitas. */
    public static Duration timeout() {
        return Duration.ofSeconds(Long.parseLong(get("timeout.seconds", "TIMEOUT_SECONDS", "10")));
    }

    private static String get(String property, String env, String defaultValue) {
        String value = System.getProperty(property);
        if (isBlank(value)) {
            value = System.getenv(env);
        }
        if (isBlank(value)) {
            value = ARCHIVO.getProperty(property);
        }
        return isBlank(value) ? defaultValue : value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static Properties cargarArchivo() {
        Properties props = new Properties();
        try (InputStream in = TestConfig.class.getResourceAsStream("/test.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer test.properties", e);
        }
        return props;
    }
}
