package account_representations;

/**
 * Created by Integrail on 7/4/2016.
 */
public class ContractorAccountFinal extends Account{
    private String address;
    private String aptNumber;
    private int interiorSkills;
    private byte exteriorSkills;
    private byte licenses;
    private boolean insurance;
    private boolean complete;
    public ContractorAccountFinal(String fName, String lName,
                             String email, String phone,
                             String zip, String password,
                             String passwordCheck, String address,
                             String aptNumber, int iS, byte eS,
                             boolean insurance, byte licenses,
                             boolean complete) {
        super(fName, lName, email, phone, zip, password, passwordCheck);
        this.address = address;
        this.aptNumber = aptNumber;
        interiorSkills = iS;
        exteriorSkills = eS;
        this.insurance = insurance;
        this.licenses = licenses;
        this.complete = complete;
    }

    public String getFirstName() {
        return super.firstName;
    }

    public String getLastName() {
        return super.lastName;
    }

    public String getEmail() {
        return super.email;
    }

    public String getPhone() {
        return super.phone;
    }

    public String getZip() {
        return super.zip;
    }

    public String getPassword() {
        return super.password;
    }

    public String getPasswordCheck() {
        return super.passwordCheck;
    }

    public String getAddress() { 
    	return address; 
    }

    public String getAptNumber() {
    	return aptNumber; 
    }

    public int getInteriorSkills() {
    	return interiorSkills;
    }

    public int getExteriorSkills() {
    	return exteriorSkills;
    }

    public byte getLicenses() {
    	return licenses; 
    }

    public boolean getInsurance() {
    	return insurance; 
    }
    
    public boolean isComplete(){
    	return complete;
    }
}
