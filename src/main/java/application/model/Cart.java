package application.model;

import java.util.ArrayList;

public class Cart {
	private ArrayList<Integer> idProducts;
	private String username;
	private int quantity;
	public Cart() {
		idProducts = new ArrayList<Integer>();
	}

	public ArrayList<Integer> getIdProducts() {
		return idProducts;
	}

	public String getUsername() {
		return username;
	}

	public int getQuantity() {
		return quantity;
	}
}
