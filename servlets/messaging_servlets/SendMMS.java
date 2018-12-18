package messaging_servlets;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import SQLManager.SQLManager;
import accountmanager.SessionManager;
import constants.FileSystemConstants;
import message_representations.Message;

public class SendMMS extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1112726643182060403L;
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		SessionManager check = new SessionManager(req, resp);
		if(check.validSession()){
			String fileName = uploadImage(req, check.getUserId());
			DataOutputStream writer = new DataOutputStream(resp.getOutputStream());
			writer.writeBytes(fileName);
			writer.flush();
			writer.close();
		}
	}
	public String uploadImage(HttpServletRequest req, long userId){
		String fileName = null;
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024*30);
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setFileSizeMax(1024*1024*40);
		upload.setSizeMax(1024*1024*50);
		File uploadDir = new File(FileSystemConstants.MESSAGE_IMAGES_PATH);
		if(!uploadDir.exists()){
			uploadDir.mkdir();
		}
		try{
			List<?> formItems = upload.parseRequest(req);
			Iterator<?> it = formItems.iterator();
			while(it.hasNext()){
				FileItem item = (FileItem) it.next();
				if(!item.isFormField()){
					String conversationId = null;
					fileName = new File(item.getName()).getName();
					Matcher m = Pattern.compile("^\\d+-").matcher(fileName);
					if(m.find()){
						fileName = fileName.substring(m.group().length());
						conversationId = m.group().replaceAll("-", "");
					}
					String filePath = FileSystemConstants.MESSAGE_IMAGES_PATH+File.separator+conversationId+File.separator;
					File msgDir = new File(filePath);
					if(!msgDir.exists()){
						msgDir.mkdir();
					}
					System.out.println("SendMMS servlet\nfilePath: "+filePath);
					String id = insertMMS(userId, req, fileName, Long.valueOf(conversationId));
					fileName = id+fileName;
					filePath = filePath+fileName;
					File storeFile = new File(filePath);
					item.write(storeFile);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return fileName;
	}
	public String insertMMS(long userId, HttpServletRequest request, String fileName, long conversationId){
		String messageId = "";
		long timeSent = 0;
		String selectOthers = "SELECT userId FROM Recipients where conversationId = ?";
		//"SELECT CASE WHEN ogSender = ? THEN ogReceiver WHEN ogReceiver = ? THEN ogSender END AS messageTo FROM conversations WHERE conversationId = ?";
		String insertC = "INSERT INTO conversations(conversationId, count) VALUES (?, 1) ON DUPLICATE KEY UPDATE count = count + 1";
		//String insertM = "INSERT INTO messages(messageId, id, messageFrom, message, messageRead, timeSent, mms, jSessionId) SELECT CONCAT(conversationId, '-', count), count, ?, CONCAT(conversationId,'-',count,'-',?), ?, ?, ?, ? FROM conversations WHERE conversationId = ?";
		String insertM = "INSERT INTO messages(messageId, id, messageFrom, message, timeSent, mms, jSessionId) VALUES (?, ?, ?, ?, ?, ?, ?)";
		String selectConvId = "SELECT CONCAT(conversationId, '-', count) AS messageId, count AS id from conversations WHERE conversationId = ?";
		String insertMD = "INSERT INTO messageDetails(userId, messageId) VALUES (?, ?)";
		String insertMDR = "INSERT INTO messageDetails(userId, messageId, timeReceived, messageRead) VALUES (?, ?, ?, ?)";
		//Have messageRead's default be 0 as well as timeReceived.
		boolean tryAgain = true;
		SQLManager manager = new SQLManager();
		Message[] messages = manager.selectWithObjects(selectOthers, Message[].class, new Object[]{conversationId});
		manager.updateWithObjects(insertC, conversationId);
		while(tryAgain){
			//manager.updateWithObjects(insertC, conversationId);
			Message convId = manager.selectWithObjects(selectConvId, Message.class, new Object[]{conversationId});
			messageId = convId.getMessageId();	
	  		Object[] object = new Object[7];
	  		object[0] = convId.getMessageId();
	  		//object[0] = userId;
	  		object[1] = convId.getId();
	  		object[2] = userId;
	  		//object[1] = fileName;
	  		//object[2] = false;
		    object[3] = messageId+"-"+fileName;
		    //object[3] = (Long) System.currentTimeMillis();
		  	timeSent = (Long) System.currentTimeMillis();
		  	object[4] = timeSent;
			object[5] = true;
			//object[5] = request.getSession().getId();
			object[6] = request.getSession().getId();
			//object[6] = conversationId;
			try{
				manager.updateWithObjectsorThrow(insertM, object);
				tryAgain = false;
			} catch(MySQLIntegrityConstraintViolationException e){
				e.printStackTrace();
			}
		}
		//long id = manager.selectWithObjects("SELECT count FROM conversations WHERE conversationId = ?", LatestConversationData.class, conversationId).getCount();
		Object[] objects = new Object[messages.length];
		String update = "UPDATE checkMessages SET checkMT = 1 WHERE userId IN(";
		for(int i = 0; i<messages.length; i++) {
			update+="?";
			if(i < (messages.length - 1))
				update+=",";
			else if(i == (messages.length - 1))
				update+=")";
		}
		for(int i = 0; i<messages.length; i++)
			objects[i] = messages[i].getUserId();
		for(int i = 0; i<objects.length; i++) {
			if((Long)objects[i] != userId)
				manager.updateWithObjects(insertMD, new Object[]{ objects[i], messageId });
			else
				manager.updateWithObjects(insertMDR, new Object[] {objects[i], messageId, timeSent, 1 });
		}
		manager.updateWithObjects(update , objects);
		return messageId + "-";
	}
}