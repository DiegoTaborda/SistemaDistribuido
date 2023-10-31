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

public class ExcluirConta extends JFrame {

    private JFrame frame;
    private JPanel contentPane;
    private JTextField textEmail;
    private JPasswordField textSenha;
    private JButton btnCadastrar;
    private Socket echoSocket;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private Gson gson = new Gson();

    public ExcluirConta(Socket eSocket, JsonObject aleluia) {
        System.out.println("View Atualizar Cadastro Conectado");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 725, 396);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel_1 = new JLabel("Email");
        lblNewLabel_1.setBounds(147, 97, 45, 13);
        contentPane.add(lblNewLabel_1);

        textEmail = new JTextField();
        textEmail.setBounds(186, 94, 314, 19);
        contentPane.add(textEmail);
        textEmail.setColumns(10);

        JLabel lblNewLabel_2 = new JLabel("Senha");
        lblNewLabel_2.setBounds(147, 154, 45, 13);
        contentPane.add(lblNewLabel_2);

        textSenha = new JPasswordField();
        textSenha.setBounds(186, 151, 314, 19);
        contentPane.add(textSenha);

        btnCadastrar = new JButton("Remover");
        btnCadastrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ViewLogin telaLogin = new ViewLogin(eSocket);
                Usuario usuario = new Usuario();

                String menString = usuario.removerConta(getTextEmail(), getTextSenha(), aleluia.get("token").getAsString(),
                        aleluia.get("id_usuario").getAsInt());

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
                        int resposta = JOptionPane.showConfirmDialog(null, "Deseja realmente excluir sua conta?",
                                "Excluir Conta", JOptionPane.YES_NO_OPTION);
                        if (resposta == JOptionPane.YES_OPTION) {
                            JOptionPane.showMessageDialog(null, "Conta Removida com Sucesso", "Remoção Conta",
                                    JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                            telaLogin.setVisible(true);
                            telaLogin.setLocationRelativeTo(null);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Remoção Inválida", "Erro Remoção Conta",
                                JOptionPane.ERROR_MESSAGE);
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnCadastrar.setBounds(185, 214, 102, 21);
        contentPane.add(btnCadastrar);

        JLabel lblExcluirConta = new JLabel("Excluir Conta");
        lblExcluirConta.setBounds(297, 27, 128, 13);
        contentPane.add(lblExcluirConta);

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                HomePage homePage = new HomePage(eSocket, aleluia);

                homePage.setVisible(true);
                homePage.setLocationRelativeTo(null);
                dispose();

            }
        });
        btnVoltar.setBounds(380, 214, 102, 21);
        contentPane.add(btnVoltar);

    }

    public String getTextEmail() {
        return textEmail.getText();
    }

    public String getTextSenha() {
        char[] password = textSenha.getPassword();
        return new String(password);
    }

}
