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

public class ListarIncidentes extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel tableModel;
	private int selectedIncidentId = -1;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private Gson gson = new Gson();

	public ListarIncidentes(Socket eSocket, JsonObject aleluia, JsonArray listaIncidentes) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

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



		
		

	}
}
