/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aaa;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ArchivoItem {
    private File archivo;

    public ArchivoItem(File archivo) {
        this.archivo = archivo;
    }

    public File getArchivo() {
        return archivo;
    }

    public String getNombre() {
        return archivo.getName();
    }

    public long getTamano() {
        if (archivo.isDirectory()) {
            return 0;
        }
        return archivo.length();
    }

    public long getFechaModificacion() {
        return archivo.lastModified();
    }

    public String getFechaFormateada() {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return formato.format(new Date(archivo.lastModified()));
    }

    public String getTipo() {
        if (archivo.isDirectory()) {
            return "Carpeta";
        }

        String nombre = archivo.getName().toLowerCase();

        if (nombre.endsWith(".jpg") || nombre.endsWith(".jpeg") || nombre.endsWith(".png") || nombre.endsWith(".gif")) {
            return "Imagen";
        }

        if (nombre.endsWith(".pdf") || nombre.endsWith(".docx") || nombre.endsWith(".txt")) {
            return "Documento";
        }

        if (nombre.endsWith(".mp3") || nombre.endsWith(".wav")) {
            return "Música";
        }

        int punto = nombre.lastIndexOf(".");
        if (punto != -1) {
            return nombre.substring(punto + 1).toUpperCase();
        }

        return "Archivo";
    }

    public String getTamanoFormateado() {
        if (archivo.isDirectory()) {
            return "-";
        }

        long bytes = archivo.length();

        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return (bytes / 1024) + " KB";
        } else {
            return (bytes / (1024 * 1024)) + " MB";
        }
    }
}
