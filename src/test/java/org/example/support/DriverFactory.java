package org.example.support;

import org.openqa.selenium.Capabilities;
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

/**
 * Crea el WebDriver según {@link TestConfig}:
 *  - SELENIUM_REMOTE_URL definido  -> RemoteWebDriver (Docker / Selenium Grid)
 *  - sin SELENIUM_REMOTE_URL       -> driver local (Selenium Manager resuelve el driver)
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver create() {
        return create(TestConfig.browser());
    }

    public static WebDriver create(String browser) {
        Capabilities options = options(browser);
        String remote = TestConfig.remoteUrl();
        if (!remote.isEmpty()) {
            try {
                return new RemoteWebDriver(URI.create(remote).toURL(), options);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("SELENIUM_REMOTE_URL inválida: " + remote, e);
            }
        }
        return switch (browser) {
            case "edge" -> new EdgeDriver((EdgeOptions) options);
            case "firefox" -> new FirefoxDriver((FirefoxOptions) options);
            default -> new ChromeDriver((ChromeOptions) options);
        };
    }

    private static Capabilities options(String browser) {
        boolean headless = TestConfig.headless();
        return switch (browser) {
            case "edge" -> {
                EdgeOptions o = new EdgeOptions();
                if (headless) o.addArguments("--headless=new");
                o.addArguments("--window-size=1366,768");
                yield o;
            }
            case "firefox" -> {
                FirefoxOptions o = new FirefoxOptions();
                if (headless) o.addArguments("-headless");
                o.addArguments("--width=1366", "--height=768");
                yield o;
            }
            default -> {
                ChromeOptions o = new ChromeOptions();
                if (headless) o.addArguments("--headless=new");
                o.addArguments("--window-size=1366,768", "--no-sandbox", "--disable-dev-shm-usage");
                yield o;
            }
        };
    }
}
