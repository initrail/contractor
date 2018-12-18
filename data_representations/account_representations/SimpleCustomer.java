package account_representations;


public class SimpleCustomer{
	private long userId;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String zip;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getFname() {
		return firstName;
	}
	public void setFname(String fname) {
		this.firstName = fname;
	}
	public String getLname() {
		return lastName;
	}
	public void setLname(String lname) {
		this.lastName = lname;
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
	public SimpleCustomer(String fname, String lname, String email, String phone, String zip){
		this.firstName= fname;
		this.lastName= lname;
		this.email = email;
		this.phone = phone;
		this.zip = zip;
	}
	public SimpleCustomer(){
		
	}
}
