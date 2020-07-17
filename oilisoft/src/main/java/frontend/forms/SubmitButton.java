package frontend.forms;

import frontend.labels.NodeLabel;

import javax.swing.*;

/**
 * Custom JButton to submit forms
 */
public class SubmitButton extends JButton {

    private NodeLabel nodeLabel;
    private SubmitForm submitForm;

    /**
     * Create SubmitButton
     * @param nodeLabel NodeLabel the SubmitForm was invoked on
     * @param submitForm SubmitForm the SubmitButton is located in
     */
    public SubmitButton(NodeLabel nodeLabel, SubmitForm submitForm) {
        this.nodeLabel = nodeLabel;
        this.submitForm = submitForm;
    }

    public NodeLabel getNodeLabel() {
        return nodeLabel;
    }

    public SubmitForm getSubmitForm() {
        return submitForm;
    }

    public Forms getSubmitFormType() {
        return submitForm.getFormType();
    }
}
