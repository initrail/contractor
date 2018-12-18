package message_representations;

/**
 * Created by Integrail on 7/13/2016.
 */

public class Message {
    private String messageId;
    private long id;
    public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	private long messageFrom;
    private String message;
    private long messageRead;
    private long timeSent;
    private long timeReceived;
    private boolean mms;
    private String jSessionId;
    private long userId;
    public boolean isMms() {
		return mms;
	}
	public void setMms(boolean mms) {
		this.mms = mms;
	}
	public String getjSessionId() {
		return jSessionId;
	}
	public void setjSessionId(String jSessionId) {
		this.jSessionId = jSessionId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public Message(){
    	id = 0;
    	messageRead = 0;
    }
    public Message(String message){
        this.message = message;
    }
    public Message(long userId) {
    	
    }
    public Message(String cId, long id, long mF, String m, long mR, long t, long t2){
        messageId = cId;
        this.id = id;
        messageFrom = mF;
        message = m;
        messageRead = mR;
        timeSent = t;
        timeReceived = t;
    }
    public long getTimeSent() {
		return timeSent;
	}
	public void setTimeSent(long timeSent) {
		this.timeSent = timeSent;
	}
	public long getTimeReceived() {
		return timeReceived;
	}
	public void setTimeReceived(long timeReceived) {
		this.timeReceived = timeReceived;
	}
    public String getMessageId(){
    	return messageId;
    }
	public void setConversationId(String conversationId) {
		this.messageId = conversationId;
	}
	public long getMessageRead(){
		return messageRead;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getMessageFrom() {
		return messageFrom;
	}
	public void setMessageFrom(long messageFrom) {
		this.messageFrom = messageFrom;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public long isMessageRead() {
		return messageRead;
	}
	public void setMessageRead(long messageRead) {
		this.messageRead = messageRead;
	}
}
