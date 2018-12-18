package account_representations;

public class LoginCredentials {
	private String email;
	private String password;
	private long userId;
	public LoginCredentials(String email, String password, long userId){
		this.email = email;
		this.password = password;
		this.userId = userId;
	}
	public LoginCredentials(){
		
	}
	public String getPassword(){
		return password;
	}
	public String getEmail(){
		return email;
	}
	public void setUserId(long id) {
		this.userId = id;
	}
	public long getUserId() {
		return userId;
	}
}
