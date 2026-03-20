package navegador;

import java.util.Calendar;

public class VirtualFileSystem {

    private final VirtualNode root;

    public VirtualFileSystem() {
        root = new VirtualNode("Usuario", true, 0, time(2022, 3, 1));
        buildTree();
    }

    private void buildTree() {

        VirtualNode docs = dir("Documentos", 2026, 3, 1);
        add(docs, file("tesnica_utadoc.txt", 1024, 2025, 3, 1));
        add(docs, file("marcos.txt", 1024, 2026, 3, 1));
        add(docs, file("script.txt", 1024, 2025, 3, 1));
        add(docs, file("tema.txt", 1024, 2025, 8, 29));
        root.addChild(docs);

        VirtualNode imgs = dir("Imagenes", 2026, 1, 15);
        add(imgs, file("foto_perfil.jpg", 204800, 2025, 1, 15));
        add(imgs, file("campus.png", 512000, 2025, 12, 3));
        add(imgs, file("logo_unitec.png", 40960, 2026, 11, 20));
        add(imgs, file("banner.gif", 61440, 2026, 1, 2));
        root.addChild(imgs);

        VirtualNode music = dir("Musica", 2026, 11, 5);
        add(music, file("cancion1.mp3", 4194304, 2025, 11, 5));
        add(music, file("cancion2.mp3", 3670016, 2026, 10, 22));
        add(music, file("podcast.wav", 8388608, 2024, 9, 14));
        add(music, file("jingle.ogg", 524288, 2025, 8, 30));
        root.addChild(music);
    }

    private VirtualNode dir(String name, int y, int m, int d) {
        return new VirtualNode(name, true, 0, time(y, m, d));
    }

    private VirtualNode file(String name, long size, int y, int m, int d) {
        return new VirtualNode(name, false, size, time(y, m, d));
    }

    private void add(VirtualNode parent, VirtualNode child) {
        parent.addChild(child);
    }

    private long time(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day, 8, 54, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public VirtualNode getRoot() { return root; }

    public String getPath(VirtualNode node) {
        if (node == null) return "";
        if (node.getParent() == null) return node.getName() + "'documentos";
        StringBuilder sb = new StringBuilder();
        buildPath(node, sb);
        return sb.toString();
    }

    private void buildPath(VirtualNode node, StringBuilder sb) {
        if (node.getParent() == null) {
            sb.append(node.getName()).append("'documentos");
            return;
        }
        buildPath(node.getParent(), sb);
        sb.append(" > ").append(node.getName());
    }

    public VirtualNode findByPath(String path) {
        String[] parts = path.split(">");
        VirtualNode current = root;
        for (int i = 1; i < parts.length; i++) {
            String seg = parts[i].trim();
            boolean found = false;
            for (VirtualNode child : current.getChildren()) {
                if (child.getName().equalsIgnoreCase(seg)) {
                    current = child;
                    found = true;
                    break;
                }
            }
            if (!found) return null;
        }
        return current;
    }
}