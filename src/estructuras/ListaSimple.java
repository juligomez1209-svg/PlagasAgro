package estructuras;

/** Lista simplemente enlazada. Uso en el proyecto: ver SimulacionPlagas. */
public class ListaSimple<T> {
    private static class Nodo<T> { T dato; Nodo<T> sig; Nodo(T d) { dato = d; } }
    private Nodo<T> cabeza, cola;
    private int tam;

    public void agregar(T d) {                 // O(1)
        Nodo<T> n = new Nodo<>(d);
        if (cabeza == null) cabeza = cola = n; else { cola.sig = n; cola = n; }
        tam++;
    }
    public T obtener(int i) {                  // O(n)
        Nodo<T> a = cabeza;
        for (int k = 0; k < i; k++) a = a.sig;
        return a.dato;
    }
    public int tamanio() { return tam; }
    public void limpiar() { cabeza = cola = null; tam = 0; }
}
