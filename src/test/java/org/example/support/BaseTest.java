package org.example.support;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * Base de los tests E2E:
 *  - un navegador nuevo por test (sin sesión ni localStorage compartido entre tests)
 *  - reporte ExtentReports en reportes/ResultadoPruebas.html, con captura de pantalla si el test falla
 */
public abstract class BaseTest {

    private static ExtentReports reporte;

    protected WebDriver driver;
    protected ExtentTest testLog;

    @BeforeSuite(alwaysRun = true)
    public void configurarReporte() {
        ExtentSparkReporter spark = new ExtentSparkReporter("reportes/ResultadoPruebas.html");
        spark.config().setDocumentTitle("Tests E2E - Front CRUD");
        spark.config().setReportName("Flujo de login");
        reporte = new ExtentReports();
        reporte.attachReporter(spark);
        reporte.setSystemInfo("Navegador", TestConfig.browser());
        reporte.setSystemInfo("URL", TestConfig.baseUrl());
        reporte.setSystemInfo("Modo", TestConfig.remoteUrl().isEmpty() ? "local" : "remoto (" + TestConfig.remoteUrl() + ")");
    }

    @BeforeMethod(alwaysRun = true)
    public void iniciarNavegador(Method metodo) {
        Test test = metodo.getAnnotation(Test.class);
        String descripcion = (test != null && !test.description().isBlank()) ? test.description() : metodo.getName();
        testLog = reporte.createTest(descripcion, metodo.getName());
        driver = DriverFactory.create();
        testLog.info("Navegador iniciado: " + TestConfig.browser());
    }

    protected void paso(String descripcion) {
        testLog.info(descripcion);
    }

    @AfterMethod(alwaysRun = true)
    public void cerrarNavegador(ITestResult resultado) {
        if (testLog != null) {
            switch (resultado.getStatus()) {
                case ITestResult.SUCCESS -> testLog.pass("Test exitoso");
                case ITestResult.SKIP -> testLog.skip("Test omitido");
                default -> {
                    testLog.fail(resultado.getThrowable());
                    adjuntarCaptura();
                }
            }
        }
        if (driver != null) {
            driver.quit();
            driver = null;
        }
        testLog = null;
    }

    private void adjuntarCaptura() {
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

    @AfterSuite(alwaysRun = true)
    public void cerrarReporte() {
        if (reporte != null) {
            reporte.flush();
        }
    }
}
