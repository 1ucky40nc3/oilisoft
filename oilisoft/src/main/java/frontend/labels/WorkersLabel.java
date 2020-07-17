package frontend.labels;

import backend.entities.Node;
import backend.entities.Worker;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Custom JLabel that represents a collection of workers
 * @author Louis Wendler
 * @since 1.0
 * @version 1.0
 */
public class WorkersLabel extends NodeLabel {

    private final Node invoker;
    private final ArrayList<Worker> workers;

    /**
     * Create a ShipLabel
     * @param invoker Node of NodeLabel the WorkersLabel was invoked on
     * @param workers List of worker node the WorkerLabel represents
     * @param icon Image of a worker icon
     * @param frame Frame the Popup will be added to
     * @param x Coordinate of the ImageIcon
     * @param y Coordinate of the ImageIcon
     */
    public WorkersLabel(Node invoker, ArrayList<Worker> workers, ImageIcon icon, JFrame frame, int x, int y) {
        super("Workers",
                icon,
                frame,
                x, y);

        this.invoker = invoker;
        this.workers = workers;
    }

    public Node getInvoker() {
        return invoker;
    }

    @Override
    public ArrayList<Worker> getNode() {
        return workers;
    }

    /**
     * See NodeLabel.showHoverPopup()
     * @param point Position of the Popup
     */
    @Override
    public void showHoverPopup(Point point) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        String text = "Number of workers: " + workers.size();
        JLabel label = new JLabel(text);
        panel.add(label);

        panel.setOpaque(true);
        panel.setBackground(new Color(211, 224, 235));

        panel.setBorder(BorderFactory.createLineBorder(new Color(46, 53, 59, 100), 2, true));

        hoverPopup = popupFactory.getPopup(frame, panel, point.x, point.y);
        hoverPopup.show();
    }
}
