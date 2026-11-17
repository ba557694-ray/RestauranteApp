package vista;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Principal {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception excepcion) {
            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo aplicar el estilo del sistema.\n" + excepcion.getMessage(),
                    "Aviso de apariencia",
                    JOptionPane.WARNING_MESSAGE
            );
        }
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
