package application.model;

public class Order {
	private int idOrder;
	private String dateOrder;
	private int state;
	private String name;
	private String surname;
	private String email;
	private String city;
	private String address;
	private String cap;
	private Double price;

	public Order(String dateOrder, int state, String email, String city, String address, String cap, String name,
			String surname, Double price) {
		super();
		this.name = name;
		this.surname = surname;
		this.dateOrder = dateOrder;
		this.state = state;
		this.email = email;
		this.city = city;
		this.address = address;
		this.cap = cap;
		this.price = price;
	}

	public Order(int idOrder, String dateOrder, int state, String name, String surname, String email, String city,
			String address, String cap, Double price) {
		super();
		this.idOrder = idOrder;
		this.dateOrder = dateOrder;
		this.state = state;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.city = city;
		this.address = address;
		this.cap = cap;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public int getIdOrder() {
		return idOrder;
	}

	public String getDateOrder() {
		return dateOrder;
	}

	public int getState() {
		return state;
	}

	public String getEmail() {
		return email;
	}

	public String getCity() {
		return city;
	}

	public String getAddress() {
		return address;
	}

	public String getCap() {
		return cap;
	}

	public Double getPrice() {
		return price;
	}

	public void setState(int state) {
		this.state = state;
	}
}
