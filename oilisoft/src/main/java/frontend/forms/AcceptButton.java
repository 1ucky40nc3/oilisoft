package frontend.forms;

import javax.swing.*;

/**
 * Custom JButton to accept an evacuation
 */
public class AcceptButton extends JButton {

    private final boolean accept;
    private final EvacuateForm evacuateForm;

    /**
     * Create an AcceptButton
     * @param evacuateForm The EvacuateForm this is part of
     * @param accept True if this's function is to accept the evacuation
     */
    public AcceptButton(EvacuateForm evacuateForm, boolean accept) {
        this.accept = accept;
        this.evacuateForm = evacuateForm;
    }

    public boolean getAccept() {
        return accept;
    }

    public EvacuateForm getEvacuateForm() {
        return evacuateForm;
    }
}
