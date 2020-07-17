package backend.databases;

import backend.DatabaseManagement;
import backend.entities.OilRig;

import java.util.ArrayList;

/**
 * Create a example neo4j graph database
 * @author Louis Wendler
 * @since 1.0
 * @version 1.0
 */
public class ExampleDatabase {

    /**
     * Create a example neo4j graph database
     * @param man The DatabaseManagement instance that is used to write the transactions
     */
    public ExampleDatabase(DatabaseManagement man) {
        man.writeTx("MATCH (n) DETACH DELETE n");
        man.writeTx(cypherQuery());
    }

    /**
     * Create cypher query for an example neo4j graph database
     * @return String which represents the cypher query
     */
    private String cypherQuery() {
        ArrayList<OilRig> oilRigs = new ArrayList<>();
        OilRig oilRig0 = new OilRig("Sea Troll", 760, 4, 5);
        OilRig oilRig1 = new OilRig("Byford Dolphin", 520, 4, 4);
        oilRigs.add(oilRig1);
        OilRig oilRig2 = new OilRig("Petronius", 360, 4, 3);
        oilRigs.add(oilRig2);
        OilRig oilRig3 = new OilRig("Perdido", 120, 2, 2);
        oilRigs.add(oilRig3);

        return oilRig0.cypher("CREATE", "o0") + "\n"
                + oilRig1.cypher("CREATE", "o1") + "\n"
                + oilRig2.cypher("CREATE", "o2") + "\n"
                + oilRig3.cypher("CREATE", "o3") + "\n"
                + oilRig0.cypherRelationshipToOilRigs(oilRigs) + "\n"
                + oilRig0.cypherCreateShips()
                + oilRig0.cypherCreateWorkers()
                + oilRig1.cypherCreateShips()
                + oilRig1.cypherCreateWorkers()
                + oilRig2.cypherCreateShips()
                + oilRig2.cypherCreateWorkers()
                + oilRig3.cypherCreateShips()
                + oilRig3.cypherCreateWorkers();
    }
}
