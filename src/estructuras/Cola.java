package estructuras;

/** Cola FIFO enlazada. Uso en el proyecto: ver SimulacionPlagas. */
public class Cola<T> {
    private static class Nodo<T> { T dato; Nodo<T> sig; Nodo(T d) { dato = d; } }
    private Nodo<T> frente, fin;
    private int tam;

    public void encolar(T d) {
        Nodo<T> n = new Nodo<>(d);
        if (fin == null) frente = fin = n; else { fin.sig = n; fin = n; }
        tam++;
    }
    public T desencolar() {
        if (frente == null) return null;
        T d = frente.dato; frente = frente.sig; if (frente == null) fin = null; tam--; return d;
    }
    public boolean vacia() { return frente == null; }
    public int tamanio() { return tam; }
}
