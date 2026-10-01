package org.example.support;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Optional;

/**
 * Único punto donde se crea el navegador: todos los tests piden el WebDriver acá,
 * así que cambiar de navegador es cambiar UNA línea en test.properties (browser=...).
 *
 *  - browser=auto           -> el primero instalado en la PC (Chrome -> Edge -> Firefox)
 *  - browser=chrome|edge|firefox -> ese navegador
 *  - SELENIUM_REMOTE_URL     -> navegador remoto (Docker / Selenium Grid), no hace falta instalar nada
 *
 * En modo local, Selenium Manager (incluido en Selenium 4) descarga solo el driver que
 * corresponde a la versión del navegador; no hay que instalar chromedriver/msedgedriver/geckodriver.
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver create() {
        return create(TestConfig.browser());
    }

    public static WebDriver create(String browserConfigurado) {
        String remote = TestConfig.remoteUrl();
        if (!remote.isEmpty()) {
            String nombre = "auto".equals(browserConfigurado) ? "chrome" : browserConfigurado;
            try {
                return new RemoteWebDriver(URI.create(remote).toURL(), options(nombre, null));
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("SELENIUM_REMOTE_URL inválida: " + remote, e);
            }
        }

        Optional<BrowserDetector.Instalado> instalado = "auto".equals(browserConfigurado)
                ? BrowserDetector.primeroInstalado()
                : BrowserDetector.buscar(browserConfigurado);
        // si no se detecta ninguno, se usa el pedido (o chrome) y Selenium Manager intenta resolverlo
        String nombre = instalado.map(BrowserDetector.Instalado::nombre)
                .orElse("auto".equals(browserConfigurado) ? "chrome" : browserConfigurado);
        String binario = instalado.map(BrowserDetector.Instalado::ruta).orElse(null);

        MutableCapabilities options = options(nombre, binario);
        return switch (nombre) {
            case "edge" -> new EdgeDriver((EdgeOptions) options);
            case "firefox" -> new FirefoxDriver((FirefoxOptions) options);
            case "chrome" -> new ChromeDriver((ChromeOptions) options);
            default -> throw new IllegalArgumentException(
                    "Navegador no soportado: '" + nombre + "'. Usar auto, chrome, edge o firefox.");
        };
    }

    private static MutableCapabilities options(String nombre, String binario) {
        boolean headless = TestConfig.headless();
        return switch (nombre) {
            case "edge" -> {
                EdgeOptions o = new EdgeOptions();
                if (binario != null) o.setBinary(binario);
                if (headless) o.addArguments("--headless=new");
                o.addArguments("--window-size=1366,768");
                yield o;
            }
            case "firefox" -> {
                FirefoxOptions o = new FirefoxOptions();
                if (binario != null) o.setBinary(binario);
                if (headless) o.addArguments("-headless");
                o.addArguments("--width=1366", "--height=768");
                yield o;
            }
            case "chrome" -> {
                ChromeOptions o = new ChromeOptions();
                if (binario != null) o.setBinary(binario);
                if (headless) o.addArguments("--headless=new");
                o.addArguments("--window-size=1366,768", "--no-sandbox", "--disable-dev-shm-usage");
                yield o;
            }
            default -> throw new IllegalArgumentException(
                    "Navegador no soportado: '" + nombre + "'. Usar auto, chrome, edge o firefox.");
        };
    }
}
