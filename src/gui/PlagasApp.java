package gui;

import modelo.SimulacionPlagas;
import javax.swing.*;
import java.awt.*;

/** GUI Swing del Simulador de Propagacion de Plagas. */
public class PlagasApp extends JFrame {
    private SimulacionPlagas sim = new SimulacionPlagas(20, 3);
    private final JTextArea log = new JTextArea();
    private final JLabel lblEst = new JLabel(), lblEd = new JLabel();
    private final Grilla grilla = new Grilla();
    private final Timer timer = new Timer(300, e -> { sim.paso(); refrescar(); });
    private final JSpinner spN = new JSpinner(new SpinnerNumberModel(20, 8, 30, 1));
    private final JSpinner spF = new JSpinner(new SpinnerNumberModel(3, 1, 8, 1));
    private final JSpinner spG = new JSpinner(new SpinnerNumberModel(0.35, 0.0, 1.0, 0.05));
    private final JSpinner spD = new JSpinner(new SpinnerNumberModel(0.20, 0.0, 1.0, 0.05));

    public PlagasApp() {
        super("Simulador de Propagacion de Plagas (Estructura de Datos)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        grilla.setBorder(BorderFactory.createTitledBorder("Lote (verde = sano, rojo = infestado | borde azul = cuarentena | punto negro = enjambre)"));
        log.setEditable(false);
        JScrollPane sp = new JScrollPane(log);
        sp.setBorder(BorderFactory.createTitledBorder("Bitacora"));
        sp.setPreferredSize(new Dimension(380, 300));

        JPanel c = new JPanel(new GridLayout(0, 4, 4, 4));
        c.setBorder(BorderFactory.createTitledBorder("Controles"));
        c.add(new JLabel("Tamano (n):")); c.add(spN); c.add(new JLabel("Focos:")); c.add(spF);
        c.add(new JLabel("Crecimiento g:")); c.add(spG); c.add(new JLabel("Difusion d:")); c.add(spD);
        c.add(btn("Nuevo lote", e -> nuevo())); c.add(btn("Paso (1 dia)", e -> { aplicarParametros(); sim.paso(); refrescar(); }));
        c.add(btn("Auto / Pausa", e -> { if (timer.isRunning()) timer.stop(); else { aplicarParametros(); timer.start(); } }));
        c.add(btn("Deshacer (Pila)", e -> { msg(sim.deshacer() ? "Snapshot restaurado (pop)." : "Pila vacia."); refrescar(); }));
        c.add(btn("Tratar cuarentena (Cola)", e -> { msg(sim.tratar()); refrescar(); }));
        c.add(btn("Resistencias (Doble)", e -> msg(sim.resistentes.tamanio() == 0 ? "Sin plantas resistentes aun." : "--- Resistencias (reciente -> antiguo) ---\n" + sim.resistentes.recorrerAtras())));
        c.add(btn("Resistencias (cronologico)", e -> msg(sim.resistentes.tamanio() == 0 ? "Sin plantas resistentes aun." : "--- Resistencias (antiguo -> reciente) ---\n" + sim.resistentes.recorrerAdelante())));
        c.add(new JLabel("Clic en lote = nuevo foco"));
        JPanel sur = new JPanel(new GridLayout(2, 1)); sur.add(lblEst); sur.add(lblEd);
        JPanel abajo = new JPanel(new BorderLayout()); abajo.add(c); abajo.add(sur, BorderLayout.SOUTH);

        add(grilla, BorderLayout.CENTER); add(sp, BorderLayout.EAST); add(abajo, BorderLayout.SOUTH);
        grilla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                int[] rc = grilla.celda(e.getX(), e.getY());
                if (rc != null) { sim.inf[rc[0]][rc[1]] = 0.9; msg("Foco manual en (" + rc[0] + "," + rc[1] + ")"); refrescar(); }
            }
        });
        refrescar();
        setSize(1100, 720); setLocationRelativeTo(null);
    }

    private JButton btn(String t, java.awt.event.ActionListener a) { JButton b = new JButton(t); b.addActionListener(a); return b; }
    private void msg(String s) { log.append(s + "\n"); log.setCaretPosition(log.getDocument().getLength()); }
    private void aplicarParametros() { sim.crecimiento = (Double) spG.getValue(); sim.difusion = (Double) spD.getValue(); }
    private void nuevo() {
        timer.stop(); aplicarParametros();
        sim.generar((Integer) spN.getValue(), (Integer) spF.getValue());
        log.setText(""); msg("Nuevo lote " + sim.n + "x" + sim.n + " con focos en (fila,col): " + focosTexto()); refrescar();
    }
    private String focosTexto() {
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < sim.focos.length; k += 2) sb.append("(").append(sim.focos[k]).append(",").append(sim.focos[k + 1]).append(") ");
        return sb.toString();
    }
    private void refrescar() {
        int[] e = sim.enjambreActual();
        lblEst.setText(String.format(" Dia %d | Infestacion media %.1f%% | Plantas infestadas (>10%%): %d de %d",
                sim.dia, sim.promedio() * 100, sim.infectadas(), sim.n * sim.n));
        lblEd.setText(String.format(" Pila: %d snapshots | Cola cuarentena: %d | Lista doble resistentes: %d | Lista simple tratamientos: %d | Enjambre en turno: (%d,%d)",
                sim.snapshots.tamanio(), sim.colaCuarentena.tamanio(), sim.resistentes.tamanio(), sim.tratamientos.tamanio(), e[0], e[1]));
        grilla.repaint();
    }

    class Grilla extends JPanel {
        int cs, ox, oy;
        int[] celda(int x, int y) {
            int c = (x - ox) / Math.max(cs, 1), r = (y - oy) / Math.max(cs, 1);
            return (x < ox || y < oy || r >= sim.n || c >= sim.n) ? null : new int[]{r, c};
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int n = sim.n;
            cs = Math.max(8, Math.min(getWidth() - 20, getHeight() - 40) / n);
            ox = 10; oy = 22;
            for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) {
                double v = sim.inf[i][j];
                g.setColor(new Color((int) (60 + 195 * v), (int) (170 - 140 * v), 50));
                g.fillRect(ox + j * cs, oy + i * cs, cs, cs);
                g.setColor(new Color(0, 0, 0, 40)); g.drawRect(ox + j * cs, oy + i * cs, cs, cs);
                if (sim.cuarentena[i][j]) { g.setColor(Color.BLUE); g.drawRect(ox + j * cs + 1, oy + i * cs + 1, cs - 2, cs - 2); }
            }
            int[] e = sim.enjambreActual(); g.setColor(Color.BLACK);
            g.fillOval(ox + e[1] * cs + cs / 4, oy + e[0] * cs + cs / 4, cs / 2, cs / 2);
        }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new PlagasApp().setVisible(true)); }
}
