package navegdor;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class FileExplorer extends JFrame {

    private VirtualFileSystem vfs;

    private JTree fileTree;
    private DefaultTreeModel treeModel;
    private JTable fileTable;
    private FileTableModel tableModel;
    private JTextField pathField;

    private VirtualNode currentNode;

    private java.util.List<VirtualNode> clipboard = new ArrayList<>();

    private boolean updatingTree = false;

    public FileExplorer() {
        vfs = new VirtualFileSystem();
        initUI();
        navigateTo(vfs.getRoot());
    }

    private void initUI() {
        setTitle("JAVA CENTER");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        setLayout(new BorderLayout());
        add(buildToolbar(), BorderLayout.NORTH);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildTreePanel(), buildTablePanel());
        split.setDividerLocation(220);
        split.setDividerSize(4);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildToolbar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(180, 200, 230));
        top.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(140, 160, 190)));

        JPanel navRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        navRow.setOpaque(false);
        pathField = new JTextField(40);
        pathField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pathField.addActionListener(e -> navigateByPath(pathField.getText()));
        navRow.add(pathField);
        top.add(navRow, BorderLayout.NORTH);

        JPanel actRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        actRow.setOpaque(false);
        actRow.add(actionBtn("Nueva Carpeta", this::createFolder));
        actRow.add(actionBtn("Renombrar",     this::renameItem));
        actRow.add(actionBtn("Copiar",        this::copyItems));
        actRow.add(actionBtn("Pegar",         this::pasteItems));
        actRow.add(actionBtn("Eliminar",      this::deleteItem));
        actRow.add(new JSeparator(JSeparator.VERTICAL));
        actRow.add(actionBtn("Organizar",     this::organizeFolder));
        top.add(actRow, BorderLayout.SOUTH);
        return top;
    }

    private JButton actionBtn(String text, Runnable action) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        b.setFocusPainted(false);
        b.addActionListener(e -> action.run());
        return b;
    }

    private JScrollPane buildTreePanel() {
        treeModel = new DefaultTreeModel(buildTreeNode(vfs.getRoot()));
        fileTree  = new JTree(treeModel);
        fileTree.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fileTree.setRowHeight(22);
        fileTree.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        fileTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override public Component getTreeCellRendererComponent(JTree t, Object val,
                    boolean sel, boolean exp, boolean leaf, int row, boolean focus) {
                super.getTreeCellRendererComponent(t, val, sel, exp, leaf, row, focus);
                if (val instanceof DefaultMutableTreeNode n && n.getUserObject() instanceof VirtualNode vn) {
                    setText(vn.getName());
                    setIcon(vn.isDirectory() ? UIManager.getIcon("FileView.directoryIcon")
                                             : UIManager.getIcon("FileView.fileIcon"));
                }
                return this;
            }
        });
        fileTree.addTreeSelectionListener(e -> {
            if (updatingTree) return;
            DefaultMutableTreeNode selected = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
            if (selected != null && selected.getUserObject() instanceof VirtualNode vn) {
                navigateTo(vn);
            }
        });
        JScrollPane sp = new JScrollPane(fileTree);
        sp.setPreferredSize(new Dimension(220, 0));
        sp.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(180, 180, 180)));
        return sp;
    }

    private DefaultMutableTreeNode buildTreeNode(VirtualNode node) {
        DefaultMutableTreeNode tn = new DefaultMutableTreeNode(node);
        if (node.isDirectory()) {
            for (VirtualNode child : node.getChildren()) {
                if (child.isDirectory()) tn.add(buildTreeNode(child));
            }
        }
        return tn;
    }

    private JScrollPane buildTablePanel() {
        tableModel = new FileTableModel();
        fileTable  = new JTable(tableModel);
        fileTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fileTable.setRowHeight(22);
        fileTable.setShowGrid(false);
        fileTable.setIntercellSpacing(new Dimension(0, 0));
        fileTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        fileTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fileTable.getColumnModel().getColumn(0).setPreferredWidth(250);
        fileTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        fileTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        fileTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        fileTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) openSelected();
            }
        });
        JPopupMenu popup = new JPopupMenu();
        addMenuItem(popup, "Nueva Carpeta", this::createFolder);
        addMenuItem(popup, "Renombrar",     this::renameItem);
        addMenuItem(popup, "Copiar",        this::copyItems);
        addMenuItem(popup, "Pegar",         this::pasteItems);
        addMenuItem(popup, "Eliminar",      this::deleteItem);
        popup.addSeparator();
        addMenuItem(popup, "Organizar",     this::organizeFolder);
        fileTable.setComponentPopupMenu(popup);
        JScrollPane sp = new JScrollPane(fileTable);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }

    private void addMenuItem(JPopupMenu m, String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        item.addActionListener(e -> action.run());
        m.add(item);
    }

    private void navigateTo(VirtualNode node) {
        currentNode = node;
        refreshTable();
        refreshPath();
        syncTree(node);
    }

    private void navigateByPath(String path) {
        VirtualNode found = vfs.findByPath(path);
        if (found != null) navigateTo(found);
        else showError("Ruta no encontrada: " + path);
    }

    private void openSelected() {
        int row = fileTable.getSelectedRow();
        if (row < 0) return;
        VirtualNode vn = tableModel.getNodeAt(row);
        if (vn != null && vn.isDirectory()) navigateTo(vn);
    }

    private void refreshTable() {
        tableModel.setNodes(currentNode.getChildren());
    }

    private void refreshPath() {
        pathField.setText(vfs.getPath(currentNode));
    }

    private void syncTree(VirtualNode target) {
        updatingTree = true;
        try {
            treeModel.setRoot(buildTreeNode(vfs.getRoot()));
            expandAndSelect(target);
        } finally {
            updatingTree = false;
        }
    }

    private void expandAndSelect(VirtualNode target) {
        String path = vfs.getPath(target);
        TreeNode root = (TreeNode) treeModel.getRoot();
        selectNodeByPath(root, path, new TreePath(root));
    }

    private void selectNodeByPath(TreeNode tn, String targetPath, TreePath tp) {
        if (tn instanceof DefaultMutableTreeNode dmtn && dmtn.getUserObject() instanceof VirtualNode vn) {
            if (vfs.getPath(vn).equals(targetPath)) {
                fileTree.setSelectionPath(tp);
                fileTree.scrollPathToVisible(tp);
                return;
            }
        }
        for (int i = 0; i < tn.getChildCount(); i++) {
            TreeNode child = tn.getChildAt(i);
            selectNodeByPath(child, targetPath, tp.pathByAddingChild(child));
        }
    }

    private void createFolder() {
        String name = JOptionPane.showInputDialog(this, "Nombre de la nueva carpeta:", "Nueva Carpeta", JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.trim().isEmpty()) return;
        name = name.trim();
        if (currentNode.hasChild(name)) { showError("Ya existe un elemento con ese nombre."); return; }
        VirtualNode newDir = new VirtualNode(name, true, 0, System.currentTimeMillis());
        currentNode.addChild(newDir);
        newDir.setParent(currentNode);
        refreshTable();
        syncTree(currentNode);
    }

    private void renameItem() {
        int row = fileTable.getSelectedRow();
        if (row < 0) { showError("Selecciona un archivo o carpeta."); return; }
        VirtualNode vn = tableModel.getNodeAt(row);
        String newName = (String) JOptionPane.showInputDialog(this, "Nuevo nombre:", "Renombrar",
                JOptionPane.PLAIN_MESSAGE, null, null, vn.getName());
        if (newName == null || newName.trim().isEmpty()) return;
        newName = newName.trim();
        if (currentNode.hasChild(newName) && !newName.equals(vn.getName())) {
            showError("Ya existe un elemento con ese nombre."); return;
        }
        vn.setName(newName);
        vn.setLastModified(System.currentTimeMillis());
        refreshTable();
        syncTree(currentNode);
    }

    private void copyItems() {
        int[] rows = fileTable.getSelectedRows();
        if (rows.length == 0) { showError("Selecciona al menos un elemento."); return; }
        clipboard.clear();
        for (int r : rows) clipboard.add(tableModel.getNodeAt(r));
    }

    private void pasteItems() {
        if (clipboard.isEmpty()) { showError("El portapapeles esta vacio."); return; }
        for (VirtualNode original : clipboard) {
            VirtualNode copy = original.deepCopy();
            String base = copy.getName(), name = base; int cnt = 1;
            while (currentNode.hasChild(name)) name = base + " (" + (cnt++) + ")";
            copy.setName(name);
            copy.setParent(currentNode);
            currentNode.addChild(copy);
        }
        refreshTable();
        syncTree(currentNode);
    }

    private void deleteItem() {
        int[] rows = fileTable.getSelectedRows();
        if (rows.length == 0) { showError("Selecciona al menos un elemento."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Eliminar " + rows.length + " elemento(s)?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        for (int i = rows.length - 1; i >= 0; i--) {
            VirtualNode vn = tableModel.getNodeAt(rows[i]);
            currentNode.removeChild(vn);
        }
        refreshTable();
        syncTree(currentNode);
    }

    private void organizeFolder() {
        Map<String, String> typeRules = new LinkedHashMap<>();
        typeRules.put(".jpg",  "Imagenes");
        typeRules.put(".jpeg", "Imagenes");
        typeRules.put(".png",  "Imagenes");
        typeRules.put(".gif",  "Imagenes");
        typeRules.put(".bmp",  "Imagenes");
        typeRules.put(".svg",  "Imagenes");
        typeRules.put(".webp", "Imagenes");
        typeRules.put(".pdf",  "Documentos");
        typeRules.put(".docx", "Documentos");
        typeRules.put(".doc",  "Documentos");
        typeRules.put(".txt",  "Documentos");
        typeRules.put(".xlsx", "Documentos");
        typeRules.put(".pptx", "Documentos");
        typeRules.put(".odt",  "Documentos");
        typeRules.put(".mp3",  "Musica");
        typeRules.put(".wav",  "Musica");
        typeRules.put(".flac", "Musica");
        typeRules.put(".aac",  "Musica");
        typeRules.put(".ogg",  "Musica");
        typeRules.put(".wma",  "Musica");
        typeRules.put(".mp4",  "Videos");
        typeRules.put(".avi",  "Videos");
        typeRules.put(".mkv",  "Videos");
        typeRules.put(".mov",  "Videos");
        typeRules.put(".java", "Codigo");
        typeRules.put(".py",   "Codigo");
        typeRules.put(".js",   "Codigo");
        typeRules.put(".html", "Codigo");
        typeRules.put(".css",  "Codigo");

        java.util.List<VirtualNode> files = new ArrayList<>();
        for (VirtualNode child : new ArrayList<>(currentNode.getChildren())) {
            if (!child.isDirectory()) files.add(child);
        }

        String currentName = currentNode.getName().toLowerCase();
        boolean alreadyInTypeFolder = currentName.equals("imagenes") ||
                                      currentName.equals("documentos") ||
                                      currentName.equals("musica") ||
                                      currentName.equals("videos") ||
                                      currentName.equals("codigo");

        int moved = 0;
        for (VirtualNode file : files) {
            String ext = getExtension(file.getName()).toLowerCase();
            String parentFolderName = typeRules.get(ext);
            if (parentFolderName == null) continue;

            String subFolderName = ext.substring(1).toLowerCase();

            VirtualNode destFolder;
            if (alreadyInTypeFolder) {
                destFolder = currentNode.getChildDir(subFolderName);
                if (destFolder == null) {
                    destFolder = new VirtualNode(subFolderName, true, 0, System.currentTimeMillis());
                    destFolder.setParent(currentNode);
                    currentNode.addChild(destFolder);
                }
            } else {
                VirtualNode parentFolder = currentNode.getChildDir(parentFolderName);
                if (parentFolder == null) {
                    parentFolder = new VirtualNode(parentFolderName, true, 0, System.currentTimeMillis());
                    parentFolder.setParent(currentNode);
                    currentNode.addChild(parentFolder);
                }
                destFolder = parentFolder.getChildDir(subFolderName);
                if (destFolder == null) {
                    destFolder = new VirtualNode(subFolderName, true, 0, System.currentTimeMillis());
                    destFolder.setParent(parentFolder);
                    parentFolder.addChild(destFolder);
                }
            }

            currentNode.removeChild(file);
            file.setParent(destFolder);
            destFolder.addChild(file);
            moved++;
        }

        refreshTable();
        syncTree(currentNode);
        JOptionPane.showMessageDialog(this, moved + " archivo(s) organizados.", "Organizar", JOptionPane.INFORMATION_MESSAGE);
    }

    private void sortBy(String criterion) {
        LinkedFileList list = new LinkedFileList();
        for (VirtualNode vn : currentNode.getChildren()) list.add(vn);
        switch (criterion) {
            case "Nombre" -> list.mergeSort(Comparator.comparing(VirtualNode::getName, String.CASE_INSENSITIVE_ORDER));
            case "Fecha"  -> list.mergeSort(Comparator.comparingLong(VirtualNode::getLastModified).reversed());
            case "Tipo"   -> list.mergeSort(Comparator.comparing(vn -> vn.isDirectory() ? "0" : getExtension(vn.getName())));
            case "Tamano" -> list.mergeSort(Comparator.comparingLong(VirtualNode::getSize).reversed());
        }
        currentNode.setChildren(list.toList());
        refreshTable();
    }

    private String getExtension(String name) {
        int i = name.lastIndexOf('.');
        return (i >= 0) ? name.substring(i) : "";
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileExplorer().setVisible(true));
    }
}