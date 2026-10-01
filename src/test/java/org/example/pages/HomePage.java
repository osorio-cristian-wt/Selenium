package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Vista de inicio luego del login + Navbar con el usuario autenticado. */
public class HomePage {

    public static final By BIENVENIDA = By.xpath("//h2[normalize-space()='Bienvenido al Sistema']");
    public static final By NOMBRE_USUARIO = By.xpath("//nav//div[contains(@class,'fw-bold')]");
    public static final By ROL_USUARIO = By.xpath("//nav//div[contains(@class,'fw-bold')]/following-sibling::div");
    public static final By BOTON_SALIR = By.xpath("//nav//button[normalize-space()='Salir']");
    public static final By BOTON_LOGIN_NAVBAR = By.xpath("//nav//button[normalize-space()='Login']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public HomePage(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    /** Espera el mensaje de bienvenida (el login mock demora ~1 segundo) y devuelve su texto. */
    public String mensajeDeBienvenida() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(BIENVENIDA)).getText().trim();
    }

    public boolean bienvenidaPresente() {
        return !driver.findElements(BIENVENIDA).isEmpty();
    }

    public String nombreDeUsuario() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(NOMBRE_USUARIO)).getText().trim();
    }

    public String rolDeUsuario() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ROL_USUARIO)).getText().trim();
    }

    public boolean botonSalirVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(BOTON_SALIR)).isDisplayed();
    }

    public boolean botonLoginEnNavbarPresente() {
        return !driver.findElements(BOTON_LOGIN_NAVBAR).isEmpty();
    }

    public void salir() {
        wait.until(ExpectedConditions.elementToBeClickable(BOTON_SALIR)).click();
    }

    /** Dato de sesión que el front guarda en localStorage al loguearse. */
    public String sesionGuardada() {
        Object valor = ((JavascriptExecutor) driver).executeScript("return window.localStorage.getItem('userData');");
        return valor == null ? null : valor.toString();
    }
}
