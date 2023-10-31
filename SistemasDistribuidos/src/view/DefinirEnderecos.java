package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

public class DefinirEnderecos extends JFrame {

	private JPanel contentPane;
	private JTextField textIP;
	private JTextField textPorta;
    private static Socket echoSocket = null;
    private static PrintWriter out = null;
	private static BufferedReader in = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DefinirEnderecos frame = new DefinirEnderecos();
					frame.setVisible(true);
					frame.setLocationRelativeTo(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DefinirEnderecos() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnConfirmar = new JButton("Confirmar");
		btnConfirmar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {	
					
					
					echoSocket = new Socket(textIP.getText(), Integer.parseInt(textPorta.getText().toString()));
//					echoSocket = new Socket("127.1.0.0", 24001);
					out = new PrintWriter(echoSocket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
					System.out.println("Conexão Iniciada");
					ViewLogin login = new ViewLogin(echoSocket);
					login.setVisible(true);
					login.setLocationRelativeTo(null);
					dispose();
					
					
				} catch (UnknownHostException e1) {
					System.err.println("Don't know about host: " + textIP.getText());
					System.exit(1);
				} catch (IOException e1) {
					System.err.println("Couldn't get I/O for " + "the connection to: " + textIP.getText());
					System.exit(1);
				}
				
			}
		});
		btnConfirmar.setBounds(170, 168, 111, 21);
		contentPane.add(btnConfirmar);
		
		textIP = new JTextField();
		textIP.setBounds(127, 56, 185, 19);
		contentPane.add(textIP);
		textIP.setColumns(10);
		
		textPorta = new JTextField();
		textPorta.setBounds(127, 106, 185, 19);
		contentPane.add(textPorta);
		textPorta.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("IP");
		lblNewLabel.setBounds(83, 59, 45, 13);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Porta");
		lblNewLabel_1.setBounds(83, 109, 45, 13);
		contentPane.add(lblNewLabel_1);
	}

	public String getTextIP() {
		return textIP.getText();
	}

	public String getTextPorta() {
		return textPorta.getText();
	}
}
