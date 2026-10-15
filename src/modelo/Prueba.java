package modelo;
public class Prueba {
    public static void main(String[] a) {
        SimulacionPlagas s = new SimulacionPlagas(15, 2);
        System.out.printf("Dia 0: prom=%.4f infectadas=%d%n", s.promedio(), s.infectadas());
        for (int i = 0; i < 40; i++) s.paso();
        System.out.printf("Dia %d: prom=%.4f infectadas=%d cuarentena=%d snapshots=%d%n", s.dia, s.promedio(), s.infectadas(), s.colaCuarentena.tamanio(), s.snapshots.tamanio());
        System.out.println(s.tratar());
        System.out.println(s.tratar());
        System.out.print("Resistentes (reciente->antiguo):\n" + s.resistentes.recorrerAtras());
        double p = s.promedio(); int d = s.dia;
        System.out.println("Deshacer=" + s.deshacer() + " dia " + d + " -> " + s.dia);
        System.out.println("Enjambre actual: " + s.enjambreActual()[0] + "," + s.enjambreActual()[1] + " tratamientos=" + s.tratamientos.tamanio());
    }
}
