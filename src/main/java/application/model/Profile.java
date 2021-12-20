package application.model;

public class Profile {
	private String username;
	private String password;
	private boolean typeProfile;
	private String date;
	private boolean typeVIP;

	public Profile() {
	}

	public Profile(String username, String password, boolean typeProfile) {
		super();
		this.username = username;
		this.password = password;
		this.typeProfile = typeProfile;
	}

	public Profile(String username, String password, boolean typeProfile, String date, boolean typeVIP) {
		super();
		this.username = username;
		this.password = password;
		this.typeProfile = typeProfile;
		this.date = date;
		this.typeVIP = typeVIP;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isTypeProfile() {
		return typeProfile;
	}

	public void setTypeProfile(boolean typeProfile) {
		this.typeProfile = typeProfile;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isTypeVIP() {
		return typeVIP;
	}

	public void setTypeVIP(boolean typeVIP) {
		this.typeVIP = typeVIP;
	}
}
