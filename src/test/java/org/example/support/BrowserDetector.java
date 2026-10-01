package org.example.support;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Detecta qué navegadores están instalados en la PC (Windows, macOS o Linux)
 * para el modo browser=auto. No depende de Selenium.
 */
public final class BrowserDetector {

    /** Navegador encontrado: nombre lógico (chrome | edge | firefox) y ruta del ejecutable. */
    public record Instalado(String nombre, String ruta) {
    }

    /** Orden de preferencia del modo auto. */
    public static final List<String> PREFERENCIA = List.of("chrome", "edge", "firefox");

    private BrowserDetector() {
    }

    /** El primero instalado según {@link #PREFERENCIA}, o vacío si no se encontró ninguno. */
    public static Optional<Instalado> primeroInstalado() {
        for (String nombre : PREFERENCIA) {
            Optional<Instalado> encontrado = buscar(nombre);
            if (encontrado.isPresent()) {
                return encontrado;
            }
        }
        return Optional.empty();
    }

    /** Busca un navegador puntual (chrome | edge | firefox). */
    public static Optional<Instalado> buscar(String nombre) {
        for (String ruta : rutasCandidatas(nombre)) {
            if (ruta != null && Files.isExecutable(Path.of(ruta))) {
                return Optional.of(new Instalado(nombre, ruta));
            }
        }
        return Optional.empty();
    }

    private static List<String> rutasCandidatas(String nombre) {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        List<String> rutas = new ArrayList<>();
        if (os.contains("win")) {
            String pf = System.getenv("ProgramFiles");
            String pf86 = System.getenv("ProgramFiles(x86)");
            String local = System.getenv("LOCALAPPDATA");
            switch (nombre) {
                case "chrome" -> {
                    rutas.add(join(pf, "Google\\Chrome\\Application\\chrome.exe"));
                    rutas.add(join(pf86, "Google\\Chrome\\Application\\chrome.exe"));
                    rutas.add(join(local, "Google\\Chrome\\Application\\chrome.exe"));
                }
                case "edge" -> {
                    rutas.add(join(pf86, "Microsoft\\Edge\\Application\\msedge.exe"));
                    rutas.add(join(pf, "Microsoft\\Edge\\Application\\msedge.exe"));
                }
                case "firefox" -> {
                    rutas.add(join(pf, "Mozilla Firefox\\firefox.exe"));
                    rutas.add(join(pf86, "Mozilla Firefox\\firefox.exe"));
                }
                default -> { }
            }
        } else if (os.contains("mac")) {
            String home = System.getProperty("user.home");
            String app = switch (nombre) {
                case "chrome" -> "Google Chrome.app/Contents/MacOS/Google Chrome";
                case "edge" -> "Microsoft Edge.app/Contents/MacOS/Microsoft Edge";
                case "firefox" -> "Firefox.app/Contents/MacOS/firefox";
                default -> null;
            };
            if (app != null) {
                rutas.add("/Applications/" + app);
                rutas.add(home + "/Applications/" + app);
            }
        } else {
            List<String> ejecutables = switch (nombre) {
                case "chrome" -> List.of("google-chrome", "google-chrome-stable", "chromium", "chromium-browser");
                case "edge" -> List.of("microsoft-edge", "microsoft-edge-stable");
                case "firefox" -> List.of("firefox");
                default -> List.of();
            };
            for (String ejecutable : ejecutables) {
                rutas.add(enPath(ejecutable));
            }
            // instalaciones típicas fuera del PATH
            switch (nombre) {
                case "chrome" -> {
                    rutas.add("/opt/google/chrome/chrome");
                    rutas.add("/snap/bin/chromium");
                    // Chromium de Playwright (contenedores de CI / entornos cloud)
                    rutas.add("/opt/pw-browsers/chromium");
                }
                case "edge" -> rutas.add("/opt/microsoft/msedge/msedge");
                case "firefox" -> rutas.add("/snap/bin/firefox");
                default -> { }
            }
        }
        return rutas;
    }

    private static String join(String base, String resto) {
        return base == null ? null : base + "\\" + resto;
    }

    private static String enPath(String ejecutable) {
        String path = System.getenv("PATH");
        if (path == null) {
            return null;
        }
        for (String dir : path.split(File.pathSeparator)) {
            Path candidato = Path.of(dir, ejecutable);
            if (Files.isExecutable(candidato)) {
                return candidato.toString();
            }
        }
        return null;
    }
}
