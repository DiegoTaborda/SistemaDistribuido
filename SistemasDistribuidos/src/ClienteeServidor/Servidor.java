package ClienteeServidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysql.cj.QueryReturnType;

import dao.BancoDados;

public class Servidor extends Thread {
	protected Socket clientSocket;
	public int codigo = 0;

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;

		try {
//
			InetAddress enderecoIP = InetAddress.getByName("0.0.0.0");
			serverSocket = new ServerSocket(24001, 0, enderecoIP);
//			serverSocket = new ServerSocket(24001);

			System.out.println("Connection Socket Created");
			try {
				while (true) {
					System.out.println("Waiting for Connection");
					new Servidor(serverSocket.accept());
				}
			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port: 10008.");
			System.exit(1);
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				System.err.println("Could not close port: 10008.");
				System.exit(1);
			}
		}
	}

	private Servidor(Socket clientSoc) {
		clientSocket = clientSoc;
		start();
	}

	public Servidor() {
		// TODO Auto-generated constructor stub
	}

	public void run() {
		System.out.println("New Communication Thread Started");

		try {

			Connection ligacao = null;
			PreparedStatement statement = null;
			ResultSet resultSet = null;

			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String inputLine;

			Gson gson = new Gson();

			int contador = 1;
			int contadorIncidente = 1;

			ArrayList<Usuario> usuarios = new ArrayList<Usuario>();
			ArrayList<Incidente> incidentes = new ArrayList<Incidente>();
			JsonArray listaJson = new JsonArray();

			while (true) {

				inputLine = in.readLine();
				JsonObject jsonObject = JsonParser.parseString(inputLine).getAsJsonObject();
				int id_operacao = jsonObject.get("id_operacao").getAsInt();

				Usuario usuario = null;

				System.out.println("************ Mensagem Recebida!!! ************ ");
				System.out.println("Cliente: " + inputLine);
				Usuario mensagemRecebida = new Gson().fromJson(inputLine, Usuario.class);
				String gSonString = "";

				TokenGenerator tokenGenerator = new TokenGenerator();

				if (inputLine == "0")
					break;
				switch (id_operacao) {

				case 1:
				    ligacao = BancoDados.conectar();
				    JsonObject jCadastroObject = new JsonObject();
				    
				    String nomeCadastro = jsonObject.get("nome").getAsString();
				    String emailCadastro = jsonObject.get("email").getAsString();
				    String senhaCadastro = jsonObject.get("senha").getAsString();
				    
				    if (nomeCadastro != null && emailCadastro != null && senhaCadastro != null) {
				        usuario = new Usuario(nomeCadastro, emailCadastro, senhaCadastro);
				        
				        try {
				            if (usuario.verificarCadastroExiste(emailCadastro, ligacao)) {
				                codigo = 500;
				                jCadastroObject.addProperty("codigo", codigo);
				                jCadastroObject.addProperty("mensagem", "Usuário existente");
				            } else {
				                if (usuario.verificarCadastro(usuario)) {
				                    statement = ligacao.prepareStatement("INSERT INTO usuario (nome, email, senha, token) VALUES (?, ?, ?, ?)");
				                    statement.setString(1, nomeCadastro);
				                    statement.setString(2, emailCadastro);
				                    statement.setString(3, senhaCadastro);
				                    statement.setString(4, "");
				                    statement.executeUpdate();

				                    codigo = 200;
				                    jCadastroObject.addProperty("codigo", codigo);
				                } else {
				                    codigo = 500;
				                    jCadastroObject.addProperty("codigo", codigo);
				                    jCadastroObject.addProperty("mensagem", "Erro ao cadastrar usuário.");
				                }
				            }
				        } catch (SQLException e) {
				            codigo = 500;
				            jCadastroObject.addProperty("codigo", codigo);
				            jCadastroObject.addProperty("mensagem", "Erro ao executar a declaração SQL.");
				        } finally {
				            BancoDados.finalizarStatement(statement);
				        }
				    } else {
				        codigo = 500;
				        jCadastroObject.addProperty("codigo", codigo);
				        jCadastroObject.addProperty("mensagem", "Dados incompletos.");
				    }
				    
				    gSonString = gson.toJson(jCadastroObject);
				    out.println(gSonString);
				    System.out.println("Echo: " + gSonString);
				    
				    try {
				        BancoDados.desconectar();
				    } catch (SQLException e) {
				        System.out.println("Erro ao desconectar do banco de dados.");
				    }
				    
				    break;



				case 2:
				    ligacao = BancoDados.conectar();
				    JsonObject jAtualizarCadastroObject = new JsonObject();
				    
				    int idUsuarioAtualizar = jsonObject.get("id_usuario").getAsInt();
				    String tokenAtualizar = jsonObject.get("token").getAsString();
				    String novoTokenString = tokenGenerator.generateToken();
				    
				    String nomeAtualizar = jsonObject.get("nome").getAsString();
				    String emailAtualizar = jsonObject.get("email").getAsString();
				    String senhaAtualizar = jsonObject.get("senha").getAsString();
				    
				    if (nomeAtualizar != null && emailAtualizar != null && senhaAtualizar != null) {
				        Usuario usuarioAtualizar = new Usuario(nomeAtualizar, emailAtualizar, senhaAtualizar);
				        
				        try {
				            if (usuarioAtualizar.verificarCadastro(usuarioAtualizar)) {
				                statement = ligacao.prepareStatement("UPDATE usuario SET nome = ?, email = ?, senha = ?, token = ? WHERE id_usuario = ?");
				                statement.setString(1, nomeAtualizar);
				                statement.setString(2, emailAtualizar);
				                statement.setString(3, senhaAtualizar);
				                statement.setString(4, novoTokenString);
				                statement.setInt(5, idUsuarioAtualizar);
				                statement.executeUpdate();

				                codigo = 200;
				                jAtualizarCadastroObject.addProperty("codigo", codigo);
				                jAtualizarCadastroObject.addProperty("token", novoTokenString);
				            } else {
				                codigo = 500;
				                jAtualizarCadastroObject.addProperty("codigo", codigo);
				                jAtualizarCadastroObject.addProperty("mensagem", "Usuário não encontrado ou não logado");
				            }
				        } catch (SQLException e) {
				            codigo = 500;
				            jAtualizarCadastroObject.addProperty("codigo", codigo);
				            jAtualizarCadastroObject.addProperty("mensagem", "Erro ao executar a declaração SQL.");
				        } finally {
				            BancoDados.finalizarStatement(statement);
				        }
				    } else {
				        codigo = 500;
				        jAtualizarCadastroObject.addProperty("codigo", codigo);
				        jAtualizarCadastroObject.addProperty("mensagem", "Dados incompletos.");
				    }
				    
				    gSonString = gson.toJson(jAtualizarCadastroObject);
				    out.println(gSonString);
				    System.out.println("Echo: " + gSonString);
				    
				    try {
				        BancoDados.desconectar();
				    } catch (SQLException e) {
				        System.out.println("Erro ao desconectar do banco de dados.");
				    }
				    
				    break;


				case 3:
				    ligacao = BancoDados.conectar();
				    JsonObject jloginObject = new JsonObject();
				    String gsoString2 = "";
				    
				    String emailLogin = jsonObject.get("email").getAsString();
				    String senhaLogin = jsonObject.get("senha").getAsString();
				    
				    if (emailLogin != null && senhaLogin != null) {
				        try {
				            statement = ligacao.prepareStatement("SELECT * FROM usuario WHERE email = ? AND senha = ?");
				            statement.setString(1, emailLogin);
				            statement.setString(2, senhaLogin);
				            resultSet = statement.executeQuery();

				            if (resultSet.next()) {
				                statement = ligacao.prepareStatement("UPDATE usuario SET token = ? WHERE id_usuario = ?");
				                String token = tokenGenerator.generateToken();
				                int id_usuario = resultSet.getInt("id_usuario");
				                statement.setString(1, token);
				                statement.setInt(2, id_usuario);
				                statement.executeUpdate();

				                codigo = 200;
				                jloginObject.addProperty("codigo", codigo);
				                jloginObject.addProperty("token", token);
				                jloginObject.addProperty("id_usuario", id_usuario);
				            } else {
				                codigo = 500;
				                jloginObject.addProperty("codigo", codigo);
				                jloginObject.addProperty("mensagem", "Usuário Inexistente");
				            }
				        } catch (SQLException e) {
				            codigo = 500;
				            jloginObject.addProperty("codigo", codigo);
				            jloginObject.addProperty("mensagem", "Erro ao executar a declaração SQL.");
				        } finally {
				            BancoDados.finalizarStatement(statement);
				            BancoDados.finalizarResultSet(resultSet);
				        }
				    } else {
				        codigo = 500;
				        jloginObject.addProperty("codigo", codigo);
				        jloginObject.addProperty("mensagem", "Dados incompletos.");
				    }
				    
				    gsoString2 = gson.toJson(jloginObject);
				    out.println(gsoString2);
				    System.out.println("Echo: " + gsoString2);
				    
				    try {
				        BancoDados.desconectar();
				    } catch (SQLException e) {
				        System.out.println("Erro ao desconectar do banco de dados.");
				    }
				    
				    break;


				case 4:
				    ligacao = BancoDados.conectar();
				    JsonObject jReporteObject = new JsonObject();
				    String gsoString3 = "";

				    String data = jsonObject.get("data").getAsString();
				    String rodovia = jsonObject.get("rodovia").getAsString();
				    int km = jsonObject.get("km").getAsInt();
				    int tipoIncidente = jsonObject.get("tipo_incidente").getAsInt();
				    String token = jsonObject.get("token").getAsString();
				    int idUsuario = jsonObject.get("id_usuario").getAsInt();

				    if (data != null && rodovia != null && token != null) {
				        try {
				            Incidente incidente = new Incidente(data, rodovia, km, tipoIncidente, token, idUsuario);

				            if (incidente.verificarReporteIncidente(incidente)) {
				                String query = "INSERT INTO incidente (data, rodovia, km, tipo_incidente, id_usuario) VALUES (?, ?, ?, ?, ?)";
				                statement = ligacao.prepareStatement(query);
				                statement.setString(1, incidente.getData());
				                statement.setString(2, incidente.getRodovia());
				                statement.setInt(3, incidente.getKm());
				                statement.setInt(4, incidente.getTipo_incidente());
				                statement.setInt(5, incidente.getId_usuario());
				                statement.executeUpdate();

				                codigo = 200;
				                jReporteObject.addProperty("codigo", codigo);
				                gsoString3 = gson.toJson(jReporteObject);
				            } else {
				                codigo = 500;
				                jReporteObject.addProperty("codigo", codigo);
				                jReporteObject.addProperty("mensagem", "Erro ao reportar o incidente. Campos inválidos.");
				                gsoString3 = gson.toJson(jReporteObject);
				            }
				        } catch (SQLException e) {
				            codigo = 500;
				            jReporteObject.addProperty("codigo", codigo);
				            jReporteObject.addProperty("mensagem", "Erro ao executar a declaração SQL.");
				            gsoString3 = gson.toJson(jReporteObject);
				        } finally {
				            BancoDados.finalizarStatement(statement);
				            BancoDados.finalizarResultSet(resultSet);
				        }
				    } else {
				        codigo = 500;
				        jReporteObject.addProperty("codigo", codigo);
				        jReporteObject.addProperty("mensagem", "Dados incompletos.");
				        gsoString3 = gson.toJson(jReporteObject);
				    }

				    out.println(gsoString3);
				    System.out.println("Echo: " + gsoString3);

				    try {
				        BancoDados.desconectar();
				    } catch (SQLException e) {
				        System.out.println("Erro ao desconectar do banco de dados.");
				    }

				    break;

			
				case 5:
				    JsonObject jsonlistaincidentesUsuariObject = new JsonObject();
				    String gsoString4 = "";

				    try {
				        LocalDateTime dataResult = LocalDateTime.parse(jsonObject.get("data").getAsString(),
				            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				        String data1 = dataResult.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				        String horario = dataResult.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
				        String faixaKm = jsonObject.has("faixa_km") ? jsonObject.get("faixa_km").getAsString() : "";
				        String rodovia1 = jsonObject.get("rodovia").getAsString();
				        int periodo = jsonObject.get("periodo").getAsInt();
				        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

				        boolean algumParametroVazio = data1.isEmpty() || rodovia1.isEmpty() || (periodo < 0 || periodo > 4);
				        boolean incidenteEncontrado = false;

				        if (algumParametroVazio) {
				            jsonlistaincidentesUsuariObject.addProperty("codigo", 500);
				            jsonlistaincidentesUsuariObject.addProperty("mensagem", "Erro ao acessar dados ou dados inválidos/inexistentes");
				        } else {
				            ligacao = BancoDados.conectar();
				            statement = ligacao.prepareStatement("SELECT * FROM incidente WHERE rodovia = ?");

				            statement.setString(1, rodovia1);
				            resultSet = statement.executeQuery();

				            listaJson = new JsonArray();

				            while (resultSet.next()) {
				                String dataIncidenteString = resultSet.getString("data");
				                LocalDateTime dataIncidente = LocalDateTime.parse(dataIncidenteString, formatter);
				                LocalTime horarioIncidente = dataIncidente.toLocalTime();
				                boolean dentroDoPeriodo = Periodo.verificarPeriodo(periodo, horarioIncidente);

				                boolean faixaKmVazia = faixaKm.isEmpty();
				                boolean kmDentroDaFaixa = !faixaKmVazia && (resultSet.getInt("km") >= Integer.parseInt(faixaKm.split("-")[0])
				                    && resultSet.getInt("km") <= Integer.parseInt(faixaKm.split("-")[1]));

				                if (faixaKmVazia || kmDentroDaFaixa) {
				                    if (dentroDoPeriodo) {
				                        incidenteEncontrado = true;

				                        JsonObject incidenteJson = new JsonObject();
				                        incidenteJson.addProperty("id_incidente", resultSet.getInt("id_incidente"));
				                        incidenteJson.addProperty("data", resultSet.getString("data"));
				                        incidenteJson.addProperty("rodovia", resultSet.getString("rodovia"));
				                        incidenteJson.addProperty("km", resultSet.getInt("km"));
				                        incidenteJson.addProperty("tipo_incidente", resultSet.getInt("tipo_incidente"));

				                        listaJson.add(incidenteJson);
				                    }
				                }
				            }

				            if (incidenteEncontrado) {
				                jsonlistaincidentesUsuariObject.addProperty("codigo", 200);
				                jsonlistaincidentesUsuariObject.add("lista_incidentes", listaJson);
				            } else {
				                jsonlistaincidentesUsuariObject.addProperty("codigo", 200);
				                jsonlistaincidentesUsuariObject.add("lista_incidentes", new JsonArray());
				            }

				            BancoDados.finalizarStatement(statement);
				            BancoDados.finalizarResultSet(resultSet);
				        }

				        BancoDados.desconectar();

				    } catch (SQLException e) {
				        jsonlistaincidentesUsuariObject.addProperty("codigo", 500);
				        jsonlistaincidentesUsuariObject.addProperty("mensagem", "Erro ao executar a declaração SQL.");
				    } catch (DateTimeParseException e) {
				        jsonlistaincidentesUsuariObject.addProperty("codigo", 500);
				        jsonlistaincidentesUsuariObject.addProperty("mensagem", "Erro ao analisar a data.");
				    } finally {
				        gsoString4 = gson.toJson(jsonlistaincidentesUsuariObject);
				        out.println(gsoString4);
				        System.out.println("Echo: " + gsoString4);

				        try {
				            BancoDados.desconectar();
				        } catch (SQLException e) {
				            System.out.println("Erro ao desconectar do banco de dados.");
				        }
				    }

				    break;



				case 6:
				    JsonObject jsonlistaincidentesUsuariObject1 = new JsonObject();
				    JsonArray listaJson1 = new JsonArray();
				    String gsoString5 = "";

				    try {
				        int idUsuario1 = jsonObject.get("id_usuario").getAsInt();
				        String token1 = jsonObject.get("token").getAsString();

				        ligacao = BancoDados.conectar();
				        statement = ligacao.prepareStatement("SELECT * FROM incidente WHERE id_usuario = ?");
				        statement.setInt(1, idUsuario1);
				        resultSet = statement.executeQuery();

				        while (resultSet.next()) {
				            JsonObject incidenteJson = new JsonObject();
				            incidenteJson.addProperty("id_incidente", resultSet.getInt("id_incidente"));
				            incidenteJson.addProperty("data", resultSet.getString("data"));
				            incidenteJson.addProperty("rodovia", resultSet.getString("rodovia"));
				            incidenteJson.addProperty("km", resultSet.getInt("km"));
				            incidenteJson.addProperty("tipo_incidente", resultSet.getInt("tipo_incidente"));
				            listaJson1.add(incidenteJson);
				        }

				        if (listaJson1.size() > 0) {
				            codigo = 200;
				            jsonlistaincidentesUsuariObject1.addProperty("codigo", codigo);
				            jsonlistaincidentesUsuariObject1.add("lista_incidentes", listaJson1);
				        } else {
				            codigo = 500;
				            jsonlistaincidentesUsuariObject1.addProperty("codigo", codigo);
				            jsonlistaincidentesUsuariObject1.addProperty("mensagem", "Incidente Não Encontrado ou Usuário ainda não reportou nenhum incidente");
				        }

				        BancoDados.finalizarStatement(statement);
				        BancoDados.finalizarResultSet(resultSet);
				        BancoDados.desconectar();

				    } catch (SQLException e) {
				        jsonlistaincidentesUsuariObject1.addProperty("codigo", 500);
				        jsonlistaincidentesUsuariObject1.addProperty("mensagem", "Erro ao executar a declaração SQL.");
				    } catch (NumberFormatException e) {
				        jsonlistaincidentesUsuariObject1.addProperty("codigo", 500);
				        jsonlistaincidentesUsuariObject1.addProperty("mensagem", "Erro ao converter o ID do usuário.");
				    } finally {
				        gsoString5 = gson.toJson(jsonlistaincidentesUsuariObject1);
				        out.println(gsoString5);
				        System.out.println("Echo: " + gsoString5);

				        try {
				            BancoDados.desconectar();
				        } catch (SQLException e) {
				            System.out.println("Erro ao desconectar do banco de dados.");
				        }
				    }

				    break;

				
			case 7:
			    JsonObject jsonRemocaoIncidenteObject = new JsonObject();

			    int idUsuario11 = jsonObject.get("id_usuario").getAsInt();
			    int idIncidente = jsonObject.get("id_incidente").getAsInt();
			    String token11 = jsonObject.get("token").getAsString();

			    // Verificar a existência do incidente com base nos dois identificadores
			    ligacao = BancoDados.conectar();
			    statement = ligacao.prepareStatement("SELECT * FROM incidente WHERE id_usuario = ? AND id_incidente = ?");
			    statement.setInt(1, idUsuario11);
			    statement.setInt(2, idIncidente);
			    resultSet = statement.executeQuery();

			    if (resultSet.next()) {
			        // O incidente existe, realizar a remoção
			        statement = ligacao.prepareStatement("DELETE FROM incidente WHERE id_usuario = ? AND id_incidente = ?");
			        statement.setInt(1, idUsuario11);
			        statement.setInt(2, idIncidente);
			        int linhasAfetadas = statement.executeUpdate();

			        if (linhasAfetadas > 0) {
			            // Remoção bem-sucedida
			            codigo = 200;
			            jsonRemocaoIncidenteObject.addProperty("codigo", codigo);
			        } else {
			            // Nenhuma linha afetada, remoção não foi bem-sucedida
			            codigo = 500;
			            jsonRemocaoIncidenteObject.addProperty("codigo", codigo);
			            jsonRemocaoIncidenteObject.addProperty("mensagem", "Falha ao remover o incidente");
			        }
			    } else {
			        // O incidente não existe
			        codigo = 500;
			        jsonRemocaoIncidenteObject.addProperty("codigo", codigo);
			        jsonRemocaoIncidenteObject.addProperty("mensagem", "Incidente não encontrado");
			    }

			    String gSonString3 = gson.toJson(jsonRemocaoIncidenteObject);
			    out.println(gSonString3);
			    System.out.println("Echo: " + gSonString3);
			    BancoDados.finalizarStatement(statement);
			    BancoDados.finalizarResultSet(resultSet);
			    BancoDados.desconectar();
			    break;


			case 8:
			    JsonObject jExcluirUsuarioObject = new JsonObject();
			    String gSonString31 = "";

			    try {
			        int idUsuarioExcluir = jsonObject.get("id_usuario").getAsInt();
			        String emailExcluir = jsonObject.get("email").getAsString();
			        String senhaExcluir = jsonObject.get("senha").getAsString();
			        String tokenExcluir = jsonObject.get("token").getAsString();

			        boolean usuarioLogadoExcluir = false;

			        ligacao = BancoDados.conectar();

			        // Verifica se o usuário está logado
			        statement = ligacao.prepareStatement("SELECT * FROM usuario WHERE id_usuario = ? AND token = ? AND email = ? AND senha = ?");
			        statement.setInt(1, idUsuarioExcluir);
			        statement.setString(2, tokenExcluir);
			        statement.setString(3, emailExcluir);
			        statement.setString(4, senhaExcluir);
			        resultSet = statement.executeQuery();

			        if (resultSet.next()) {
			            usuarioLogadoExcluir = true;

			            // Exclui o usuário do banco de dados
			            statement = ligacao.prepareStatement("DELETE FROM usuario WHERE id_usuario = ?");
			            statement.setInt(1, idUsuarioExcluir);
			            statement.executeUpdate();

			            codigo = 200;
			            jExcluirUsuarioObject.addProperty("codigo", codigo);
			            jExcluirUsuarioObject.addProperty("mensagem", "Usuário excluído com sucesso.");
			        } else {
			            codigo = 500;
			            jExcluirUsuarioObject.addProperty("codigo", codigo);
			            jExcluirUsuarioObject.addProperty("mensagem", "Usuário não encontrado ou não logado.");
			        }

			        BancoDados.finalizarResultSet(resultSet);
			        BancoDados.finalizarStatement(statement);
			        BancoDados.desconectar();

			    } catch (SQLException e) {
			        jExcluirUsuarioObject.addProperty("codigo", 500);
			        jExcluirUsuarioObject.addProperty("mensagem", "Erro ao executar a declaração SQL.");
			    } catch (NumberFormatException e) {
			        jExcluirUsuarioObject.addProperty("codigo", 500);
			        jExcluirUsuarioObject.addProperty("mensagem", "Erro ao converter o ID do usuário.");
			    } finally {
			        gSonString31 = gson.toJson(jExcluirUsuarioObject);
			        out.println(gSonString31);
			        System.out.println("Echo: " + gSonString31);

			        try {
			            BancoDados.desconectar();
			        } catch (SQLException e) {
			            System.out.println("Erro ao desconectar do banco de dados.");
			        }
			    }

			    break;



			case 9:
			    JsonObject jlogoutObject = new JsonObject();
			    String gSonString4 = "";

			    try {
			        ligacao = BancoDados.conectar();

			        String tokenLogout = jsonObject.get("token").getAsString();

			        statement = ligacao.prepareStatement("SELECT * FROM usuario WHERE id_usuario = ? AND token = ?");
			        statement.setInt(1, jsonObject.get("id_usuario").getAsInt());
			        statement.setString(2, jsonObject.get("token").getAsString());
			        resultSet = statement.executeQuery();

			        if (resultSet.next()) {

			            if (resultSet.getString("token").equals(jsonObject.get("token").getAsString())) {

			                statement = ligacao.prepareStatement("UPDATE usuario SET token = ? WHERE id_usuario = ?");
			                statement.setString(1, "");
			                statement.setInt(2, jsonObject.get("id_usuario").getAsInt());
			                statement.executeUpdate();

			                codigo = 200;
			                jlogoutObject.addProperty("codigo", codigo);
			                gSonString4 = gson.toJson(jlogoutObject);
			                out.println(gSonString4);
			            }

			        } else {
			            codigo = 500;
			            jlogoutObject.addProperty("codigo", codigo);
			            jlogoutObject.addProperty("mensagem", "Usuário deslogado");
			            gSonString4 = gson.toJson(jlogoutObject);
			            out.println(gSonString4);
			        }

			        System.out.println("Echo: " + gSonString4);
			        BancoDados.finalizarStatement(statement);
			        BancoDados.finalizarResultSet(resultSet);
			        BancoDados.desconectar();

			    } catch (SQLException e) {
			        jlogoutObject.addProperty("codigo", 500);
			        jlogoutObject.addProperty("mensagem", "Erro ao executar a declaração SQL.");
			        gSonString4 = gson.toJson(jlogoutObject);
			        out.println(gSonString4);
			        System.out.println("Echo: " + gSonString4);
			    } finally {
			        try {
			            BancoDados.desconectar();
			        } catch (SQLException e) {
			            System.out.println("Erro ao desconectar do banco de dados.");
			        }
			    }

			    break;

					
				case 10:
				    JsonObject jsonEdicaoIncidenteObject = new JsonObject();

				    int idUsuario111 = jsonObject.get("id_usuario").getAsInt();
				    int idIncidente1 = jsonObject.get("id_incidente").getAsInt();
				    String token111 = jsonObject.get("token").getAsString();

				    // Verificar a existência do usuário com base no id_usuario e token
				    if (verificarUsuario(idUsuario111, token111)) {
				        // Listar todos os incidentes reportados pelo usuário
				        ligacao = BancoDados.conectar();
				        statement = ligacao.prepareStatement("SELECT * FROM incidente WHERE id_usuario = ?");
				        statement.setInt(1, idUsuario111);
				        resultSet = statement.executeQuery();

				        boolean incidenteEncontrado1 = false;
				        while (resultSet.next()) {
				            if (resultSet.getInt("id_incidente") == idIncidente1) {
				                // O incidente foi encontrado, realizar a edição
				                incidenteEncontrado1 = true;

				                // Obter os dados para edição
				                String novaData = jsonObject.get("data").getAsString();
				                String novaRodovia = jsonObject.get("rodovia").getAsString();
				                int novoKm = jsonObject.get("km").getAsInt();
				                int novoTipoIncidente = jsonObject.get("tipo_incidente").getAsInt();

				                // Atualizar os dados do incidente
				                statement = ligacao.prepareStatement("UPDATE incidente SET data = ?, rodovia = ?, km = ?, tipo_incidente = ? WHERE id_incidente = ?");
				                statement.setString(1, novaData);
				                statement.setString(2, novaRodovia);
				                statement.setInt(3, novoKm);
				                statement.setInt(4, novoTipoIncidente);
				                statement.setInt(5, idIncidente1);
				                int linhasAfetadas = statement.executeUpdate();

				                if (linhasAfetadas > 0) {
				                    // Edição bem-sucedida
				                    codigo = 200;
				                    jsonEdicaoIncidenteObject.addProperty("codigo", codigo);
				                    jsonEdicaoIncidenteObject.addProperty("mensagem", "Incidente editado com sucesso");
				                } else {
				                    // Nenhuma linha afetada, edição não foi bem-sucedida
				                    codigo = 500;
				                    jsonEdicaoIncidenteObject.addProperty("codigo", codigo);
				                    jsonEdicaoIncidenteObject.addProperty("mensagem", "Falha ao editar o incidente");
				                }

				                break;
				            }
				        }

				        if (!incidenteEncontrado1) {
				            // O incidente não foi encontrado
				            codigo = 500;
				            jsonEdicaoIncidenteObject.addProperty("codigo", codigo);
				            jsonEdicaoIncidenteObject.addProperty("mensagem", "Incidente não encontrado");
				        }
				    } else {
				        // Usuário inválido
				        codigo = 500;
				        jsonEdicaoIncidenteObject.addProperty("codigo", codigo);
				        jsonEdicaoIncidenteObject.addProperty("mensagem", "Acesso não autorizado");
				    }

				    String gSonString41 = gson.toJson(jsonEdicaoIncidenteObject);
				    out.println(gSonString41);
				    System.out.println("Echo: " + gSonString41);
				    BancoDados.finalizarStatement(statement);
				    BancoDados.finalizarResultSet(resultSet);
				    BancoDados.desconectar();
				    break;


				default:
					break;
				}
				
				
				

			}

			
			out.close();
			in.close();
			clientSocket.close();
		} catch (IOException e) {
			System.err.println("Problem with Communication Server");
			System.exit(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private boolean verificarUsuario(int idUsuario, String token) throws SQLException, IOException {
	    Connection ligacao1 = BancoDados.conectar();
	    PreparedStatement statement = null;
	    ResultSet resultSet = null;

	    try {
	        // Verificar se o usuário existe no banco de dados com base no id_usuario e token
	        statement = ligacao1.prepareStatement("SELECT * FROM usuario WHERE id_usuario = ? AND token = ?");
	        statement.setInt(1, idUsuario);
	        statement.setString(2, token);
	        resultSet = statement.executeQuery();

	        // Se houver algum resultado, o usuário é válido
	        return resultSet.next();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        // Em caso de exceção, tratamento adequado ou retorno de valor padrão, de acordo com a sua aplicação
	        return false;
	    } finally {
	        // Fechar os recursos
	        BancoDados.finalizarStatement(statement);
	        BancoDados.finalizarResultSet(resultSet);
	        BancoDados.desconectar();
	    }
	}


	public int getCodigo() {
		return codigo;
	}
}