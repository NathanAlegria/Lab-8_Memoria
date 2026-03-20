/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aaa;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class OperacionesArchivo {

    private static File[] portapapeles;

    public static boolean crearCarpeta(File carpetaActual, String nombre) {
        if (carpetaActual == null || nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        File nuevaCarpeta = new File(carpetaActual, nombre.trim());

        if (nuevaCarpeta.exists()) {
            return false;
        }

        return nuevaCarpeta.mkdir();
    }

    public static boolean renombrar(File archivo, String nuevoNombre) {
        if (archivo == null || nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            return false;
        }

        File nuevoArchivo = new File(archivo.getParent(), nuevoNombre.trim());

        if (nuevoArchivo.exists()) {
            return false;
        }

        return archivo.renameTo(nuevoArchivo);
    }

    public static void copiar(File[] archivos) {
        portapapeles = archivos;
    }

    public static boolean pegar(File destino) {
        if (portapapeles == null || destino == null || !destino.isDirectory()) {
            return false;
        }

        try {
            for (int i = 0; i < portapapeles.length; i++) {
                File origen = portapapeles[i];
                File nuevoDestino = new File(destino, origen.getName());

                if (origen.isDirectory()) {
                    copiarCarpeta(origen, nuevoDestino);
                } else {
                    Files.copy(origen.toPath(), nuevoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void copiarCarpeta(File origen, File destino) throws IOException {
        if (!destino.exists()) {
            destino.mkdir();
        }

        File[] contenido = origen.listFiles();

        if (contenido != null) {
            for (int i = 0; i < contenido.length; i++) {
                File archivo = contenido[i];
                File nuevoDestino = new File(destino, archivo.getName());

                if (archivo.isDirectory()) {
                    copiarCarpeta(archivo, nuevoDestino);
                } else {
                    Files.copy(archivo.toPath(), nuevoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    public static void organizarArchivos(File carpetaSeleccionada) {
        if (carpetaSeleccionada == null || !carpetaSeleccionada.isDirectory()) {
            return;
        }

        File[] archivos = carpetaSeleccionada.listFiles();

        if (archivos == null) {
            return;
        }

        File carpetaImagenes = new File(carpetaSeleccionada, "Imagenes");
        File carpetaDocumentos = new File(carpetaSeleccionada, "Documentos");
        File carpetaMusica = new File(carpetaSeleccionada, "Musica");

        for (int i = 0; i < archivos.length; i++) {
            File archivo = archivos[i];

            if (archivo.isDirectory()) {
                continue;
            }

            String nombre = archivo.getName().toLowerCase();
            File destino = null;

            if (nombre.endsWith(".jpg") || nombre.endsWith(".jpeg") || nombre.endsWith(".png") || nombre.endsWith(".gif")) {
                if (!carpetaImagenes.exists()) {
                    carpetaImagenes.mkdir();
                }
                destino = new File(carpetaImagenes, archivo.getName());
            } else if (nombre.endsWith(".pdf") || nombre.endsWith(".docx") || nombre.endsWith(".txt")) {
                if (!carpetaDocumentos.exists()) {
                    carpetaDocumentos.mkdir();
                }
                destino = new File(carpetaDocumentos, archivo.getName());
            } else if (nombre.endsWith(".mp3") || nombre.endsWith(".wav")) {
                if (!carpetaMusica.exists()) {
                    carpetaMusica.mkdir();
                }
                destino = new File(carpetaMusica, archivo.getName());
            }

            if (destino != null) {
                try {
                    Files.move(archivo.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
