package validate;

public class InValid {
	private String inValidFirstName;
	private String inValidLastName;
	private String inValidEmail;
	private String inValidPassword;
	private String inValidZip;
	private String inValidPhone;
	private String inValidAddress;
	private String inValidSkills;
	private String projectFlag;
	private String projectName;
	private String projectAddress;
	private String projectPhone;
	private String projectBudget;
	private String projectDate;
	public String getProjectBudget() {
		return projectBudget;
	}
	public void setProjectBudget(String projectBudget) {
		this.projectBudget = projectBudget;
	}
	public String getProjectFlag() {
		return projectFlag;
	}
	public void setProjectFlag(String projectFlag) {
		this.projectFlag = projectFlag;
	}
	public String getProjectDate() {
		return projectDate;
	}
	public void setProjectDate(String projectDate) {
		this.projectDate = projectDate;
	}
	public InValid(String email, String password){
		inValidEmail = email;
		inValidPassword = password;
	}
	public InValid(String firstName, String lastName, String email,
			String password, String zip, String phone){
		inValidFirstName = firstName;
		inValidLastName = lastName;
		inValidEmail = email;
		inValidPassword = password;
		inValidZip = zip;
		inValidPhone = phone;
	}
	public InValid(String p, String n, String a, String ph, String b, String d, int v){
		projectFlag = p;
		setProjectName(n);
		setProjectAddress(a);
		setProjectPhone(ph);
		projectBudget = b;
		projectDate = d;
	}
	public InValid(String firstName, String lastName,
			String email, String password,
			String zip, String phone, String address, String skills){
		inValidFirstName = firstName;
		inValidLastName = lastName;
		inValidEmail = email;
		inValidPassword = password;
		inValidZip = zip;
		inValidPhone = phone;
		inValidAddress = address;
		inValidSkills = skills;
	}
	public InValid(String firstName, String lastName,
				   String email, String password,
				   String zip, String phone, String address){
		inValidFirstName = firstName;
		inValidLastName = lastName;
		inValidEmail = email;
		inValidPassword = password;
		inValidZip = zip;
		inValidPhone = phone;
		inValidAddress = address;
	}
	public String getInValidEmail(){ return inValidEmail; }
	public String getInValidPassword(){ return inValidPassword; }
	public String getInValidZip(){
		return inValidZip;
	}
	public String getInValidFirstName(){
		return inValidFirstName;
	}
	public String getInValidLastName(){ return inValidLastName; }
	public String getInValidPhone(){ return inValidPhone; }
	public String getInValidAddress() { return inValidAddress; }
	public String getInValidSkills() { return inValidSkills; }
	public String getInValidProjectFlag(){
		return projectFlag;
	}
	public String getProjectPhone() {
		return projectPhone;
	}
	public void setProjectPhone(String projectPhone) {
		this.projectPhone = projectPhone;
	}
	public String getProjectAddress() {
		return projectAddress;
	}
	public void setProjectAddress(String projectAddress) {
		this.projectAddress = projectAddress;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
