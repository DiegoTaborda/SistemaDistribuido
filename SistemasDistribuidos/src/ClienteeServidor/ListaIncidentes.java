package ClienteeServidor;

import java.util.ArrayList;

public class ListaIncidentes {
	
	String rodovia;
	
	String data;
	
	int periodo;
	
	int km_inicial;
	
	int km_fim;
	
	ArrayList<ListaIncidentes> listaIncidentes;

	public ListaIncidentes(String rodovia, String data, int periodo, int km_inicial, int km_fim) {
		this.rodovia = rodovia;
		this.data = data;
		this.periodo = periodo;
		this.km_inicial = km_inicial;
		this.km_fim = km_fim;
		this.listaIncidentes = new ArrayList<ListaIncidentes>();
	}
	
	public ListaIncidentes() {
		 this.listaIncidentes = new ArrayList<ListaIncidentes>();
	}
	
	public ListaIncidentes(String rodovia, String data,int periodo) {
		this.rodovia = rodovia;
		this.data = data;
		this.periodo = periodo;
		this.listaIncidentes = new ArrayList<ListaIncidentes>();
	}
	
	public void adiocionarIncidente(String rodovia,String data,int periodo,int km_inicial,int km_fim) {
		ListaIncidentes incindente = new ListaIncidentes(rodovia, data, periodo, km_inicial, km_fim);
		
	 listaIncidentes.add(incindente);
		
	}
	
	public void adiocionarIncidente(String rodovia,String data,int periodo) {
		ListaIncidentes incindente = new ListaIncidentes(rodovia, data, periodo);
		
	 listaIncidentes.add(incindente);
		
	}
	
	
	

	public String getRodovia() {
		return rodovia;
	}

	public void setRodovia(String rodovia) {
		this.rodovia = rodovia;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public int getKm_inicial() {
		return km_inicial;
	}

	public void setKm_inicial(int km_inicial) {
		this.km_inicial = km_inicial;
	}

	public int getKm_fim() {
		return km_fim;
	}

	public void setKm_fim(int km_fim) {
		this.km_fim = km_fim;
	}
	
	
	
	

}
