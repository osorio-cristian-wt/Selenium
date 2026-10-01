# Tests E2E con Selenium

Java 21 · Selenium · TestNG · ExtentReports — Programación IV, UAP.

Prueba el **flujo de login** del front [`front-crud`](https://github.com/elprofevancho-uap/front-crud) (modo mock).

## Casos (`LoginTest`, patrón Page Object en `pages/`)

1. Login exitoso → "Bienvenido al Sistema"
2. Email desconocido → alerta "Credenciales de Mock inválidas"
3. Email correcto + contraseña incorrecta → alerta de error
4. Campos vacíos → validación HTML5 `required`, no se envía
5. Email sin formato válido → validación HTML5 `type=email`, no se envía
6. Navbar con usuario logueado (nombre, rol, botón Salir; desaparece Login)
7. La sesión persiste al recargar (localStorage `userData`)
8. Logout vuelve al login y borra la sesión
9. Navegación Login → Registro → Login
10. Reintento exitoso luego de un error (la alerta desaparece)

## Configuración

Los tests leen la configuración de variables de entorno (o `-Dpropiedad=` en Maven):

| Variable | Propiedad | Default | Uso |
|---|---|---|---|
| `BASE_URL` | `base.url` | `http://localhost:3001` | URL del frontend |
| `BROWSER` | `browser` | `chrome` | `chrome`, `edge`, `firefox` |
| `HEADLESS` | `headless` | `false` | sin ventana |
| `SELENIUM_REMOTE_URL` | `selenium.remote.url` | *(vacío = local)* | Selenium Grid/Docker |
| `TIMEOUT_SECONDS` | `timeout.seconds` | `10` | esperas explícitas |

## Correr en tu PC (sin Docker)

Con el front levantado en `http://localhost:3001` (`npm install --legacy-peer-deps && npm start` en front-crud):

```bash
mvn test                                  # Chrome local con ventana
mvn test -Dbrowser=edge                   # Edge
mvn test -Dheadless=true                  # sin ventana
```

## Correr con Docker (solo hace falta Docker)

```bash
docker compose run --rm e2e               # levanta front (3001) + navegador y corre los tests
```

- Ver el navegador en vivo mientras corren los tests: **http://localhost:7900** (noVNC).
- Reporte HTML: `reportes/ResultadoPruebas.html`.
- El front se construye directo desde GitHub. Para usar tu copia local: `FRONTEND_CONTEXT=../front-crud docker compose run --rm e2e`.
- Para probar un front que ya corre en tu máquina: `BASE_URL=http://host.docker.internal:3001 docker compose run --rm e2e`.
- Backend construido desde GitHub: `docker compose --profile stack up -d backend`.

## Sin PC: GitHub Codespaces

**Code → Codespaces → Create codespace on main**. Trae Java 21, Maven, Docker y Node.
El puerto 7900 se reenvía automáticamente para ver el navegador desde el celular.

## MCP de Selenium

`.mcp.json` registra [`@angiejones/mcp-selenium`](https://www.npmjs.com/package/@angiejones/mcp-selenium) para que un agente pueda manejar el navegador al diseñar los tests.
