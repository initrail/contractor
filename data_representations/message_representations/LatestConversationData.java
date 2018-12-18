package message_representations;

/**
 * Created by Integrail on 7/22/2016.
 */

public class LatestConversationData {
    private long conversationId;
	private String ogSender;
    private String senderName;
	private String ogReceiver;
	@SuppressWarnings("unused")
    private long userId;
    private String receiverName;
    private long count;
    private long oldCount;
    public LatestConversationData(){
    	count = 0;
    	oldCount = 0;
    }
    public LatestConversationData(long conversationId, long userId, String senderName, long count) {
    	this.conversationId = conversationId;
    	this.userId = userId;
    	this.senderName = senderName;
    	this.count = count;
    }
    public LatestConversationData(long c, String ogSender, String senderName, String ogReceiver, String receiverName, int i){
        conversationId = c;
        this.ogSender = ogSender;
        this.ogReceiver = ogReceiver;
        this.senderName = senderName;
        this.receiverName = receiverName;
        count = i;
    }
	public void setOgSender(String ogSender) {
		this.ogSender = ogSender;
	}
    public String getOgSender() {
		return ogSender;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getOgReceiver() {
		return ogReceiver;
	}
	public void setOgReceiver(String ogReceiver) {
		this.ogReceiver = ogReceiver;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
    public long getConversationId(){
        return conversationId;
    }
    public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public long getOldCount() {
		return oldCount;
	}
	public void setOldCount(long oldCount) {
		this.oldCount = oldCount;
	}
	public void setConversationId(long conversationId) {
		this.conversationId = conversationId;
	}
	public long getId(){
        return count;
    }
}
