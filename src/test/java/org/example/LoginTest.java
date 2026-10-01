package org.example;

import org.example.pages.HomePage;
import org.example.pages.LoginPage;
import org.example.pages.RegisterPage;
import org.example.support.BaseTest;
import org.example.support.TestConfig;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests E2E del flujo de login del front (front-crud, modo mock: REACT_APP_USE_MOCK=true).
 * Credenciales válidas del mock: admin@correo.com / 123.
 */
public class LoginTest extends BaseTest {

    private static final String EMAIL_VALIDO = "admin@correo.com";
    private static final String PASSWORD_VALIDO = "123";
    private static final String ERROR_CREDENCIALES = "Credenciales de Mock inválidas";

    private LoginPage login;
    private HomePage home;

    private void abrirLogin() {
        login = new LoginPage(driver, TestConfig.timeout());
        home = new HomePage(driver, TestConfig.timeout());
        paso("Abrir " + TestConfig.baseUrl() + " sin sesión previa");
        login.abrir(TestConfig.baseUrl());
    }

    private void loguearseOk() {
        paso("Ingresar con " + EMAIL_VALIDO);
        login.ingresar(EMAIL_VALIDO, PASSWORD_VALIDO);
        Assert.assertEquals(home.mensajeDeBienvenida(), "Bienvenido al Sistema");
    }

    @Test(priority = 1, description = "Login exitoso con credenciales válidas muestra la bienvenida")
    public void validarCredencialesValidas() {
        abrirLogin();
        paso("Completar email y contraseña válidos y presionar Ingresar");
        login.ingresar(EMAIL_VALIDO, PASSWORD_VALIDO);

        Assert.assertEquals(home.mensajeDeBienvenida(), "Bienvenido al Sistema",
                "El mensaje de bienvenida no mostró el texto esperado.");
        Assert.assertFalse(login.hayMensajeDeError(), "No debería haber alerta de error.");
    }

    @Test(priority = 2, description = "Login fallido con email desconocido muestra alerta de error")
    public void validarCredencialesInvalidas() {
        abrirLogin();
        paso("Ingresar con un usuario que no existe");
        login.ingresar("ivan.luna@email.com", "123456");

        Assert.assertEquals(login.mensajeDeError(), ERROR_CREDENCIALES,
                "La alerta de error no mostró el texto esperado.");
        Assert.assertTrue(login.estaVisible(), "Debería seguir en la pantalla de login.");
        Assert.assertFalse(home.bienvenidaPresente(), "No debería mostrarse la bienvenida.");
    }

    @Test(priority = 3, description = "Login fallido con email correcto y contraseña incorrecta")
    public void validarPasswordIncorrecta() {
        abrirLogin();
        paso("Ingresar con email válido y contraseña incorrecta");
        login.ingresar(EMAIL_VALIDO, "contraseña-incorrecta");

        Assert.assertEquals(login.mensajeDeError(), ERROR_CREDENCIALES);
        Assert.assertFalse(home.bienvenidaPresente());
    }

    @Test(priority = 4, description = "No se puede enviar el formulario con los campos vacíos")
    public void validarCamposObligatorios() {
        abrirLogin();
        paso("Presionar Ingresar sin completar ningún campo");
        login.enviar();

        Assert.assertTrue(login.emailConValorFaltante(), "El email debería marcarse como obligatorio.");
        Assert.assertTrue(login.passwordConValorFaltante(), "La contraseña debería marcarse como obligatoria.");
        Assert.assertTrue(login.estaVisible(), "Debería seguir en la pantalla de login.");
        Assert.assertFalse(login.hayMensajeDeError(), "El formulario no debería haberse enviado.");
    }

    @Test(priority = 5, description = "No se puede enviar el formulario con un email de formato inválido")
    public void validarFormatoDeEmail() {
        abrirLogin();
        paso("Ingresar 'admin' (sin @) como email");
        login.ingresar("admin", PASSWORD_VALIDO);

        Assert.assertTrue(login.emailConFormatoInvalido(), "El email sin @ debería ser inválido.");
        Assert.assertTrue(login.estaVisible());
        Assert.assertFalse(home.bienvenidaPresente(), "No debería loguearse con un email inválido.");
    }

    @Test(priority = 6, description = "Después del login el navbar muestra el usuario, su rol y el botón Salir")
    public void validarNavbarConUsuarioLogueado() {
        abrirLogin();
        loguearseOk();

        Assert.assertEquals(home.nombreDeUsuario(), "Usuario de Prueba");
        Assert.assertEquals(home.rolDeUsuario(), "admin");
        Assert.assertTrue(home.botonSalirVisible(), "Debería verse el botón Salir.");
        Assert.assertFalse(home.botonLoginEnNavbarPresente(), "El botón Login del navbar debería ocultarse.");
    }

    @Test(priority = 7, description = "La sesión se mantiene al recargar la página")
    public void validarSesionPersistente() {
        abrirLogin();
        loguearseOk();
        Assert.assertNotNull(home.sesionGuardada(), "El front debería guardar userData en localStorage.");

        paso("Recargar la página");
        driver.navigate().refresh();

        Assert.assertEquals(home.mensajeDeBienvenida(), "Bienvenido al Sistema",
                "Después de recargar debería seguir logueado.");
    }

    @Test(priority = 8, description = "Cerrar sesión vuelve al login y borra la sesión guardada")
    public void validarLogout() {
        abrirLogin();
        loguearseOk();

        paso("Presionar Salir");
        home.salir();

        Assert.assertTrue(login.estaVisible(), "Debería volver al formulario de login.");
        Assert.assertNull(home.sesionGuardada(), "userData debería borrarse del localStorage.");
        Assert.assertFalse(home.bienvenidaPresente());
    }

    @Test(priority = 9, description = "Desde el login se navega al registro y se vuelve al login")
    public void validarNavegacionLoginRegistro() {
        abrirLogin();
        paso("Ir a 'Regístrate aquí'");
        RegisterPage registro = login.irARegistro();
        Assert.assertTrue(registro.estaVisible(), "Debería mostrarse el formulario de registro.");

        paso("Volver con 'Inicia Sesión'");
        registro.volverAlLogin();
        Assert.assertTrue(login.estaVisible(), "Debería volver al formulario de login.");
    }

    @Test(priority = 10, description = "Tras un intento fallido se puede reintentar y loguear correctamente")
    public void validarReintentoLuegoDeError() {
        abrirLogin();
        paso("Primer intento con credenciales inválidas");
        login.ingresar("otro@correo.com", "000");
        Assert.assertEquals(login.mensajeDeError(), ERROR_CREDENCIALES);

        paso("Segundo intento con credenciales válidas");
        login.ingresar(EMAIL_VALIDO, PASSWORD_VALIDO);
        Assert.assertEquals(home.mensajeDeBienvenida(), "Bienvenido al Sistema");
        Assert.assertFalse(login.hayMensajeDeError(), "La alerta de error debería desaparecer.");
    }
}
