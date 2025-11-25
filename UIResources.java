package payroll.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Small utility for loading app theme and logo.
 * If you move the logo file, update LOGO_PATH accordingly.
 */
public class UIResources {
    // path to uploaded logo image (change if you move the file)
    public static final String LOGO_PATH = "/mnt/data/Screenshot 2025-11-19 at 15.27.14.png";

    public static ImageIcon loadLogo(int w, int h) {
        try {
            ImageIcon ic = new ImageIcon(LOGO_PATH);
            Image img = ic.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception ex) {
            return new ImageIcon(); // empty icon if file missing
        }
    }

    public static void setAppTheme() {
        try {
            // requires flatlaf jar on classpath
            com.formdev.flatlaf.FlatLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.focusWidth", 2);
        } catch (Exception ex) {
            System.out.println("FlatLaf not available — using default LAF");
        }
    }
}
