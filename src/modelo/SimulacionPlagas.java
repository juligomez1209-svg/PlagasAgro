package modelo;

import estructuras.*;
import java.util.Random;

public class SimulacionPlagas {
    public int n, dia;
    public double[][] inf, res;
    public boolean[][] cuarentena;
    public int[] focos;
    public double crecimiento = 0.35, difusion = 0.20, umbralCuarentena = 0.70;
    public final ListaCircular<int[]> enjambres = new ListaCircular<>();
    public final ListaDoble<String> resistentes = new ListaDoble<>();
    public final ListaSimple<String> tratamientos = new ListaSimple<>();
    public final Pila<Snapshot> snapshots = new Pila<>();
    public final Cola<int[]> colaCuarentena = new Cola<>();
    private final Random rnd = new Random();

    public static class Snapshot {
        public final double[][] inf, res; public final int dia;
        Snapshot(double[][] i, double[][] r, int d) { inf = copiar(i); res = copiar(r); dia = d; }
    }
    static double[][] copiar(double[][] m) {
        double[][] c = new double[m.length][];
        for (int i = 0; i < m.length; i++) c[i] = m[i].clone();
        return c;
    }

    public SimulacionPlagas(int n, int numFocos) { generar(n, numFocos); }

    public final void generar(int n, int numFocos) {
        this.n = n; dia = 0;
        inf = new double[n][n]; res = new double[n][n]; cuarentena = new boolean[n][n];
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) res[i][j] = rnd.nextDouble() * 0.25;
        focos = new int[numFocos * 2];
        for (int k = 0; k < numFocos; k++) {
            focos[2 * k] = rnd.nextInt(n); focos[2 * k + 1] = rnd.nextInt(n);
            inf[focos[2 * k]][focos[2 * k + 1]] = 0.8;
        }
        while (!snapshots.vacia()) snapshots.pop();
        while (!colaCuarentena.vacia()) colaCuarentena.desencolar();
        // lista circular: enjambres partiendo de los focos
        // (se recrean listas vaciando por reasignacion de contenido no es posible: se agregan una vez)
        if (enjambres.tamanio() == 0) for (int k = 0; k < Math.max(3, numFocos); k++) enjambres.agregar(new int[]{rnd.nextInt(n), rnd.nextInt(n)});
        else reubicarEnjambres();
    }

    private void reubicarEnjambres() {
        int t = enjambres.tamanio();
        for (int k = 0; k < t; k++) { int[] e = enjambres.actual(); e[0] = rnd.nextInt(n); e[1] = rnd.nextInt(n); enjambres.avanzar(); }
    }

    /** Un dia de simulacion. */
    public void paso() {
        snapshots.push(new Snapshot(inf, res, dia));                 // PILA
        double[][] nu = new double[n][n];
        int[][] d4 = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) {
            double s = 0; int c = 0;
            for (int[] d : d4) { int a = i + d[0], b = j + d[1]; if (a >= 0 && b >= 0 && a < n && b < n) { s += inf[a][b]; c++; } }
            double v = inf[i][j];
            v += (1 - res[i][j]) * (crecimiento * v * (1 - v) + difusion * (s / c - v));
            nu[i][j] = Math.max(0, Math.min(1, v));
        }
        // migracion: un enjambre por dia (round-robin en la lista circular)
        int[] e = enjambres.actual();
        e[0] = Math.max(0, Math.min(n - 1, e[0] + rnd.nextInt(3) - 1));
        e[1] = Math.max(0, Math.min(n - 1, e[1] + rnd.nextInt(3) - 1));
        nu[e[0]][e[1]] = Math.min(1, nu[e[0]][e[1]] + 0.25 * (1 - res[e[0]][e[1]]));
        enjambres.avanzar();
        inf = nu; dia++;
        // seleccion de resistencia en plantas muy infestadas
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) if (inf[i][j] > 0.5 && res[i][j] < 1) {
            double antes = res[i][j]; res[i][j] = Math.min(1, antes + 0.01);
            if (antes < 0.6 && res[i][j] >= 0.6)
                resistentes.agregar(String.format("Dia %d: planta (%d,%d) alcanzo resistencia %.2f", dia, i, j, res[i][j]));  // LISTA DOBLE
        }
        // cuarentena sanitaria
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++)
            if (inf[i][j] > umbralCuarentena && !cuarentena[i][j]) { cuarentena[i][j] = true; colaCuarentena.encolar(new int[]{i, j}); }  // COLA
    }

    /** Trata la celda mas antigua de la cuarentena (FIFO) y su vecindario 3x3. */
    public String tratar() {
        int[] c = colaCuarentena.desencolar();
        if (c == null) return "Cuarentena vacia: no hay celdas criticas.";
        double antes = inf[c[0]][c[1]];
        for (int i = c[0] - 1; i <= c[0] + 1; i++) for (int j = c[1] - 1; j <= c[1] + 1; j++)
            if (i >= 0 && j >= 0 && i < n && j < n) inf[i][j] *= 0.3;
        res[c[0]][c[1]] = Math.min(1, res[c[0]][c[1]] + 0.08);
        cuarentena[c[0]][c[1]] = false;
        String m = String.format("Dia %d: tratamiento en (%d,%d): infestacion %.2f -> %.2f", dia, c[0], c[1], antes, inf[c[0]][c[1]]);
        tratamientos.agregar(m);                                      // LISTA SIMPLE
        return m;
    }

    public boolean deshacer() {
        Snapshot s = snapshots.pop();
        if (s == null) return false;
        inf = copiar(s.inf); res = copiar(s.res); dia = s.dia;
        while (!colaCuarentena.vacia()) colaCuarentena.desencolar();   // reconstruir cuarentena coherente
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) {
            cuarentena[i][j] = inf[i][j] > umbralCuarentena;
            if (cuarentena[i][j]) colaCuarentena.encolar(new int[]{i, j});
        }
        return true;
    }

    public double promedio() { double s = 0; for (double[] f : inf) for (double v : f) s += v; return s / (n * n); }
    public int infectadas() { int c = 0; for (double[] f : inf) for (double v : f) if (v > 0.1) c++; return c; }
    public int[] enjambreActual() { return enjambres.actual(); }
}
