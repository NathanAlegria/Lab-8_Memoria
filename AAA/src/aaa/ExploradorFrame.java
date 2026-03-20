/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aaa;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.util.Comparator;

public class ExploradorFrame extends JFrame {

    private JTree arbol;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtRuta;
    private JComboBox<String> comboOrden;
    private File carpetaRaiz;
    private File carpetaActual;

    public ExploradorFrame() {
        setTitle("JAVA CENTER");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        carpetaRaiz = new File(System.getProperty("user.dir"), "raiz");

        if (!carpetaRaiz.exists()) {
            carpetaRaiz.mkdir();
        }
        crearEstructuraInicial();

        construirInterfaz();
        cargarArbol();
        mostrarContenido(carpetaRaiz);
        crearDatosPrueba();
    }

    private void crearDatosPrueba() {
        try {
            new File(carpetaRaiz, "archivo.txt").createNewFile();
            new File(carpetaRaiz, "foto.jpg").createNewFile();
            new File(carpetaRaiz, "cancion.mp3").createNewFile();
            new File(carpetaRaiz, "documento.pdf").createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void crearEstructuraInicial() {
        File imagenes = new File(carpetaRaiz, "Imagenes");
        File documentos = new File(carpetaRaiz, "Documentos");
        File musica = new File(carpetaRaiz, "Musica");

        if (!imagenes.exists()) {
            imagenes.mkdir();
        }

        if (!documentos.exists()) {
            documentos.mkdir();
        }

        if (!musica.exists()) {
            musica.mkdir();
        }
    }

    private void construirInterfaz() {
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        txtRuta = new JTextField();
        txtRuta.setEditable(false);
        panelSuperior.add(txtRuta, BorderLayout.CENTER);

        add(panelSuperior, BorderLayout.NORTH);

        DefaultMutableTreeNode raizTemporal = new DefaultMutableTreeNode("Cargando...");
        arbol = new JTree(raizTemporal);
        arbol.setRootVisible(true);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(UIManager.getIcon("FileView.fileIcon"));
        renderer.setClosedIcon(UIManager.getIcon("FileView.directoryIcon"));
        renderer.setOpenIcon(UIManager.getIcon("FileView.directoryIcon"));
        arbol.setCellRenderer(renderer);

        arbol.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) arbol.getLastSelectedPathComponent();

                if (nodo == null) {
                    return;
                }

                Object objeto = nodo.getUserObject();

                if (objeto instanceof File) {
                    File archivoSeleccionado = (File) objeto;

                    if (archivoSeleccionado.isDirectory()) {
                        mostrarContenido(archivoSeleccionado);
                    }
                }
            }
        });

        JScrollPane scrollArbol = new JScrollPane(arbol);
        scrollArbol.setPreferredSize(new Dimension(260, 0));

        modeloTabla = new DefaultTableModel(
                new Object[]{"Nombre", "Fecha de modificación", "Tipo", "Tamaño"}, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(24);
        JScrollPane scrollTabla = new JScrollPane(tabla);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollArbol, scrollTabla);
        splitPane.setDividerLocation(260);

        add(splitPane, BorderLayout.CENTER);

        JButton btnNuevaCarpeta = new JButton("Nueva Carpeta");
        JButton btnRenombrar = new JButton("Renombrar");
        JButton btnCopiar = new JButton("Copiar");
        JButton btnPegar = new JButton("Pegar");
        JButton btnOrganizar = new JButton("Organizar");
        JButton btnOrdenar = new JButton("Ordenar");

        comboOrden = new JComboBox<String>(new String[]{
            "Ordenar por Nombre",
            "Ordenar por Fecha",
            "Ordenar por Tipo",
            "Ordenar por Tamaño"
        });

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.add(btnNuevaCarpeta);
        panelBotones.add(btnRenombrar);
        panelBotones.add(btnCopiar);
        panelBotones.add(btnPegar);
        panelBotones.add(btnOrganizar);
        panelBotones.add(comboOrden);
        panelBotones.add(btnOrdenar);

        JPanel panelFooter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelFooter.add(new JLabel("Derechos Reservados UNITEC"));

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(panelBotones, BorderLayout.CENTER);
        panelInferior.add(panelFooter, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);

        btnNuevaCarpeta.addActionListener(e -> crearCarpeta());
        btnRenombrar.addActionListener(e -> renombrarSeleccionado());
        btnCopiar.addActionListener(e -> copiarSeleccionados());
        btnPegar.addActionListener(e -> pegarEnCarpetaActual());
        btnOrganizar.addActionListener(e -> organizarArchivos());
        btnOrdenar.addActionListener(e -> ordenarContenido());
    }

    private void cargarArbol() {
        DefaultMutableTreeNode raiz = crearNodo(carpetaRaiz);
        DefaultTreeModel modelo = new DefaultTreeModel(raiz);
        arbol.setModel(modelo);

        TreePath ruta = new TreePath(raiz.getPath());
        arbol.expandPath(ruta);
        arbol.setSelectionPath(ruta);
    }

    private DefaultMutableTreeNode crearNodo(File archivo) {
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(archivo);

        if (archivo.isDirectory()) {
            File[] hijos = archivo.listFiles();

            if (hijos != null) {
                for (int i = 0; i < hijos.length; i++) {
                    if (hijos[i].isDirectory()) {
                        nodo.add(crearNodo(hijos[i]));
                    }
                }
            }
        }

        return nodo;
    }

    private void mostrarContenido(File carpeta) {
        carpetaActual = carpeta;
        txtRuta.setText(formatearRuta(carpeta));

        modeloTabla.setRowCount(0);

        File[] archivos = carpeta.listFiles();

        if (archivos == null) {
            return;
        }

        for (int i = 0; i < archivos.length; i++) {
            ArchivoItem item = new ArchivoItem(archivos[i]);

            modeloTabla.addRow(new Object[]{
                item.getNombre(),
                item.getFechaFormateada(),
                item.getTipo(),
                item.getTamanoFormateado()
            });
        }
    }

    private String formatearRuta(File carpeta) {
        String rutaCompleta = carpeta.getAbsolutePath();

        // Convertir \ a /
        rutaCompleta = rutaCompleta.replace("\\", "/");

        // Buscar la carpeta "raiz" y mostrar desde ahí
        int index = rutaCompleta.indexOf("/raiz");

        if (index != -1) {
            return "C:/" + rutaCompleta.substring(index + 1);
        }

        return rutaCompleta;
    }

    private void refrescar() {
        cargarArbol();

        if (carpetaActual != null && carpetaActual.exists()) {
            mostrarContenido(carpetaActual);
        } else {
            mostrarContenido(carpetaRaiz);
        }
    }

    private File obtenerArchivoSeleccionadoTabla() {
        int fila = tabla.getSelectedRow();

        if (fila == -1 || carpetaActual == null) {
            return null;
        }

        String nombre = modeloTabla.getValueAt(fila, 0).toString();
        return new File(carpetaActual, nombre);
    }

    private void crearCarpeta() {
        if (carpetaActual == null) {
            return;
        }

        String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre de la carpeta:");

        if (nombre == null) {
            return;
        }

        if (nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.");
            return;
        }

        boolean creada = OperacionesArchivo.crearCarpeta(carpetaActual, nombre);

        if (creada) {
            JOptionPane.showMessageDialog(this, "Carpeta creada correctamente.");
            refrescar();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo crear la carpeta o ya existe.");
        }
    }

    private void renombrarSeleccionado() {
        File seleccionado = obtenerArchivoSeleccionadoTabla();

        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un archivo o carpeta.");
            return;
        }

        String nuevoNombre = JOptionPane.showInputDialog(this, "Nuevo nombre:", seleccionado.getName());

        if (nuevoNombre == null) {
            return;
        }

        if (nuevoNombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.");
            return;
        }

        boolean renombrado = OperacionesArchivo.renombrar(seleccionado, nuevoNombre);

        if (renombrado) {
            JOptionPane.showMessageDialog(this, "Elemento renombrado correctamente.");
            refrescar();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo renombrar.");
        }
    }

    private void copiarSeleccionados() {
        int[] filas = tabla.getSelectedRows();

        if (filas.length == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione uno o varios elementos.");
            return;
        }

        File[] seleccionados = new File[filas.length];

        for (int i = 0; i < filas.length; i++) {
            String nombre = modeloTabla.getValueAt(filas[i], 0).toString();
            seleccionados[i] = new File(carpetaActual, nombre);
        }

        OperacionesArchivo.copiar(seleccionados);
        JOptionPane.showMessageDialog(this, "Elemento(s) copiado(s).");
    }

    private void pegarEnCarpetaActual() {
        if (carpetaActual == null) {
            return;
        }

        boolean pegado = OperacionesArchivo.pegar(carpetaActual);

        if (pegado) {
            JOptionPane.showMessageDialog(this, "Elemento(s) pegado(s) correctamente.");
            refrescar();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo pegar.");
        }
    }

    private void organizarArchivos() {
        if (carpetaActual == null) {
            return;
        }

        OperacionesArchivo.organizarArchivos(carpetaActual);
        JOptionPane.showMessageDialog(this, "Archivos organizados correctamente.");
        refrescar();
    }

    private void ordenarContenido() {
        if (carpetaActual == null) {
            return;
        }

        File[] archivos = carpetaActual.listFiles();

        if (archivos == null) {
            return;
        }

        ListaEnlazadaArchivos lista = new ListaEnlazadaArchivos();

        for (int i = 0; i < archivos.length; i++) {
            lista.agregar(new ArchivoItem(archivos[i]));
        }

        String criterio = comboOrden.getSelectedItem().toString();
        Comparator<ArchivoItem> comparador;

        if (criterio.equals("Ordenar por Fecha")) {
            comparador = Comparator.comparingLong(ArchivoItem::getFechaModificacion);
        } else if (criterio.equals("Ordenar por Tipo")) {
            comparador = Comparator.comparing(ArchivoItem::getTipo, String.CASE_INSENSITIVE_ORDER);
        } else if (criterio.equals("Ordenar por Tamaño")) {
            comparador = Comparator.comparingLong(ArchivoItem::getTamano);
        } else {
            comparador = Comparator.comparing(ArchivoItem::getNombre, String.CASE_INSENSITIVE_ORDER);
        }

        lista.mergeSort(comparador);

        modeloTabla.setRowCount(0);
        ArchivoItem[] ordenados = lista.toArray();

        for (int i = 0; i < ordenados.length; i++) {
            modeloTabla.addRow(new Object[]{
                ordenados[i].getNombre(),
                ordenados[i].getFechaFormateada(),
                ordenados[i].getTipo(),
                ordenados[i].getTamanoFormateado()
            });
        }
    }
}
