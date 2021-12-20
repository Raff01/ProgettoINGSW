package application.model;

public class OrderList {
	private int id;
	private String username;
	private int idOrder;

	public OrderList(int id, String username, int idOrder) {
		this.id = id;
		this.username = username;
		this.idOrder = idOrder;
	}

	public int getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public int getIdOrder() {
		return idOrder;
	}
}
