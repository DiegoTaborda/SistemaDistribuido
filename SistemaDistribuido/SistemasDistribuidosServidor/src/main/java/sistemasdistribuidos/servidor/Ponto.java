package sistemasdistribuidos.servidor;
import org.json.JSONObject;
import org.json.JSONException;

public class Ponto {
	private int id;
    private String name;
    private String obs;
    
    public Ponto(int id, String name, String obs) {
        this.id = id;
        this.name = name;
        this.obs = obs;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	@Override
	public String toString() {
		return "Ponto [id=" + id + ", name=" + name + ", obs=" + obs + "]";
	}

	public JSONObject toJson() {
		
		JSONObject jsonPonto = new JSONObject();
			
		try {
			jsonPonto.put("id", this);
			jsonPonto.put("name", this);
			jsonPonto.put("obs", this);
			
		} catch (JSONException e) {
			System.out.println(e);
		}
		
		return jsonPonto;
	}
}
