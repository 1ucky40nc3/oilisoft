package frontend.labels.clickmenus;

import frontend.labels.NodeLabel;

import javax.swing.*;

/** Custom JMenuItem for click menus
 * @author Louis Wendler
 * @since 1.0
 * @version 1.0
 */
public class NodeClickMenuItem extends JMenuItem {

    private ClickMenuAction action;
    private NodeLabel nodeLabel;

    /** Creates an NodeClickMenuItem,
     *  which displays text and is associated with an action
     * @param text The text which has to be displayed
     * @param nodeLabel The NodeLabel the Entity(Right/Left)ClickMenu was invoked on
     * @param action The associated action
     */
    public NodeClickMenuItem(String text, NodeLabel nodeLabel, ClickMenuAction action) {
        super(text);

        this.action = action;
        this.nodeLabel = nodeLabel;
    }

    public ClickMenuAction getClickMenuAction() {
        return action;
    }

    public NodeLabel getNodeLabel() {
        return nodeLabel;
    }
}
