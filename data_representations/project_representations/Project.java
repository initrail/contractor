package project_representations;
public class Project {
    private int exteriorProjectFlag;
    private int interiorProjectFlag;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String aptNumber;
    private String lockBoxCode;
    private String budget;
    private String dateOfCompletion;
    private String projectDescription;
    private String creator;
    private int id;
    private boolean finished = false;
    public int getExteriorProjectFlag() {
        return exteriorProjectFlag;
    }

    public void setExteriorProjectFlag(int exteriorProjectFlag) {
        this.exteriorProjectFlag = exteriorProjectFlag;
    }

    public int getInteriorProjectFlag() {
        return interiorProjectFlag;
    }

    public void setInteriorProjectFlag(int interiorProjectFlag) {
        this.interiorProjectFlag = interiorProjectFlag;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLockBoxCode() {
        return lockBoxCode;
    }

    public void setLockBoxCode(String lockBoxCode) {
        this.lockBoxCode = lockBoxCode;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getDateOfCompletion() {
        return dateOfCompletion;
    }

    public void setDateOfCompletion(String dateOfCompletion) {
        this.dateOfCompletion = dateOfCompletion;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getAptNumber() {
        return aptNumber;
    }

    public void setAptNumber(String aptNumber) {
        this.aptNumber = aptNumber;
    }


    public Project(int exteriorProjectFlag, int interiorProjectFlag, String fullName, String phoneNumber, String address, String aptNumber, String lockBoxCode, String budget, String dateOfCompletion, String projectDescription) {
        this.exteriorProjectFlag = exteriorProjectFlag;
        this.interiorProjectFlag = interiorProjectFlag;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.lockBoxCode = lockBoxCode;
        this.budget = budget;
        this.dateOfCompletion = dateOfCompletion;
        this.projectDescription = projectDescription;
        this.aptNumber = aptNumber;
    }
    public Project(){
    	interiorProjectFlag = 0;
    	exteriorProjectFlag = 0;
    	id = 0;
    }
    public Project(int exteriorProjectFlag, int interiorProjectFlag, String fullName, String phoneNumber, String address, String aptNumber, String lockBoxCode, String budget, String dateOfCompletion, String projectDescription, String creator, int id) {
        this.exteriorProjectFlag = exteriorProjectFlag;
        this.interiorProjectFlag = interiorProjectFlag;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.lockBoxCode = lockBoxCode;
        this.budget = budget;
        this.dateOfCompletion = dateOfCompletion;
        this.projectDescription = projectDescription;
        this.aptNumber = aptNumber;
        this.creator = creator;
        this.id = id;
    }
    public void setFinished(boolean f){
        finished = f;
    }
    public boolean getFinished(){
        return finished;
    }
    public void setId(int id){
    	this.id = id;
    }
    public void setCreator(String creator){
    	this.creator = creator;
    }
    public int getId(){
    	return id;
    }
    public String getCreator(){
    	return creator;
    }
}
