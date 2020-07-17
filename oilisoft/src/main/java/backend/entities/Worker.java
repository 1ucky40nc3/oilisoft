package backend.entities;

import java.util.HashMap;

/** Represents a worker node
 * @author Louis Wendler
 * @since 1.0
 * @version 1.0
 */
public class Worker extends Node {

    private final String name;
    private final String job;

    /**
     * Creates a Worker with a name and a job
     * @param name The worker's unique name
     * @param job The worker's job
     */
    public Worker(String name, String job) {
        super("Worker", NodeLabels.WORKER);

        this.name = name;
        this.job = job;

        super.setAttributes(initAttributes());
    }

    /**
     * Creates a worker node as a blueprint
     */
    public Worker() {
        super("Worker", NodeLabels.WORKER);

        this.name = null;
        this.job = null;
    }

    /**
     * See Node.initAttributes()
     * @return The worker's attributes as HashMap
     */
    @Override
    protected HashMap initAttributes() {
        HashMap<String, Object> attributes = new HashMap<>();

        attributes.put("name", name);
        attributes.put("job", job);

        return attributes;
    }

    @Override
    public String getName() {
        return name;
    }
}
