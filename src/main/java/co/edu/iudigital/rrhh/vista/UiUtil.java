package co.edu.iudigital.rrhh.vista;

import co.edu.iudigital.rrhh.excepciones.ValidacionException;
import co.edu.iudigital.rrhh.modelo.Catalogo;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/** Utilidades compartidas por los formularios Swing. */
final class UiUtil {
    static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private UiUtil() { }

    static JComboBox<Catalogo> combo(List<Catalogo> items) {
        JComboBox<Catalogo> cb = new JComboBox<>(items.toArray(new Catalogo[0]));
        cb.setSelectedIndex(items.isEmpty() ? -1 : 0);
        return cb;
    }

    static void seleccionar(JComboBox<Catalogo> cb, Catalogo valor) {
        if (valor != null) {
            cb.setSelectedItem(valor);
        }
    }

    static Catalogo valor(JComboBox<Catalogo> cb) {
        return (Catalogo) cb.getSelectedItem();
    }

    /** Agrega una fila "etiqueta + componente" a un panel con GridBagLayout. */
    static void fila(JPanel panel, int fila, String etiqueta, JComponent componente) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.gridy = fila;
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        panel.add(new JLabel(etiqueta), c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(componente, c);
    }

    static LocalDate parseFecha(String texto, String campo) throws ValidacionException {
        try {
            return LocalDate.parse(texto.trim(), FECHA);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new ValidacionException("La fecha " + campo + " debe tener el formato AAAA-MM-DD (ej. 1990-03-25).");
        }
    }

    static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    static void aviso(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Verifique los datos", JOptionPane.WARNING_MESSAGE);
    }

    static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}
