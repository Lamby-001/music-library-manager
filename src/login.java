import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.Scanner;

public class login extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextField textField_1;
    static Scanner scanner = new Scanner(System.in);

	/**
	 * Create the panel.
	 */
	public login(window parentWindow) {
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("username");
		lblNewLabel.setBounds(73, 89, 66, 12);
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("password");
		lblNewLabel_1.setBounds(73, 138, 66, 12);
		add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("New label");
		lblNewLabel_2.setBounds(180, 24, 77, 12);
		add(lblNewLabel_2);
		
		textField = new JTextField();
		textField.setBounds(161, 86, 96, 18);
		add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setBounds(161, 135, 96, 18);
		add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnNewButton = new JButton("continue");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// Store the connection on window.conn (the field mainpage actually reads from),
					// not a local field on login — that was the bug: mainpage.getConn() was reading
					// window.conn, which was never assigned, so it always looked "disconnected".
					window.conn = DBconnection.getConnection(textField.getText(), textField_1.getText());

					parentWindow.switchToMainPage();
				}
				catch(Exception exp) {
					JOptionPane.showMessageDialog(login.this,"Login Failed: " + exp.getMessage(), "Database Error",JOptionPane.ERROR_MESSAGE);
					System.out.println(exp.getMessage());
				}
				
			}
		});
		btnNewButton.setBounds(161, 204, 84, 20);
		add(btnNewButton);

	}
}
