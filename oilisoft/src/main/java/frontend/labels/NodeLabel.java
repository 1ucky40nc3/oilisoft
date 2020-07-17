package frontend.labels;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/** Custom JLabel that represents a node
 * @author Louis Wendler
 * @since 1.0
 * @version 1.0
 */
public abstract class NodeLabel extends JLabel {

    private String name;

    private Dimension labelExtra = new Dimension(5, 5);
    private Dimension imageSize;

    JFrame frame;
    PopupFactory popupFactory;
    Popup hoverPopup;

    private boolean dragged;

    /**
     * Create a new NodeLabel
     * @param name Name of the NodeLabel
     * @param icon Image which will be displayed in the ImageIcon
     * @param frame Frame the Popup will be added in
     * @param x Coordinate of the ImageIcon
     * @param y Coordinate of the ImageIcon
     */
    public NodeLabel(String name,
                     ImageIcon icon,
                     JFrame frame,
                     int x, int y) {
        super(icon);

        this.dragged = false;

        this.name = name;

        this.frame = frame;
        popupFactory = new PopupFactory();

        setSize(getPreferredSize());

        LineBorder roundedLineBorder = new LineBorder(new Color(71, 67, 79), 3, true);
        TitledBorder roundedTitledBorder = new TitledBorder(roundedLineBorder, name);
        roundedTitledBorder.setTitleColor(new Color(127, 167, 199));
        setBorder(roundedTitledBorder);

        setLocation(x, y);
    }

    /**
     * Initialize and show a custom Popup via the PopupFactory
     * @param point Position of the Popup
     */
    public abstract void showHoverPopup(Point point);

    /**
     * Hide the Popup
     */
    public void hideHoverPopup() {
        hoverPopup.hide();
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isDragged() {
        return dragged;
    }

    public void setDragged(boolean dragged) {
        this.dragged = dragged;
    }

    /**
     * Getter for the Nodes the NodeLabel represents
     * @param <T>
     * @return Node(s)
     */
    public abstract <T> T getNode();
}
