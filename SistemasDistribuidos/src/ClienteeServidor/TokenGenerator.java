package ClienteeServidor;

import java.math.BigInteger;
import java.security.SecureRandom;

public class TokenGenerator {
	
	private SecureRandom random = new SecureRandom();

	  public String generateToken() {
	    // Gera um número aleatório grande o suficiente para alocar 128 bits de segurança
	    String token = new BigInteger(128, random).toString(32);

	    // Certifica-se de que o token tenha entre 16 e 36 caracteres
	    while (token.length() < 16 || token.length() > 36) {
	      token = new BigInteger(128, random).toString(32);
	    }

	    return token;
	  }
	

}
