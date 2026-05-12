import controller.FlotteController;
import view.MainFrame;

import javax.swing.*;

/**
 * Point d'entrée de l'application.
 * Lance l'interface Swing sur l'Event Dispatch Thread.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            FlotteController controller = new FlotteController();
            MainFrame frame = new MainFrame(controller);
            frame.setVisible(true);
        });
    }
}
