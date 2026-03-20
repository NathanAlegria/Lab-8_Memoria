/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package navegdor;

import java.util.Calendar;

/**
 *
 * @author croge
 */
public class VirtualFileSystem {

    private final VirtualNode root;

    public VirtualFileSystem() {
        root = new VirtualNode("enequi", true, 0, time(2022, 3, 1));
        buildTree();
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

    private void buildTree() {
        //nombre, a;o, mes, dia
        VirtualNode docs = dir("Documentos", 2022, 3, 1);
        add(docs, file("tesnica_utadoc.txt", 1024, 2022, 3, 1));
        add(docs, file("marcos.txt", 1024, 2022, 3, 1));
        add(docs, file("script.txt", 1024, 2022, 3, 1));
        add(docs, file("tema.txt", 1024, 2021, 8, 29));
        add(docs, file("informe_final.pdf", 204800, 2022, 2, 14));
        add(docs, file("contrato.docx", 35840, 2022, 1, 10));
        add(docs, file("presupuesto.xlsx", 20480, 2022, 2, 28));

        VirtualNode proyectos = dir("Proyectos", 2022, 2, 20);
        add(proyectos, file("proyecto_java.docx", 51200, 2022, 2, 20));
        add(proyectos, file("diagrama_uml.png", 81920, 2022, 2, 18));
        add(proyectos, file("README.txt", 2048, 2022, 2, 15));
        add(docs, proyectos);

        root.addChild(docs);

        VirtualNode imgs = dir("Imágenes", 2022, 1, 15);
        add(imgs, file("foto_perfil.jpg", 204800, 2022, 1, 15));
        add(imgs, file("campus.png", 512000, 2021, 12, 3));
        add(imgs, file("logo_unitec.png", 40960, 2021, 11, 20));
        add(imgs, file("graduacion.jpg", 819200, 2021, 10, 5));
        add(imgs, file("banner.gif", 61440, 2022, 1, 2));

        VirtualNode capturas = dir("Capturas", 2022, 1, 10);
        add(capturas, file("captura_001.png", 102400, 2022, 1, 10));
        add(capturas, file("captura_002.png", 98304, 2022, 1, 10));
        add(imgs, capturas);

        root.addChild(imgs);

        VirtualNode music = dir("Música", 2021, 11, 5);
        add(music, file("cancion1.mp3", 4194304, 2021, 11, 5));
        add(music, file("cancion2.mp3", 3670016, 2021, 10, 22));
        add(music, file("podcast.wav", 8388608, 2021, 9, 14));
        add(music, file("jingle.ogg", 524288, 2021, 8, 30));

        VirtualNode favoritos = dir("Favoritos", 2021, 9, 1);
        add(favoritos, file("top1.mp3", 5242880, 2021, 9, 1));
        add(favoritos, file("top2.mp3", 4718592, 2021, 9, 1));
        add(music, favoritos);

        root.addChild(music);

        VirtualNode dl = dir("Descargas", 2022, 2, 28);
        add(dl, file("instalador.exe", 52428800, 2022, 2, 28));
        add(dl, file("libreria.zip", 10485760, 2022, 2, 25));
        add(dl, file("apuntes.pdf", 307200, 2022, 2, 20));
        add(dl, file("video_clase.mp4", 104857600, 2022, 2, 10));
        add(dl, file("foto_evento.jpg", 614400, 2022, 1, 30));
        add(dl, file("nota_rapida.txt", 1024, 2022, 2, 28));
        root.addChild(dl);

        VirtualNode cal = dir("Calendario", 2022, 2, 1);
        add(cal, file("agenda_2022.docx", 20480, 2022, 2, 1));
        add(cal, file("horario.xlsx", 15360, 2022, 1, 15));
        add(cal, file("eventos.txt", 2048, 2022, 2, 1));
        root.addChild(cal);

        VirtualNode mail = dir("Correo", 2022, 3, 1);
        add(mail, file("bienvenida.txt", 1024, 2021, 8, 1));
        add(mail, file("respuesta.docx", 12288, 2022, 2, 14));
        add(mail, file("adjunto.pdf", 40960, 2022, 2, 28));
        root.addChild(mail);

        VirtualNode nueva = dir("Nueva Carpeta", 2022, 3, 1);
        root.addChild(nueva);

        VirtualNode consola = dir("Consola", 2022, 1, 20);
        add(consola, file("Main.java", 3072, 2022, 1, 20));
        add(consola, file("Config.java", 2048, 2022, 1, 18));
        add(consola, file("output.log", 8192, 2022, 1, 20));
        root.addChild(consola);

    }
}
