package frontend.labels;

import backend.entities.OilRig;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/** Custom JLabel that represents an oil rig
 * @author Louis Wendler
 * @since 1.0
 * @version 1.0
 */
public class OilRigLabel extends NodeLabel {

    private OilRig oilRig;

    private int x;
    private int y;
    private int radius;

    /**
     * Create a OilRigLabel
     * @param oilRig Oil rig node the OilRigLabel represents
     * @param icon Image of oil rig icon
     * @param frame Frame the Popup will be added to
     * @param x Coordinate of the ImageIcon
     * @param y Coordinate of the ImageIcon
     * @param radius Radius of the circle that surrounds the OilRigLabel
     */
    public OilRigLabel(OilRig oilRig, ImageIcon icon, JFrame frame, int x, int y, int radius) {
        super(oilRig.getAttributes().get("name").toString(),
                icon,
                frame,
                x, y);

        this.oilRig = oilRig;

        this.x = x;
        this.y = y;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.CYAN);
        g.fillOval(x, y, radius, radius);

    }

    @Override
    public OilRig getNode() {
        return oilRig;
    }


    /**
     * See NodeLabel.showHoverPopup()
     * @param point Position of the Popup
     */
    @Override
    public void showHoverPopup(Point point) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        ArrayList<String> texts = new ArrayList<>();
        texts.add(String.format(
                "Number of workers: %s / %s",
                oilRig.getNumberWorkers().toString(),
                oilRig.getMaxWorkers().toString()
        ));
        texts.add(String.format(
                "Number of small ships: %s / %s",
                oilRig.getNumberSmallShips().toString(),
                oilRig.getMaxNumberSmallShips()
        ));
        texts.add(String.format(
                "Number of big ships: %s / %s",
                oilRig.getNumberBigShips().toString(),
                oilRig.getMaxNumberBigShips()
        ));
        texts.add(String.format(
                "Number of  ships: %s / %s",
                oilRig.getNumberShips().toString(),
                oilRig.getMaxShips().toString()
        ));

        for (String text : texts) {
            JLabel label = new JLabel(text);
            panel.add(label);
        }

        panel.setOpaque(true);
        panel.setBackground(new Color(211, 224, 235));

        panel.setBorder(BorderFactory.createLineBorder(new Color(46, 53, 59, 100), 2, true));

        hoverPopup = popupFactory.getPopup(frame, panel, point.x, point.y);
        hoverPopup.show();
    }
}
