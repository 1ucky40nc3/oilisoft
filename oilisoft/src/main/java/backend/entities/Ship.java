package backend.entities;

import java.util.HashMap;

/** Represents a ship node
 * @author Louis Wendler
 * @since 1.0
 * @version 1.0
 */
public class Ship extends Node {

    private final String name;
    private final Integer maxCapacity;
    private Integer numberWorkers;

    /**
     * Create a Ship with name and capacity
     * @param name The ship's unique name
     * @param maxWorkers The maximum amount of workers the ship can transport (makes ship small/big)
     */
    public Ship(String name, int maxWorkers) {
        super("Ship", NodeLabels.SHIP);

        this.name = name;
        this.maxCapacity = maxWorkers;

        super.setAttributes(initAttributes());
    }

    /**
     * Create a oil rig node as blueprint
     */
    public Ship() {
        super("Ship", NodeLabels.SHIP);

        this.name = null;
        this.maxCapacity = null;
    }

    /**
     * See Node.initAttributes()
     * @return The ship's attributes as HashMap
     */
    @Override
    protected HashMap initAttributes() {
        HashMap<String, Object> attributes = new HashMap<>();

        attributes.put("name", name);
        attributes.put("maxCapacity", maxCapacity);

        return attributes;
    }

    /**
     * Create a cypher query that counts the number of workers who are connected to this ship
     * @return String which represents the cypher query
     */
    public String cypherCountWorkers() {
        Worker worker = new Worker();
        worker.setNodeVariable("w");
        Relationship transported = new Relationship("DEPLOYED");

        String cypherQuery = cypher("MATCH", "s") + "\n"
                + worker.cypherRelationshipTo("MATCH", "s", transported, "") + "\n"
                + "RETURN count(w) as count";

        return cypherQuery;
    }

    @Override
    public String getName() {
        return name;
    }

    public Integer getMaxWorkers() {
        return maxCapacity;
    }

    public void setNumberWorkers(int numberWorkers) {
        this.numberWorkers = numberWorkers;
    }

    public Integer getNumberWorkers() {
        return numberWorkers;
    }
}
