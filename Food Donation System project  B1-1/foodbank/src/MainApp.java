import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        SystemManager manager = new SystemManager();
        SwingUtilities.invokeLater(() -> {
            new PrimaryWindow(manager);
            new SystemManagerGUI(manager);
        });
    }
}
