package application.model;

public class Favorites {
	private String username;
	private int idProd;
	
	public void Favorites(String username, int idProd) {
		this.username=username;
		this.idProd=idProd;
	}
	
	
	public int getIdProd() {
		return idProd;
	}
	public String getUsername() {
		return username;
	}
	
}
