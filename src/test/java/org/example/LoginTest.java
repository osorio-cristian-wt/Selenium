package org.example;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static ExtentReports reporte;
    private ExtentTest testLog;

    @BeforeClass
    public void configurarReporte() {
        // Configura la ruta del archivo HTML del reporte
        ExtentSparkReporter spark = new ExtentSparkReporter("reportes/ResultadoPruebas.html");
        reporte = new ExtentReports();
        reporte.attachReporter(spark);
    }

    @Test
    public void validarCredencialesInvalidas() {
        // Crear la prueba en el reporte
        testLog = reporte.createTest("Validar Login Fallido", "Prueba para verificar alerta de error");

        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        testLog.info("Navegador iniciado con éxito.");

        // Flujo de prueba
        driver.get("http://localhost:3001");
        // 4. Espera explícita para asegurar que el elemento cargue en el DOM
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement emailBox = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("email"))
        );

        WebElement passBox = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("password"))
        );

        // 5. Interacción con el Frontend (Escribir y Presionar Enter)
        emailBox.sendKeys("ivan.luna@email.com", Keys.TAB);
        passBox.sendKeys("123456", Keys.ENTER);

        // 6. Esperar el resultado

        //Opcion si el div se renderiza
        // Localizador XPath que busca la clase y el texto exacto
        By alertaConTexto = By.xpath("//div[contains(@class, 'alert-danger') and text()='Credenciales de Mock inválidas']");

        // Espera hasta que el elemento sea completamente visible en la pantalla
        WebElement mensaje = wait.until(ExpectedConditions.visibilityOfElementLocated(alertaConTexto));

        // 2. ASERCIÓN DEL FRAMEWORK (Suma la validación formal al test)
        Assert.assertTrue(mensaje.getText().equals("Credenciales de Mock inválidas"), "La alerta de error no mostró el texto esperado.");

        // Registrar éxito en el reporte si la aserción pasa
        testLog.pass("La alerta con el texto de credenciales inválidas apareció correctamente.");
    }

    
    @Test
    public void validarCredencialesValidas() {
        // Crear la prueba en el reporte
        testLog = reporte.createTest("Validar Login Exitoso", "Prueba para verificar login correcto");

        WebDriverManager.edgedriver().setup();
        driver = new EdgeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        testLog.info("Navegador iniciado con éxito.");

        // Flujo de prueba
        driver.get("http://localhost:3001");
        // 4. Espera explícita para asegurar que el elemento cargue en el DOM
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement emailBox = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("email"))
        );

        WebElement passBox = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("password"))
        );

    
        // 5. Interacción con el Frontend (Escribir y Presionar Enter)
        emailBox.sendKeys("admin@correo.com", Keys.TAB);
        passBox.sendKeys("123", Keys.ENTER);
        // 6. Esperar el resultado

        //Opcion si el div se renderiza
        // Localizador XPath que busca la clase y el texto exacto
        By alertaConTexto = By.xpath("//div[contains(@class, 'conteiner mb-5') and text()='Bienvenido al Sistema']");

        // Espera hasta que el elemento sea completamente visible en la pantalla
        WebElement mensaje = wait.until(ExpectedConditions.visibilityOfElementLocated(alertaConTexto));

        // 2. ASERCIÓN DEL FRAMEWORK (Suma la validación formal al test)
        Assert.assertTrue(mensaje.getText().equals("Bienvenido al Sistema"), "El mensaje de confirmación no mostró el texto esperado.");

        // Registrar éxito en el reporte si la aserción pasa
        testLog.pass("El mensaje de confirmacion de login con credenciales válidas apareció correctamente.");
    }

    @AfterClass
    public void finalizarSujeto() {
        if (driver != null) {
            driver.quit();
        }
        // Escribe y cierra el reporte HTML de manera obligatoria
        reporte.flush();
    }
}
