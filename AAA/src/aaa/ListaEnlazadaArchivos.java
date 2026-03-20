/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aaa;
import java.util.Comparator;

public class ListaEnlazadaArchivos {

    private Nodo cabeza;

    private static class Nodo {
        ArchivoItem dato;
        Nodo siguiente;

        public Nodo(ArchivoItem dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    public void agregar(ArchivoItem item) {
        Nodo nuevo = new Nodo(item);

        if (cabeza == null) {
            cabeza = nuevo;
            return;
        }

        Nodo actual = cabeza;
        while (actual.siguiente != null) {
            actual = actual.siguiente;
        }
        actual.siguiente = nuevo;
    }

    public int size() {
        int contador = 0;
        Nodo actual = cabeza;

        while (actual != null) {
            contador++;
            actual = actual.siguiente;
        }

        return contador;
    }

    public ArchivoItem[] toArray() {
        ArchivoItem[] arreglo = new ArchivoItem[size()];
        Nodo actual = cabeza;
        int i = 0;

        while (actual != null) {
            arreglo[i] = actual.dato;
            actual = actual.siguiente;
            i++;
        }

        return arreglo;
    }

    public void mergeSort(Comparator<ArchivoItem> comparador) {
        cabeza = mergeSortRec(cabeza, comparador);
    }

    private Nodo mergeSortRec(Nodo nodo, Comparator<ArchivoItem> comparador) {
        if (nodo == null || nodo.siguiente == null) {
            return nodo;
        }

        Nodo mitad = obtenerMitad(nodo);
        Nodo inicioDerecha = mitad.siguiente;
        mitad.siguiente = null;

        Nodo izquierda = mergeSortRec(nodo, comparador);
        Nodo derecha = mergeSortRec(inicioDerecha, comparador);

        return merge(izquierda, derecha, comparador);
    }

    private Nodo obtenerMitad(Nodo nodo) {
        if (nodo == null) {
            return null;
        }

        Nodo lento = nodo;
        Nodo rapido = nodo.siguiente;

        while (rapido != null && rapido.siguiente != null) {
            lento = lento.siguiente;
            rapido = rapido.siguiente.siguiente;
        }

        return lento;
    }

    private Nodo merge(Nodo a, Nodo b, Comparator<ArchivoItem> comparador) {
        if (a == null) {
            return b;
        }

        if (b == null) {
            return a;
        }

        Nodo resultado;

        if (comparador.compare(a.dato, b.dato) <= 0) {
            resultado = a;
            resultado.siguiente = merge(a.siguiente, b, comparador);
        } else {
            resultado = b;
            resultado.siguiente = merge(a, b.siguiente, comparador);
        }

        return resultado;
    }
}
