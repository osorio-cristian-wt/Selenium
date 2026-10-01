package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Page Object del formulario de registro (RegisterForm.js). */
public class RegisterPage {

    public static final By TITULO = By.xpath("//h2[normalize-space()='Registro']");
    public static final By LINK_LOGIN = By.xpath("//form//button[normalize-space()='Inicia Sesión']");

    private final WebDriverWait wait;

    RegisterPage(WebDriver driver, WebDriverWait wait) {
        this.wait = wait;
    }

    public boolean estaVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(TITULO)).isDisplayed();
    }

    public void volverAlLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(LINK_LOGIN)).click();
    }
}
