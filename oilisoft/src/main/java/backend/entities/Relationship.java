package backend.entities;

import java.util.HashMap;
import java.util.Iterator;

/** Represents relationship between two nodes
 * @author Louis Wendler
 * @since 1.0
 * @version 1.0
 */
public class Relationship {

    private String relationshipVariable;
    private final String label;

    private HashMap<String, Object> attributes;

    public Relationship(String label) {
        this.label = label;
        attributes = new HashMap<>();
    }

    public String getCypherString(String relationshipVariable) {
        this.relationshipVariable = relationshipVariable;
        if (label.equals("") && getAttributeString().equals(""))
            return String.format("[%s]", relationshipVariable);

        return String.format("[%s:%s%s]", relationshipVariable, label, getAttributeString());
    }

    public void setAttributes(HashMap<String, Object> attributes) {
        this.attributes = attributes;
    }

    private String getAttributeString() {
        String attributeString = "";

        if (attributes.isEmpty())
            return attributeString;

        for (Iterator<String> keys = attributes.keySet().iterator(); keys.hasNext();) {
            String key = keys.next();

            Object value = attributes.get(key);
            String valueString = value.toString();
            if (value instanceof String)
                valueString = "\"" + valueString + "\"";

            if (keys.hasNext())
                attributeString += String.format("%s:%s, ", key, valueString);
            else
                attributeString += String.format("%s:%s", key, valueString);
        }

        return " {" + attributeString + "}";
    }
}
