package apphampers;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new FormLogin().setVisible(true);
        });
    }
}
