package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Page Object del formulario de login (LoginForm.js del front-crud). */
public class LoginPage {

    public static final By TITULO = By.xpath("//h2[normalize-space()='Iniciar Sesión']");
    public static final By EMAIL = By.name("email");
    public static final By PASSWORD = By.name("password");
    public static final By INGRESAR = By.xpath("//form//button[normalize-space()='Ingresar']");
    public static final By ALERTA_ERROR = By.cssSelector(".alert.alert-danger");
    public static final By LINK_REGISTRO = By.xpath("//form//button[normalize-space()='Regístrate aquí']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public LoginPage(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    /** Abre el front con el almacenamiento del navegador limpio (sin sesión previa). */
    public LoginPage abrir(String baseUrl) {
        driver.get(baseUrl);
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
        driver.get(baseUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(TITULO));
        return this;
    }

    public boolean estaVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(TITULO)).isDisplayed();
    }

    public LoginPage completar(String email, String password) {
        WebElement emailBox = wait.until(ExpectedConditions.elementToBeClickable(EMAIL));
        emailBox.clear();
        emailBox.sendKeys(email);
        WebElement passBox = driver.findElement(PASSWORD);
        passBox.clear();
        passBox.sendKeys(password);
        return this;
    }

    public void enviar() {
        wait.until(ExpectedConditions.elementToBeClickable(INGRESAR)).click();
    }

    public void ingresar(String email, String password) {
        completar(email, password).enviar();
    }

    /** Espera a que aparezca la alerta de error y devuelve su texto. */
    public String mensajeDeError() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ALERTA_ERROR)).getText().trim();
    }

    public boolean hayMensajeDeError() {
        return !driver.findElements(ALERTA_ERROR).isEmpty();
    }

    /** Validación nativa HTML5 del input (required / type=email), consultada vía JavaScript. */
    public boolean emailConValorFaltante() {
        return validity(EMAIL, "valueMissing");
    }

    public boolean emailConFormatoInvalido() {
        return validity(EMAIL, "typeMismatch");
    }

    public boolean passwordConValorFaltante() {
        return validity(PASSWORD, "valueMissing");
    }

    private boolean validity(By campo, String propiedad) {
        WebElement input = driver.findElement(campo);
        Object valor = ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].validity[arguments[1]];", input, propiedad);
        return Boolean.TRUE.equals(valor);
    }

    public RegisterPage irARegistro() {
        wait.until(ExpectedConditions.elementToBeClickable(LINK_REGISTRO)).click();
        return new RegisterPage(driver, wait);
    }
}
