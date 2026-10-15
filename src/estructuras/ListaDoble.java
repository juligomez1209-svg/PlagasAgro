package estructuras;

/** Lista doblemente enlazada. Uso en el proyecto: ver SimulacionPlagas. */
public class ListaDoble<T> {
    private static class Nodo<T> { T dato; Nodo<T> sig, ant; Nodo(T d) { dato = d; } }
    private Nodo<T> cabeza, cola;
    private int tam;

    public void agregar(T d) {                 // O(1)
        Nodo<T> n = new Nodo<>(d);
        if (cola == null) cabeza = cola = n; else { cola.sig = n; n.ant = cola; cola = n; }
        tam++;
    }
    public int tamanio() { return tam; }
    public String recorrerAdelante() {         // mas antiguo -> mas reciente
        StringBuilder sb = new StringBuilder();
        for (Nodo<T> a = cabeza; a != null; a = a.sig) sb.append(a.dato).append('\n');
        return sb.toString();
    }
    public String recorrerAtras() {            // mas reciente -> mas antiguo
        StringBuilder sb = new StringBuilder();
        for (Nodo<T> a = cola; a != null; a = a.ant) sb.append(a.dato).append('\n');
        return sb.toString();
    }
}
