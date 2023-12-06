package sistemasdistribuidos.cliente;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import java.io.*;
import java.net.*;
import java.util.Optional;

import javafx.application.Platform;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class ClientController {
    private static String SECRET_KEY = "AoT3QFTTEkj16rCby/TPVBWvfSQHL3GeEz3zVwEd6LDrQDT97sgDY8HJyxgnH79jupBWFOQ1+7fRPBLZfpuA2lwwHqTgk+NJcWQnDpHn31CVm63Or5c5gb4H7/eSIdd+7hf3v+0a5qVsnyxkHbcxXquqk9ezxrUe93cFppxH4/kF/kGBBamm3kuUVbdBUY39c4U3NRkzSO+XdGs69ssK5SPzshn01axCJoNXqqj+ytebuMwF8oI9+ZDqj/XsQ1CLnChbsL+HCl68ioTeoYU9PLrO4on+rNHGPI0Cx6HrVse7M3WQBPGzOd1TvRh9eWJrvQrP/hm6kOR7KrWKuyJzrQh7OoDxrweXFH8toXeQRD8=";

    private boolean connected = false;
    User user;
    private String token; 
    private Socket clientSocket;
    
    @FXML
    private Label pageTitle;

    @FXML
    private TextField connectionIP;

    @FXML
    private TextField connectionPort;

    @FXML
    private HBox loginBox;

    @FXML
    private HBox connectBox;

    @FXML
    private VBox registerBox;

    @FXML
    private VBox profileBox;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField register_name;

    @FXML
    private TextField register_email;

    @FXML
    private PasswordField register_password;
    
    @FXML
    private VBox listBox;
    
    @FXML
    private VBox changeBox;
    
    @FXML
    private TextField change_id;

    @FXML
    private TextField change_name;
    
    @FXML
    private TextField change_email;
    
    @FXML
    private TextField change_password;
    
    @FXML
    private Button changeButton;
    
    @FXML
    private TextField deleteUserId;

    @FXML
    private TextField id_segmento;
    @FXML
    private TextField idOrigem;
    @FXML
    private TextField nome_origem;
    @FXML
    private TextField obs_origem;
    @FXML
    private TextField idDestino;
    @FXML
    private TextField nome_destino;
    @FXML
    private TextField obs_destino;
    @FXML
    private TextField direcao;
    @FXML
    private TextField distancia;
    @FXML
    private TextField segmentoObs;

    @FXML
    private TextField id_ponto;
    @FXML
    private TextField name_ponto;
    @FXML
    private TextField pontoObs;
    
    @FXML
    private VBox listRotaBox;

    @FXML
    private TextField idInputBox;
    
    @FXML
    private HBox deleteUserBox;
    
    @FXML
    private Button listUsersButton;
    
    @FXML
    private Button pontoBoxButton;
    
    @FXML
    private VBox rotasBox;

    @FXML
    private VBox pontoBox;
    
    @FXML
    private VBox segmentoBox;
    
    @FXML
    protected void onHelloButtonClick() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    String serverAddress = connectionIP.getText();
                    int serverPort = Integer.parseInt(connectionPort.getText());
                    clientSocket = new Socket(serverAddress, serverPort);

                    goToLogin();
//                    goToRegister();

                    connected = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private String hashPasswordMD5(String password) {
        return DigestUtils.md5Hex(password).toUpperCase();
    }

    private void setTitle(String str) {
        Platform.runLater(() -> {
            pageTitle.setText(str); // Update the label on the JavaFX Application Thread
        });
    }

    private void goToConnection() {
        setTitle("Conectar a servidor");
        connectBox.setVisible(true);
        connectBox.setManaged(true);
        profileBox.setVisible(false);
        profileBox.setManaged(false);
        loginBox.setVisible(false);
        loginBox.setManaged(false);
        registerBox.setVisible(false);
        registerBox.setManaged(false);
        changeBox.setVisible(false);
        changeBox.setManaged(false);
        rotasBox.setVisible(false);
    	rotasBox.setManaged(false);
    	pontoBox.setVisible(false);
    	pontoBox.setManaged(false);
    	segmentoBox.setVisible(false);
    	segmentoBox.setManaged(false);
    }
    @FXML
    private void goToLogin() {
        setTitle("Faca seu login");
        connectBox.setVisible(false);
        connectBox.setManaged(false);
        profileBox.setVisible(false);
        profileBox.setManaged(false);
        loginBox.setVisible(true);
        loginBox.setManaged(true);
        registerBox.setVisible(false);
        registerBox.setManaged(false);
        changeBox.setVisible(false);
        changeBox.setManaged(false);
        rotasBox.setVisible(false);
    	rotasBox.setManaged(false);
    	pontoBox.setVisible(false);
    	pontoBox.setManaged(false);
    	segmentoBox.setVisible(false);
    	segmentoBox.setManaged(false);
    }

    private void goToProfile() {
        setTitle("Perfil/Opções");
        connectBox.setVisible(false);
        connectBox.setManaged(false);
        profileBox.setVisible(true);
        profileBox.setManaged(true);
        loginBox.setVisible(false);
        loginBox.setManaged(false);
        registerBox.setVisible(false);
        registerBox.setManaged(false);
        changeBox.setVisible(false);
        changeBox.setManaged(false);
        rotasBox.setVisible(false);
    	rotasBox.setManaged(false);
    	pontoBox.setVisible(false);
    	pontoBox.setManaged(false);
    	segmentoBox.setVisible(false);
    	segmentoBox.setManaged(false);
    }

    private void goToRegister() {
        setTitle("Faça seu autocadastro");
        connectBox.setVisible(false);
        connectBox.setManaged(false);
        profileBox.setVisible(false);
        profileBox.setManaged(false);
        loginBox.setVisible(false);
        loginBox.setManaged(false);
        registerBox.setVisible(true);
        registerBox.setManaged(true);
        changeBox.setVisible(false);
        changeBox.setManaged(false);
        rotasBox.setVisible(false);
    	rotasBox.setManaged(false);
    	pontoBox.setVisible(false);
    	pontoBox.setManaged(false);
    	segmentoBox.setVisible(false);
    	segmentoBox.setManaged(false);
    }

    private void goToChange() {
        setTitle("Alteração de cadastro");
        connectBox.setVisible(false);
        connectBox.setManaged(false);
        profileBox.setVisible(false);
        profileBox.setManaged(false);
        loginBox.setVisible(false);
        loginBox.setManaged(false);
        registerBox.setVisible(false);
        registerBox.setManaged(false);
        changeBox.setVisible(true);
        changeBox.setManaged(true);
        rotasBox.setVisible(false);
    	rotasBox.setManaged(false);
    	pontoBox.setVisible(false);
    	pontoBox.setManaged(false);
    	segmentoBox.setVisible(false);
    	segmentoBox.setManaged(false);
    }
    @FXML
    private void goToRotas() {
    	setTitle("Configuração de Rotas");
    	connectBox.setVisible(false);
    	connectBox.setManaged(false);
    	profileBox.setVisible(false);
    	profileBox.setManaged(false);
    	loginBox.setVisible(false);
    	loginBox.setManaged(false);
    	registerBox.setVisible(false);
    	registerBox.setManaged(false);
    	changeBox.setVisible(false);
    	changeBox.setManaged(false);
    	rotasBox.setVisible(true);
    	rotasBox.setManaged(true);
    	pontoBox.setVisible(false);
    	pontoBox.setManaged(false);
    	segmentoBox.setVisible(false);
    	segmentoBox.setManaged(false);
    }
    @FXML
    private void goToPontoBox() {
    	setTitle("Configuração de Pontos");
    	connectBox.setVisible(false);
        connectBox.setManaged(false);
        profileBox.setVisible(false);
        profileBox.setManaged(false);
        loginBox.setVisible(false);
        loginBox.setManaged(false);
        registerBox.setVisible(false);
        registerBox.setManaged(false);
        changeBox.setVisible(false);
        changeBox.setManaged(false);
        rotasBox.setVisible(false);
    	rotasBox.setManaged(false);
    	pontoBox.setVisible(true);
    	pontoBox.setManaged(true);
    	segmentoBox.setVisible(false);
    	segmentoBox.setManaged(false);
    }
    @FXML
    private void goToSegmentoBox() {
    	setTitle("Configuração de Rotas");
    	connectBox.setVisible(false);
    	connectBox.setManaged(false);
    	profileBox.setVisible(false);
    	profileBox.setManaged(false);
    	loginBox.setVisible(false);
    	loginBox.setManaged(false);
    	registerBox.setVisible(false);
    	registerBox.setManaged(false);
    	changeBox.setVisible(false);
    	changeBox.setManaged(false);
    	rotasBox.setVisible(false);
    	rotasBox.setManaged(false);
    	pontoBox.setVisible(false);
    	pontoBox.setManaged(false);
    	segmentoBox.setVisible(true);
    	segmentoBox.setManaged(true);
    }
    
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showUser(User user) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Usuário:");
        alert.setContentText(user.displayUser());
        alert.showAndWait();
    }
    private static Jws<Claims> parseToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
    }
    public static boolean isAdmin(String token) {
        Jws<Claims> parsedToken = parseToken(token);

        return (boolean) parsedToken.getBody().get("admin", Boolean.class);
    }
    @FXML
    protected void onLoginClick() {
        if (connected) {
            String email = emailField.getText();
            String password = passwordField.getText();

            String hashedPassword = hashPasswordMD5(password);
            String loginRequest = "{ \"action\": \"login\", \"data\": { \"email\": \"" + email + "\", \"password\": \"" + hashedPassword + "\" } }";

            System.out.println("Mandando para servidor: " + loginRequest);
            sendMessageToServer(loginRequest);

            String response = receiveMessageFromServer();
            if (response != null) {
                Platform.runLater(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);

                        String action = jsonResponse.getString("action");
                        boolean error = jsonResponse.getBoolean("error");
                        String message = jsonResponse.getString("message");

                        if (!error) {
                            token = jsonResponse.getJSONObject("data").getString("token");

                            clearLoginFields();
                            goToProfile();
                            System.out.println("Resposta do servidor: " + response);
                            System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
                            if(isAdmin(token)) {
                            	System.out.println("teste" + isAdmin(token));
                                listUsersButton.setVisible(true);
                                listUsersButton.setManaged(true);
                                deleteUserBox.setVisible(true);
                                deleteUserBox.setManaged(true);
                            } else {
                            	
                            }
//                          System.out.println("Token recebido: " + token);
                        } else {
                            showWarning(message);
                            System.out.println("Server Response: " + response);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("Error parsing JSON response");
                    }
                });
            }
        } else {
        }
    }

    private void clearRegisterFields() {
        register_password.clear();
        register_email.clear();
        register_name.clear();
    }

    private void clearLoginFields() {
        emailField.clear();
        passwordField.clear();
    }
    
    private void clearChangeFields() {
    	change_id.clear();
    	change_name.clear();
    	change_password.clear();
    }
    
    @FXML
    protected void onSelfRegisterClick() {
        goToRegister();
    }
    @FXML
    protected void ongoToProfile() {
    	goToProfile();
    }
    @FXML
    protected void ongoToRotasClick() {
    	goToRotas();
    }
    @FXML
    protected void onChangeUserClick() {
    	clearChangeFields();
    	
    	if(isAdmin(token)) {
    		change_id.setVisible(true);
    		System.out.println(isAdmin(token));
    	} else {
    		change_id.setVisible(false);
    		//change_id.setEditable(false);
    		//change_id.setText("1");
    	}
    	
    	goToChange();
    }

    @FXML
    protected void onRegisterClick() {
        if (connected) {
            String name = register_name.getText();
            String email = register_email.getText();
            String password = register_password.getText();

            String hashedPassword = hashPasswordMD5(password);
            String registerRequest = "{ \"action\": \"autocadastro-usuario\", \"data\": {  \"name\": \"" + name + "\", \"email\": \"" + email + "\", \"password\": \"" + hashedPassword + "\" } }";

            System.out.println("Mandando para servidor: " + registerRequest);
            sendMessageToServer(registerRequest);

            String response = receiveMessageFromServer();
            if (response != null) {
                Platform.runLater(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);

                        String action = jsonResponse.getString("action");
                        boolean error = jsonResponse.getBoolean("error");
                        String message = jsonResponse.getString("message");

                        if (!error && action.equals("autocadastro-usuario")) {
                            clearRegisterFields();
                        } else {
                            showWarning(message);
                        }
                        System.out.println("Resposta do servidor: " + response);
                        System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("Error parsing JSON response");
                    }
                });
            }
        } else {
            showWarning("Erro de conexão!");
        }
    }

    @FXML
    protected void onMyDataClick() {
        if (connected) {
            String clientToServer = "{ \"action\": \"pedido-proprio-usuario\", \"data\": {  \"token\": \"" + token + "\"} }";

            System.out.println("Mandando para servidor: " + clientToServer);
            sendMessageToServer(clientToServer);

            String response = receiveMessageFromServer();
            if (response != null) {
                Platform.runLater(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject jsonData = jsonResponse.getJSONObject("data");
                        
                        String action = jsonResponse.getString("action");
                        boolean error = jsonResponse.getBoolean("error");
                        String message = jsonResponse.getString("message");

                        if (!error && action.equals("pedido-proprio-usuario")) {
                            user = new User(
                                    Integer.parseInt(jsonData.getJSONObject("user").getString("id")),
                                    jsonData.getJSONObject("user").getString("name"),
                                    jsonData.getJSONObject("user").getString("email"),
                                    token
                                    );
                            user.setAdmin(jsonData.getJSONObject("user").getString("type").equals("admin"));
                            showUser(user);
                        } else {
                            showWarning(message);
                        }
                        System.out.println("Resposta do servidor: " + response);
                        System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("Error parsing JSON response");
                    }
                });
            }
        } else {
            showWarning("Erro de conexão!");
        }
    }

    @FXML
    protected void onListUsersClick() {
        //System.out.println("A");
        String listRequest = "{ \"action\": \"listar-usuarios\", \"data\": {  \"token\": \"" + token + "\"} }";
        //System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("Mandando para servidor: " + listRequest);
        sendMessageToServer(listRequest);
        
        String response = receiveMessageFromServer();
        System.out.println("Resposta do servidor: " + response);
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
        
        try {
        	JSONObject jsonObject = new JSONObject(response);
        	JSONObject dataObject = jsonObject.getJSONObject("data");
        	JSONArray usersArray = dataObject.getJSONArray("users");
        		
        	listBox.setVisible(true);
        	listBox.getChildren().clear();
        	
		        for (int i = 0; i < usersArray.length(); i++) {
		        	JSONObject userObject = usersArray.getJSONObject(i);
		        	
		        	String id = userObject.getString("id");
		        	String name = userObject.getString("name");
		        	String type = userObject.getString("type");
		        	String email = userObject.getString("email");
		        	
		        	Text usuario = new Text("ID: " + id + " | Nome: " + name + " | Tipo: " + type + " | email: " + email);
		        	listBox.getChildren().add(usuario);		        	
		        }		       
	    	} catch(JSONException e) {
            System.out.println(e.toString());
        }
    }
        
    @FXML
    protected void onChangeClick() {
    	
    	String user_id = change_id.getText();
    	String name = change_name.getText();
    	String email = change_email.getText();
    	String password = change_password.getText();
    	String hashedPassword = hashPasswordMD5(password);
    	
    	if(isAdmin(token)) {
    			String changeRequest = "{ \"action\": \"edicao-usuario\", \"data\": {  \"token\": \"" + token + "\", \"user_id\": \"" + user_id + "\", \"name\": \"" + name + "\", \"email\": \"" + email + "\", \"password\": \"" + hashedPassword + "\", \"type\": \"" + "admin" + "\" } }";    		
        		sendMessageToServer(changeRequest);
        		String response = receiveMessageFromServer();
        		System.out.println("Enviado para o server: " + changeRequest);
        		System.out.println("Recebido do servidor: " + response);
        		System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
   
    	} else {
    		try {
    			//pedido para definir id
    			String DataRequest = "{ \"action\": \"pedido-proprio-usuario\", \"data\": {  \"token\": \"" + token + "\"} }";
    			sendMessageToServer(DataRequest);
    			System.out.println("Enviado para o server: " + DataRequest);
    			String response = receiveMessageFromServer();
    			System.out.println("Recebido do server: " + response);
    			
    			JSONObject jsonresponse = new JSONObject(response);
    			JSONObject dataObject = jsonresponse.getJSONObject("data");
    			//System.out.println(dataObject);
    			JSONObject userObject = dataObject.getJSONObject("user");
    			String id = userObject.getString("id");
    			
    			String changeRequest = "{ \"action\": \"autoedicao-usuario\", \"data\": { \"token\": \"" + token + "\", \"id\": \"" + id + "\", \"name\": \"" + name + "\", \"email\": \"" + email + "\", \"password\": \"" + hashedPassword + "\"} }";
    			
    			sendMessageToServer(changeRequest);
    			System.out.println("Enviado para o server: " + changeRequest);
    			response = receiveMessageFromServer();
    			System.out.println("Recebido do server: " + response);
    			
    		} catch (JSONException e) {
                e.printStackTrace();
                System.out.println("Error parsing JSON response");
            }
    	}
    	
    	
    	//System.out.println("Funcionou");
    }
   
    @FXML
    protected void onLogoutClick() {
        if (connected) {
            String logoutRequest = "{ \"action\": \"logout\", \"data\": { \"token\": \"" + token + "\" } }";
            
            System.out.println("Mandando para servidor: " + logoutRequest);
            sendMessageToServer(logoutRequest);

            String response = receiveMessageFromServer();
            if (response != null) {
                Platform.runLater(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);

                        boolean error = jsonResponse.getBoolean("error");
                        String message = jsonResponse.getString("message");

                        if (!error) {
                            System.out.println("Resposta do servidor: " + response);
                            System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
                            
                            emailField.setText("");
                            passwordField.setText("");

                            if (clientSocket != null && !clientSocket.isClosed()) {
                                try {
                                    clientSocket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            listUsersButton.setVisible(false);
                            listUsersButton.setManaged(false);
                            goToConnection();
                        } else {
                            showWarning(message);
                            System.out.println("Resposta do servidor: " + response);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("Error parsing JSON response");
                    }
                });
            }
        } else {
            showWarning("Conexão falhou");
        }
    }

    @FXML
    protected void onSelfDeleteClick() {
        if (connected) {
            // Ask for email using an input dialog
            TextInputDialog emailDialog = new TextInputDialog();
            emailDialog.setTitle("Email Input");
            emailDialog.setHeaderText("Enter Email:");
            emailDialog.setContentText("Email:");
            Optional<String> emailResult = emailDialog.showAndWait();

            if (emailResult.isPresent()) {
                String email = emailResult.get();

                // Ask for password using an input dialog
                TextInputDialog passwordDialog = new TextInputDialog();
                passwordDialog.setTitle("Password Input");
                passwordDialog.setHeaderText("Enter Password:");
                passwordDialog.setContentText("Password:");
                Optional<String> passwordResult = passwordDialog.showAndWait();

                if (passwordResult.isPresent()) {
                    String password = passwordResult.get();
                    String hashedPassword = hashPasswordMD5(password);
                    
                    String logoutRequest = "{ \"action\": \"excluir-proprio-usuario\", \"data\": { \"token\": \"" + token + "\", \"email\": \"" + email + "\", \"password\": \"" + hashedPassword + "\" } }";
                    System.out.println("Mandando para servidor: " + logoutRequest);
                    sendMessageToServer(logoutRequest);

                    String response = receiveMessageFromServer();
                    if (response != null) {
                        Platform.runLater(() -> {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);

                                boolean error = jsonResponse.getBoolean("error");
                                String message = jsonResponse.getString("message");

                                if (!error) {
                                    System.out.println("Resposta do servidor: " + response);
                                    System.out.println("------------------------------------------------------------------------------------------------------------------------------------");

                                    emailField.setText("");
                                    passwordField.setText("");

                                    if (clientSocket != null && !clientSocket.isClosed()) {
                                        try {
                                            clientSocket.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    goToConnection();
                                } else {
                                    showWarning(message);
                                    System.out.println("Resposta do servidor: " + response);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                System.out.println("Error parsing JSON response");
                            }
                        });
                    }
                }
            }
        } else {
            showWarning("Conexão falhou");
        }
    }

    @FXML
    private void onDeleteUserClick() {
    	
    	String id = deleteUserId.getText();
    	String deleteRequest = "{ \"action\": \"excluir-usuario\", \"data\": { \"token\": \"" + token + "\", \"user_id\": \"" + id + "\"} }";
    	
    	sendMessageToServer(deleteRequest);
    	System.out.println("Mensagem enviada: " + deleteRequest);
    	
    	String response = receiveMessageFromServer();
    	
    	try {
    		JSONObject jsonResponse = new JSONObject(response);
    		System.out.println("Mensagem recebida: " + jsonResponse.toString());
    		System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
    	} catch(JSONException e) {
    		System.out.println(e);
    	}
    	
    }
    
    @FXML
    private void onCriarSegmentoClick() {
    	
//    	String nomep1 = ;
//    	String obsp1 = ;
//    	String nomep2 = ;
//    	String obsp2 = ;
//    	String direcao = ;
//    	String distancia = ;
//    	String obsSegmento = ;
    	
    	JSONObject sendJson = new JSONObject();
    	JSONObject data = new JSONObject();
    	JSONObject segmento = new JSONObject();
    	JSONObject pontoOrigem = new JSONObject();
    	JSONObject pontoDestino = new JSONObject();
    	
    	try {
    		
    		sendJson.put("action", "cadastro-segmento");
    		sendJson.put("data", data);
	    		data.put("token", token);
	    		data.put("segmento", segmento);
			    		pontoOrigem.put("id", Integer.parseInt(idOrigem.getText()));
			    		pontoOrigem.put("name", nome_origem.getText());
			    		pontoOrigem.put("obs", obs_origem.getText());
			    		segmento.put("ponto_origem", pontoOrigem);
			    		pontoDestino.put("id", Integer.parseInt(idDestino.getText()));
			    		pontoDestino.put("name", nome_destino.getText());
			    		pontoDestino.put("obs", obs_destino.getText());
			    		segmento.put("ponto_destino", pontoDestino);
			    	segmento.put("direcao", direcao.getText());
			    	segmento.put("distancia", Integer.parseInt(distancia.getText()));
			    	segmento.put("obs", segmentoObs.getText());
			    	segmento.put("bloqueado", false); //arrumar pegando essa informacao do text field
    		
			    	sendMessageToServer(sendJson.toString());
	        		String response = receiveMessageFromServer();
	        		
	        		System.out.println("Mandando para servidor: " + sendJson);
	    	    	System.out.println("Resposta do servidor: " + response);
	                System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
	        		
    	} catch(JSONException e) {
    		System.out.println(e.toString());
    	}
    }
    
    @FXML
    private void onListarSegmentosClick() {
    	
//    	String nome = ;
//		String obs = ;
    	
    	JSONObject sendJson = new JSONObject();
    	JSONObject data = new JSONObject();
    	
    	
    	try {
    		
    		sendJson.put("action", "listar-segmentos");
    		data.put("token", token);
    		sendJson.put("data", data);
    			
    		listRotaBox.setVisible(true);
    		listRotaBox.getChildren().clear();
        	
    		sendMessageToServer(sendJson.toString());
    		String response = receiveMessageFromServer();
    		
        	JSONObject jsonObject = new JSONObject(response);
        	JSONObject dataObject = jsonObject.getJSONObject("data");
        	JSONArray segmentosArray = dataObject.getJSONArray("segmentos");
        	
        	for (int i = 0; i < segmentosArray.length(); i++) {
        		JSONObject segmentoObject = segmentosArray.getJSONObject(i);
		        	
		        String id = segmentoObject.getString("id");
		        String direcao = segmentoObject.getString("direcao");
		        String distancia = segmentoObject.getString("distancia");		        
		        String obs = segmentoObject.getString("obs");
		        	
		        Text segmento= new Text("ID: " + id + " | Direção: " + direcao + "| Disância: " + distancia + " | Observação: " + obs);
		        listRotaBox.getChildren().add(segmento);
        	}
    		
        	System.out.println("Mandando para servidor: " + sendJson);
	    	System.out.println("Resposta do servidor: " + response);
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
	
    	} catch(JSONException e) {
    		System.out.println(e.toString());
    	}

    }
    
    @FXML
    private void onRequisitarMudancaSegmento() {
    	
//    	String nome = ;
//		String obs = ;
    	
    	JSONObject sendJson = new JSONObject();
    	JSONObject data = new JSONObject();
    	
    	
    	try {
    		
    		sendJson.put("action", "pedido-edicao-segmento");
    		data.put("token", token);
    		data.put("segmento_id", Integer.parseInt(idInputBox.getText()));
    		sendJson.put("data", data);
    			
    		sendMessageToServer(sendJson.toString());
        	String response = receiveMessageFromServer();
        	
    		goToSegmentoBox();
    		
    		JSONObject responseJson = new JSONObject(response).getJSONObject("data");
    		
    		id_segmento.setText(responseJson.getJSONObject("segmento").getString("id"));
    		idOrigem.setText(responseJson.getJSONObject("segmento").getJSONObject("ponto_origem").getString("id"));
    		nome_origem.setText(responseJson.getJSONObject("segmento").getJSONObject("ponto_origem").getString("name"));
    		obs_origem.setText(responseJson.getJSONObject("segmento").getJSONObject("ponto_origem").getString("obs"));
    		idDestino.setText(responseJson.getJSONObject("segmento").getJSONObject("ponto_destino").getString("id"));
    		nome_destino.setText(responseJson.getJSONObject("segmento").getJSONObject("ponto_destino").getString("name"));
    		obs_destino.setText(responseJson.getJSONObject("segmento").getJSONObject("ponto_destino").getString("obs"));
    		direcao.setText(responseJson.getJSONObject("segmento").getString("direcao"));
    		distancia.setText(responseJson.getJSONObject("segmento").getString("distancia"));
    		segmentoObs.setText(responseJson.getJSONObject("segmento").getString("obs"));
    		
    		System.out.println("Mandando para servidor: " + sendJson);
	    	System.out.println("Resposta do servidor: " + response);
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
	
    	} catch(JSONException e) {
    		System.out.println(e.toString());
    	}

    }
    
    @FXML
    private void onMudarSegmento() {
    	
//    	String nome = ;
//		String obs = ;
    	
    	JSONObject sendJson = new JSONObject();
    	JSONObject data = new JSONObject();
    	JSONObject segmento = new JSONObject();
    	JSONObject origem = new JSONObject();
    	JSONObject destino = new JSONObject();    	
    	
    	try {
    		
    		sendJson.put("action", "edicao-segmento");
    		data.put("token", token);
    		data.put("segmento_id", Integer.parseInt(id_segmento.getText()));
    		
    		segmento.put("direcao", direcao.getText());
    		segmento.put("distancia", Integer.parseInt(distancia.getText()));
    		segmento.put("obs", segmentoObs.getText());
    		segmento.put("bloqueado", true); //arrumar pegando a informação do text field    		
    		
    		origem.put("id", Integer.parseInt(idOrigem.getText()));
    		origem.put("name", nome_origem.getText());
    		origem.put("obs", obs_origem.getText());

    		destino.put("id", Integer.parseInt(idDestino.getText()));
    		destino.put("name", nome_destino.getText() );
    		destino.put("obs", obs_destino.getText() );
    		
    		segmento.put("ponto_origem", origem);
    		segmento.put("ponto_destino", destino);
    		
    		data.put("segmento",segmento);
    		
    		sendJson.put("data", data);
    		
    		sendMessageToServer(sendJson.toString());
    		String response = receiveMessageFromServer();
    		
    		System.out.println("Mandando para servidor: " + sendJson);
	    	System.out.println("Resposta do servidor: " + response);
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
    		
    	} catch(JSONException e) {
    		System.out.println(e.toString());
    	}
    	
    }
    
    @FXML
    private void onExcluirSegmentoClick() {
    	
//    	String nome = ;
//		String obs = ;
    	
    	JSONObject sendJson = new JSONObject();
    	JSONObject data = new JSONObject();
    	
    	
    	try {
    		
    		sendJson.put("action", "excluir-segmento");
    		sendJson.put("data", data);
    			data.put("token", token);
    			data.put("segmento_id", Integer.parseInt(idInputBox.getText()));
    		
    		sendMessageToServer(sendJson.toString());
        	String response = receiveMessageFromServer();
        	
        	System.out.println("Mandando para servidor: " + sendJson);
	    	System.out.println("Resposta do servidor: " + response);
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
	
    	} catch(JSONException e) {
    		System.out.println(e.toString());
    	}

    }
    
    @FXML
    private void onCriarPontoClick() {
    	
//    	String nome = ;
//		String obs = ;
    	
    	JSONObject sendJson = new JSONObject();
    	JSONObject data = new JSONObject();
    	
    	
    	try {
    		
    		sendJson.put("action", "cadastro-ponto");
	    		data.put("token", token);
	    		data.put("name", name_ponto.getText());
	    		data.put("obs", pontoObs.getText());
	    		sendJson.put("data", data);
	    		
	    	sendMessageToServer(sendJson.toString());
	    	String response = receiveMessageFromServer();
    		
	    	name_ponto.clear();
	    	pontoObs.clear();
	    	
	    	System.out.println("Mandando para servidor: " + sendJson);
	    	System.out.println("Resposta do servidor: " + response);
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
	    	
            
    	} catch(JSONException e) {
    		System.out.println(e.toString());
    	}
    }
    
    @FXML
    private void onListarPontoClick() {
    	
//    	String nome = ;
//		String obs = ;
    	
    	JSONObject sendJson = new JSONObject();
    	JSONObject data = new JSONObject();
    	
    	
    	try {
    		
    		sendJson.put("action", "listar-pontos");
    		data.put("token", token);
    		sendJson.put("data", data);
    		
    		listRotaBox.setVisible(true);
    		listRotaBox.getChildren().clear();
        	
        	sendMessageToServer(sendJson.toString());
        	String response = receiveMessageFromServer();
        	
        	JSONObject jsonObject = new JSONObject(response);
        	JSONObject dataObject = jsonObject.getJSONObject("data");
        	JSONArray pontosArray = dataObject.getJSONArray("pontos");
        	
        	for (int i = 0; i < pontosArray.length(); i++) {
        		JSONObject pontoObject = pontosArray.getJSONObject(i);
		        	
		        String id = pontoObject.getString("id");
		        String name = pontoObject.getString("name");
		        String obs = pontoObject.getString("obs");
		        	
		        Text ponto= new Text("ID: " + id + " | Nome: " + name + " | Observação: " + obs);
		        listRotaBox.getChildren().add(ponto);
        	}
    		
        	System.out.println("Mandando para servidor: " + sendJson);
	    	System.out.println("Resposta do servidor: " + response);
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
    		
    	} catch(JSONException e) {
    		System.out.println(e.toString());
    	}
    }

    @FXML
    private void onRequisitarMudancaPonto() {
    	
//    	String nome = ;
//		String obs = ;
    	
    	JSONObject sendJson = new JSONObject();
    	JSONObject data = new JSONObject();
    	
    	
    	try {
    		
    		sendJson.put("action", "pedido-edicao-ponto");
    		data.put("token", token);
    		data.put("ponto_id", Integer.parseInt(idInputBox.getText()));
    		sendJson.put("data", data);
    	
    		
    		
    		sendMessageToServer(sendJson.toString());
    		String response = receiveMessageFromServer();
    		
    		goToPontoBox();
    		
    		JSONObject responseJson = new JSONObject(response);
    		JSONObject responsePonto= responseJson.getJSONObject("data").getJSONObject("ponto");
    		
    		id_ponto.setText(responsePonto.getString("id"));
    		name_ponto.setText(responsePonto.getString("name"));
    		pontoObs.setText(responsePonto.getString("obs"));
    		
    		System.out.println("Mandando para servidor: " + sendJson);
	    	System.out.println("Resposta do servidor: " + response);
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
    		
    	} catch(JSONException e) {
    		System.out.println(e.toString());
    	}
    	
    }
    
    @FXML
    private void onAlterarPontoClick() {
    	
//    	String nome = ;
//		String obs = ;
    	
//    	JSONObject changeRequestJson = new JSONObject();
//    	JSONObject changeRequestData = new JSONObject();

    	JSONObject changeJson = new JSONObject();
    	JSONObject changeData = new JSONObject();
    	
   	
    	try {
    		
    		
    			//recebe resposta do server informando se é possivel alterar
    		
    		changeJson.put("action", "edicao-ponto");
    			changeData.put("token", token);
    			changeData.put("ponto_id", Integer.parseInt(id_ponto.getText()));
    			changeData.put("name", name_ponto.getText());
    			changeData.put("obs", pontoObs.getText());
    			changeJson.put("data", changeData);
    			
    		sendMessageToServer(changeJson.toString());
    	    String response = receiveMessageFromServer();
    			
    	    System.out.println("Mandando para servidor: " + changeJson);
	    	System.out.println("Resposta do servidor: " + response);
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
            
    	} catch(JSONException e) {
    		System.out.println(e.toString());
    	}
    }
    
    @FXML
    private void onDeletarPontoClick() {
    	
//    	String nome = ;
//		String obs = ;
    	
    	JSONObject sendJson = new JSONObject();
    	JSONObject data = new JSONObject();
    	
    	
    	try {
    		
    		sendJson.put("action", "excluir-ponto");
    			data.put("token", token);
    			data.put("ponto_id", Integer.parseInt(idInputBox.getText()));
    			sendJson.put("data", data);
	
    		sendMessageToServer(sendJson.toString());
    	    String response = receiveMessageFromServer();
    	    	
    	    System.out.println("Mandando para servidor: " + sendJson);
	    	System.out.println("Resposta do servidor: " + response);
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
            
    	} catch(JSONException e) {
    		System.out.println(e.toString());
    	}

    }
    
    private void sendMessageToServer(String message) {
        try {
            OutputStream out = clientSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(out, true);
            writer.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String receiveMessageFromServer() {
        try {
            InputStream in = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}