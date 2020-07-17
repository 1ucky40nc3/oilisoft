package frontend.labels.clickmenus;

import javax.swing.*;
import java.awt.event.ActionListener;

/** Custom JPopupMenu that opens at a left click
 * @author Louis Wendler
 * @since 1.0
 * @version 1.0
 */
public class RightClickMenu extends JPopupMenu {

    private ClickMenuItem reset;

    /** Creates an RightClickMenu,
     *  which uses the shared ActionListener UI
     *  and is invoked on an NodeLabel
     * @param actionListener Shared ActionListener UI
     */
    public RightClickMenu(ActionListener actionListener) {

        reset = new ClickMenuItem("Reset database", ClickMenuAction.RESET);
        reset.addActionListener(actionListener);

        add(reset);
    }
}
