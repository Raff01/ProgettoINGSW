package application.model;


public class User {
	private String email;
	private String name;
	private String surname;
	private String birthDate;
	private String username;

	public User() {
	}

	public User(String email, String name, String surname, String birthDate, String username) {
		super();
		this.email = email;
		this.name = name;
		this.surname = surname;
		this.birthDate = birthDate;
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}