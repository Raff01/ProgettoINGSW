package application.model;

import java.io.InputStream;

public class Product {
	private int id;
	private String name;
	private String description;
	private double price;
	private double priceVIP;
	private InputStream imageProduct;
	private String typeAnimal;
	private String typeProduct;

	public Product(int id, String name, String description, double price, double priceVIP, InputStream imageProduct,
			String typeAnimal, String typeProduct) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.priceVIP = priceVIP;
		this.imageProduct = imageProduct;
		this.typeAnimal = typeAnimal;
		this.typeProduct = typeProduct;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public double getPrice() {
		return price;
	}

	public double getPriceVIP() {
		return priceVIP;
	}

	public InputStream getImageProduct() {
		return imageProduct;
	}

	public String getTypeAnimal() {
		return typeAnimal;
	}

	public String getTypeProduct() {
		return typeProduct;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (id != other.id)
			return false;
		return true;
	}
}