package estructuras;

/** Lista circular simplemente enlazada. Uso en el proyecto: ver SimulacionPlagas. */
public class ListaCircular<T> {
    private static class Nodo<T> { T dato; Nodo<T> sig; Nodo(T d) { dato = d; } }
    private Nodo<T> ultimo;      // ultimo.sig es la cabeza
    private Nodo<T> actual;
    private int tam;

    public void agregar(T d) {                 // O(1)
        Nodo<T> n = new Nodo<>(d);
        if (ultimo == null) { n.sig = n; ultimo = n; actual = n; }
        else { n.sig = ultimo.sig; ultimo.sig = n; ultimo = n; }
        tam++;
    }
    public T actual() { return actual == null ? null : actual.dato; }
    /** Avanza al siguiente sector del ciclo y lo devuelve. */
    public T avanzar() { actual = actual.sig; return actual.dato; }
    public int tamanio() { return tam; }
}
