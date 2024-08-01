package Calculadora;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.applet.AudioClip;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 *
 * @author profesor
 */
public class Calculadora extends JFrame {
    private JLabel lblResultado = new JLabel(" ");
    private JPanel pnlBotones = new JPanel(new GridLayout(4, 4));
    private JPanel pnlIgual = new JPanel(new GridLayout(1, 1));
    private JButton[] botones = {
            new JButton("1"), new JButton("2"), new JButton("3"), new JButton("+"),
            new JButton("4"), new JButton("5"), new JButton("6"), new JButton("-"),
            new JButton("7"), new JButton("8"), new JButton("9"), new JButton("*"),
            new JButton("C"), new JButton("0"), new JButton(","), new JButton("/"),
            new JButton("=")
    };
    private Dimension dmVentana = new Dimension(300, 440);

    private double resultado = 0;
    private double numero;
    private static final int SUMA = 1;
    private static final int RESTA = 2;
    private static final int MULTIPLICACION = 3;
    private static final int DIVISION = 4;
    private static final int NINGUNO = 0;
    private int operador = Calculadora.NINGUNO;
    private boolean hayPunto = false;
    private boolean nuevoNumero = true;
    private NumberFormat nf = NumberFormat.getInstance();
    private List<String> historialOperaciones = new ArrayList<>();
    private AudioClip sonidoTecla;

    public Calculadora() {
        Dimension dmPantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (dmPantalla.width - dmVentana.width) / 2;
        int y = (dmPantalla.height - dmVentana.height) / 2;
        this.setLocation(x, y);
        this.setSize(dmVentana);
        this.setTitle("Calculadora");

        lblResultado.setBackground(Color.white);
        lblResultado.setOpaque(true);
        lblResultado.setFont(new Font("Arial", Font.PLAIN, 32));
        PulsaRaton pr = new PulsaRaton();
        PulsaTecla pt = new PulsaTecla();
        for (int i = 0; i < botones.length - 1; i++) {
            pnlBotones.add(botones[i]);
            botones[i].addActionListener(pr);
            botones[i].addKeyListener(pt);
        }

        pnlIgual.add(botones[botones.length - 1]);
        botones[botones.length - 1].addActionListener(pr);
        botones[botones.length - 1].addKeyListener(pt);

        pnlIgual.setPreferredSize(new Dimension(0, 50));
        this.add(lblResultado, BorderLayout.NORTH);
        this.add(pnlBotones);
        this.add(pnlIgual, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        lblResultado.setText("0,0");
        lblResultado.setHorizontalAlignment(JLabel.RIGHT);

        crearMenu();
        cargarSonido();
    }

    private void crearMenu() {
        JMenuBar menuBar = new JMenuBar();

        // Menú Opciones
        JMenu menuOpciones = new JMenu("Opciones");
        JMenuItem menuNuevo = new JMenuItem("Nuevo");
        JMenuItem menuHistorial = new JMenuItem("Historial");

        menuNuevo.addActionListener(e -> reiniciarCalculadora());
        menuHistorial.addActionListener(e -> mostrarHistorial());

        menuOpciones.add(menuNuevo);
        menuOpciones.add(menuHistorial);

        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem menuManual = new JMenuItem("Manual de Usuario");

        menuManual.addActionListener(e -> mostrarManual());

        menuAyuda.add(menuManual);

        menuBar.add(menuOpciones);
        menuBar.add(menuAyuda);

        this.setJMenuBar(menuBar);
    }

    private void reiniciarCalculadora() {
        lblResultado.setText("0,0");
        resultado = 0;
        operador = NINGUNO;
        nuevoNumero = true;
        hayPunto = false;
        historialOperaciones.add("Nuevo");
        escribirEnBitacora("Nuevo");
    }

    private void mostrarHistorial() {
        JFrame historialFrame = new JFrame("Historial de Operaciones");
        JTextArea textArea = new JTextArea(20, 30);
        textArea.setEditable(false);

        for (String operacion : historialOperaciones) {
            textArea.append(operacion + "\n");
        }

        historialFrame.add(new JScrollPane(textArea));
        historialFrame.pack();
        historialFrame.setVisible(true);
    }

    private void mostrarManual() {
        JOptionPane.showMessageDialog(this, "Manual de Usuario:\n- Utilice los botones para realizar operaciones.\n- Utilice el menú Opciones para reiniciar o ver el historial.", "Manual de Usuario", JOptionPane.INFORMATION_MESSAGE);
    }

    private void escribirEnBitacora(String operacion) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("bitacora.txt", true))) {
            writer.write(operacion + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void cargarSonido() {
        try {
            URL url = getClass().getResource("/sonido.wav");
            sonidoTecla = Applet.newAudioClip(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Calculadora();
    }

    class PulsaRaton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (sonidoTecla != null) {
                sonidoTecla.play();
            }
            JButton origen = (JButton) e.getSource();
            String texto = origen.getText();
            accion(texto);
        }
    }

    class PulsaTecla extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (sonidoTecla != null) {
                sonidoTecla.play();
            }
            if ((e.getKeyCode() >= KeyEvent.VK_0 && e.getKeyCode() <= KeyEvent.VK_9) ||
                    e.getKeyCode() == KeyEvent.VK_COMMA ||
                    e.getKeyCode() == KeyEvent.VK_ADD ||
                    e.getKeyCode() == KeyEvent.VK_MINUS ||
                    e.getKeyCode() == KeyEvent.VK_MULTIPLY ||
                    e.getKeyCode() == KeyEvent.VK_DIVIDE ||
                    e.getKeyCode() == KeyEvent.VK_C ||
                    (e.getKeyCode() >= KeyEvent.VK_NUMPAD0 && e.getKeyCode() <= KeyEvent.VK_NUMPAD9)) {
                accion("" + e.getKeyChar());
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                accion("=");
            }
        }
    }

    private void accion(String texto) {
        switch (texto) {
            case "+":
                operar(Calculadora.SUMA);
                historialOperaciones.add("+");
                break;
            case "-":
                operar(Calculadora.RESTA);
                historialOperaciones.add("-");
                break;
            case "*":
                operar(Calculadora.MULTIPLICACION);
                historialOperaciones.add("*");
                break;
            case "/":
                operar(Calculadora.DIVISION);
                historialOperaciones.add("/");
                break;
            case ",":
                if (!nuevoNumero) {
                    if (!hayPunto) {
                        String rdo = lblResultado.getText();
                        lblResultado.setText(rdo + ",");
                    }
                } else {
                    lblResultado.setText("0,");
                    nuevoNumero = false;
                }
                hayPunto = true;
                historialOperaciones.add(",");
                break;
            case "C":
                lblResultado.setText("0,0");
                nuevoNumero = true;
                hayPunto = false;
                historialOperaciones.add("C");
                break;
            case "=":
                if (operador != Calculadora.NINGUNO) {
                    String rdo = lblResultado.getText();
                    if (!rdo.isEmpty()) {
                        Number n = null;
                        try {
                            n = (Number) nf.parse(rdo);
                            numero = n.doubleValue();
                        } catch (ParseException ex) {
                            numero = 0;
                        }
                        switch (operador) {
                            case Calculadora.SUMA:
                                resultado += numero;
                                historialOperaciones.add(String.valueOf(numero) + " = " + resultado);
                                break;
                            case Calculadora.RESTA:
                                resultado -= numero;
                                historialOperaciones.add(String.valueOf(numero) + " = " + resultado);
                                break;
                            case Calculadora.MULTIPLICACION:
                                resultado *= numero;
                                historialOperaciones.add(String.valueOf(numero) + " = " + resultado);
                                break;
                            case Calculadora.DIVISION:
                                resultado /= numero;
                                historialOperaciones.add(String.valueOf(numero) + " = " + resultado);
                                break;
                        }
                        operador = Calculadora.NINGUNO;
                        lblResultado.setText(nf.format(resultado));
                    }
                }
                break;
            default:
                String rdo = lblResultado.getText();
                if (nuevoNumero) {
                    lblResultado.setText(texto);
                } else {
                    lblResultado.setText(rdo + texto);
                }
                nuevoNumero = false;
                historialOperaciones.add(texto);
                break;
        }
    }

    public void operar(int operacion) {
        if (!nuevoNumero) {
            String rdo = lblResultado.getText();
            if (!rdo.isEmpty()) {
                Number n = null;
                try {
                    n = (Number) nf.parse(rdo);
                    numero = n.doubleValue();
                } catch (ParseException ex) {
                    numero = 0;
                }
                switch (operacion) {
                    case Calculadora.SUMA:
                        resultado += numero;
                        historialOperaciones.add(String.valueOf(numero) + " +");
                        break;
                    case Calculadora.RESTA:
                        resultado -= numero;
                        historialOperaciones.add(String.valueOf(numero) + " -");
                        break;
                    case Calculadora.MULTIPLICACION:
                        resultado *= numero;
                        historialOperaciones.add(String.valueOf(numero) + " *");
                        break;
                    case Calculadora.DIVISION:
                        resultado /= numero;
                        historialOperaciones.add(String.valueOf(numero) + " /");
                        break;
                    default:
                        break;
                }
                nuevoNumero = true;
                hayPunto = false;
                lblResultado.setText(nf.format(resultado));
            }
        }
        operador = operacion;
    }
}
