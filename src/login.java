import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Font;
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

	// Panel size this layout is centered against — matches mainpage's
	// 900x600 convention so both cards line up inside the 920x650 window.
	private static final int PANEL_WIDTH = 900;
	private static final int PANEL_HEIGHT = 600;

	/**
	 * Create the panel.
	 */
	public login(window parentWindow) {
		setLayout(null);
		setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

		// Form block: a 100px label column + 10px gap + 200px field column,
		// centered horizontally; rows centered vertically as a group.
		int labelWidth = 100;
		int fieldWidth = 200;
		int gap = 10;
		int blockWidth = labelWidth + gap + fieldWidth;
		int blockLeft = (PANEL_WIDTH - blockWidth) / 2;
		int fieldLeft = blockLeft + labelWidth + gap;

		int rowHeight = 25;
		int rowGap = 15;
		int titleHeight = 40;
		int titleToFormGap = 30;
		int formToButtonGap = 25;
		int buttonHeight = 30;

		int blockHeight = titleHeight + titleToFormGap + rowHeight + rowGap + rowHeight + formToButtonGap + buttonHeight;
		int top = (PANEL_HEIGHT - blockHeight) / 2;

		int titleTop = top;
		int usernameTop = titleTop + titleHeight + titleToFormGap;
		int passwordTop = usernameTop + rowHeight + rowGap;
		int buttonTop = passwordTop + rowHeight + formToButtonGap;

		JLabel lblNewLabel_2 = new JLabel("LOGIN", SwingConstants.CENTER);
		lblNewLabel_2.setFont(lblNewLabel_2.getFont().deriveFont(Font.BOLD, 20f));
		lblNewLabel_2.setBounds(-41, 202, PANEL_WIDTH, titleHeight);
		add(lblNewLabel_2);

		JLabel lblNewLabel = new JLabel("Username : ", SwingConstants.RIGHT);
		lblNewLabel.setBounds(273, 275, labelWidth, rowHeight);
		add(lblNewLabel);

		textField = new JTextField();
		textField.setBounds(405, 275, 133, 25);
		add(textField);
		textField.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Password : ", SwingConstants.RIGHT);
		lblNewLabel_1.setBounds(273, 315, labelWidth, rowHeight);
		add(lblNewLabel_1);

		textField_1 = new JTextField();
		textField_1.setBounds(405, 315, 133, 25);
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
		int buttonWidth = 100;
		btnNewButton.setBounds(369, 376, buttonWidth, buttonHeight);
		add(btnNewButton);

	}
}
