package sistemasdistribuidos.servidor;

public class Segmento {
	private int id;
	private Ponto origem;
	private Ponto destino;
    private String direcao;
    private String distancia;
    private String obs;
    private boolean bloqueado;

    public Segmento(int id,Ponto origem,Ponto destino, String direcao, String distancia, String obs, boolean bloqueado) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.direcao = direcao;
        this.distancia = distancia;
        this.obs = obs;
        this.bloqueado = bloqueado;
    }

    public boolean getBloqueado() {
    	return bloqueado;
    }
    
    public void setBloqueado(boolean bloqueado) {
    	this.bloqueado = bloqueado;
    }
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Ponto getOrigem() {
		return origem;
	}
	
	public void setOrigem(Ponto origem) {
		this.origem = origem;
	}
	
	public Ponto getDestino() {
		return destino;
	}
	
	public void setdestino(Ponto destino) {
		this.destino = destino;
	}

	public String getDirecao() {
		return direcao;
	}

	public void setDirecao(String direcao) {
		this.direcao = direcao;
	}

	public String getDistancia() {
		return distancia;
	}

	public void setDistancia(String distancia) {
		this.distancia = distancia;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	@Override
	public String toString() {
		return "Segmento [id=" + id + ", direcao=" + direcao + ", distancia=" + distancia + ", obs=" + obs + "]";
	}

}
