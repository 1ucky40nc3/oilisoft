package frontend.labels;

import backend.entities.Ship;

import javax.swing.*;
import java.awt.*;

/** Custom JLabel that represents a ship
 * @author Louis Wendler
 * @since 1.0
 * @version 1.0
 */
public class ShipLabel extends NodeLabel {

    private Ship ship;

    /**
     * Create a ShipLabel
     * @param ship Ship node the ShipLabel represents
     * @param icon Image of a ship icon
     * @param frame Frame the Popup will be added to
     * @param x Coordinate of the ImageIcon
     * @param y Coordinate of the ImageIcon
     */
    public ShipLabel(Ship ship, ImageIcon icon, JFrame frame, int x, int y) {
        super(ship.getAttributes().get("name").toString(),
                icon,
                frame,
                x, y);

        this.ship = ship;
    }

    @Override
    public Ship getNode() {
        return ship;
    }

    /**
     * See NodeLabel.showHoverPopup()
     * @param point Position of the Popup
     */
    @Override
    public void showHoverPopup(Point point) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        String text = String.format(
                "Number of workers: %s / %s",
                ship.getNumberWorkers().toString(),
                ship.getMaxWorkers().toString()
        );

        JLabel label = new JLabel(text);
        panel.add(label);

        panel.setOpaque(true);
        panel.setBackground(new Color(211, 224, 235));

        panel.setBorder(BorderFactory.createLineBorder(new Color(46, 53, 59, 100), 2, true));

        hoverPopup = popupFactory.getPopup(frame, panel, point.x, point.y);
        hoverPopup.show();
    }
}
