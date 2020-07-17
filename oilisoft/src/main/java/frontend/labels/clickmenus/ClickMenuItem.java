package frontend.labels.clickmenus;

import javax.swing.*;

/**
 * Custom JMenuItem
 */
public class ClickMenuItem extends JMenuItem {

    private final ClickMenuAction action;

    /**
     * Create a ClickMenuItem
     * @param text The text this will display
     * @param action The action a click on this will provoke
     */
    public ClickMenuItem(String text, ClickMenuAction action) {
        super(text);

        this.action = action;
    }

    public ClickMenuAction getClickMenuAction() {
        return action;
    }
}
