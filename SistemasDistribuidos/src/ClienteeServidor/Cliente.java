package ClienteeServidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Cliente {
	private static String tokenString = "";
	private static int id_usuario = 0;
	private static int id_incidente = 0;

	public static void main(String[] args) throws IOException {

		// biel 10.20.8.179
		// mairon 10.50.3.13//10.20.8.77
		// biel salles 10.20.8.78
		// kenji 10.20.8.81
		// 10.20.8.131
		// danilo 10.40.23.102
		// Antony 10.40.12.47 - 10.20.8.133
		// matheus 10.40.11.3
		// igor 10.40.11.86 - 10.20.8.153
		// Sauter 10.20.8.198
		// gui quintero 10.20.8.93"
		// gui sanches 10.20.8.76
		// julia 10.20.8.58

		String serverHostname = new String("127.1.0.0");

		if (args.length > 0)
			serverHostname = args[0];
		System.out.println("Attemping to connect to host " + serverHostname + " on port 10008.");

		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		try {
			// 24001
			// 20008
			echoSocket = new Socket(serverHostname, 24001);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + serverHostname);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for " + "the connection to: " + serverHostname);
			System.exit(1);
		}

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Type Message (\"Bye.\" to quit)");

		boolean running = true;

		while (running) {

			System.out.println("Digite o Número de Operação");
			System.out.println("1 - Cadastro");
			System.out.println("2 - Atualizar Cadastro");
			System.out.println("3 - Login");
			System.out.println("4 - Reportar Incidentes");
			System.out.println("5 - Listar Incidentes");
			System.out.println("6 - Listar Incidentes do Usuário");
			System.out.println("7 - Remover Incidentes");
			System.out.println("8 - Excluir Conta");
			System.out.println("9 - Logout");
			System.out.println("10 - Editar um Incidente");

			int menu = Integer.parseInt(stdIn.readLine());
			Usuario mensagem = new Usuario();
			Incidente incidente = new Incidente();

			
			LocalDateTime agora = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String dataAtual = formatter.format(agora);

			switch (menu) {
			case 0:
				System.out.println("Encerrando");
				running = false;
				break;
				 
			case 1:
				System.out.println(" **************** - CADASTRAR - ****************");
				System.out.println("Nome: ");
				String nome = stdIn.readLine();
				System.out.println("Email: ");
				String email = stdIn.readLine();
				System.out.println("Senha: ");
				String senha = stdIn.readLine();
				String mensagemCadastro = mensagem.cadastrar(nome, email, senha);
				System.out.println("Echo: " + mensagemCadastro);
				out.println(mensagemCadastro);
				break;

			case 2:
				System.out.println(" **************** - Atualizar Cadastro - ****************");
				System.out.println("Novo Nome: ");
				String novoNome = stdIn.readLine();
				System.out.println("Novo Email: ");
				String novoEmail = stdIn.readLine();
				System.out.println("Nova Senha: ");
				String novaSenha = stdIn.readLine();
				String mensagemAtualizacao = mensagem.atualizarCadastro(novoNome, novoEmail, novaSenha, tokenString,
						id_usuario);
				System.out.println("Echo: " + mensagemAtualizacao);
				out.println(mensagemAtualizacao);
				break;

			case 3:
				System.out.println(" **************** - LOGIN - ****************");
				System.out.println("Email: ");
				String loginEmail = stdIn.readLine();
				System.out.println("Senha: ");
				String loginSenha = stdIn.readLine();
				String mensagemLogin = mensagem.logar(loginEmail, loginSenha);
				System.out.println("Echo: " + mensagemLogin);
				out.println(mensagemLogin);
				break;

			case 4:
				System.out.println(" **************** - Reportar Incidentes - ****************");

				System.out.println("Rodovia: ");
				String rodovia = stdIn.readLine();
				System.out.println("KM: ");
				int km = Integer.parseInt(stdIn.readLine());

				System.out.println("Tipos de Incidente disponíveis:");
				for (TipoIncidente tipo : TipoIncidente.values()) {
					System.out.println(tipo.getCodigo() + "\t" + tipo.name());
				}
				System.out.println("Tipo Incidente: ");
				int tipoIncidente = Integer.parseInt(stdIn.readLine());

				String mensagemReportarIncidente = incidente.reportarIncidente(dataAtual, rodovia, km, tipoIncidente,
						tokenString, id_usuario);
				
				System.out.println("Echo: " + mensagemReportarIncidente);
				out.println(mensagemReportarIncidente);
				break;

			case 5:
				System.out.println(" **************** - Solicitar Lista de Incidentes - ****************");
				System.out.println("Rodovia: ");
				String rodoviaSolicitada = stdIn.readLine();
				System.out.println("KM: ");
				String kmSolicitado = stdIn.readLine();
				System.out.println("Períodos: ");
				for (Periodo periodo : Periodo.values()) {
					System.out.println(periodo.getDescricao() + ": " + periodo.getInicio() + " ~ " + periodo.getFim());
				}
				int periodoSolicitado = Integer.parseInt(stdIn.readLine());
				String mensagemListaIncidentes;
				if (kmSolicitado.equals("")) {
					mensagemListaIncidentes = incidente.solicitarIncidente(rodoviaSolicitada, dataAtual,
							periodoSolicitado);
				} else {
					mensagemListaIncidentes = incidente.solicitarIncidente(rodoviaSolicitada, dataAtual, kmSolicitado,
							periodoSolicitado);
				}
				System.out.println("Echo: " + mensagemListaIncidentes);
				out.println(mensagemListaIncidentes);
				break;

			case 6:
				System.out.println(" **************** - Lista de Incidentes Reportados pelo Usuário - ****************");
				String mensagemListaIncidentesUsuario = incidente.listaIncidentesUsuario(tokenString, id_usuario);
				System.out.println("Echo: " + mensagemListaIncidentesUsuario);
				out.println(mensagemListaIncidentesUsuario);
				break;
			
			case 7:
				System.out.println(" **************** - Remover Incidentes Reportados pelo Usuário - ****************");
				System.out.println("id_incidente: ");
				int id_incidente1 = Integer.parseInt(stdIn.readLine());	
				String mensagemRemoverIncidente = incidente.removerIncidente(tokenString, id_incidente1, id_usuario);
				System.out.println("Echo: " + mensagemRemoverIncidente);
				out.println(mensagemRemoverIncidente);
				break;

			case 8:
				System.out.println(" **************** - Remover Conta - ****************");
				System.out.println("Email: ");
				String emailString = stdIn.readLine();
				System.out.println("Senha: ");
				String senhaString = stdIn.readLine();
				String mensagemRemoverConta = mensagem.removerConta(emailString, senhaString, tokenString, id_usuario);
				System.out.println("Echo: " + mensagemRemoverConta);
				out.println(mensagemRemoverConta);
			break;

			case 9:
				System.out.println(" **************** - Logout - ****************");
				String mensagemLogout = mensagem.fazerLogout(id_usuario, tokenString);
				System.out.println("Echo: " + mensagemLogout);
				out.println(mensagemLogout);	
				break;
				
			case 10:
			    System.out.println(" **************** - Editar Incidente - ****************");
			    System.out.println("Informe o ID do incidente a ser editado: ");
			    int idIncidenteEditar = Integer.parseInt(stdIn.readLine());

			    // Listar todos os incidentes do usuário antes de permitir a edição
			    String mensagemListarIncidentesUsuario = incidente.listaIncidentesUsuario(tokenString, id_usuario);
			    System.out.println("Echo: " + mensagemListarIncidentesUsuario);
			    out.println(mensagemListarIncidentesUsuario);

			    // Obter os novos valores para o incidente a ser editado
			    System.out.println("Informe a nova data do incidente: ");
			    String novaData = stdIn.readLine();
			    System.out.println("Informe a nova rodovia do incidente: ");
			    String novaRodovia = stdIn.readLine();
			    System.out.println("Informe o novo valor de km do incidente: ");
			    int novoKm = Integer.parseInt(stdIn.readLine());
			    System.out.println("Informe o novo tipo de incidente: ");
			    int novoTipoIncidente = Integer.parseInt(stdIn.readLine());

			    // Enviar a solicitação de edição do incidente
			    String mensagemEditarIncidente = incidente.editarIncidente(tokenString, idIncidenteEditar, novaData, novaRodovia, novoKm, novoTipoIncidente,id_usuario);
			    System.out.println("Echo: " + mensagemEditarIncidente);
			    out.println(mensagemEditarIncidente);
			    break;
				

			default:
				System.out.println("Opção inválida");
				break;
			}
			
	
			String userInput;
			if ((userInput = in.readLine()) != null) {
				System.out.println(userInput);
				JsonObject jsonObject = JsonParser.parseString(userInput).getAsJsonObject();
				if (jsonObject.has("token")) {
					tokenString = jsonObject.get("token").getAsString();
				}
				if (jsonObject.has("id_usuario")) {
					id_usuario = jsonObject.get("id_usuario").getAsInt();
				}
				if(jsonObject.has("id_incidente")) {
					id_incidente = jsonObject.get("id_incidente").getAsInt();
				}
			}
		}

		out.close();
		in.close();
		stdIn.close();
		echoSocket.close();
	}
}
