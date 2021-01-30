import javax.swing.*;
import java.io.IOException;


public class Main {
        public static void main(String[] args) throws IOException {

            SwingUtilities.invokeLater(() -> {
                Frame frame = new Frame();
                frame.setVisible(true);
            });
        }
    }

