package uiax.example;

import uia.core.context.Context;

import api.swing.ContextSwing;

import java.awt.*;

/**
 * Utility collects shared functions for sample creation.
 */

public final class Utility {

    private Utility() {
    }

    /**
     * Creates and starts a new application with screen width and height.
     */

    public static Context startApplication() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height - 100;
        return ContextSwing.createAndStart(screenWidth, screenHeight);
    }
}
