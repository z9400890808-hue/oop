package roadaccident;

import javax.swing.SwingUtilities;
import roadaccident.ui.AppTheme;
import roadaccident.ui.HomeFrame;

public class Main {

    public static void main(String[] args) {
        AppTheme.applyTheme();

        SwingUtilities.invokeLater(() -> {
            HomeFrame frame =
                    new HomeFrame();

            frame.setVisible(true);
        });
    }

}