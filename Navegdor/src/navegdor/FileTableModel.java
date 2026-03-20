/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package navegdor;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Nathan
 */
public class FileTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
        "Nombre", "Fecha de modificación", "Tipo", "Tamaño"
    };

    private List<VirtualNode> nodes = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public void setNodes(List<VirtualNode> newNodes) {
        this.nodes = new ArrayList<>(newNodes);
        fireTableDataChanged();
    }

    public VirtualNode getNodeAt(int row) {
        if (row < 0 || row >= nodes.size()) return null;
        return nodes.get(row);
    }
    
    @Override public int getRowCount()    { return nodes.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int col) { return COLUMNS[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        VirtualNode vn = nodes.get(row);
        return switch (col) {
            case 0 -> vn.getName();
            case 1 -> sdf.format(new Date(vn.getLastModified()));
            case 2 -> vn.isDirectory() ? "Carpeta de archivos" : typeLabel(vn.getName());
            case 3 -> vn.isDirectory() ? "" : formatSize(vn.getSize());
            default -> "";
        };
    }
    
    private String typeLabel(String name) {
        int i = name.lastIndexOf('.');
        if (i < 0) return "Archivo";
        return switch (name.substring(i).toLowerCase()) {
            case ".txt"  -> "Documento de texto";
            case ".pdf"  -> "Documento PDF";
            case ".docx", ".doc" -> "Documento Word";
            case ".xlsx" -> "Hoja de cálculo";
            case ".pptx" -> "Presentación";
            case ".jpg", ".jpeg" -> "Imagen JPEG";
            case ".png"  -> "Imagen PNG";
            case ".gif"  -> "Imagen GIF";
            case ".bmp"  -> "Imagen BMP";
            case ".mp3"  -> "Archivo MP3";
            case ".wav"  -> "Archivo WAV";
            case ".ogg"  -> "Archivo OGG";
            case ".mp4"  -> "Video MP4";
            case ".avi"  -> "Video AVI";
            case ".zip"  -> "Archivo ZIP";
            case ".exe"  -> "Aplicación";
            case ".java" -> "Archivo Java";
            case ".py"   -> "Script Python";
            case ".log"  -> "Archivo de registro";
            default      -> "Documento d...";
        };
    }

    private String formatSize(long bytes) {
        if (bytes < 1024)           return bytes + " B";
        if (bytes < 1024 * 1024)    return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }
}
