package frontend.forms;

import frontend.UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CompletableFuture;

/**
 * loginForm designed with IntelliJ Swing UI Designer - GUI Form
 */
public class LoginForm extends JFrame {
    private JLabel uriLabel;
    private JTextField uriTextField;
    private JLabel userNameLabel;
    private JTextField userNameTextField;
    private JLabel passwordLabel;
    private JPanel rootPanel;
    private JPasswordField passwordField;
    private JButton submitButton;

    /**
     * Create Login form
     */
    public LoginForm() {
        setTitle("ÖLISOFT - v1.0 - login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("resources/icon.png").getImage());

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String uri = uriTextField.getText();
                String userName = userNameTextField.getText();
                String password = String.valueOf(passwordField.getPassword());

                UI ui = new UI();

                CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(
                        () -> ui.init(uri, userName, password)
                );

                while (!completableFuture.isDone()) {
                    System.out.println("CompletableFuture is not finished yet...");
                }

                try {
                    System.out.println("CompletableFuture turned out to be: " + completableFuture.get().toString());
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            ui.display();
                        }
                    });
                    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                } catch (Exception ex) {
                    String title = "Bad database connection!";
                    String message = "Check your login credentials, or your database connection!";
                    JOptionPane.showMessageDialog(null, message, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        add(rootPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginForm();
    }
}
