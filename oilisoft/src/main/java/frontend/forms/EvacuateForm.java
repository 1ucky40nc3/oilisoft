package frontend.forms;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * loginForm designed with IntelliJ Swing UI Designer - GUI Form
 */
public class EvacuateForm extends JFrame {
    private JPanel rootPanel;
    private JTextArea evacuateTextArea;
    private JButton acceptButton;
    private JButton declineButton;
    private JScrollPane scrollPane;

    private final ArrayList<String> declineQueries;
    private final ArrayList<String> lines;

    /**
     * Create evacuation form
     * @param actionListener The shared ActionListener (UI)
     * @param queries The queries which will be executed when a evacuation was denied
     * @param evacuateText The text that represents the actions of an evacuation
     */
    public EvacuateForm(ActionListener actionListener,
                        ArrayList<String> queries,
                        ArrayList<String> evacuateText) {

        this.declineQueries = queries;
        this.lines = evacuateText;

        setEvacuateTextArea(evacuateText);

        acceptButton.addActionListener(actionListener);
        declineButton.addActionListener(actionListener);

        scrollPane = new JScrollPane();
        scrollPane.setEnabled(true);
        scrollPane.setVerticalScrollBarPolicy(20);

        setTitle("ÖLISOFT - v1.0 - evacuation form");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setIconImage(new ImageIcon("resources/icon.png").getImage());
        add(rootPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setEvacuateTextArea(ArrayList<String> evacuateText) {
        String text = "";

        for (String line : evacuateText) {
            text += line + "\n";
        }

        evacuateTextArea.setText(text);
    }

    public ArrayList<String> getDeclineQueries() {
        return declineQueries;
    }

    private void createUIComponents() {
        acceptButton = new AcceptButton(this, true);
        declineButton = new AcceptButton(this, false);
    }
}
