package org.example;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.example.support.DriverFactory;
import org.example.support.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
        reporte.setSystemInfo("Navegador", TestConfig.browser());
        reporte.setSystemInfo("URL", TestConfig.baseUrl());
        reporte.setSystemInfo("Modo", TestConfig.remoteUrl().isEmpty() ? "local" : "remoto (" + TestConfig.remoteUrl() + ")");
    }

    @BeforeMethod
    public void iniciarNavegador() {
        // El navegador se define por configuración (BROWSER, HEADLESS, SELENIUM_REMOTE_URL)
        driver = DriverFactory.create();
        wait = new WebDriverWait(driver, TestConfig.timeout());
    }

    @Test
    public void validarCredencialesInvalidas() {
        // Crear la prueba en el reporte
        testLog = reporte.createTest("Validar Login Fallido", "Prueba para verificar alerta de error");
        testLog.info("Navegador iniciado con éxito.");

        // Flujo de prueba
        driver.get(TestConfig.baseUrl());
        // Espera explícita para asegurar que el elemento cargue en el DOM
        WebElement emailBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("email")));
        WebElement passBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("password")));

        // Interacción con el Frontend (Escribir y Presionar Enter)
        emailBox.sendKeys("ivan.luna@email.com", Keys.TAB);
        passBox.sendKeys("123456", Keys.ENTER);

        // Localizador XPath que busca la clase y el texto exacto
        By alertaConTexto = By.xpath("//div[contains(@class, 'alert-danger') and text()='Credenciales de Mock inválidas']");

        // Espera hasta que el elemento sea completamente visible en la pantalla
        WebElement mensaje = wait.until(ExpectedConditions.visibilityOfElementLocated(alertaConTexto));

        // Aserción del framework
        Assert.assertEquals(mensaje.getText(), "Credenciales de Mock inválidas",
                "La alerta de error no mostró el texto esperado.");

        testLog.pass("La alerta con el texto de credenciales inválidas apareció correctamente.");
    }

    @Test
    public void validarCredencialesValidas() {
        // Crear la prueba en el reporte
        testLog = reporte.createTest("Validar Login Exitoso", "Prueba para verificar login correcto");
        testLog.info("Navegador iniciado con éxito.");

        // Flujo de prueba
        driver.get(TestConfig.baseUrl());
        WebElement emailBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("email")));
        WebElement passBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("password")));

        // Interacción con el Frontend (Escribir y Presionar Enter)
        emailBox.sendKeys("admin@correo.com", Keys.TAB);
        passBox.sendKeys("123", Keys.ENTER);

        // Localizador XPath que busca la clase y el texto exacto
        By alertaConTexto = By.xpath("//div[contains(@class, 'conteiner mb-5') and text()='Bienvenido al Sistema']");

        WebElement mensaje = wait.until(ExpectedConditions.visibilityOfElementLocated(alertaConTexto));

        Assert.assertEquals(mensaje.getText(), "Bienvenido al Sistema",
                "El mensaje de confirmación no mostró el texto esperado.");

        testLog.pass("El mensaje de confirmacion de login con credenciales válidas apareció correctamente.");
    }

    @AfterMethod(alwaysRun = true)
    public void cerrarNavegador(ITestResult resultado) {
        if (testLog != null && resultado.getStatus() == ITestResult.FAILURE) {
            testLog.fail(resultado.getThrowable());
            if (driver instanceof TakesScreenshot ts) {
                try {
                    String captura = ts.getScreenshotAs(OutputType.BASE64);
                    testLog.fail("Captura al fallar",
                            MediaEntityBuilder.createScreenCaptureFromBase64String(captura).build());
                } catch (RuntimeException e) {
                    testLog.warning("No se pudo tomar la captura: " + e.getMessage());
                }
            }
        }
        if (driver != null) {
            driver.quit();
            driver = null;
        }
        testLog = null;
    }

    @AfterClass(alwaysRun = true)
    public void finalizarSujeto() {
        // Escribe y cierra el reporte HTML de manera obligatoria
        reporte.flush();
    }
}
