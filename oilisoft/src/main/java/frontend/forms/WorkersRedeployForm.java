package frontend.forms;

import frontend.labels.NodeLabel;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * WorkersRedeployForm designed with IntelliJ Swing UI Designer - GUI Form
 */
public class WorkersRedeployForm extends JFrame implements SubmitForm {

    private JLabel nWorkersLabel;
    private JTextField nWorkersTextField;
    private JButton submitButton;
    private JPanel rootPanel;

    private NodeLabel nodeLabel;

    /**
     * Create WorkersRedeployForm
     * @param nodeLabel The NodeLabel the form was invoked on
     * @param actionListener Shared ActionListener UI
     */
    public WorkersRedeployForm(NodeLabel nodeLabel, ActionListener actionListener) {
        this.nodeLabel = nodeLabel;

        submitButton.addActionListener(actionListener);

        setTitle("ÖLISOFT - v1.0 - workers redeployment form");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon("resources/icon.png").getImage());
        add(rootPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public Forms getFormType() {
        return Forms.WORKERS_REDEPLOY_FORM;
    }

    @Override
    public ArrayList<Object> getSubmits() {
        ArrayList<Object> submits = new ArrayList<>();
        submits.add(nWorkersTextField.getText());

        return submits;
    }

    @Override
    public NodeLabel getNodeLabel() {
        return nodeLabel;
    }

    @Override
    public void disposeFrame() {
        dispose();
    }

    /**
     * Create custom JButton as SubmitButton
     */
    private void createUIComponents() {
        submitButton = new SubmitButton(nodeLabel, this);
    }
}
