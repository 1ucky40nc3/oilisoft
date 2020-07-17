package backend.entities;

import java.util.ArrayList;
import java.util.HashMap;

/** Represents an oil rig node
 * @author Louis Wendler
 * @since 1.0
 * @version 1.0
 */
public class OilRig extends Node {

    private final String name;
    private final Integer initNumberWorkers;
    private Integer numberWorkers;
    private final Integer minWorkers;
    private final Integer maxWorkers;
    private final Integer initNumberSmallShips;
    private Integer numberSmallShips;
    private final Integer maxNumberSmallShips;
    private final Integer initNumberBigShips;
    private Integer numberBigShips;
    private final Integer maxNumberBigShips;
    private Integer numberShips;

    /**
     * Create a OilRig
     * @param name The oil rig's unique name
     * @param initNumberWorkers Initial number of workers who are deployed on the oil rig
     * @param initNumberSmallShips Initial number of small ships which anchor on the oil rig
     * @param initNumberBigShips Initial number of big ships which anchor on the oil rig
     */
    public OilRig(String name,
                  int initNumberWorkers,
                  int initNumberSmallShips,
                  int initNumberBigShips) {

        super("OilRig", NodeLabels.OIL_RIG);

        this.name = name;
        this.initNumberWorkers = initNumberWorkers;
        this.minWorkers = (int)(0.1 * initNumberWorkers);
        this.maxWorkers = 2 * initNumberWorkers;
        this.initNumberSmallShips = initNumberSmallShips;
        this.numberSmallShips = initNumberSmallShips;
        this.maxNumberSmallShips = 4 + initNumberSmallShips;
        this.initNumberBigShips = initNumberBigShips;
        this.numberBigShips = initNumberBigShips;
        this.maxNumberBigShips = 4 + initNumberBigShips;
        this.numberShips = initNumberSmallShips + initNumberBigShips;

        super.setAttributes(initAttributes());
    }

    /**
     * Create a oil rig node as blueprint
     */
    public OilRig() {
        super("OilRig", NodeLabels.OIL_RIG);

        this.name = null;
        this.initNumberWorkers = null;
        this.minWorkers = null;
        this.maxWorkers = null;
        this.initNumberSmallShips = null;
        this.maxNumberSmallShips = null;
        this.initNumberBigShips = null;
        this.maxNumberBigShips = null;
    }

    /**
     * See Node.initAttributes()
     * @return The oil rig's attributes as HashMap
     */
    @Override
    protected HashMap initAttributes() {
        HashMap<String, Object> attributes = new HashMap<>();

        attributes.put("name", name);
        attributes.put("initNumberWorkers", initNumberWorkers);
        attributes.put("initNumberSmallShips", initNumberSmallShips);
        attributes.put("initNumberBigShips", initNumberBigShips);

        return attributes;
    }

    /**
     * Create a cypher query that relates oil rigs as partners
     * @param oilRigs All other oil rigs which shall be related
     * @return String which represents the cypher query
     */
    public String cypherRelationshipToOilRigs(ArrayList<OilRig> oilRigs) {
        String cypherQuery = "";
        Relationship partner = new Relationship("PARTNER");

        oilRigs.add(this);
        for (int i = 1; i < oilRigs.size(); i++) {
            OilRig newOilRig = oilRigs.get(i);
            for (int j = i-1; j > -1; j--) {
                cypherQuery += newOilRig.cypherRelationshipTo("CREATE", oilRigs.get(j).getNodeVariable(), partner, "") + "\n";
            }
        }

        return cypherQuery;
    }

    /**
     * Create a cypher query that creates and relates an all initial ships
     * @return String which represents the cypher query
     */
    public String cypherCreateShips() {
        String cypherQuery = "";
        Relationship anchored = new Relationship("ANCHORED");

        for (int i = 0; i < initNumberSmallShips; i++) {
            String iToString = Integer.toString(i);
            Ship ship = new Ship("smallShip" + iToString + getNodeVariable(), 50);
            cypherQuery += ship.cypher("CREATE", ship.getName()) + "\n";
            cypherQuery += ship.cypherRelationshipTo("CREATE", getNodeVariable(), anchored, "") + "\n";
        }

        for (int i = 0; i < initNumberBigShips; i++) {
            String iToString = Integer.toString(i);
            Ship ship = new Ship("bigShip" + iToString + getNodeVariable(), 100);
            cypherQuery += ship.cypher("CREATE", ship.getName()) + "\n";
            cypherQuery += ship.cypherRelationshipTo("CREATE", getNodeVariable(), anchored, "") + "\n";
        }

        return cypherQuery;
    }

    /**
     * Create a cypher query that finds all ships which anchor on this oil rig
     * @return String which represents the cypher query
     */
    public String cypherMatchShips() {
        Ship ship = new Ship();
        ship.setNodeVariable("Ship");
        Relationship anchored = new Relationship("ANCHORED");

        return cypher("MATCH", "o") + "\n"
                + ship.cypherRelationshipTo("MATCH", "o", anchored, "") + "\n"
                + "RETURN Ship.name, Ship.maxCapacity";
    }

    /**
     * Create a cypher query that counts the number of small ships which anchor on this oil rig
     * @return String which represents the cypher query
     */
    public String cypherCountSmallShips() {
        Ship ship = new Ship();
        ship.setNodeVariable("s");
        Relationship anchored = new Relationship("ANCHORED");

        String cypherQuery = cypher("MATCH", "o") + "\n"
                + ship.cypherRelationshipTo("MATCH", "o", anchored, "") + "\n"
                + "WHERE s.maxCapacity = 50 RETURN count(s) as count";

        return cypherQuery;
    }

    /**
     * Create a cypher query that counts the number of big ships which anchor on this oil rig
     * @return String which represents the cypher query
     */
    public String cypherCountBigShips() {
        Ship ship = new Ship();
        ship.setNodeVariable("s");
        Relationship anchored = new Relationship("ANCHORED");

        String cypherQuery = cypher("MATCH", "o") + "\n"
                + ship.cypherRelationshipTo("MATCH", "o", anchored, "") + "\n"
                + "WHERE s.maxCapacity = 100 RETURN count(s) as count";

        return cypherQuery;
    }

    /**
     * Create a cypher query that creates and relates an all initial workers
     * @return String which represents the cypher query
     */
    public String cypherCreateWorkers() {
        String cypherQuery = "";
        Relationship deployed = new Relationship("DEPLOYED");

        for (int i = 0; i < initNumberWorkers; i++) {
            String iToString = Integer.toString(i);
            Worker worker = new Worker("worker" + iToString + getNodeVariable(), "mechanic");
            cypherQuery += worker.cypher("CREATE", worker.getName());
            cypherQuery += worker.cypherRelationshipTo("CREATE", getNodeVariable(), deployed, "");
        }

        return cypherQuery;
    }

    /**
     * Create a cypher query that counts the number of workers who are deployed on this oil rig
     * @return String which represents the cypher query
     */
    public String cypherCountWorkers() {
        Worker worker = new Worker();
        worker.setNodeVariable("w");
        Relationship deployed = new Relationship("DEPLOYED");

        String cypherQuery = cypher("MATCH", "o") + "\n"
                + worker.cypherRelationshipTo("MATCH", "o", deployed, "") + "\n"
                + "RETURN count(w) as count";

        return cypherQuery;
    }

    @Override
    public String getName() {
        return name;
    }

    public Integer getInitNumberWorkers() {
        return initNumberWorkers;
    }

    public void setNumberWorkers(int numberWorkers) {
        this.numberWorkers = numberWorkers;
    }

    public Integer getNumberWorkers() {
        return numberWorkers;
    }

    public Integer getMinWorkers() {
        return minWorkers;
    }

    public Integer getMaxWorkers() {
        return maxWorkers;
    }

    public void setNumberSmallShips(int numberSmallShips) {
        this.numberSmallShips = numberSmallShips;
        this.numberShips = numberSmallShips + numberBigShips;
    }

    public Integer getNumberSmallShips() {
        return numberSmallShips;
    }

    public Integer getMaxNumberSmallShips() {
        return maxNumberSmallShips;
    }

    public void setNumberBigShips(int numberBigShips) {
        this.numberBigShips = numberBigShips;
        this.numberShips = numberSmallShips + numberBigShips;
    }

    public Integer getNumberBigShips() {
        return numberBigShips;
    }

    public Integer getMaxNumberBigShips() {
        return maxNumberBigShips;
    }

    public void setNumberShips(Integer numberShips) {
        this.numberShips = numberShips;
    }

    public Integer getNumberShips() {
        return numberShips;
    }

    public Integer getMaxShips() {
        return maxNumberSmallShips + maxNumberBigShips;
    }
}
