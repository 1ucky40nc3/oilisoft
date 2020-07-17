package backend;

import org.neo4j.driver.*;

import java.util.ArrayList;

/**
 * Database management
 * @author Louis Wendler
 * @since 1.0
 * @version 1.0
 */
public class DatabaseManagement implements AutoCloseable {

    private final String uri;
    private final String user;
    private final String password;

    private Driver driver;

    /**
     * Creates an DatabaseManagement object,
     * which is taylor made for one database connection
     * @param uri Address of the database
     * @param user Username of a specific user
     * @param password Password to authenticate user
     */
    public DatabaseManagement(String uri, String user, String password) {
        this.uri = uri;
        this.user = user;
        this.password = password;

        start();
    }

    /**
     * Initialize a new unclosed database driver
     */
    public void start() {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    /**
     * Close the database driver
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        driver.close();
    }

    /**
     * Write a database transaction and receive records
     * @param cypherQuery Cypher query as a String
     * @return Result of query as List of Records
     */
    public ArrayList<Record> writeTx(String cypherQuery) {
        ArrayList<Record> records = new ArrayList<>();

        try (Session session = driver.session()) {
            Result result = session.run(cypherQuery);
            while(result.hasNext()) {
                records.add(result.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return records;
    }
}
