package org.example.support;

import java.time.Duration;

/**
 * Configuración de los tests E2E.
 * Cada valor se lee primero como propiedad de sistema (-Dbase.url=...)
 * y si no existe, como variable de entorno (BASE_URL=...).
 */
public final class TestConfig {

    private TestConfig() {
    }

    /** URL del frontend bajo prueba. */
    public static String baseUrl() {
        return get("base.url", "BASE_URL", "http://localhost:3001");
    }

    /** Navegador: chrome | edge | firefox. */
    public static String browser() {
        return get("browser", "BROWSER", "chrome").toLowerCase();
    }

    /** true para correr sin ventana (obligatorio en servidores sin pantalla). */
    public static boolean headless() {
        return Boolean.parseBoolean(get("headless", "HEADLESS", "false"));
    }

    /** URL de un Selenium Grid / standalone (ej: http://localhost:4444). Vacío = driver local. */
    public static String remoteUrl() {
        return get("selenium.remote.url", "SELENIUM_REMOTE_URL", "");
    }

    /** Timeout de las esperas explícitas. */
    public static Duration timeout() {
        return Duration.ofSeconds(Long.parseLong(get("timeout.seconds", "TIMEOUT_SECONDS", "10")));
    }

    private static String get(String property, String env, String defaultValue) {
        String value = System.getProperty(property);
        if (value == null || value.isBlank()) {
            value = System.getenv(env);
        }
        return (value == null || value.isBlank()) ? defaultValue : value.trim();
    }
}
