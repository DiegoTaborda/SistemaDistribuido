package ClienteeServidor;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import dao.BancoDados;



public class Usuario {
	
	private int id_operacao;
	
	private int id_usuario;
	
	private String token;
	
	private String nome;
	
	private String email;
	
	private String senha;
	
	
    
    public Usuario(String nome,String email,String senha) {
    	this.nome = nome;
    	this.email = email;
    	this.senha = senha;
    }
    
	
	public Usuario() {
		
	
	}
	
	public String logar(String email, String senha){
		
		
		Gson gson = new Gson();
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id_operacao", 3);
		jsonObject.addProperty("email", email);
		jsonObject.addProperty("senha", hashed(senha));
		
		String gString = gson.toJson(jsonObject);
		
		
		return gString;
	}
	
	public String cadastrar(String nome, String email, String senha) {
	 
	    Gson gson = new Gson();
	    JsonObject jsonObject = new JsonObject();

	        jsonObject.addProperty("id_operacao", 1);
		    jsonObject.addProperty("nome", nome);
		    jsonObject.addProperty("email", email);
		    jsonObject.addProperty("senha", hashed(senha));
		
		    String gsonString = gson.toJson(jsonObject);
		    
	 
	    return gsonString;
	}
	
	public String atualizarCadastro(String nome, String email,String senha, String token, int id_usuario) {
		
		Gson gson = new Gson();
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addProperty("id_operacao", 2);
		jsonObject.addProperty("nome", nome);
		jsonObject.addProperty("email", email);
		jsonObject.addProperty("senha", hashed(senha));
		jsonObject.addProperty("token", token);
		jsonObject.addProperty("id_usuario", id_usuario);
		
		String gsonString = gson.toJson(jsonObject);
		
		return gsonString;
	
	}
	
	public String removerConta(String email,String senha,String token,int id_usuario) {
		
		Gson gson = new Gson();
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addProperty("id_operacao", 8);
		jsonObject.addProperty("email", email);
		jsonObject.addProperty("senha", hashed(senha));
		jsonObject.addProperty("token", token);
		jsonObject.addProperty("id_usuario", id_usuario);
		
		String gsonString = gson.toJson(jsonObject);
		
		return gsonString;
	}
	
	
	
	public String fazerLogout(int id_usuario,String token) {
		
		Gson gson = new Gson();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id_operacao", 9);
		jsonObject.addProperty("token", token);
		jsonObject.addProperty("id_usuario", id_usuario);
		String gsonString = gson.toJson(jsonObject);
		return gsonString;
		
	}
	
	public String hashed (String pswd) {
    	
    	String hashed = "";
    	
        for (int i = 0; i < pswd.length(); i++) {
            char c = pswd.charAt(i);
            int asciiValue = (int) c; 
            int novoAsciiValue = asciiValue + pswd.length();
            if (novoAsciiValue > 127) {
                novoAsciiValue = novoAsciiValue - 127 + 32;
            }
            char novoCaractere = (char) novoAsciiValue;
            hashed += novoCaractere;
        }
        return hashed;
    }
	
	
	
	
	public boolean verificarCadastroExiste(String email,Connection connection) throws SQLException {
		
		PreparedStatement statement = null;
        ResultSet resultStatement = null;
        
        statement = connection.prepareStatement("SELECT *FROM usuario WHERE email =?");
		
        statement.setString(1, email);
        
        resultStatement = statement.executeQuery();
        
        if(resultStatement.next()) {
        	return true;
        }
        
        BancoDados.finalizarResultSet(resultStatement);
        BancoDados.finalizarStatement(statement);
		return false;
	}
	
	 public boolean verificarCadastro(Usuario usuario) {
	    	
	    	if (validarNome(usuario.getNome()) && validarEmail(usuario.getEmail()) && validarSenha(usuario.getSenha())) {
	    		return true;
	    		
	    	} else return false;
	    	
	    }

	 public boolean validarNome(String nome) {
	    	
	    	if (nome.length() >= 3 && nome.length() <= 32) {
	    		//boolean teste = nome.matches("[a-zA-Z ]+");
	    		return nome.matches("[a-zA-Z ]+");
	    	} else return false;
	    }
	    
	    public boolean validarEmail(String email) {
	    	
	    	if (email.length() >= 16 && email.length() <= 50 && email.contains("@")) {
	    		return true;
	    	} else return false;
	    }
	    
	    public boolean validarSenha(String senha) {
	    	if (senha.length() >= 8 && senha.length() <= 32) {
	    		return true;
	    	} else return false;
	    }
	


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getSenha() {
		return senha;
	}


	public void setSenha(String senha) {
		this.senha = senha;
	}

	public int getId_operacao() {
		return id_operacao;
	}

	public void setId_operacao(int id_operacao) {
		this.id_operacao = id_operacao;
	}

	public int getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(int id_usuario) {
		this.id_usuario = id_usuario;
	}



	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	

}
