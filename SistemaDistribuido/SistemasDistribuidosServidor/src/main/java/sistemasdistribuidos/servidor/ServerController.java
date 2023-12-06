package sistemasdistribuidos.servidor;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.mindrot.jbcrypt.BCrypt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController {
    private String SECRET_KEY = "AoT3QFTTEkj16rCby/TPVBWvfSQHL3GeEz3zVwEd6LDrQDT97sgDY8HJyxgnH79jupBWFOQ1+7fRPBLZfpuA2lwwHqTgk+NJcWQnDpHn31CVm63Or5c5gb4H7/eSIdd+7hf3v+0a5qVsnyxkHbcxXquqk9ezxrUe93cFppxH4/kF/kGBBamm3kuUVbdBUY39c4U3NRkzSO+XdGs69ssK5SPzshn01axCJoNXqqj+ytebuMwF8oI9+ZDqj/XsQ1CLnChbsL+HCl68ioTeoYU9PLrO4on+rNHGPI0Cx6HrVse7M3WQBPGzOd1TvRh9eWJrvQrP/hm6kOR7KrWKuyJzrQh7OoDxrweXFH8toXeQRD8=";
    @FXML
    private Label welcomeText;

    @FXML
    private TextField serverPort;

    private ExecutorService threadPool = Executors.newCachedThreadPool();

    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<Segmento> segmentoList = new ArrayList<>();
    private ArrayList<Ponto> pontoList = new ArrayList<>();

    private int idCounter = 1;
    private int idSegmento = 1;
    private int idPonto = 1;

    private boolean acceptingConnections = true;

    @FXML
    protected void onHelloButtonClick() {
        ServerService serverService = new ServerService();
        serverService.setOnSucceeded(event -> {
            welcomeText.setText("Servidor Iniciado!");
        });

        initializeUsers();

        serverService.start();
    }

    private void initializeUsers() {
        //int id = idCounter++;
        //senhas 123456 para ambos
        User exampleAdmin = new User(idCounter, "admin", "admin@admin.com", hash("E10ADC3949BA59ABBE56E057F20F883E"), true, "");
        idCounter++;
        User exampleUser = new User(idCounter, "usuario default", "user@email.com", hash("E10ADC3949BA59ABBE56E057F20F883E"), false, "");
        idCounter++;
        userList.add(exampleAdmin);
        userList.add(exampleUser);      
    }
    
    private String getResponse(String request) {
        JSONObject responseJson = new JSONObject();
        
        try {
            JSONObject requestJson = new JSONObject(request);

            // Check if the action is "login"
            if (requestJson.has("action") && requestJson.getString("action").equals("autocadastro-usuario")) {
                responseJson =  getRegisterResponse(requestJson);
            }
            else if (requestJson.has("action") && requestJson.getString("action").equals("login")) {
                responseJson =  getLoginResponse(requestJson);
            }
            else if (requestJson.has("action") && requestJson.getString("action").equals("logout")) {
                responseJson = getLogoutResponse(requestJson);
            }
            else if (requestJson.has("action") && requestJson.getString("action").equals("pedido-proprio-usuario")) {
                responseJson = getLoggedUserData(requestJson);
            }
            else if (requestJson.has("action") && requestJson.getString("action").equals("listar-usuarios")) {
                responseJson = getListUsers(requestJson);
            }
            else if (requestJson.has("action") && (requestJson.getString("action").equals("autoedicao-usuario") || requestJson.getString("action").equals("edicao-usuario"))) {   	
                responseJson = getChangeUser(requestJson);
            }
            else if (requestJson.has("action") && (requestJson.getString("action").equals("excluir-proprio-usuario") || requestJson.getString("action").equals("excluir-usuario"))) {   	
                responseJson = getDeleteUser(requestJson);
            } 
            else if (requestJson.has("action") && requestJson.getString("action").equals("cadastro-segmento")) {
            	responseJson = setSegmento(requestJson);
            } 
            else if (requestJson.has("action") && requestJson.getString("action").equals("pedido-edicao-segmento")) {
                responseJson = changeRequestSegmento(requestJson);
            } 
            else if (requestJson.has("action") && requestJson.getString("action").equals("edicao-segmento")) {
            	responseJson = changeSegmento(requestJson);
            } 
            else if (requestJson.has("action") && requestJson.getString("action").equals("listar-segmentos")) {
                responseJson = getListSegmento(requestJson);
            } 
            else if (requestJson.has("action") && requestJson.getString("action").equals("excluir-segmento")) {
            	responseJson = excluirSegmento(requestJson);
            } 
            else if (requestJson.has("action") && requestJson.getString("action").equals("cadastro-ponto")) {
            	responseJson = cadastrarPonto(requestJson);
            } 
            else if (requestJson.has("action") && requestJson.getString("action").equals("pedido-edicao-ponto")) {
            	responseJson = changeRequestPonto(requestJson);
            } 
            else if (requestJson.has("action") && requestJson.getString("action").equals("edicao-ponto")) {
            	responseJson = changePonto(requestJson);
            } 
            else if (requestJson.has("action") && requestJson.getString("action").equals("listar-pontos")) {
            	responseJson = getListPonto(requestJson);
            } 
            else if (requestJson.has("action") && requestJson.getString("action").equals("excluir-ponto")) {
            	responseJson = excluirPonto(requestJson);
            } 
            else {
                responseJson.put("action", requestJson.has("action"));
                responseJson.put("error", true);
                responseJson.put("message", "Ação desconhecida");
            }
        }
        catch(JSONException e) {
            System.out.println(e);
        }
        String response = responseJson.toString();
        //System.out.println("---------------------------------");
        System.out.println("Respondendo ao cliente: " + response);
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
        return response;
    }
    
    private JSONObject excluirPonto(JSONObject requestJSON) {
    	
    	JSONObject responseJson = new JSONObject();
    	
    	try {
    		
    		JSONObject jsonData = requestJSON.getJSONObject("data");
    		int id = Integer.parseInt(jsonData.getString("ponto_id"));
    		
    		Iterator<Segmento> iterator = segmentoList.iterator();
            while (iterator.hasNext()) {
                Segmento segmento = iterator.next();
                if (segmento.getOrigem().getId() == id || segmento.getDestino().getId() == id) {
                    iterator.remove(); // Remove o ponto de maneira segura
                    break; // Encerra o loop após remover o ponto
                }
            }
    		
    		Iterator<Ponto> iterator2 = pontoList.iterator();
            while (iterator2.hasNext()) {
                Ponto ponto = iterator2.next();
                if (ponto.getId() == id) {
                    iterator2.remove(); // Remove o ponto de maneira segura
                    break; // Encerra o loop após remover o ponto
                }
            }
    		    	
    		responseJson.put("error", "false");
    		responseJson.put("message", "Ponto removido com sucesso!");
    		responseJson.put("action", "excluir-ponto");
    		
    		return responseJson;
    		
    	} catch (JSONException e) {
    		System.out.println(e);
    	}
    	
    	return responseJson;
    }
    
    private JSONObject getListPonto(JSONObject requestJSON) {
    	
    	JSONObject responseJson = new JSONObject();
    	
    	try {
    		
    		JSONArray pontosArray = new JSONArray();
        	JSONObject data = new JSONObject();
        	    
        	for(Ponto ponto: pontoList) {
        		JSONObject pontoJson = new JSONObject();
        		pontoJson.put("id", ponto.getId());
        		pontoJson.put("name", ponto.getName());
        		pontoJson.put("obs", ponto.getObs());
        		pontosArray.put(pontoJson);
        	}
        	
        	data.put("pontos", pontosArray);
        	responseJson.put("data", data);
    		
    		responseJson.put("error", false);
    		responseJson.put("message", "Sucesso");
    		responseJson.put("action", "listar-pontos");
    		
    		return responseJson;
    		
    	} catch (JSONException e) {
    		System.out.println(e);
    	}
    	
    	return responseJson;
    }

    private JSONObject changePonto(JSONObject requestJSON) {
    	
    	JSONObject responseJson = new JSONObject();
    	
    	try {
    		
    		JSONObject jsonData = requestJSON.getJSONObject("data");
    		
    		
    		
    		int id = Integer.parseInt(jsonData.getString("ponto_id"));  		    		
    		
    		for(Ponto ponto: pontoList) {
    			if(ponto.getId() == id) {
    				ponto.setName(jsonData.getString("name"));
    				ponto.setObs(jsonData.getString("obs"));
    			}
    		}
    		
    		responseJson.put("error", false);
    		responseJson.put("message", "Ponto atualizado com sucesso!");
    		responseJson.put("action", "edicao-ponto");
    		
    		return responseJson;
    		
    	} catch (JSONException e) {
    		System.out.println(e);
    	}
    	
    	return responseJson;
    }

    private JSONObject changeRequestPonto(JSONObject requestJSON) {
    	
    	JSONObject responseJson = new JSONObject();
    	
    	try {
    		
    		JSONObject jsonData = requestJSON.getJSONObject("data");
    		int id = Integer.parseInt(jsonData.getString("ponto_id"));  		    		

    		JSONObject jsonPonto = new JSONObject();
    		JSONObject responseData = new JSONObject();
    		
    		for(Ponto ponto: pontoList) {
        		if(ponto.getId() == id) {
        			jsonPonto.put("id", id);
        			jsonPonto.put("name", ponto.getName());
        			jsonPonto.put("obs", ponto.getObs());
        		}
        	}
    		
    		responseData.put("ponto", jsonPonto);
    		responseJson.put("data", responseData);
    		
    		responseJson.put("error", "false");
    		responseJson.put("message", "Sucesso");
    		responseJson.put("action", "pedido-edicao-ponto");
    		
    		return responseJson;
    		
    	} catch (JSONException e) {
    		System.out.println(e);
    	}
    	
    	return responseJson;
    }
    
    private JSONObject cadastrarPonto(JSONObject requestJSON) {
    	
    	JSONObject responseJson = new JSONObject();
    	
    	try {
    		
    		JSONObject jsonData = requestJSON.getJSONObject("data");
    	
    		if(!jsonData.has("name") || !jsonData.has("obs")) {
    			responseJson.put("error", true);
        		responseJson.put("message", "Informações insuficientes");
        		responseJson.put("action", "edicao-ponto");
        		
        		return responseJson;
    		}
    		
    		String name = jsonData.getString("name");
    		String obs = jsonData.getString("obs");
    	
    		Ponto ponto = new Ponto(idPonto,name,obs);
    		pontoList.add(ponto);
    		idPonto++;
    		
    		responseJson.put("error", "false");
    		responseJson.put("message", "Ponto de referência cadastrado com sucesso!");
    		responseJson.put("action", "cadastro-ponto");
    		
    		return responseJson;
    		
    	} catch (JSONException e) {
    		System.out.println(e);
    	}
    	
    	
    	//System.out.println("Teste função de criação de Segmento");
    	
    	return responseJson;
    }
    
    private JSONObject setSegmento(JSONObject requestJSON) {
    	
    	JSONObject responseJson = new JSONObject();
    	
    	try {
    		
    		JSONObject jsonData = requestJSON.getJSONObject("data");
    		JSONObject jsonSegmento = jsonData.getJSONObject("segmento");
    		int idOrigem = Integer.parseInt(jsonSegmento.getJSONObject("ponto_origem").getString("id"));
    		
    		Ponto origem = null, destino = null;
    		    	
    		if(!jsonSegmento.has("direcao") || !jsonSegmento.has("obs") || !jsonSegmento.has("distancia")) {
    			responseJson.put("error", true);
        		responseJson.put("message", "Informações insuficientes");
        		responseJson.put("action", "edicao-ponto");
        		
        		return responseJson;
    		}
    		
    		for(Ponto ponto : pontoList) {
    			if(idOrigem == ponto.getId()) {
    				origem = ponto;
    			}
    		}
    		
    		int idDestino= Integer.parseInt(jsonSegmento.getJSONObject("ponto_destino").getString("id"));
    		
    		for(Ponto ponto : pontoList) {
    			if(idDestino == ponto.getId()) {
    				destino = ponto;
    			}
    		}
    		
    		String direcao = jsonSegmento.getString("direcao");
    		String distancia = jsonSegmento.getString("distancia");
    		String obs = jsonSegmento.getString("obs");
    		boolean bloqueado = jsonSegmento.getBoolean("bloqueado");
    		
    		Segmento segmento = new Segmento(idSegmento,origem,destino,direcao,distancia,obs,bloqueado);
    		segmentoList.add(segmento);
    		idSegmento++;
   	
    		responseJson.put("message", "Segmento cadastrado com sucesso!");
            responseJson.put("error", false);
            responseJson.put("action", "cadastro-segmento");
    		
    	} catch (JSONException e) {
    		System.out.println(e);
    	}
    	
    	
    	System.out.println("Teste função de criação de Segmento");
    	
    	return responseJson;
    }

    private JSONObject changeRequestSegmento(JSONObject requestJSON) {
    	
    	JSONObject responseJson = new JSONObject();
    	
    	try {
    		
    		JSONObject jsonData = requestJSON.getJSONObject("data");
    		JSONObject jsonSegmento = new JSONObject();
    		
    		int id = Integer.parseInt(jsonData.getString("segmento_id"));
    		
    		for(Segmento segmento : segmentoList) {
    			if(id == segmento.getId()) {
    				
    				JSONObject origem = new JSONObject();
    				origem.put("id", segmento.getOrigem().getId() );
    				origem.put("name", segmento.getOrigem().getName());
    				origem.put("obs", segmento.getOrigem().getObs());
    				
    				JSONObject destino = new JSONObject();
    				destino.put("id",segmento.getDestino().getId() );
    				destino.put("name", segmento.getDestino().getName());
    				destino.put("obs", segmento.getDestino().getObs());  				
    				
    				jsonSegmento.put("id", segmento.getId());
    				jsonSegmento.put("direcao", segmento.getDirecao());
    				jsonSegmento.put("distancia", Integer.parseInt(segmento.getDistancia()));
    				jsonSegmento.put("obs", segmento.getObs());
    				jsonSegmento.put("bloqueado", segmento.getBloqueado());
    				
    				jsonSegmento.put("ponto_origem", origem);
    				jsonSegmento.put("ponto_destino", destino);
    				
    			}
    		}
    		
    		jsonData.remove("segmento_id");
    		jsonData.remove("token");
    		jsonData.put("segmento", jsonSegmento);
    		responseJson.put("data", jsonData);
    		
    		responseJson.put("action", "pedido-edicao-segmento");
    		responseJson.put("error", false);
    		responseJson.put("message", "Sucesso");
    		
    	} catch (JSONException e) {
    		System.out.println(e);
    	}
    	
    	return responseJson;
    }
    
    private JSONObject changeSegmento(JSONObject requestJSON) {
    	
    	JSONObject responseJson = new JSONObject();
    	
    	try {
    		
    		
    		JSONObject jsonData = requestJSON.getJSONObject("data");
    		JSONObject jsonSegmento = jsonData.getJSONObject("segmento");


    		int id = Integer.parseInt(jsonData.getString("segmento_id"));
    		
    		
    		
    		for(Segmento segmento : segmentoList) {
    			if(id == segmento.getId()) {
    				
    				segmento.setDirecao(jsonSegmento.getString("direcao"));
    				segmento.setDistancia(jsonSegmento.getString("distancia"));
    				segmento.setObs(jsonSegmento.getString("obs"));
    				segmento.setBloqueado(jsonSegmento.getBoolean("bloqueado"));
    				
    			}
    		}

    		responseJson.put("action", "edicao-segmento");
    		responseJson.put("error", false);
    		responseJson.put("message", "Segmento atualizado com sucesso!");
    		
    	} catch (JSONException e) {
    		System.out.println(e);
    	}
    	
    	return responseJson;
    }

    private JSONObject getListSegmento(JSONObject requestJSON) {
    	
    	JSONObject responseJson = new JSONObject();
    	
    	JSONArray segmentosArray = new JSONArray();
    	
    	try {
    		
    		JSONObject jsonData = new JSONObject();
    		//JSONObject jsonSegmento = jsonData.getJSONObject("segmento");
    		
    		//int id = Integer.parseInt(jsonData.getString("segmento_id"));
    		
    		for(Segmento segmento : segmentoList) {
    			JSONObject segmentoJson = new JSONObject();
    			
    			segmentoJson.put("id", segmento.getId());
    			segmentoJson.put("direcao", segmento.getDirecao());
    			segmentoJson.put("distancia", Integer.parseInt(segmento.getDistancia()));
    			segmentoJson.put("obs", segmento.getObs());
    			segmentoJson.put("bloqueado", segmento.getBloqueado());
    			
    			JSONObject jsonOrigem = new JSONObject();
    			jsonOrigem.put("id", segmento.getOrigem().getId());
    			jsonOrigem.put("name", segmento.getOrigem().getName());
    			jsonOrigem.put("obs", segmento.getOrigem().getObs());
    			
    			JSONObject jsonDestino = new JSONObject();
    			jsonDestino.put("id", segmento.getDestino().getId());
    			jsonDestino.put("name", segmento.getDestino().getName());
    			jsonDestino.put("obs", segmento.getDestino().getObs());
    			
    			segmentoJson.put("ponto_origem", jsonOrigem);
    			segmentoJson.put("ponto_destino", jsonDestino);
    			
    			segmentosArray.put(segmentoJson);
    		
    		}
    		
    		jsonData.put("segmentos", segmentosArray);
    		responseJson.put("data", jsonData);
    		
    		responseJson.put("action", "listar-segmentos");
    		responseJson.put("error", false);
    		responseJson.put("message", "Sucesso");
    		
    	} catch (JSONException e) {
    		System.out.println(e);
    	}
    	
    	return responseJson;
    }

    private JSONObject excluirSegmento(JSONObject requestJSON) {
    	
    	JSONObject responseJson = new JSONObject();
    	
    	try {
    		
    		JSONObject jsonData = requestJSON.getJSONObject("data");
    		int id = Integer.parseInt(jsonData.getString("segmento_id"));
    		
    		Iterator<Segmento> iterator = segmentoList.iterator();
            while (iterator.hasNext()) {
                Segmento segmento = iterator.next();
                if (id == segmento.getId()) {
                    iterator.remove();
                    break; // Encerra o loop após remover o segmento
                }
            }
    		
    		responseJson.put("action", "excluir-segmento");
    		responseJson.put("error", false);
    		responseJson.put("message", "Segmento removido com sucesso!");
    		
    	} catch (JSONException e) {
    		System.out.println(e);
    	}
    	
    	return responseJson;
    }
    
    private JSONObject getListUsers(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        //System.out.println("Entrei Lista de usuarios");
        try {
        	
        	JSONArray users = new JSONArray();
        	JSONObject data = new JSONObject();
        	
        	//String token = requestJson.getJSONObject("data").getString("token");
        	
        	responseJson.put("message", "Sucesso");
            responseJson.put("error", false);
            responseJson.put("action", "listar-usuarios");
        	
        	for(User user: userList) {
        		JSONObject usuario = new JSONObject();
        		usuario.put("id", user.getId());
        		usuario.put("name", user.getName());
        		usuario.put("type", user.getType());
        		usuario.put("email", user.getEmail());
        		users.put(usuario);
        	}
        	responseJson.put("data", data);
        	data.put("users", users);        
        }
        catch(JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }
    
    private JSONObject getChangeUser(JSONObject requestJson) {
    	JSONObject responseJson = new JSONObject();
    	System.out.println("Entrei Mudança de usuario");
    	try {
    		
    		int id;
    		JSONObject jsonData = requestJson.getJSONObject("data");
    		
    		if(requestJson.getString("action").equals("edicao-usuario")) {
    			id = Integer.parseInt(jsonData.getString("user_id"));
    			responseJson.put("action",requestJson.getString("action"));
    		} else {
    			id = Integer.parseInt(jsonData.getString("id"));
    			responseJson.put("action",requestJson.getString("action"));
    		}
    		
    		//pesquisar usuario e atualizar
    		for(User user : userList) {
    			if(user.getId() == id) {
    				if(!jsonData.getString("name").isBlank() && !jsonData.isNull("name")) {
    					user.setName(jsonData.getString("name"));
    				}
    				if(!jsonData.getString("email").isBlank() && !jsonData.isNull("email")) {
    					user.setEmail(jsonData.getString("email"));
    				}
    				if(!jsonData.getString("password").isBlank() && !jsonData.isNull("password")) {
    					String hspw = hash(jsonData.getString("password"));
    					user.setPassword(hspw);
    				}
    				
    				responseJson.put("error", false);
    				responseJson.put("message", "Usuário atualizado com sucesso!");
    				
    				return responseJson;
    			}
    		}
    		
    		responseJson.put("error", true);
			responseJson.put("message", "Usuário não encontrado!");
			
			return responseJson;
    		
    	}
    	catch(JSONException e) {
    		System.out.println(e.toString());
    	}
    	
    	return responseJson;
    }
    
    private JSONObject getDeleteUser(JSONObject requestJson) {
    	JSONObject responseJson = new JSONObject();
    	try {
    		if(requestJson.getString("action").equals("excluir-usuario")) {
    			for(User user : userList) {
    				if(user.getId() == Integer.parseInt(requestJson.getJSONObject("data").getString("user_id"))) {
    					userList.remove(user);
    					
    					responseJson.put("error", false);
    					responseJson.put("message", "Usuário removido com sucesso!");
    					responseJson.put("action", "excluir-usuario");
    					
    					return responseJson;
    				}
    			}
    		} else {
    			for(User user : userList) {
    				if(requestJson.getJSONObject("data").getString("token").equals(user.getToken())) {
    					userList.remove(user);
    					
    					responseJson.put("error", false);
    					responseJson.put("message", "Usuário removido com sucesso!");
    					responseJson.put("action", "excluir-proprio-usuario");
    					
    					return responseJson;
    				}
    			}
    		}
    		
    		responseJson.put("error", true);
			responseJson.put("message", "Usuario nao encontrado!");
			responseJson.put("action", "excluir-proprio-usuario");
    		
    	} catch (JSONException e) {
    		System.out.println(e.toString());
    	}
    	return responseJson;
    }
    
    private JSONObject getLogoutResponse(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        try {
        	JSONObject requestData = requestJson.getJSONObject("data");
            responseJson.put("action", requestJson.has("logout"));
            responseJson.put("error", false);
            responseJson.put("message", "Logout efetuado com sucesso");
            
            for(User user : userList) {
            	if(user.getToken() == requestData.getString("token")) { 
            		user.setToken("");
            	}
            }
            
        }
        catch(JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }

    private JSONObject getLoggedUserData(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        try {
            String token = requestJson.getJSONObject("data").getString("token");
            for (User user: userList) {
                if(user.getToken().equals(token)) {
                    JSONObject data = new JSONObject();
                    JSONObject jasonuser = new JSONObject();
                    data.put("user", jasonuser);
                    jasonuser.put("id", user.getId());
                    jasonuser.put("name", user.getName());
                    jasonuser.put("type", user.getType());
                    jasonuser.put("email", user.getEmail());

                    responseJson.put("action", "pedido-proprio-usuario");
                    responseJson.put("error", false);
                    responseJson.put("message", "Sucesso");
                    responseJson.put("data", data);
                    return responseJson;
                }
            }

            responseJson.put("action", "pedido-proprio-usuario");
            responseJson.put("error", true);
            responseJson.put("message", "Sessão falhou!");
        }
        catch (JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }
    private JSONObject getRegisterResponse(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        try {
            String name = requestJson.getJSONObject("data").getString("name");
            String email = requestJson.getJSONObject("data").getString("email");
            String password = requestJson.getJSONObject("data").getString("password");
            String hashedpw = hash(password);
            int id = idCounter++;
            System.out.println("PW: " + password+ " || HASH: " + hashedpw);

            // Check if there is a user in the userList with the provided email and password
            for (User user : userList) {
                if (user.getEmail().equals(email)) {
                    // Return a successful login response with the user's token
                    JSONObject data = new JSONObject();
                    responseJson.put("action", "autocadastro-usuario");
                    responseJson.put("error", true);
                    responseJson.put("message", "email já cadastrado!");

                    return responseJson;
                }
            }

            // If no matching user is found, register user and return success message
            User newUser = new User(id, name , email, hashedpw, false, generateToken(id,false));
            System.out.println(newUser.toString());
            userList.add(newUser);
            responseJson.put("action", "autocadastro-usuario");
            responseJson.put("error", false);
            responseJson.put("message", "Usuário cadastrado com sucesso");
        }
        catch (JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }
    
    private JSONObject getLoginResponse(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        try {
            String email = requestJson.getJSONObject("data").getString("email");
            String password = requestJson.getJSONObject("data").getString("password");

            // Check if there is a user in the userList with the provided email and password
            for (User user : userList) {
                if (user.getEmail().equals(email) && BCrypt.checkpw(password,user.getPassword()) ) {
                    // Return a successful login response with the user's token
                    user.setToken(generateToken(user.getId(),user.isAdmin()));
                    JSONObject data = new JSONObject();
                    data.put("token", user.getToken());

                    responseJson.put("action", "login");
                    responseJson.put("error", false);
                    responseJson.put("message", "logado com sucesso");
                    responseJson.put("data", data);

                    return responseJson;
                }
            }

            // If no matching user is found, return an error response
            responseJson.put("action", "login");
            responseJson.put("error", true);
            responseJson.put("message", "Credenciais incorretas");
        }
        catch (JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }

    public String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public String generateToken(int id, boolean isAdmin) {
        return Jwts.builder()
                .claim("user_id", id)
                .claim("admin", isAdmin)
                .setSubject(Integer.toString(id))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    private class ServerService extends Service<Void> {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() {
                    int port = Integer.parseInt(serverPort.getText());
                    try (ServerSocket serverSocket = new ServerSocket(port)) {
                        System.out.println("Servidor na porta " + port);

                        while (acceptingConnections) {
                            Socket clientSocket = serverSocket.accept();
                            System.out.println("Cliente conectou");

                            threadPool.execute(() -> {
                                try {
                                    InputStream in = clientSocket.getInputStream();
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                                    String message;
                                    while ((message = reader.readLine()) != null) {
                                        System.out.println("Recebida do cliente: " + message);

                                        String response = getResponse(message);

                                        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                                        writer.println(response);

                                        if (message.equals("EOF")) {
                                            break;
                                        }
                                    }

                                    clientSocket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
        }
    }

    @FXML
    protected void onStopButtonClick() {
        acceptingConnections = false;
    }
}
