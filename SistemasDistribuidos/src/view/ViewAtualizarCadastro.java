package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ClienteeServidor.Usuario;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JTextField;
import javax.swing.JButton;

public class ViewAtualizarCadastro extends JFrame {

	private JFrame frame;
	private JPanel contentPane;
	private JTextField textNome;
	private JTextField textEmail;
	private JPasswordField textSenha;
	private JButton btnCadastrar;
	private Socket echoSocket;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private Gson gson = new Gson();

	public ViewAtualizarCadastro(Socket eSocket,JsonObject aleluia) {
		System.out.println("View Atualizar Cadastro Conectado");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 725, 396);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Nome");
		lblNewLabel.setBounds(147, 92, 45, 13);
		contentPane.add(lblNewLabel);

		textNome = new JTextField();
		textNome.setBounds(186, 89, 314, 19);
		contentPane.add(textNome);
		textNome.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Email");
		lblNewLabel_1.setBounds(147, 139, 45, 13);
		contentPane.add(lblNewLabel_1);

		textEmail = new JTextField();
		textEmail.setBounds(186, 136, 314, 19);
		contentPane.add(textEmail);
		textEmail.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Senha");
		lblNewLabel_2.setBounds(147, 180, 45, 13);
		contentPane.add(lblNewLabel_2);

		textSenha = new JPasswordField();
		textSenha.setBounds(186, 177, 314, 19);
		contentPane.add(textSenha);

		btnCadastrar = new JButton("Cadastrar");
		btnCadastrar.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        ViewLogin telaLogin = new ViewLogin(eSocket);
		        Usuario usuario = new Usuario();

		        String menString = usuario.atualizarCadastro(getTextNome(), getTextEmail(), getTextSenha(),aleluia.get("token").getAsString(),aleluia.get("id_usuario").getAsInt());

		        try {
		            out = new PrintWriter(eSocket.getOutputStream(), true);
		            in = new BufferedReader(new InputStreamReader(eSocket.getInputStream()));
		            out.println(menString);
		            out.flush();
		            System.out.println("Echo: " + menString);

		            String respostaServidor = in.readLine();
		            JsonObject respostaObj = gson.fromJson(respostaServidor, JsonObject.class);
		            int codigoServidor = respostaObj.get("codigo").getAsInt();
		            System.out.println("servidor: " + respostaServidor);
		            
		            if (codigoServidor == 200) {
		                JOptionPane.showMessageDialog(null, "Cadastro Atualizado com Sucesso", "Cadastro Cliente",
		                        JOptionPane.INFORMATION_MESSAGE);

		                dispose();
		                telaLogin.setVisible(true);
		                telaLogin.setLocationRelativeTo(null);
		            } else {
		                JOptionPane.showMessageDialog(null, "Atualização Inválida", "Erro Cadastro Cliente",
		                        JOptionPane.ERROR_MESSAGE);
		            }
		            
		        } catch (IOException ex) {
		            ex.printStackTrace();
		        }
		    }
		});

		btnCadastrar.setBounds(186, 242, 102, 21);
		contentPane.add(btnCadastrar);

		JLabel lblNewLabel_3 = new JLabel("Atualizar Cadastro");
		lblNewLabel_3.setBounds(297, 27, 128, 13);
		contentPane.add(lblNewLabel_3);

		JButton btnVoltar = new JButton("Voltar");
		btnVoltar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HomePage homePage = new HomePage(eSocket,aleluia);

				homePage.setVisible(true);
				homePage.setLocationRelativeTo(null);
				dispose();

			}
		});
		btnVoltar.setBounds(386, 242, 102, 21);
		contentPane.add(btnVoltar);

	}

	public String getTextNome() {
		return textNome.getText();
	}

	public String getTextEmail() {
		return textEmail.getText();
	}

	public String getTextSenha() {
		char[] password = textSenha.getPassword();
		return new String(password);
	}

}
