package estructuras;

/** Pila LIFO enlazada. Uso en el proyecto: ver SimulacionPlagas. */
public class Pila<T> {
    private static class Nodo<T> { T dato; Nodo<T> sig; Nodo(T d, Nodo<T> s) { dato = d; sig = s; } }
    private Nodo<T> tope;
    private int tam;

    public void push(T d) { tope = new Nodo<>(d, tope); tam++; }
    public T pop() {
        if (tope == null) return null;
        T d = tope.dato; tope = tope.sig; tam--; return d;
    }
    public T peek() { return tope == null ? null : tope.dato; }
    public boolean vacia() { return tope == null; }
    public int tamanio() { return tam; }
}
