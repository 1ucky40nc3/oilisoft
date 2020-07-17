package frontend;

import backend.DatabaseManagement;
import backend.databases.ExampleDatabase;
import backend.entities.*;
import frontend.forms.*;
import frontend.labels.NodeLabel;
import frontend.labels.OilRigLabel;
import frontend.labels.ShipLabel;
import frontend.labels.WorkersLabel;
import frontend.labels.clickmenus.*;
import org.neo4j.driver.Record;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * User interface
 * @author Louis Wendler
 * @since 1.0
 * @version 1.0
 */
public class UI extends MouseAdapter implements ActionListener {

    private final ImageIcon OIL_RIG_ICON;
    private final ImageIcon SMALL_SHIP_ICON;
    private final ImageIcon BIG_SHIP_ICON;
    private final ImageIcon WORKER_ICON;

    private final int SMALL_IMAGE_WIDTH = 45;
    private final int SMALL_IMAGE_HEIGHT = 45;
    private final int BIG_IMAGE_WIDTH = 95;
    private final int BIG_IMAGE_HEIGHT = 95;

    private Point mouseOffset;

    private DatabaseManagement man;
    private HashMap<OilRig, ArrayList<Ship>> map;
    private ArrayList<NodeLabel> nodeLabelArrayList;

    private final Dimension screenSize;
    private JLayeredPane pane;
    private JFrame frame;

    /**
     * Create new UI class
     */
    public UI() {
        OIL_RIG_ICON = new ImageIcon("resources/oilRig.png");
        SMALL_SHIP_ICON = new ImageIcon("resources/smallShip.png");
        BIG_SHIP_ICON = new ImageIcon("resources/bigShip.png");
        WORKER_ICON = new ImageIcon("resources/worker.png");

        pane = new JLayeredPane();
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        pane.setPreferredSize(new Dimension(screenSize.width/2, screenSize.height));

        frame = new JFrame("ÖLISOFT - v1.0 - management map");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addMouseListener(this);
        frame.setIconImage(new ImageIcon("resources/icon.png").getImage());
    }

    /**
     * Initialize and structure the Nodes, NodeLabels
     * @param uri The URI to the database
     * @param userName The user name to access the database
     * @param password Password to authenticate at the database
     * @return True if process was successful
     */
    public boolean init(String uri, String userName, String password) {
        try {
            man = new DatabaseManagement(uri, userName, password);
            initNodeLabels(pane);
            man.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Initialize and structure the Nodes and NodeLabels
     * @param pane Add the NodeLabels to pane
     */
    private void initNodeLabels(JLayeredPane pane) {
        nodeLabelArrayList = new ArrayList<>();
        map = new HashMap<>();

        // Get the oil rigs out of the database
        ArrayList<Record> oilRigRecords = man.writeTx(cypherOilRigs());
        for (Record oilRigRecord : oilRigRecords) {
            String oilRigName = oilRigRecord.get("o.name").asString();
            int initNumberWorkers = oilRigRecord.get("o.initNumberWorkers").asInt();
            int initNumberSmallShips = oilRigRecord.get("o.initNumberSmallShips").asInt();
            int initNumberBigShips = oilRigRecord.get("o.initNumberBigShips").asInt();
            // Create corresponding oil rig nodes
            OilRig oilRig = new OilRig(
                    oilRigName,
                    initNumberWorkers,
                    initNumberSmallShips,
                    initNumberBigShips
            );
            setOilRigStats(oilRig);

            // Get the ships that anchor on the oil rig
            ArrayList<Ship> ships = new ArrayList<>();
            ArrayList<Record> shipRecords = man.writeTx(oilRig.cypherMatchShips());
            for (Record shipRecord : shipRecords) {
                String shipName = shipRecord.get("Ship.name").asString();
                int maxCapacity = shipRecord.get("Ship.maxCapacity").asInt();
                // Create corresponding ship nodes
                Ship ship = new Ship(shipName, maxCapacity);
                setShipStats(ship);
                ships.add(ship);
            }
            map.put(oilRig, ships);
        }

        Point mapPaneCenter = new Point(
                pane.getPreferredSize().width/2,
                pane.getPreferredSize().height/2
        );

        ArrayList<Point> oilRigLabelPositions = computeLabelPoints(
                mapPaneCenter,
                pane.getPreferredSize().width/3.3,
                map.keySet().size()
        );

        int i = 0;
        // Sort the oil rig alphabetically
        ArrayList<OilRig> oilRigArrayList = new ArrayList(map.keySet());
        Collections.sort(oilRigArrayList, new Comparator<OilRig>() {
            @Override
            public int compare(OilRig o1, OilRig o2) {
                String o1Name = o1.getAttributes().get("name").toString();
                String o2Name = o2.getAttributes().get("name").toString();
                return o1Name.compareTo(o2Name) * -1;
            }
        });
        double oilRigRadius = pane.getPreferredSize().width/6.6;
        // Create labels that correspond with their nodes
        // and position them by their computed points
        for (Iterator<OilRig> oilRigs = oilRigArrayList.iterator(); oilRigs.hasNext(); i++) {
            OilRig oilRig = oilRigs.next();
            Point oilRigLabelPos = oilRigLabelPositions.get(i);
            OilRigLabel oilRigLabel = new OilRigLabel(
                    oilRig,
                    scaleIcon(OIL_RIG_ICON, BIG_IMAGE_WIDTH, BIG_IMAGE_HEIGHT),
                    frame,
                    oilRigLabelPos.x - BIG_IMAGE_WIDTH/2,
                    oilRigLabelPos.y - BIG_IMAGE_HEIGHT/2,
                    (int) oilRigRadius
            );
            oilRigLabel.addMouseListener(this);
            oilRigLabel.addMouseMotionListener(this);

            pane.add(oilRigLabel);
            nodeLabelArrayList.add(oilRigLabel);
            // Sort the ship nodes alphabetically
            ArrayList<Ship> shipArrayList = new ArrayList(map.get(oilRig));
            Collections.sort(shipArrayList, new Comparator<Ship>() {
                @Override
                public int compare(Ship s1, Ship s2) {
                    String s1Name = s1.getAttributes().get("name").toString();
                    String s2Name = s2.getAttributes().get("name").toString();
                    return s1Name.compareTo(s2Name) * -1;
                }
            });
            ArrayList<Point> shipLabelPositions = computeLabelPoints(oilRigLabelPos,
                    pane.getPreferredSize().width/6.6,
                    map.get(oilRig).size()
            );
            int j = 0;
            for (Iterator<Ship> ships = shipArrayList.iterator(); ships.hasNext(); j++) {
                Ship ship = ships.next();
                Point shipLabelPos = shipLabelPositions.get(j);
                ShipLabel shipLabel = null;
                if (ship.getMaxWorkers() == 50) {
                    shipLabel = new ShipLabel(
                            ship,
                            scaleIcon(SMALL_SHIP_ICON, SMALL_IMAGE_WIDTH, SMALL_IMAGE_HEIGHT),
                            frame,
                            shipLabelPos.x - SMALL_IMAGE_WIDTH/2,
                            shipLabelPos.y - SMALL_IMAGE_HEIGHT/2
                    );
                } else {
                    shipLabel = new ShipLabel(
                            ship,
                            scaleIcon(BIG_SHIP_ICON, SMALL_IMAGE_WIDTH, SMALL_IMAGE_HEIGHT),
                            frame,
                            shipLabelPos.x - SMALL_IMAGE_WIDTH/2,
                            shipLabelPos.y - SMALL_IMAGE_HEIGHT/2
                    );
                }

                shipLabel.addMouseListener(this);
                shipLabel.addMouseMotionListener(this);

                pane.add(shipLabel);
                pane.moveToFront(shipLabel);
                nodeLabelArrayList.add(shipLabel);
            }
        }
    }

    /**
     * Create a cypher query that returns all oil rigs with their attributes such as
     * name, initial number of workers and small/big ships
     * @return String which represents the cypher query
     */
    private String cypherOilRigs() {
        return new OilRig().cypher("MATCH", "o")
                + "RETURN o.name, o.initNumberWorkers, o.initNumberSmallShips, o.initNumberBigShips";
    }

    /**
     * Set an oil rigs attributes (stats) via records of of cypher queries
     * @param oilRig The oil rig whose attributes shall be set
     */
    private void setOilRigStats(OilRig oilRig) {
        ArrayList<Record> records = man.writeTx(oilRig.cypherCountWorkers());
        oilRig.setNumberWorkers(records.get(0).get("count").asInt());

        records = man.writeTx(oilRig.cypherCountSmallShips());
        oilRig.setNumberSmallShips(records.get(0).get("count").asInt());

        records = man.writeTx(oilRig.cypherCountBigShips());
        oilRig.setNumberBigShips(records.get(0).get("count").asInt());
    }

    /**
     * Set an ships attributes (stats) via records of of cypher queries
     * @param ship The oil rig whose attributes shall be set
     */
    private void setShipStats(Ship ship) {
        ArrayList<Record> records = man.writeTx(ship.cypherCountWorkers());
        ship.setNumberWorkers(records.get(0).get("count").asInt());
    }

    /**
     * Scale an ImageIcon to desired width and height
     * @param icon Icon that needs to be scaled
     * @param scaleWidth The width after scaling
     * @param scaleHeight The height after scaling
     * @return The scaled ImageIcon
     */
    private ImageIcon scaleIcon(ImageIcon icon, int scaleWidth, int scaleHeight) {
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    /**
     * Compute the positions of labels
     * on an outer perimeter of a circle in the window
     * @param center The center point of the circle
     * @param radius The radius of the circle
     * @param n The number of labels that have to be put on the circle
     * @return A List of positions for the labels
     */
    private ArrayList<Point> computeLabelPoints(Point center, double radius, int n) {
        double radians = 2*Math.PI/n;
        ArrayList<Point> points = new ArrayList<>();

        double x = center.x;
        double y = center.y + radius;

        points.add(new Point((int) x, (int) y));

        // Compute points on the outer perimeter of a circle with r = radius
        for (int i = 1; i < n; i++) {
            double tmpX = x;

            x = Math.cos(radians) * (x - center.x) - Math.sin(radians) * (y - center.y) + center.x;
            y = Math.sin(radians) * (tmpX - center.x) + Math.cos(radians) * (y - center.y) + center.y;

            points.add(new Point((int) x, (int) y));
        }

        return points;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() instanceof JFrame) {
            JFrame jFrame = (JFrame) e.getSource();
            if (SwingUtilities.isRightMouseButton(e)) {
                RightClickMenu menu = new RightClickMenu(this);
                menu.show(jFrame, e.getX(), e.getY());
            }
        } else if (e.getSource() instanceof NodeLabel) {
            NodeLabel label = (NodeLabel) e.getSource();
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (!(label instanceof WorkersLabel)) {
                    NodeLeftClickMenu menu = new NodeLeftClickMenu(this, label);
                    menu.show(label, e.getX(), e.getY());
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseOffset = e.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getSource() instanceof NodeLabel) {
            NodeLabel label = (NodeLabel) e.getSource();
            if (label instanceof OilRigLabel)
                return;

            label.hideHoverPopup();

            ((NodeLabel) e.getSource()).setDragged(true);

            int x = e.getPoint().x - mouseOffset.x;
            int y = e.getPoint().y - mouseOffset.y;

            Point location = label.getLocation();
            location.x += x;
            location.y += y;

            ((NodeLabel) e.getSource()).setLocation(location);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getSource() instanceof ShipLabel) {
            ShipLabel label = (ShipLabel) e.getSource();
            if (label.isDragged()) {
                try {
                    man.start();
                    connectShipLabelDrag(label);
                    man.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ((ShipLabel) e.getSource()).setDragged(false);
            }
        } else if (e.getSource() instanceof WorkersLabel) {
            WorkersLabel label = (WorkersLabel) e.getSource();
            if (label.isDragged()) {
                try {
                    man.start();
                    connectWorkersLabelDrag(label);
                    man.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ((WorkersLabel) e.getSource()).setDragged(false);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource() instanceof NodeLabel) {
            NodeLabel nodeLabel = (NodeLabel) e.getSource();
            nodeLabel.showHoverPopup(e.getLocationOnScreen());
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() instanceof NodeLabel) {
            ((NodeLabel) e.getSource()).hideHoverPopup();
        }
    }

    /**
     * Try to find a overlapping OilRigLabel with the ShipLabel to detect an drag-and-drop process
     * If detected: Redeploy the ship via a cypher query
     * @param label The ShipLabel that has been moved as part of drag-and-drop process
     * @throws Exception May throw an exception as result of the refresh() method call
     */
    private void connectShipLabelDrag(ShipLabel label) throws Exception {
        Point labelPos = label.getLocationOnScreen();
        Rectangle labelBounds = new Rectangle(
                labelPos.x,
                labelPos.y,
                label.getWidth(),
                label.getHeight()
        );

        // Compute all OilRigLabels the ShipLabels bounds intersect with
        ArrayList<OilRigLabel> oilRigsEntered = new ArrayList<>();
        for (NodeLabel nodeLabel : nodeLabelArrayList) {
            if (nodeLabel instanceof OilRigLabel) {
                Point entityLabelPos = nodeLabel.getLocationOnScreen();
                Rectangle entityLabelBounds = new Rectangle(
                        entityLabelPos.x,
                        entityLabelPos.y,
                        nodeLabel.getWidth(),
                        nodeLabel.getHeight()
                );

                if (labelBounds.intersects(entityLabelBounds)) {
                    oilRigsEntered.add((OilRigLabel) nodeLabel);
                }
            }
        }

        if (oilRigsEntered.size() > 1) {
            String title = "Unsuccessful drag!";
            String message = "WARNING UI.connectShipLabelDrag() was unsuccessful!\nMore than one immobile EntityLabels have been entered";
            JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
            System.out.println(message);
            return;
        } else if (oilRigsEntered.size() == 1) {
            OilRigLabel oilRigLabel = oilRigsEntered.get(0);
            Ship ship = label.getNode();

            if (!canShipLeaveOilRig(ship))
                return;

            if (!hasOilRigShipCapacity(oilRigLabel.getNode(), ship))
                return;

            man.writeTx(cypherRedeployShip(
                    label.getNode(),
                    oilRigLabel.getNode()
            ));

            refresh();
        }
    }

    /**
     * Determine if ship can leave it's current oil rig
     * @param ship The ship that may be redeployed
     * @return True if the ship can leave it's current oil rig
     */
    private boolean canShipLeaveOilRig(Ship ship) {
        for (OilRig oilRig : map.keySet()) {
            if (map.get(oilRig).contains(ship)) {
                if (oilRig.getNumberSmallShips() + oilRig.getNumberBigShips() < 2) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determine if an additional ship can anchor at an oil rig
     * @param oilRig The oil rig the ship wants to to anchor
     * @param ship The ship that needs to be redeployed
     * @return True if the ship can anchor at the oil rig
     */
    private boolean hasOilRigShipCapacity(OilRig oilRig, Ship ship) {
        boolean smallShip = ship.getMaxWorkers() == 50;
        if (smallShip) {
            int numberSmallShips = oilRig.getNumberSmallShips();

            if (numberSmallShips + 1 > oilRig.getMaxNumberSmallShips()) {
                String title = "Unsuccessful drag!";
                String message = "WARNING UI.connectShipLabelDrag() was unsuccessful!\nNo more small ship can be added to selected oil rig!";
                JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
                System.out.println(message);
                return false;
            }
        } else {
            int numberBigShips = oilRig.getNumberBigShips();

            if (numberBigShips + 1 > oilRig.getMaxNumberBigShips()) {
                String title = "Unsuccessful drag!";
                String message = "WARNING UI.connectShipLabelDrag() was unsuccessful!\nNo more big ship can be added to selected oil rig!";
                JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
                System.out.println(message);
                return false;
            }
        }
        return true;
    }

    /**
     * Try to find a overlapping NodeLabel with the WorkersLabel to detect an drag-and-drop process
     * If detected: Redeploy the workers via a cypher query
     * @param label The WorkersLabel that has been moved as part of drag-and-drop process
     * @throws Exception May throw an exception as result of the refresh() method call
     */
    private void connectWorkersLabelDrag(WorkersLabel label) throws Exception {
        Point labelPos = label.getLocationOnScreen();
        Rectangle labelBounds = new Rectangle(
                labelPos.x,
                labelPos.y,
                label.getWidth(),
                label.getHeight()
        );

        // Compute all NodeLabels the WorkersLabels bounds intersect with
        ArrayList<NodeLabel> nodeLabelsEntered = new ArrayList<>();
        for (NodeLabel nodeLabel : nodeLabelArrayList) {
            Point entityLabelPos = nodeLabel.getLocationOnScreen();
            Rectangle entityLabelBounds = new Rectangle(
                    entityLabelPos.x,
                    entityLabelPos.y,
                    nodeLabel.getWidth(),
                    nodeLabel.getHeight()
            );

            if (labelBounds.intersects(entityLabelBounds)) {
                nodeLabelsEntered.add(nodeLabel);
            }
        }

        if (nodeLabelsEntered.size() > 1) {
            String title = "Unsuccessful drag!";
            String message = "WARNING UI.connectWorkersLabelDrag() was unsuccessful!\nMore than one EntityLabels have been entered";
            JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
            System.out.println(message);
            return;
        } else if (nodeLabelsEntered.size() == 1) {
            System.out.println("add workers");
            int numberRedeployedWorkers = label.getNode().size();

            NodeLabel nodeLabel = nodeLabelsEntered.get(0);

            if (nodeLabel instanceof OilRigLabel) {
                OilRigLabel oilRigLabel = (OilRigLabel) nodeLabel;
                OilRig oilRig = oilRigLabel.getNode();

                if (!nodesFromSameFleet(label.getInvoker(), oilRig)) {
                    String title = "Unsuccessful drag!";
                    String message = "WARNING UI.connectWorkersLabelDrag was unsuccessful!\nThe workers must be redeployed from a ship that anchors at the desired oil rig!";
                    JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
                    System.out.println(message);
                    return;
                }

                int initNumberWorkers = oilRig.getInitNumberWorkers();
                int numberWorkers = oilRig.getNumberWorkers();

                if (numberWorkers + numberRedeployedWorkers > 2 * initNumberWorkers) {
                    String title = "Unsuccessful drag!";
                    String message = "WARNING UI.connectWorkersLabelDrag was unsuccessful!\nDue to the drag, the oil rigs worker capacity would be out ouf bounds.";
                    JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
                    System.out.println(message);
                    return;
                }
            } else if (nodeLabel instanceof ShipLabel){
                ShipLabel shipLabel = (ShipLabel) nodeLabel;
                Ship ship = shipLabel.getNode();

                if (!nodesFromSameFleet(label.getInvoker(), ship)) {
                    String title = "Unsuccessful drag!";
                    String message = "WARNING UI.connectWorkersLabelDrag was unsuccessful!\nWorkers tried to be redeployed from a different fleet!";
                    JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
                    System.out.println(message);
                    return;
                }

                int maxCapacity = ship.getMaxWorkers();
                int numberWorkers = ship.getNumberWorkers();

                if (numberRedeployedWorkers + numberWorkers > maxCapacity) {
                    String title = "Unsuccessful drag!";
                    String message = "WARNING UI.connectWorkersLabelDrag was unsuccessful!\nDue to the drag, the ships worker capacity would be out ouf bounds.";
                    JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
                    System.out.println(message);
                    return;
                }
            }

            man.writeTx(cypherRedeployWorker(
                    label.getNode(),
                    nodeLabelsEntered.get(0).getNode()
            ));

            refresh();
        }
    }

    /**
     * Create a cypher query that detaches an ship and anchors it to a oil rig node
     * @param ship The ship which shall be moved
     * @param oilRig The oil rig to ship shall be moved to
     * @return String which represents the cypher query
     */
    private String cypherRedeployShip(Ship ship, OilRig oilRig) {
        Relationship anchored = new Relationship("ANCHORED");
        return ship.cypher("MATCH", "s") + "\n"
                + oilRig.cypher("MATCH", "o") + "\n"
                + ship.cypherRelationshipTo("MATCH", "", anchored, "d") + " DELETE d" + "\n"
                + ship.cypherRelationshipTo("CREATE", "o", anchored, "");
    }

    /**
     * Create a cypher query that detaches workers and redeploy the to a node
     * @param workers The list of workers that shall be redeployed
     * @param node The node (ship/oil rig) the workers shall be redeployed to
     * @return String which represents the cypher query
     */
    private String cypherRedeployWorker(ArrayList<Worker> workers, Node node) {
        Relationship deployed = new Relationship("DEPLOYED");

        String cypherQuery = node.cypher("MATCH", "n") + "\n";
        for (int i = 0; i < workers.size(); i++) {
            Worker worker = workers.get(i);
            String iToString = Integer.toString(i);

            cypherQuery += worker.cypher("MATCH", "w" + iToString) + "\n"
                    + worker.cypherRelationshipTo("MATCH", "", deployed, "d" + iToString) + "\n";
        }
        for (int i = 0; i < workers.size(); i++) {
            Worker worker = workers.get(i);
            String iToString = Integer.toString(i);

            cypherQuery += "DELETE d" + iToString + "\n"
                    + worker.cypherRelationshipTo("CREATE", "n", deployed, "") + "\n";
        }

        return cypherQuery;
    }

    /**
     * Determine if nodes (ship/oil rig) are part of the same fleet
     * A fleet is set up by an oil rig and the anchored ships
     * @param invoker A node that is moved
     * @param node A node the invoker is moved on
     * @return True if both nodes are part of the same fleet
     */
    private boolean nodesFromSameFleet(Node invoker, Node node) {
        if (invoker.getNodeLabel() == NodeLabels.OIL_RIG && node.getNodeLabel() == NodeLabels.SHIP) {
            OilRig oilRigInvoker = (OilRig) invoker;
            Ship shipNode = (Ship) node;

            return shipAnchorsAtOilRig(shipNode, oilRigInvoker);
        } else if (invoker.getNodeLabel() == NodeLabels.SHIP && node.getNodeLabel() == NodeLabels.SHIP) {
            Ship shipInvoker = (Ship) invoker;
            Ship shipNode = (Ship) node;

            return shipsAnchorAtSameOilRig(shipInvoker, shipNode);
        } else if (invoker.getNodeLabel() == NodeLabels.SHIP && node.getNodeLabel() == NodeLabels.OIL_RIG) {
            Ship shipInvoker = (Ship) invoker;
            OilRig oilRigNode = (OilRig) node;

            return shipAnchorsAtOilRig(shipInvoker, oilRigNode);
        }
        return false;
    }

    /**
     * Determine if ships anchors at oil rig
     * @param ship The ship that is checked
     * @param oilRig The oil rig
     * @return
     */
    private boolean shipAnchorsAtOilRig(Ship ship, OilRig oilRig) {
        for (Ship other : map.get(oilRig)) {
            if (other.equals(ship))
                return true;
        }
        return false;
    }

    /**
     * Determine if multiple ships anchor at same oil rig
     * @param invoker Ship node
     * @param node Other ship node
     * @return True if the ships anchor at the same oil rig
     */
    private boolean shipsAnchorAtSameOilRig(Ship invoker, Ship node) {
        boolean invokerAtOilRig = false;
        boolean nodeAtOilRig = false;

        boolean shipFound = false;

        for (OilRig oilRig : map.keySet()) {
            for (Ship ship : map.get(oilRig)) {
                if (ship.equals(invoker) || ship.equals(node)) {
                    if (!shipFound)
                        shipFound = true;

                    if (ship.equals(invoker))
                        invokerAtOilRig = true;

                    if (ship.equals(node))
                        nodeAtOilRig = true;
                }
            }
            if (shipFound)
                return (invokerAtOilRig && nodeAtOilRig);
        }
        return false;
    }

    /**
     * Add the pane to the frame and display it
     */
    public void display() {
        frame.add(pane);
        frame.pack();
        frame.setLocation(screenSize.width/2, 0);
        frame.setVisible(true);
    }

    /**
     * Update the current pane and validate the frame
     * @param pane The pane which will be added to the frame (JFrame)
     */
    public void updateDisplay(JLayeredPane pane) {
        frame.remove(this.pane);
        this.pane = pane;
        frame.add(pane);
        frame.validate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof ClickMenuItem) {
            ClickMenuItem item = (ClickMenuItem) e.getSource();
            if (item.getClickMenuAction() == ClickMenuAction.RESET) {
                try {
                    man.start();
                    new ExampleDatabase(man);
                    refresh();
                    man.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else if (e.getSource() instanceof NodeClickMenuItem) {
            NodeClickMenuItem item = (NodeClickMenuItem) e.getSource();
            if (item.getClickMenuAction() == ClickMenuAction.REDEPLOY) {
                new WorkersRedeployForm(item.getNodeLabel(), this);
            } else if (item.getClickMenuAction() == ClickMenuAction.EVACUATE) {
                try {
                    man.start();
                    evacuate((OilRigLabel) item.getNodeLabel());
                    man.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } else if (e.getSource() instanceof SubmitButton) {
            SubmitButton button = (SubmitButton) e.getSource();
            if (button.getSubmitFormType() == Forms.WORKERS_REDEPLOY_FORM) {
                System.out.println("Will try to submit WorkersRedeployForm");
                button.getSubmitForm().disposeFrame();
                man.start();

                if (button.getNodeLabel() instanceof OilRigLabel) {
                    OilRigLabel oilRigLabel = (OilRigLabel) button.getNodeLabel();
                    OilRig oilRig = oilRigLabel.getNode();
                    int minWorkers = oilRig.getMinWorkers();

                    int numberWorkers = oilRig.getInitNumberWorkers();

                    ArrayList<Object> submits = button.getSubmitForm().getSubmits();
                    int submitNumberWorkers;
                    try {
                        submitNumberWorkers  = Integer.parseInt(submits.get(0).toString());
                    } catch (Exception ex) {
                        String title = "Bad submit!";
                        String message = "Submit was ineffective! The number could not be parsed!";
                        JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
                        System.out.println(message);
                        ex.printStackTrace();
                        return;
                    }

                    if (submitNumberWorkers < 1) {
                        String title = "Bad submit!";
                        String message = "Submit was ineffective! Practically no workers could be found!";
                        JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
                        System.out.println(message);
                        try {
                            man.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return;
                    }

                    if (submitNumberWorkers > 100) {
                        String title = "Bad submit!";
                        String message = "Submit was ineffective! To many workers wanted to be evacuated!";
                        JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
                        System.out.println(message);
                        try {
                            man.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return;
                    }

                    if (numberWorkers - submitNumberWorkers <= minWorkers) {
                        String title = "Bad submit!";
                        String message = "Submit was ineffective! Minimal number of workers was violated!";
                        JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
                        System.out.println(message);
                        try {
                            man.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return;
                    }
                } else if (button.getNodeLabel() instanceof ShipLabel) {
                    ShipLabel shipLabel = (ShipLabel) button.getNodeLabel();
                    Ship ship = shipLabel.getNode();

                    int numberWorkers = ship.getNumberWorkers();

                    ArrayList<Object> submits = button.getSubmitForm().getSubmits();
                    int submitNumberWorkers;
                    try {
                        submitNumberWorkers  = Integer.parseInt(submits.get(0).toString());
                    } catch (Exception ex) {
                        String title = "Bad submit!";
                        String message = "Submit was ineffective! The number could not be parsed!";
                        JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
                        System.out.println(message);
                        ex.printStackTrace();
                        return;
                    }

                    if (submitNumberWorkers < 1) {
                        String title = "Bad submit!";
                        String message = "Submit was ineffective! Practically no workers could be found!";
                        JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
                        System.out.println(message);
                        try {
                            man.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return;
                    }

                    if (numberWorkers - submitNumberWorkers < 0) {
                        String title = "Bad submit!";
                        String message = "Submit was ineffective! Virtual workers want to be redeployed!";
                        JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
                        System.out.println(message);
                        try {
                            man.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return;
                    }
                }

                WorkersRedeployForm workersRedeployForm = (WorkersRedeployForm) button.getSubmitForm();
                Node node = workersRedeployForm.getNodeLabel().getNode();
                String limit = workersRedeployForm.getSubmits().get(0).toString();

                CompletableFuture<ArrayList<Worker>> completableFuture = CompletableFuture.supplyAsync(
                        () -> workersList(node, limit)
                );

                while (!completableFuture.isDone()) {
                    System.out.println("CompletableFuture is not finished yet...");
                }

                WorkersLabel workersLabel = null;
                try {
                    System.out.println("CompletableFuture turned out to be: successful");
                    workersLabel = new WorkersLabel(
                            button.getNodeLabel().getNode(),
                            completableFuture.get(),
                            scaleIcon(WORKER_ICON, SMALL_IMAGE_WIDTH, SMALL_IMAGE_HEIGHT),
                            frame,
                            button.getNodeLabel().getX(),
                            button.getNodeLabel().getY()
                    );

                    workersLabel.addMouseListener(this);
                    workersLabel.addMouseMotionListener(this);

                    pane.add(workersLabel);
                    pane.moveToFront(workersLabel);
                    frame.validate();

                    man.close();
                } catch (InterruptedException ex) {
                    String title = "Unsuccessful WorkersLabel addition!";
                    String message = "CompletableFuture turned out to be: unsuccessful";
                    JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
                    System.out.println(message);
                    ex.printStackTrace();
                } catch (ExecutionException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (e.getSource() instanceof AcceptButton) {
            AcceptButton acceptButton = (AcceptButton) e.getSource();
            if (acceptButton.getAccept()) {
                acceptButton.getEvacuateForm().dispose();
                try {
                    man.start();
                    refresh();
                    man.close();
                } catch (Exception ex) {
                    String title = "Unsuccessful evacuation process!";
                    String message = "Proposed evacuation couldn't be displayed!";
                    JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
                    System.out.println(message);
                    ex.printStackTrace();
                }
                return;
            }

            EvacuateForm evacuateForm = acceptButton.getEvacuateForm();
            try {
                man.start();
                for (String query : evacuateForm.getDeclineQueries()) {
                    man.writeTx(query);
                }
                refresh();
                man.close();
            } catch (Exception ex) {
                String title = "Unsuccessful inverse evacuation process!";
                String message = "Proposed evacuation couldn't be refused!";
                JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
                System.out.println(message);
                ex.printStackTrace();
            }
            acceptButton.getEvacuateForm().dispose();
        }
    }

    /**
     * Create a cypher query returns a number of workers who are deployed at a node
     * @param node The node the workers are deployed at
     * @param limit The number of workers
     * @return String which represents the cypher query
     */
    private String cypherWorkers(Node node, String limit) {
        Worker worker = new Worker();
        worker.setNodeVariable("w");

        Relationship deployed = new Relationship("DEPLOYED");

        String cypherQuery = node.cypher("MATCH", "n") + "\n"
                + worker.cypherRelationshipTo("MATCH", "n", deployed, "") + "\n"
                + "RETURN w.name, w.job LIMIT " + limit;

        return cypherQuery;
    }

    /**
     * Create a list of workers from a given node
     * @param node The node the workers are deployed on
     * @param limit The amount of workers
     * @return The list of workers
     */
    private ArrayList<Worker> workersList(Node node, String limit) {
        ArrayList<Record> records = man.writeTx(cypherWorkers(node, limit));

        ArrayList<Worker> workers = new ArrayList<>();
        for (Record record : records) {
            String name = record.get("w.name").asString();
            String job = record.get("w.job").asString();

            workers.add(new Worker(name, job));
        }

        return workers;
    }

    /**
     * Evacuate the workers and ships from an oil rig with cypher queries
     * Ask for acceptance of the evacuation via an EvacuationForm
     * @param oilRigLabel The oil rig that shall be evacuated
     */
    public void evacuate(OilRigLabel oilRigLabel) {
        OilRig oilRig = oilRigLabel.getNode();
        int numberWorkers = oilRig.getNumberWorkers();

        ArrayList<String> declineQueries = new ArrayList<>();
        ArrayList<String> evacuateText = new ArrayList<>();

        // Iterate through all ships that anchor on the oil rig
        // Redeploy workers on these ships
        ArrayList<Ship> shipArrayList = new ArrayList<>();
        for (Ship ship : map.get(oilRig)) {
            shipArrayList.add(ship);

            if (numberWorkers <= 0)
                continue;

            Integer maxWorkers = ship.getMaxWorkers();
            ArrayList<Worker> workers = workersList(oilRig, maxWorkers.toString());

            if (ship.getNumberWorkers() + workers.size() > maxWorkers)
                continue;

            ship.setNumberWorkers(workers.size());
            man.writeTx(cypherRedeployWorker(workers, ship));
            evacuateText.add(redeployWorker(workers, oilRig, ship));

            declineQueries.add(cypherRedeployWorker(workers, oilRig));

            numberWorkers -= maxWorkers;
        }

        // If workers remain, but all anchored ships are full
        if (numberWorkers > 0) {
            // Iterate through other oil rigs and use their ships to evacuate
            for (OilRig partner : map.keySet()) {
                if (partner.equals(oilRig))
                    continue;

                ArrayList<Ship> ships = map.get(partner);
                for (int i = 0; i < ships.size()-1; i++) {
                    Ship ship = ships.get(i);
                    Integer maxCapacity = ship.getMaxWorkers();

                    ArrayList<Worker> workers = workersList(oilRig, maxCapacity.toString());
                    ship.setNumberWorkers(workers.size());
                    man.writeTx(cypherRedeployWorker(workers, ship));
                    evacuateText.add(redeployWorker(workers, oilRig, ship));

                    declineQueries.add(cypherRedeployWorker(workers, oilRig));

                    shipArrayList.add(ship);
                    numberWorkers -= maxCapacity;

                    if (numberWorkers <= 0)
                        break;
                }
                if (numberWorkers <= 0)
                    break;
            }
        }

        // Iterate through other oil rigs and determine if
        // the ships that have taken part in the evacuation (inside the shipArrayList)
        // can anchor and be unloaded there
        for (OilRig partner : map.keySet()) {
            if (partner.equals(oilRig))
                continue;
            ArrayList<Ship> checkedShips = new ArrayList<>();
            for (int i = 0; i <  shipArrayList.size(); i++) {
                Ship ship = shipArrayList.get(i);

                if (hasOilRigShipCapacity(partner, ship)) {
                    // Check if the ships workers can be redeployed on the oil rig
                    if (partner.getNumberWorkers() + ship.getNumberWorkers() <= partner.getMaxWorkers()) {
                        man.writeTx(cypherRedeployShip(ship, partner));
                        evacuateText.add(redeployShip(ship, oilRig, partner));

                        declineQueries.add(cypherRedeployShip(ship, oilRig));

                        ArrayList<Worker> workers = workersList(ship, ship.getMaxWorkers().toString());
                        if (workers.size() > 0) {
                            man.writeTx(cypherRedeployWorker(workers, partner));
                            evacuateText.add(redeployWorker(workers, ship, partner));

                            declineQueries.add(cypherRedeployWorker(workers, oilRig));
                        }

                        checkedShips.add(ship);

                        partner.setNumberWorkers(partner.getNumberWorkers() + workers.size());
                        partner.setNumberShips(partner.getNumberShips() + 1);
                    }
                }
            }
            shipArrayList.removeAll(checkedShips);
        }
        // Open an EvacuationForm to ask for acceptance of the evacuation process
        new EvacuateForm(this, declineQueries, evacuateText);
    }

    /**
     * Create evacuationString that shows the evacuation steps
     * @param workers The workers who need to be redeployed
     * @param oldNode The node the workers need to be detached from
     * @param newNode The node the workers have to ba attached to
     * @return String that represents the evacuation step
     */
    private String redeployWorker(ArrayList<Worker> workers, Node oldNode, Node newNode) {
        String format = "%d have been moved from the %s named '%s' to the %s named '%s'.";

        String oldNodeLabel = "";
        switch (oldNode.getNodeLabel()) {
            case OIL_RIG: oldNodeLabel = "oil rig"; break;
            case SHIP: oldNodeLabel = "ship"; break;
            default: oldNodeLabel = "undefined";
        }

        String newNodeLabel = "";
        switch (newNode.getNodeLabel()) {
            case OIL_RIG: newNodeLabel = "oil rig"; break;
            case SHIP: newNodeLabel = "ship"; break;
            default: newNodeLabel = "undefined";
        }

        return String.format(
                format,
                workers.size(),
                oldNodeLabel,
                oldNode.getName(),
                newNodeLabel,
                newNode.getName()
        );
    }

    /**
     * Create evacuationString that shows the evacuation steps
     * @param ship The ship that has to be evacuated
     * @param oilRig The oil rig the ship shall be detached from
     * @param partner The oil rig the ship has to be attached to
     * @return String that represents the evacuation step
     */
    private String redeployShip(Ship ship, OilRig oilRig, OilRig partner) {
        return String.format(
                "The ship named '%s' has moved from the oil rig '%s' to the oil rig '%s'",
                ship.getName(),
                oilRig.getName(),
                partner.getName()
        );
    }

    /**
     * Update the pane by calling initNodeLabels asynchronously
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private void refresh() throws InterruptedException, ExecutionException {
        JLayeredPane tmpPane = new JLayeredPane();
        tmpPane.setPreferredSize(pane.getPreferredSize());
        CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        initNodeLabels(tmpPane);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
        );

        while (!completableFuture.isDone()) {
            System.out.println("CompletableFuture is not finished yet...");
        }

        System.out.println("CompletableFuture turned out to be: " + completableFuture.get().toString());
        updateDisplay(tmpPane);
    }
}
