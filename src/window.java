import java.awt.EventQueue;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.sql.Connection;
import java.util.Scanner;
public class window extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private CardLayout cardLayout;
	static Connection conn;
    static Scanner scanner = new Scanner(System.in);
    private login loginPanel; 
    private mainpage mainPanel;
	/**
	 * Launch the application.
	 */
    
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window frame = new window();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public window() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 920, 650);
		cardLayout=new CardLayout();
		contentPane = new JPanel(cardLayout);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		loginPanel=new login(this);
		mainPanel=new mainpage();
		contentPane.add(loginPanel, "LOGIN_SCREEN");
        contentPane.add(mainPanel, "MAIN_SCREEN");
        cardLayout.show(contentPane, "LOGIN_SCREEN");

	}
	public void switchToMainPage() {
        cardLayout.show(contentPane, "MAIN_SCREEN");
    }

}
