package account_servlets;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import accountmanager.SessionManager;
import constants.FileSystemConstants;

public class SetUserImage extends HttpServlet {
	private static final long serialVersionUID = -3786891248867319906L;
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		SessionManager check = new SessionManager(req, resp);
		if(check.validSession()){
			System.out.println("Existing user...");
			long userId = check.getUserId();
			uploadImage(req, userId);
		} else {
			System.out.println("New account creation...");
			uploadImage(req, 0);
		}
	}
	public void uploadImage(HttpServletRequest req, long userId){
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024*30);
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setFileSizeMax(1024*1024*40);
		upload.setSizeMax(1024*1024*50);
		File uploadDir = new File(FileSystemConstants.USER_IMAGES_PATH);
		if(!uploadDir.exists()){
			uploadDir.mkdir();
		}
		try{
			List<?> formItems = upload.parseRequest(req);
			Iterator<?> it = formItems.iterator();
			while(it.hasNext()){
				FileItem item = (FileItem) it.next();
				if(!item.isFormField()){
					String fileName;
					if(userId==0)
						fileName = new File(item.getName()).getName();
					else
						fileName = "" + userId;
					String filePath = FileSystemConstants.USER_IMAGES_PATH+File.separator+fileName+".jpg";
					File storeFile = new File(filePath);
					item.write(storeFile);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
