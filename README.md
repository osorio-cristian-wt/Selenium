# Tests E2E con Selenium

Java 21 · Selenium · TestNG · ExtentReports — Programación IV, UAP.

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

```bash
mvn test                                  # Chrome local con ventana
mvn test -Dbrowser=edge                   # Edge
mvn test -Dheadless=true                  # sin ventana
```

## Correr con Docker (solo hace falta Docker)

```bash
docker compose up -d browser              # navegador remoto
docker compose run --rm e2e               # corre los tests
```

- Ver el navegador en vivo mientras corren los tests: **http://localhost:7900** (noVNC).
- Reporte HTML: `reportes/ResultadoPruebas.html`.
- Si el front no corre en tu máquina en el 3001: `BASE_URL=http://otra-url docker compose run --rm e2e`.
- Backend construido desde GitHub: `docker compose --profile stack up -d backend`.

## Sin PC: GitHub Codespaces

**Code → Codespaces → Create codespace on main**. Trae Java 21, Maven, Docker y Node.
El puerto 7900 se reenvía automáticamente para ver el navegador desde el celular.

## MCP de Selenium

`.mcp.json` registra [`@angiejones/mcp-selenium`](https://www.npmjs.com/package/@angiejones/mcp-selenium) para que un agente pueda manejar el navegador al diseñar los tests.
