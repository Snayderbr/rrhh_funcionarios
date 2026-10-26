package co.edu.iudigital.rrhh;

import co.edu.iudigital.rrhh.vista.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignorada) {
            // Se usa el aspecto por defecto de Swing.
        }
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
