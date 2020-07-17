package frontend.forms;

import frontend.labels.NodeLabel;

import java.util.ArrayList;

/**
 * SubmitForm interface that unites all forms with SubmitButtons
 */
public interface SubmitForm {

    Forms getFormType();

    /**
     * Collect all submits in the form
     * @return List of submits
     */
    ArrayList<Object> getSubmits();

    NodeLabel getNodeLabel();

    void disposeFrame();
}
