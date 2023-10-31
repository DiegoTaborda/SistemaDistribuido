package view;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


import ClienteeServidor.Usuario;

import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.event.ActionEvent;

public class ViewLogin extends JFrame {

   
    private JTextField txtEmail;
    private JLabel lblNewLabel_1;
    private JPasswordField txtSenha;
    private JButton btnCadastro;
    private Socket echoSocket ;
    private PrintWriter out = null;
	private BufferedReader in = null;
	private Gson gson = new Gson();
   

    public ViewLogin(Socket eSocket) {
    	System.out.println("ViewLogin Conectada");
       
        setBounds(100, 100, 614, 408);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel lblNewLabel = new JLabel("Email");
        lblNewLabel.setBounds(112, 119, 45, 13);
        getContentPane().add(lblNewLabel);

        txtEmail = new JTextField();
        txtEmail.setBounds(160, 116, 315, 19);
        getContentPane().add(txtEmail);
        txtEmail.setColumns(10);

        lblNewLabel_1 = new JLabel("Senha");
        lblNewLabel_1.setBounds(112, 163, 45, 13);
        getContentPane().add(lblNewLabel_1);

        txtSenha = new JPasswordField();
        txtSenha.setBounds(160, 160, 315, 19);
        getContentPane().add(txtSenha);

        JLabel lblNewLabel_2 = new JLabel("SIISCCM");
        lblNewLabel_2.setBounds(269, 25, 45, 13);
        getContentPane().add(lblNewLabel_2);

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               
                Usuario usuario = new Usuario();

                String mensagemString = usuario.logar(getTxtEmail(), getTxtSenha());
//                String mensagemString = usuario.logar("eduardo@gmail.com", "12345678");

                try {
                    out = new PrintWriter(eSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(eSocket.getInputStream()));
                 
                    out.println(mensagemString);
                    out.flush();
                    System.out.println("Echo: " + mensagemString);
                    
                   
                    JsonObject respostaObj = gson.fromJson(in.readLine(), JsonObject.class);
                    int codigoServidor = respostaObj.get("codigo").getAsInt();
                    
                    
                    System.out.println("servidor: " + respostaObj);
                    
                    
                    if (codigoServidor == 200) {
                        JOptionPane.showMessageDialog(null, "Login Realizado com Sucesso", "Login Cliente",
                                JOptionPane.INFORMATION_MESSAGE);
                        
                        HomePage page = new HomePage(eSocket,respostaObj);
                       
                        page.setVisible(true);
                        page.setLocationRelativeTo(null);
                        dispose();
                    } else {
                        
                        JOptionPane.showMessageDialog(null, "Login Inválido", "Erro Login Cliente",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });


        btnEntrar.setBounds(147, 252, 99, 21);
        getContentPane().add(btnEntrar);

        btnCadastro = new JButton("Cadastrar");
        btnCadastro.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		
        		ViewCadastro cadastro = new ViewCadastro(eSocket);
        		cadastro.setVisible(true);
        		cadastro.setLocationRelativeTo(null);
        		dispose();
        		

        		
        	}
        });
        btnCadastro.setBounds(348, 252, 99, 21);
        getContentPane().add(btnCadastro);
        
        
    }
    
 



	public String getTxtEmail() {
		return txtEmail.getText();
	}

	public String getTxtSenha() {
		char[] password = txtSenha.getPassword();
		return new String(password);
	}

   
}
