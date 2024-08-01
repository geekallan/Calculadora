import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class Calculadora extends JFrame implements ActionListener {
    private JTextField pantalla;
    private String operador;
    private double operando1, operando2, resultado;
    private boolean nuevaOperacion;
    private ArrayList<String> historial;

    public Calculadora() {
        setTitle("Calculadora");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        historial = new ArrayList<>();

        pantalla = new JTextField("0");
        pantalla.setEditable(false);
        pantalla.setHorizontalAlignment(JTextField.RIGHT);
        pantalla.setFont(new Font("Arial", Font.BOLD, 20));
        add(pantalla, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 4));

        String[] botones = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+"
        };

        for (String texto : botones) {
            JButton boton = new JButton(texto);
            boton.addActionListener(this);
            panel.add(boton);
        }

        add(panel, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuOpciones = new JMenu("Opciones");
        JMenuItem nuevo = new JMenuItem("Nuevo");
        JMenuItem verHistorial = new JMenuItem("Historial");
        nuevo.addActionListener(this);
        verHistorial.addActionListener(this);
        menuOpciones.add(nuevo);
        menuOpciones.add(verHistorial);

        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem manual = new JMenuItem("Manual de Usuario");
        manual.addActionListener(this);
        menuAyuda.add(manual);

        menuBar.add(menuOpciones);
        menuBar.add(menuAyuda);
        setJMenuBar(menuBar);

        nuevaOperacion = true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        Toolkit.getDefaultToolkit().beep();

        if (comando.equals("Nuevo")) {
            operando1 = operando2 = resultado = 0;
            operador = "";
            pantalla.setText("0");
            historial.add("Nuevo");
            escribirEnBitacora("Nuevo");
            nuevaOperacion = true;
        } else if (comando.equals("Historial")) {
            mostrarHistorial();
        } else if (comando.equals("Manual de Usuario")) {
            mostrarManual();
        } else if ("0123456789.".contains(comando)) {
            if (nuevaOperacion) {
                pantalla.setText(comando);
                nuevaOperacion = false;
            } else {
                pantalla.setText(pantalla.getText() + comando);
            }
        } else if ("/*-+".contains(comando)) {
            operando1 = Double.parseDouble(pantalla.getText());
            operador = comando;
            nuevaOperacion = true;
        } else if (comando.equals("=")) {
            operando2 = Double.parseDouble(pantalla.getText());
            switch (operador) {
                case "+":
                    resultado = operando1 + operando2;
                    break;
                case "-":
                    resultado = operando1 - operando2;
                    break;
                case "*":
                    resultado = operando1 * operando2;
                    break;
                case "/":
                    if (operando2 != 0) {
                        resultado = operando1 / operando2;
                    } else {
                        pantalla.setText("Error");
                        return;
                    }
                    break;
            }
            pantalla.setText(String.valueOf(resultado));
            historial.add(operando1 + " " + operador + " " + operando2 + " = " + resultado);
            escribirEnBitacora(operando1 + " " + operador + " " + operando2 + " = " + resultado);
            nuevaOperacion = true;
        }
    }

    private void escribirEnBitacora(String texto) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("bitacoraCalculadora.txt", true))) {
            writer.write(texto);
            writer.newLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarHistorial() {
        JTextArea areaTexto = new JTextArea();
        for (String linea : historial) {
            areaTexto.append(linea + "\n");
        }
        JScrollPane scrollPane = new JScrollPane(areaTexto);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        JOptionPane.showMessageDialog(this, scrollPane, "Historial", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarManual() {
        String manual = "Manual de Usuario:\n\n"
                + "1. Para realizar una operación, ingrese los números y seleccione el operador.\n"
                + "2. Presione '=' para obtener el resultado.\n"
                + "3. Utilice el menú 'Opciones' para reiniciar la calculadora o ver el historial de operaciones.\n"
                + "4. Cada acción realizada será registrada en el archivo 'bitacoraCalculadora.txt'.";
        JOptionPane.showMessageDialog(this, manual, "Manual de Usuario", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Calculadora calculadora = new Calculadora();
            calculadora.setVisible(true);
        });
    }
}

