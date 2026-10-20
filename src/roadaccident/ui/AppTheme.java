package roadaccident.ui;

import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

public final class AppTheme {

    private static final Color BACKGROUND =
            new Color(245, 248, 252);
    private static final Color FOREGROUND =
            new Color(35, 49, 66);
    private static final Color BLUE =
            new Color(37, 99, 180);

    private AppTheme() {
    }

    public static void applyTheme() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.background", BACKGROUND);
        UIManager.put("Label.foreground", FOREGROUND);
        UIManager.put("Button.background", BLUE);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put(
                "Button.font",
                new Font("SansSerif", Font.PLAIN, 14)
        );
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("PasswordField.background", Color.WHITE);
        UIManager.put("TextArea.background", Color.WHITE);
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ComboBox.foreground", FOREGROUND);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.foreground", FOREGROUND);
        UIManager.put("Table.selectionBackground", BLUE);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("TableHeader.background", BACKGROUND);
        UIManager.put("TableHeader.foreground", FOREGROUND);
    }
}
