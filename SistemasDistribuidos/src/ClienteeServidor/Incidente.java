package ClienteeServidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import dao.BancoDados;

public class Incidente {
    
    private int id_usuario;
    private int id_incidente;
    private String data;
    private String rodovia;
    private int km;
    private int tipo_incidente;
    private String tokenUsuario;
   
    

	public Incidente(String data, String rodovia, int km, int tipo_incidente, String tokenUsuario, int id_usuario) {
		this.data = data;
		this.rodovia = rodovia;
		this.km = km;
		this.tipo_incidente = tipo_incidente;
		this.tokenUsuario = tokenUsuario;
		this.id_usuario = id_usuario;
	}

    public Incidente() {}

    public String reportarIncidente(String data, String rodovia, int km, int tipo_incidente, String token, int id_usuario) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id_operacao", 4);
        jsonObject.addProperty("data", data);
        jsonObject.addProperty("rodovia", rodovia);
        jsonObject.addProperty("km", km);
        jsonObject.addProperty("tipo_incidente", tipo_incidente);
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("id_usuario", id_usuario);

        String gString = gson.toJson(jsonObject);

        return gString;
    }
    
    public String solicitarIncidente(String rodovia,String data, String faixa_km,int periodo) {
    	Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        
        jsonObject.addProperty("id_operacao", 5);
        jsonObject.addProperty("rodovia", rodovia);
        jsonObject.addProperty("data", data);
        jsonObject.addProperty("faixa_km", faixa_km);
        jsonObject.addProperty("periodo", periodo);
        
        String gString = gson.toJson(jsonObject);
        
        
  	return gString;
    }
    
 
    
    
    public String solicitarIncidente(String rodovia,String data,int periodo) {
    	  Gson gson = new Gson();
          JsonObject jsonObject = new JsonObject();
          
          jsonObject.addProperty("id_operacao", 5);
          jsonObject.addProperty("rodovia", rodovia);
          jsonObject.addProperty("data", data);
          jsonObject.addProperty("faixa_km", "");
          jsonObject.addProperty("periodo", periodo);
          
          String gString = gson.toJson(jsonObject);
          
    	return gString;
    }
    
    
    public String listaIncidentesUsuario(String token, int id_usuario) {
    	
    	Gson gson = new Gson();
    	JsonObject jsonObject = new JsonObject();
    	
    	jsonObject.addProperty("id_operacao", 6);
    	jsonObject.addProperty("token", token);
    	jsonObject.addProperty("id_usuario", id_usuario);
    	
    	String gString = gson.toJson(jsonObject);
    	return gString;
	}
    
    public String editarIncidente(String token, int id_incidente, String data, String rodovia, int km, int tipo_incidente, int id_usuario) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id_operacao", 10);
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("id_incidente", id_incidente);
        jsonObject.addProperty("data", data);
        jsonObject.addProperty("rodovia", rodovia);
        jsonObject.addProperty("km", km);
        jsonObject.addProperty("tipo_incidente", tipo_incidente);
        jsonObject.addProperty("id_usuario", id_usuario);

        String gString = gson.toJson(jsonObject);

        return gString;
    }

    
    
    
    public String removerIncidente(String token, int id_incidente, int id_usuario) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id_operacao", 7);
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("id_incidente", id_incidente);
        jsonObject.addProperty("id_usuario", id_usuario);

        String gsonString = gson.toJson(jsonObject);

        return gsonString;
    }
    

    public static List<Integer> separarNumeros(String texto) {
        List<Integer> faixaKms = new ArrayList<>();

        if (texto != null && texto.contains("-")) {
            String[] partes = texto.split("-");
            for (String parte : partes) {
                try {
                    int numero = Integer.parseInt(parte);
                    faixaKms.add(numero);
                } catch (NumberFormatException e) {
                    // Caso haja algum erro na conversão, pode ser tratado aqui
                    e.printStackTrace();
                }
            }
        }

        return faixaKms;
    }

    public boolean verificarReporteIncidente(Incidente incidente) {
        if (validarRodovia(incidente.rodovia) && validarKm(incidente.km)) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean verificarIncidenteExiste(String rodovia, Connection connection) throws SQLException {
		
    	
		PreparedStatement statement = null;
        ResultSet resultStatement = null;
        
        statement = connection.prepareStatement("SELECT *FROM incidente WHERE rodovia = ?");
        statement.setString(1, rodovia);
        
        resultStatement = statement.executeQuery();
        
        if(resultStatement.next()) {
        	return true;
        }
        
        BancoDados.finalizarResultSet(resultStatement);
        BancoDados.finalizarStatement(statement);
		return false;
    	
    }


    public boolean validarRodovia(String rodovia) {
        return rodovia.matches("[A-Za-z]{2}-\\d{3}");
    }

    public boolean validarKm(int km) {
        return km >= 0 && km <= 999;
    }

    // Getters and Setters

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRodovia() {
        return rodovia;
    }

    public void setRodovia(String rodovia) {
        this.rodovia = rodovia;
    }

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public int getTipo_incidente() {
        return tipo_incidente;
    }

    public void setTipo_incidente(int tipo_incidente) {
        this.tipo_incidente = tipo_incidente;
    }

    public String getTokenUsuario() {
        return tokenUsuario;
    }

    public void setTokenUsuario(String tokenUsuario) {
        this.tokenUsuario = tokenUsuario;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

	public int getId_incidente() {
		return id_incidente;
	}

	public void setId_incidente(int id_incidente) {
		this.id_incidente = id_incidente;
	}



}
