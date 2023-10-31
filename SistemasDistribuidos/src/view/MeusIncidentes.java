package view;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ClienteeServidor.Incidente;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MeusIncidentes extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel tableModel;
	private int selectedIncidentId = -1;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private Gson gson = new Gson();

	public MeusIncidentes(Socket eSocket, JsonObject aleluia, JsonArray listaIncidentes) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnEditar = new JButton("Editar");
		btnEditar.setBounds(10, 227, 89, 23);
		contentPane.add(btnEditar);

		JButton btnRemover = new JButton("Remover");
		btnRemover.setBounds(109, 227, 89, 23);
		contentPane.add(btnRemover);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 414, 205);
		contentPane.add(scrollPane);

		tableModel = new DefaultTableModel();
		table = new JTable(tableModel);

		tableModel.addColumn("ID");
		tableModel.addColumn("Data");
		tableModel.addColumn("Rodovia");
		tableModel.addColumn("Km");
		tableModel.addColumn("Tipo Incidente");

		scrollPane.setViewportView(table);
		
		JButton btnVoltar = new JButton("Voltar");
		btnVoltar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				HomePage homePage = new HomePage(eSocket,aleluia);

				homePage.setVisible(true);
				homePage.setLocationRelativeTo(null);
				dispose();
			}
		});
		btnVoltar.setBounds(335, 228, 89, 23);
		contentPane.add(btnVoltar);

		for (int i = 0; i < listaIncidentes.size(); i++) {
			JsonObject incidenteJson = listaIncidentes.get(i).getAsJsonObject();
			int idIncidente = incidenteJson.get("id_incidente").getAsInt();
			String data = incidenteJson.get("data").getAsString();
			String rodovia = incidenteJson.get("rodovia").getAsString();
			int km = incidenteJson.get("km").getAsInt();
			int tipoIncidente = incidenteJson.get("tipo_incidente").getAsInt();

			tableModel.addRow(new Object[] { idIncidente, data, rodovia, km, tipoIncidente });
		}

		// Adicionar ouvinte de seleção à tabela
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					// Verificar se alguma linha foi selecionada
					if (table.getSelectedRow() != -1) {
						// Obter o ID do incidente selecionado
						selectedIncidentId = (int) table.getValueAt(table.getSelectedRow(), 0);
					} else {
						// Nenhuma linha selecionada, definir o ID como inválido
						selectedIncidentId = -1;
					}
				}
			}
		});

		// Adicionar ação para o botão Editar
		btnEditar.addActionListener(e -> {
			// Verificar se algum incidente foi selecionado
			if (selectedIncidentId != -1) {
				// Criar a página de edição e passar o ID do incidente como parâmetro
				AtualizarIncidente atualizarIncidente = new AtualizarIncidente(eSocket, aleluia, selectedIncidentId);
				atualizarIncidente.setVisible(true);
				atualizarIncidente.setLocationRelativeTo(null);
				dispose();
			}
		});

		// Adicionar ação para o botão Remover
		btnRemover.addActionListener(e -> {
		    // Código para remover o incidente selecionado na tabela
		    int selectedRow = table.getSelectedRow();
		    if (selectedRow >= 0) {
		        int confirmacao = JOptionPane.showConfirmDialog(null, "Deseja remover o incidente?", "Remover Incidente", JOptionPane.YES_NO_OPTION);
		        if (confirmacao == JOptionPane.YES_OPTION) {
		            Incidente incidente = new Incidente();
		            String mensagemRemoverIncidente = incidente.removerIncidente(aleluia.get("token").getAsString(),
		                    selectedIncidentId, aleluia.get("id_usuario").getAsInt());
		            try {
		                out = new PrintWriter(eSocket.getOutputStream(), true);
		                in = new BufferedReader(new InputStreamReader(eSocket.getInputStream()));
		                out.println(mensagemRemoverIncidente);
		                out.flush();
		                System.out.println("Echo: " + mensagemRemoverIncidente);

		                String respostaServidor = in.readLine();
		                JsonObject respostaObj = gson.fromJson(respostaServidor, JsonObject.class);
		                int codigoServidor = respostaObj.get("codigo").getAsInt();
		                System.out.println("servidor: " + respostaServidor);

		                if (codigoServidor == 200) {
		                    JOptionPane.showMessageDialog(null, "Incidente Removido com Sucesso", "Remoção de Incidente",
		                            JOptionPane.INFORMATION_MESSAGE);
		                    tableModel.removeRow(selectedRow);
		                } else {
		                    JOptionPane.showMessageDialog(null, "Remoção Inválida", "Erro na Remoção de Incidente",
		                            JOptionPane.ERROR_MESSAGE);
		                }

		            } catch (IOException e1) {
		                e1.printStackTrace();
		            }
		        }
		    }
		});

	}
}
