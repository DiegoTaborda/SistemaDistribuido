package view;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ClienteeServidor.Incidente;

public class ListarIncidentesBusca extends JFrame {

	private JPanel contentPane;
	private JTextField textRodovia;
	private JTextField textKm;
	private JTextField textData;
	private JComboBox<String> comboBoxTipoIncidente;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private Gson gson = new Gson();

	public ListarIncidentesBusca(Socket eSocket, JsonObject aleluia) {
		System.out.println("View Atualizar Cadastro Conectado");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 651, 438);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblReporteIncidente = new JLabel("Listagem de Incidentes");
		lblReporteIncidente.setFont(new Font("Tahoma", Font.BOLD, 24));
		lblReporteIncidente.setBounds(177, 21, 414, 40);
		contentPane.add(lblReporteIncidente);

		JLabel lblRodovia = new JLabel("Rodovia:");
		lblRodovia.setBounds(165, 109, 68, 13);
		contentPane.add(lblRodovia);

		JLabel lblKm = new JLabel("Km:");
		lblKm.setBounds(177, 161, 45, 13);
		contentPane.add(lblKm);

		JLabel lblPeriodo = new JLabel("Periodo:");
		lblPeriodo.setBounds(165, 212, 90, 13);
		contentPane.add(lblPeriodo);

		JLabel lblData = new JLabel("Data:");
		lblData.setBounds(177, 261, 45, 13);
		contentPane.add(lblData);

		// Máscara para aceitar apenas letras maiúsculas e o formato 2letras-3números no
		// campo textRodovia
		try {
			MaskFormatter rodoviaFormatter = new MaskFormatter("UU-###");
			rodoviaFormatter.setPlaceholderCharacter('_');

			textRodovia = new JFormattedTextField(rodoviaFormatter);
			textRodovia.setBounds(224, 106, 229, 19);
			contentPane.add(textRodovia);
			textRodovia.setColumns(10);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		textKm = new JFormattedTextField();
		textKm.setBounds(224, 158, 229, 19);
		contentPane.add(textKm);
		textKm.setColumns(10);

		JButton btnReportar = new JButton("Reportar");
		btnReportar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				HomePage telaHomePage = new HomePage(eSocket,aleluia);

				Incidente incidente = new Incidente();
				String mensagemListaIncidentes = "";

				if (textKm.equals("")) {
					String mensagemReportarIncidente = incidente.solicitarIncidente(
							textRodovia.getText(), textData.getText(),comboBoxTipoIncidente.getSelectedIndex() + 1
							);
				} else {
					String mensagemReportarIncidente = incidente.solicitarIncidente(
							textRodovia.getText(), textData.getText(),textKm.getText(),comboBoxTipoIncidente.getSelectedIndex() + 1
							);
				}

				try {
					out = new PrintWriter(eSocket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(eSocket.getInputStream()));
					out.println(mensagemListaIncidentes);
					out.flush();
					System.out.println("Echo: " + mensagemListaIncidentes);

					String respostaServidor = in.readLine();
					JsonObject respostaObj = gson.fromJson(respostaServidor, JsonObject.class);
					int codigoServidor = respostaObj.get("codigo").getAsInt();
					System.out.println("servidor: " + respostaServidor);

					if (codigoServidor == 200) {
						JOptionPane.showMessageDialog(null, "Incidentes Visualizados com Sucesso", "Listagem Incidente",
								JOptionPane.INFORMATION_MESSAGE);

						dispose();
						telaHomePage.setVisible(true);
						telaHomePage.setLocationRelativeTo(null);
					} else {
						JOptionPane.showMessageDialog(null, "Incidentes Inválido", "Listagem Incorreta",
								JOptionPane.ERROR_MESSAGE);
					}

				} catch (IOException ex) {
					ex.printStackTrace();
				}

			}
		});
		btnReportar.setBounds(224, 350, 85, 21);
		contentPane.add(btnReportar);

		JButton btnVoltar = new JButton("Voltar");
		btnVoltar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HomePage homePage = new HomePage(eSocket, aleluia);

				homePage.setVisible(true);
				homePage.setLocationRelativeTo(null);
				dispose();
			}
		});
		btnVoltar.setBounds(368, 350, 85, 21);
		contentPane.add(btnVoltar);

		// Máscara para o formato de data yyyy-MM-dd HH:mm:ss no campo textData
		try {
			MaskFormatter dataFormatter = new MaskFormatter("####-##-## ##:##:##");
			dataFormatter.setPlaceholderCharacter('_');

			textData = new JFormattedTextField(dataFormatter);
			textData.setBounds(224, 258, 229, 19);
			contentPane.add(textData);
			textData.setColumns(10);

			comboBoxTipoIncidente = new JComboBox<String>();
			comboBoxTipoIncidente.addItem("MANHA - 1");
			comboBoxTipoIncidente.addItem("TARDE - 2");
			comboBoxTipoIncidente.addItem("NOITE - 3");
			comboBoxTipoIncidente.addItem("MADRUGADA - 4");

			comboBoxTipoIncidente.setBounds(224, 208, 229, 21);
			contentPane.add(comboBoxTipoIncidente);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
