package frontend.labels.clickmenus;

import backend.entities.OilRig;
import frontend.labels.NodeLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/** Custom JPopupMenu that opens on a left click
 * @author Louis Wendler
 * @since 1.0
 * @version 1.0
 */
public class NodeLeftClickMenu extends JPopupMenu {

    private NodeClickMenuItem redeploy;
    private NodeClickMenuItem evacuate;

    private NodeLabel nodeLabel;

    /** Creates an NodeLeftClickMenu,
     *  which uses the shared ActionListener UI
     *  and is invoked on an NodeLabel
     * @param actionListener Shared ActionListener UI
     * @param nodeLabel The NodeLabel which was right clicked
     */
    public NodeLeftClickMenu(ActionListener actionListener, NodeLabel nodeLabel) {
        this.nodeLabel = nodeLabel;

        redeploy = new NodeClickMenuItem("Redeploy workers", nodeLabel, ClickMenuAction.REDEPLOY);
        redeploy.addActionListener(actionListener);
        add(redeploy);

        if (nodeLabel.getNode() instanceof OilRig) {
            evacuate = new NodeClickMenuItem("Evacuate", nodeLabel, ClickMenuAction.EVACUATE);
            evacuate.setBackground(new Color(240, 10, 10, 100));
            evacuate.addActionListener(actionListener);
            add(evacuate);
        }
    }
}
