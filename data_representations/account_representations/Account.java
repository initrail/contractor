package account_representations;

public class Account{
	protected String firstName;
	protected String lastName;
	protected String email;
	protected String phone;
	protected String zip;
	protected String password;
	protected String passwordCheck;
	public Account(String fname, String lname, String email, String phone, String zip, String password, String passwordCheck){
		firstName = fname;
		lastName = lname;
		this.email = email;
		this.phone = phone;
		this.zip = zip;
		this.password = password;
		this.passwordCheck = passwordCheck;
	}
	public Account(){
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPasswordCheck() {
		return passwordCheck;
	}
	public void setPasswordCheck(String passwordCheck) {
		this.passwordCheck = passwordCheck;
	}
}