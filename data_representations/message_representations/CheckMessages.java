package message_representations;

public class CheckMessages {
	private String jSessionId;
	private long userId;
	private boolean checkMT;
	private boolean setRead;
	private boolean updatePic;
	private String email;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isUpdatePic() {
		return updatePic;
	}
	public void setUpdatePic(boolean updatePic) {
		this.updatePic = updatePic;
	}
	private int servletCount;
	public CheckMessages(String jSessionId, long userId, boolean checkMT, boolean setRead, int servletCount){
		this.jSessionId = jSessionId;
		this.userId = userId;
		this.checkMT = checkMT;
		this.servletCount = servletCount;
		this.setRead = setRead;
	}
	public boolean isSetRead() {
		return setRead;
	}
	public void setSetRead(boolean setRead) {
		this.setRead = setRead;
	}
	public int getServletCount() {
		return servletCount;
	}
	public void setServletCount(int servletCount) {
		this.servletCount = servletCount;
	}
	public CheckMessages(){
		checkMT = false;
	}
	public String getjSessionId() {
		return jSessionId;
	}
	public void setjSessionId(String jSessionId) {
		this.jSessionId = jSessionId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long email) {
		this.userId = email;
	}
	public boolean getCheckMT() {
		return checkMT;
	}
	public void setCheckMT(boolean checkMT) {
		this.checkMT = checkMT;
	}
	
}
