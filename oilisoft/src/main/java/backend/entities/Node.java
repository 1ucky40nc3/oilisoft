package backend.entities;

import java.util.HashMap;
import java.util.Iterator;

/** Represents a Node of the neo4j graph database
 * @author Louis Wendler
 * @since 1.0
 * @version 1.0
 */
public abstract class Node {

    private final String label;
    private final NodeLabels nodeLabel;
    private HashMap<String, Object> attributes;

    private String nodeVariable;

    /**
     * Create a Node
     * @param label Label of the neo4j database node
     * @param nodeLabel NodeLabel that represents a label of a node
     */
    public Node(String label, NodeLabels nodeLabel) {
        this.label = label;
        this.nodeLabel = nodeLabel;

        attributes = new HashMap<>();
    }

    /**
     * Map attributes as key-value pairs in HashMap
     * @return
     */
    abstract HashMap initAttributes();

    /**
     * Create String of attributes to match the node
     * @return String of attributes
     */
    private String getAttributeString() {
        String attributeString = "";

        for (Iterator<String> keys = attributes.keySet().iterator(); keys.hasNext();) {
            String key = keys.next();

            Object value = attributes.get(key);
            if (value == null) continue;

            String valueString = value.toString();
            if (value instanceof String)
                valueString = "\"" + valueString + "\"";

            if (keys.hasNext())
                attributeString += String.format("%s:%s, ", key, valueString);
            else
                attributeString += String.format("%s:%s", key, valueString);
        }

        return attributeString;
    }

    /**
     * Create cypher query for this Node
     * @param clause Cypher clause (MATCH, CREATE,...)
     * @param nodeVariable Variable which refers to the node in the specific query
     * @return String which represents the cypher query
     */
    public String cypher(String clause, String nodeVariable) {
        this.nodeVariable = nodeVariable;
        return String.format("%s (%s:%s {%s})", clause, nodeVariable, label, getAttributeString());
    }

    /**
     * Create cypher query for a Relationship
     * @param clause Cypher clause (MATCH, CREATE,...)
     * @param otherNodeVariable Variable which refers to the other node in the specific query
     * @param relationship Relationship that has to be worked with
     * @param relationshipVariable Variable which refers to the relationship in the specific query
     * @return String which represents the cypher query
     */
    public String cypherRelationshipTo(String clause, String otherNodeVariable, Relationship relationship, String relationshipVariable) {
        return String.format("%s (%s)-%s->(%s)", clause, nodeVariable, relationship.getCypherString(relationshipVariable), otherNodeVariable);
    }

    /**
     * Check this Node and other for equality
     * @param other Node
     * @return True if equal
     */
    public boolean equals(Node other) {
        if (!label.equals(other.label))
            return false;

        if (attributes.keySet().size() != other.attributes.keySet().size())
            return false;

        Iterator<String> keys = attributes.keySet().iterator();
        Iterator<String> otherKeys = other.attributes.keySet().iterator();

        while (keys.hasNext() && otherKeys.hasNext()) {
            String key = keys.next();
            String otherKey = otherKeys.next();

            if (!key.equals(otherKey))
                return false;

            Object value = attributes.get(key);
            Object otherValue = other.attributes.get(otherKey);

            if (!value.equals(otherValue))
                return false;
        }

        return true;
    }

    public NodeLabels getNodeLabel() {
        return nodeLabel;
    }

    public abstract String getName();

    public void setNodeVariable(String nodeVariable) {
        this.nodeVariable = nodeVariable;
    }

    public String getNodeVariable() {
        return nodeVariable;
    }

    public void setAttributes(HashMap<String, Object> attributes) {
        this.attributes = attributes;
    }

    public HashMap<String, Object> getAttributes() {
        return attributes;
    }
}