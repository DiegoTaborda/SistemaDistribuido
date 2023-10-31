package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.google.gson.JsonParser;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import ClienteeServidor.Incidente;
import ClienteeServidor.Usuario;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.event.ActionEvent;

public class HomePage extends JFrame {

	private JPanel contentPane;
	private JButton btnAtualizarCadastro;
	private JButton btnReportarIncidentes;
	private JButton btnListarIncidentes;
	private JButton btnLogout;

	private PrintWriter out = null;
	private BufferedReader in = null;
	private JButton btnExcluirConta;
	private Gson gson = new Gson();

	public HomePage(Socket eSocket, JsonObject aleluia) {
		aleluia.remove("codigo");
		System.out.println("HomePage Conectado");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 357, 338);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		btnAtualizarCadastro = new JButton("Atualizar Cadastro");
		btnAtualizarCadastro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// out = new PrintWriter(eSocket.getOutputStream(), true);
				// in = new BufferedReader(new InputStreamReader(eSocket.getInputStream()));
				//// aleluia.addProperty("id_operacao", 2);

				// out.println(aleluia);
				// out.flush();

				ViewAtualizarCadastro novoAtualizarCadastro = new ViewAtualizarCadastro(eSocket, aleluia);
				novoAtualizarCadastro.setVisible(true);
				novoAtualizarCadastro.setLocationRelativeTo(null);
				dispose();

			}
		});
		btnAtualizarCadastro.setBounds(28, 24, 293, 21);
		contentPane.add(btnAtualizarCadastro);

		btnReportarIncidentes = new JButton("Reportar Incidentes");
		btnReportarIncidentes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				

				ViewReportarIncidente novoreReportarIncidente = new ViewReportarIncidente(eSocket, aleluia);
				novoreReportarIncidente.setVisible(true);
				novoreReportarIncidente.setLocationRelativeTo(null);
				dispose();
				
			}
		});
		btnReportarIncidentes.setBounds(28, 66, 293, 21);
		contentPane.add(btnReportarIncidentes);

		btnListarIncidentes = new JButton("Incidentes Geral");
		btnListarIncidentes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ListarIncidentesBusca listarIncidentesBusca = new ListarIncidentesBusca(eSocket, aleluia);
				listarIncidentesBusca.setVisible(true);
				listarIncidentesBusca.setLocationRelativeTo(null);
			}
		});
		btnListarIncidentes.setBounds(28, 120, 293, 21);
		contentPane.add(btnListarIncidentes);

		btnLogout = new JButton("Logout");
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Usuario usuario = new Usuario();

				try {
					out = new PrintWriter(eSocket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(eSocket.getInputStream()));
					aleluia.addProperty("id_operacao", 9);

					out.println(aleluia);
					out.flush();
					System.out.println("Echo: " + aleluia);

					Gson gson = new Gson();
					JsonObject respostaObj = gson.fromJson(in.readLine(), JsonObject.class);

					System.out.println("servidor: " + respostaObj);

					if (Integer.parseInt(respostaObj.get("codigo").getAsString()) == 200) {
						JOptionPane.showMessageDialog(null, "Logout Realizado com Sucesso", "Logout Cliente",
								JOptionPane.INFORMATION_MESSAGE);
						ViewLogin login = new ViewLogin(eSocket);
						login.setVisible(true);
						login.setLocationRelativeTo(null);
						dispose();

					} else {
						JOptionPane.showMessageDialog(null, "Logout Inválido", "Erro Logout Cliente",
								JOptionPane.ERROR_MESSAGE);
					}

				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnLogout.setBounds(28, 219, 293, 21);
		contentPane.add(btnLogout);
		
		JButton btnMeusIncidentes = new JButton("Meus Incidentes");
		btnMeusIncidentes.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        
		    	Incidente incidente = new Incidente();
		        String mensagemListaIncidentesUsuario = incidente.listaIncidentesUsuario(aleluia.get("token").getAsString(), aleluia.get("id_usuario").getAsInt());
		        
		        try {
		            out = new PrintWriter(eSocket.getOutputStream(), true);
		            in = new BufferedReader(new InputStreamReader(eSocket.getInputStream()));
		            
		            out.println(mensagemListaIncidentesUsuario);
		            out.flush();
		            
		            System.out.println("Echo: " + mensagemListaIncidentesUsuario);
		            JsonObject respostaObj = gson.fromJson(in.readLine(), JsonObject.class);

					System.out.println("servidor: " + respostaObj);

		            
		            JsonObject jsonObject = new Gson().fromJson(respostaObj, JsonObject.class);
		            
		            if (jsonObject.has("lista_incidentes")) {
		                JsonArray jsonArray = jsonObject.getAsJsonArray("lista_incidentes");

		                
		                MeusIncidentes novoMeusIncidentes = new MeusIncidentes(eSocket, aleluia, jsonArray);
		                novoMeusIncidentes.setVisible(true);
		                novoMeusIncidentes.setLocationRelativeTo(null);
		                dispose();
		            } else {
		                System.out.println("Campo 'lista_incidentes' não encontrado no JSON retornado.");
		            }
		        } catch (IOException e1) {
		            e1.printStackTrace();
		        }
		    }
		});

		btnMeusIncidentes.setBounds(28, 173, 293, 21);
		contentPane.add(btnMeusIncidentes);
		
		btnExcluirConta = new JButton("Excluir Conta");
		btnExcluirConta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				ExcluirConta excluirConta = new ExcluirConta(eSocket, aleluia);
				
				excluirConta.setVisible(true);
				excluirConta.setLocationRelativeTo(null);
				
				
			}
		});
		btnExcluirConta.setBounds(28, 270, 293, 21);
		contentPane.add(btnExcluirConta);
	}
}