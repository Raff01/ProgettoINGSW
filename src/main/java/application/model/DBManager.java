package application.model;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.sqlite.SQLiteConfig;

import javafx.scene.image.Image;

public class DBManager {
	private static DBManager dbManager;
	private Connection connection;

	private DBManager() {
	}

	public static DBManager getInstance() {
		if (dbManager == null)
			dbManager = new DBManager();
		return dbManager;
	}

	public boolean startConnection() {
		String url = "jdbc:sqlite:dbProg.db";
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		try {
			connection = DriverManager.getConnection(url, config.toProperties());
			connection.setAutoCommit(true);
			if (connection != null && !connection.isClosed())
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public boolean isEnstablished() {
		try {
			if (connection != null && !connection.isClosed()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public void closedConnection() {
		if (isEnstablished()) {
			try {
				connection.close();
				connection = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public Profile getProfile(String us) {
		if (!isEnstablished()) {
			return null;
		}
		String query = "select * from Profile where Username=?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, us);
			ResultSet result = statement.executeQuery();
			if (result.isClosed()) {
				return null;
			}
			Profile p = new Profile();
			p.setUsername(us);
			p.setPassword(result.getString("Password"));
			p.setTypeProfile(result.getBoolean("VIP"));
			p.setDate(result.getString("Date"));
			p.setTypeVIP(result.getBoolean("TypeVIP"));
			statement.close();
			return p;
		} catch (SQLException e) {
			return null;
		}
	}

	public void insertProfile(Profile p) {
		if (!isEnstablished())
			return;
		if (!p.isTypeProfile()) {
			String query = "INSERT INTO Profile (Username, Password, VIP) VALUES (?,?,?);";
			try {
				PreparedStatement statement = connection.prepareStatement(query);
				statement.setString(1, p.getUsername());
				statement.setString(2, BCrypt.hashpw(p.getPassword(), BCrypt.gensalt(12)));
				statement.setBoolean(3, p.isTypeProfile());
				statement.executeUpdate();
				statement.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} else {
			String query = "INSERT INTO Profile (Username, Password, VIP, Date, TypeVIP) VALUES (?,?,?,?,?);";
			try {
				PreparedStatement statement = connection.prepareStatement(query);
				statement.setString(1, p.getUsername());
				statement.setString(2, BCrypt.hashpw(p.getPassword(), BCrypt.gensalt(12)));
				statement.setBoolean(3, p.isTypeProfile());
				statement.setString(4, p.getDate());
				statement.setBoolean(5, p.isTypeVIP());
				statement.executeUpdate();
				statement.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void updateDate(Profile p) {
		if (!isEnstablished())
			return;
		if (p.isTypeProfile()) {
			String query = "UPDATE Profile set Date=? where Username=?";
			try {
				PreparedStatement statement = connection.prepareStatement(query);
				statement.setString(1, LocalDate.now().toString());
				statement.setString(2, p.getUsername());
				statement.executeUpdate();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public String getDate(Profile p) {
		if (!isEnstablished())
			return null;
		String query = "Select Date from Profile where Username=?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, p.getUsername());
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return null;
			String date = result.getString("Date");
			statement.close();
			return date;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void removeDate(Profile p) {
		if (!isEnstablished())
			return;
		String query = "update Profile set Date=? where Username=?;";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setNull(1, java.sql.Types.VARCHAR);
			statement.setString(2, p.getUsername());
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean updateTypeProfile(Profile p) {
		if (!isEnstablished()) {
			return false;
		}
		String query = "Update Profile set VIP=? where Username=?;";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setBoolean(1, p.isTypeProfile());
			statement.setString(2, p.getUsername());
			statement.executeUpdate();
			statement.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateTypeVIP(Profile p, boolean type) {
		if (!isEnstablished())
			return false;
		String query = "Update Profile set TypeVIP=? where Username=?;";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setBoolean(1, type);
			statement.setString(2, p.getUsername());
			statement.executeUpdate();
			statement.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updatePassword(Profile p, String currentPassword, String newPass) {
		if (!isEnstablished())
			return false;
		String query = "UPDATE Profile set Password=? where Username=?;";
		if (!BCrypt.checkpw(currentPassword, p.getPassword()))
			return false;
		if (BCrypt.checkpw(newPass, p.getPassword()))
			return false;
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			String pass = BCrypt.hashpw(newPass, BCrypt.gensalt(12));
			statement.setString(1, pass);
			statement.setString(2, p.getUsername());
			statement.executeUpdate();
			statement.close();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public void changePassword(String us, String pass) {
		if (!isEnstablished())
			return;
		String query = "UPDATE Profile set Password=? where Username=?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			String psw = BCrypt.hashpw(pass, BCrypt.gensalt(12));
			statement.setString(1, psw);
			statement.setString(2, us);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getPassword(String username) {
		if (!isEnstablished())
			return null;
		String query = "Select Password from Profile where Username=?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);
			ResultSet result = statement.executeQuery();
			String pass = result.getString(1);
			statement.close();
			return pass;
		} catch (SQLException e) {
			return null;
		}
	}

	public void insertUser(User e) {
		if (!isEnstablished())
			return;
		String query = "INSERT INTO User (Email, Name, Surname, BirthDate, Username) VALUES (?,?,?,?,?);";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, e.getEmail());
			statement.setString(2, e.getName());
			statement.setString(3, e.getSurname());
			statement.setString(4, e.getBirthDate());
			statement.setString(5, e.getUsername());
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public User getUser(String username) {
		if (!isEnstablished()) {
			return null;
		}
		String query = "select * from User where Username=?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return null;
			User u = new User();
			u.setUsername(username);
			u.setEmail(result.getString("Email"));
			u.setName(result.getString("Name"));
			u.setSurname(result.getString("Surname"));
			u.setBirthDate(result.getString("BirthDate"));
			statement.close();
			return u;
		} catch (SQLException e) {
			return null;
		}
	}

	public boolean updateName(User u, String name) {
		if (!isEnstablished())
			return false;
		String query = "UPDATE User set Name=? where Username=?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, name);
			statement.setString(2, u.getUsername());
			statement.executeUpdate();
			statement.close();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public boolean updateSurname(User u, String surname) {
		if (!isEnstablished())
			return false;
		String query = "UPDATE User set Surname=? where Username=?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, surname);
			statement.setString(2, u.getUsername());
			statement.executeUpdate();
			statement.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getEmail(String username) {
		if (!isEnstablished())
			return null;
		String query = "Select Email from User where Username=?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return null;
			String email = result.getString("Email");
			statement.close();
			return email;
		} catch (SQLException e) {
			return null;
		}
	}

	public String getUsername(String email) {
		if (!isEnstablished())
			return null;
		String query = "Select Username from User where Email=?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, email);
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return null;
			String us = result.getString("Username");
			statement.close();
			return us;
		} catch (SQLException e) {
			return null;
		}
	}

	public ArrayList<String> getAllEmail() {
		if (!isEnstablished())
			return null;
		String query = "Select Email from User";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return null;
			ArrayList<String> emails = new ArrayList<String>();
			while (result.next()) {
				emails.add(result.getString("Email"));
			}
			statement.close();
			return emails;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<String> getAllUsername() {
		if (!isEnstablished())
			return null;
		String query = "Select Username from User";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return null;
			ArrayList<String> usernames = new ArrayList<String>();
			while (result.next()) {
				usernames.add(result.getString("Username"));
			}
			statement.close();
			return usernames;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void removeProduct(int id, String us) {
		if (!isEnstablished())
			return;
		String query = "delete from Cart where IdProduct=? and Username=?;";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			statement.setString(2, us);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Image getImage(String name) {
		if (!isEnstablished())
			return null;
		String query = "Select Image from Product where Name=?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, name);
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return null;
			InputStream image = result.getBinaryStream("Image");
			Image img = new Image(image);
			return img;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public float getPriceProduct(int id, Profile p) {
		if (!isEnstablished())
			return -1;
		String query;
		if (p.isTypeProfile()) {
			query = "Select VIP_Price from Product where IdProduct=?";
		} else
			query = "Select Price from Product where IdProduct=?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return -1;
			float price;
			if (p.isTypeProfile())
				price = result.getFloat("VIP_Price");
			else
				price = result.getFloat("Price");
			statement.close();
			return price;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public ArrayList<Product> getProduct() {
		if (!isEnstablished())
			return null;
		String query = "Select * from Product";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return null;
			ArrayList<Product> products = new ArrayList<Product>();
			while (result.next()) {
				Product p = new Product(result.getInt("IdProduct"), result.getString("Name"),
						result.getString("Description"), result.getDouble("Price"), result.getDouble("VIP_Price"),
						result.getBinaryStream("Image"), result.getString("AnimalType"), result.getString("Type"));
				products.add(p);
			}
			statement.close();
			return products;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Product> getProductHome(int size) {
		if (!isEnstablished())
			return null;
		String query = "Select * from Product LIMIT ?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, size);
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return null;
			ArrayList<Product> products = new ArrayList<Product>();
			while (result.next()) {
				Product p = new Product(result.getInt("IdProduct"), result.getString("Name"),
						result.getString("Description"), result.getDouble("Price"), result.getDouble("VIP_Price"),
						result.getBinaryStream("Image"), result.getString("AnimalType"), result.getString("Type"));
				products.add(p);
			}
			statement.close();
			return products;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Product> getFoodForAnimalType(String animal, String type) {
		if (!isEnstablished())
			return null;
		String query = "select * from Product where AnimalType=? and Type=? union select * from Product where AnimalType=? and Type=?";
		try {
			PreparedStatement statement;
			statement = connection.prepareStatement(query);
			statement.setString(1, animal);
			statement.setString(2, "Cibo_Secco");
			statement.setString(3, animal);
			statement.setString(4, "Cibo_Umido");
			ResultSet result = statement.executeQuery();
			if (result.isClosed()) {
				statement.close();
				return null;
			}
			ArrayList<Product> products = new ArrayList<Product>();
			while (result.next()) {
				Product p = new Product(result.getInt("IdProduct"), result.getString("Name"),
						result.getString("Description"), result.getDouble("Price"), result.getDouble("VIP_Price"),
						result.getBinaryStream("Image"), result.getString("AnimalType"), result.getString("Type"));
				products.add(p);
			}
			statement.close();
			return products;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Product> getProductForAnimalType(String animal, String type) {
		if (!isEnstablished())
			return null;
		String query = "select * from Product where AnimalType=? and Type=?";
		try {
			PreparedStatement statement;
			statement = connection.prepareStatement(query);
			statement.setString(1, animal);
			statement.setString(2, type);
			ResultSet result = statement.executeQuery();
			if (result.isClosed()) {
				statement.close();
				return null;
			}
			ArrayList<Product> products = new ArrayList<Product>();
			while (result.next()) {
				Product p = new Product(result.getInt("IdProduct"), result.getString("Name"),
						result.getString("Description"), result.getDouble("Price"), result.getDouble("VIP_Price"),
						result.getBinaryStream("Image"), result.getString("AnimalType"), result.getString("Type"));
				products.add(p);
			}
			statement.close();
			return products;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Product> getProductToOrder(Order o) {
		if (!isEnstablished())
			return null;
		String query = "Select * from Product where IdProduct in (select IdProduct from OrderContent where IdOrder=?);";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, o.getIdOrder());
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return null;
			ArrayList<Product> products = new ArrayList<Product>();
			while (result.next()) {
				Product p = new Product(result.getInt("IdProduct"), result.getString("Name"),
						result.getString("Description"), result.getDouble("Price"), result.getDouble("VIP_Price"),
						result.getBinaryStream("Image"), result.getString("AnimalType"), result.getString("Type"));
				products.add(p);
			}
			statement.close();
			return products;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void insertOrder(Order o) {
		if (!isEnstablished())
			return;
		String query = "INSERT INTO [Order] (Date, State, Email, City, Address, CAP, Name, Surname, Price) VALUES (?,?,?,?,?,?,?,?,?);";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, o.getDateOrder());
			statement.setInt(2, o.getState());
			statement.setString(3, o.getEmail());
			statement.setString(4, o.getCity());
			statement.setString(5, o.getAddress());
			statement.setString(6, o.getCap());
			statement.setString(7, o.getName());
			statement.setString(8, o.getSurname());
			statement.setDouble(9, o.getPrice());
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteOrder(int idOrder) {
		if(!isEnstablished()) {
			return;
		}
		String query="Delete from OrderContent where IdOrder=?;";
		String query2="Delete from [Order] where Id=?;";
		try {
			PreparedStatement statement=connection.prepareStatement(query);
			statement.setInt(1, idOrder);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			PreparedStatement statement=connection.prepareStatement(query2);
			statement.setInt(1, idOrder);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Order getOrder(int idOrder) {
		if (!isEnstablished())
			return null;
		String query = "select * from [Order] where Id=?;";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, idOrder);
			ResultSet result = statement.executeQuery();
			if (result.isClosed()) {
				return null;
			}
			Order o = new Order(result.getInt("Id"), result.getString("Date"), result.getInt("State"),
					result.getString("Name"), result.getString("Surname"), result.getString("Email"),
					result.getString("City"), result.getString("Address"), result.getString("CAP"),
					result.getDouble("Price"));
			statement.close();
			return o;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void updateOrderState(int idOrder, int state) {
		if (!isEnstablished())
			return;
		String query = "UPDATE [Order] set State=? where Id=?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, state);
			statement.setInt(2, idOrder);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Date getDateOrder(int idOrder) {
		if (!isEnstablished())
			return null;
		String query = "select Date from [Order] where Id=?;";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, idOrder);
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return null;
			Date d = result.getDate("Date");
			return d;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Integer> getIdOrderList(Profile p) {
		if (!isEnstablished())
			return null;
		String query = "select * from OrderList where Username=?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, p.getUsername());
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return null;
			ArrayList<Integer> id = new ArrayList<Integer>();
			while (result.next()) {
				id.add(result.getInt("IdOrder"));
			}
			statement.close();
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void insertOrderToOrderList(String us, int id) {
		if (!isEnstablished())
			return;
		String query = "INSERT INTO OrderList(Username, IdOrder) values (?,?);";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, us);
			statement.setInt(2, id);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean addProductToCart(int id, Profile profile, int quantity) {
		if (!isEnstablished()) {
			return false;
		}
		String query = "INSERT INTO Cart (Username, IdProduct, Quantity) VALUES (?,?,?);";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, profile.getUsername());
			statement.setInt(2, id);
			statement.setInt(3, quantity);
			statement.executeUpdate();
			statement.close();
			return true;
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public ArrayList<Product> getProductCart(String username) {
		if (!isEnstablished()) {
			return null;
		}
		String query = "Select * from Product where IdProduct in (select IdProduct from Cart where Username=?);";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);
			ResultSet result = statement.executeQuery();
			if (result.isClosed()) {
				return null;
			}
			ArrayList<Product> products = new ArrayList<Product>();
			while (result.next()) {
				Product p = new Product(result.getInt("IdProduct"), result.getString("Name"),
						result.getString("Description"), result.getDouble("Price"), result.getDouble("VIP_Price"),
						result.getBinaryStream("Image"), result.getString("AnimalType"), result.getString("Type"));
				products.add(p);
			}
			statement.close();
			return products;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void insertProductToOrder(Product p, int id, int quantity) {
		if (!isEnstablished())
			return;
		String query = "INSERT INTO OrderContent(IdProduct, IdOrder, Quantity) values (?,?,?);";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, p.getId());
			statement.setInt(2, id);
			statement.setInt(3, quantity);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getLastIdOrder() {
		if (!isEnstablished())
			return -1;
		String query = "Select Id from [Order] ORDER BY Id DESC LIMIT 1;";
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return -1;
			int res = result.getInt("Id");
			statement.close();
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public ArrayList<Product> ascendingOrderProducts(String s, Profile profile) {
		if (!isEnstablished())
			return null;
		String query;
		if (!profile.isTypeProfile())
			query = "select * from Product where IdProduct IN (" + s + ") ORDER BY Price ASC;";
		else
			query = "select * from Product where IdProduct IN (" + s + ") ORDER BY VIP_Price ASC;";
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return null;
			ArrayList<Product> prod = new ArrayList<Product>();
			while (result.next()) {
				Product p = new Product(result.getInt("IdProduct"), result.getString("Name"),
						result.getString("Description"), result.getDouble("Price"), result.getDouble("VIP_Price"),
						result.getBinaryStream("Image"), result.getString("AnimalType"), result.getString("Type"));
				prod.add(p);
			}
			statement.close();
			return prod;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<Product> discendingOrderProducts(String s, Profile profile) {
		if (!isEnstablished())
			return null;
		String query;
		if (!profile.isTypeProfile())
			query = "select * from Product where IdProduct IN (" + s + ") ORDER BY Price DESC;";
		else
			query = "select * from Product where IdProduct IN (" + s + ") ORDER BY VIP_Price DESC;";
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return null;
			ArrayList<Product> prod = new ArrayList<Product>();
			while (result.next()) {
				Product p = new Product(result.getInt("IdProduct"), result.getString("Name"),
						result.getString("Description"), result.getDouble("Price"), result.getDouble("VIP_Price"),
						result.getBinaryStream("Image"), result.getString("AnimalType"), result.getString("Type"));
				prod.add(p);
			}
			statement.close();
			return prod;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<Product> alphabeticalOrderProducts(String s) {
		if (!isEnstablished())
			return null;
		String query = "select * from Product where IdProduct IN (" + s + ") ORDER BY Name ASC;";
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return null;
			ArrayList<Product> prod = new ArrayList<Product>();
			while (result.next()) {
				Product p = new Product(result.getInt("IdProduct"), result.getString("Name"),
						result.getString("Description"), result.getDouble("Price"), result.getDouble("VIP_Price"),
						result.getBinaryStream("Image"), result.getString("AnimalType"), result.getString("Type"));
				prod.add(p);
			}
			statement.close();
			return prod;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getQuantityToOrder(int idProduct, int idOrder) {
		if (!isEnstablished())
			return -1;
		String query = "select Quantity from OrderContent where IdProduct=? and IdOrder=?;";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, idProduct);
			statement.setInt(2, idOrder);
			ResultSet result = statement.executeQuery();
			if (result.isClosed()) {
				return -1;
			}
			int quant = result.getInt("Quantity");
			statement.close();
			return quant;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public void removeOrderToOrderList(Order order, String username) {
		if (!isEnstablished())
			return;
		String query = "delete from OrderList where IdOrder=? and Username=?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, order.getIdOrder());
			statement.setString(2, username);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void removeProductToCart(int idProd, Profile profile) {
		if (!isEnstablished())
			return;
		String query = "delete from Cart where IdProduct=? and Username=?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, idProd);
			statement.setString(2, profile.getUsername());
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getQuantity(int idProd, Profile profile) {
		if (!isEnstablished())
			return 0;
		String query = "select Quantity from Cart where IdProduct=? and Username=?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, idProd);
			statement.setString(2, profile.getUsername());
			ResultSet result = statement.executeQuery();
			if (result.isClosed()) {
				statement.close();
				return 0;
			}
			int quant = result.getInt("Quantity");
			statement.close();
			return quant;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public boolean updateQuantityCart(int idProd, Profile profile, int quantity) {
		if (!isEnstablished())
			return false;
		String query = "UPDATE Cart set Quantity=? where IdProduct=? and Username=? ";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, quantity);
			statement.setInt(2, idProd);
			statement.setString(3, profile.getUsername());
			statement.executeUpdate();
			statement.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void clearCart(User u) {
		if (!isEnstablished())
			return;
		String query = "delete from Cart where Username = ?;";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, u.getUsername());
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Product> getProductFavorites(String us) {
		if (!isEnstablished())
			return null;
		String query = "Select * from Product where IdProduct in (select IdProd from Favorites where Username=?);";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, us);
			ResultSet result = statement.executeQuery();
			if (result.isClosed())
				return null;
			ArrayList<Product> prod = new ArrayList<Product>();
			while (result.next()) {
				Product p = new Product(result.getInt("IdProduct"), result.getString("Name"),
						result.getString("Description"), result.getDouble("Price"), result.getDouble("VIP_Price"),
						result.getBinaryStream("Image"), result.getString("AnimalType"), result.getString("Type"));
				prod.add(p);
			}
			statement.close();
			return prod;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void addProductToFavorites(Product p, String us) {
		if (!isEnstablished()) {
			return;
		}
		String query = "INSERT INTO Favorites (Username, IdProd) VALUES (?,?);";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, us);
			statement.setInt(2, p.getId());
			statement.executeUpdate();
			statement.close();
			return;
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return;
	}

	public void removeProductFromFavorites(Product p, String us) {
		if (!isEnstablished())
			return;
		String query = "delete from Favorites where IdProd=? and Username=?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, p.getId());
			statement.setString(2, us);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}