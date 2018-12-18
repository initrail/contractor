package SQLManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.mysql.jdbc.Statement;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class SQLManager {
	public static final String CONTRACTORS_TABLE = "contractors";
	public static final String CUSTOMERS_TABLE = "customers";
	public static final String MESSAGES_TABLE = "messages";
	public static final String PROJECTS_TABLE = "projects";
	protected DataSource data;
	private boolean returnKey;
	public SQLManager(){
		data = generateConnectionPool();
	}
	public DataSource generateConnectionPool(){
		DataSource dataSource = null;
		try {
			Context newCtx = (Context) new InitialContext().lookup("java:/comp/env");
			dataSource = (DataSource)newCtx.lookup("jdbc/NetWorkers");
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return dataSource;
	}
	public void returnGeneratedKeys(boolean key) {
		returnKey = key;
	}
	public long updateWithObjects(String query, Object...objects){
		long recentId = -1;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = data.getConnection();
			if(returnKey)
				stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			else
				stmt = conn.prepareStatement(query);
			if(objects!=null)
				for(int i = 0; i<objects.length; i++)
					stmt.setObject(i + 1, objects[i]);
			stmt.execute();
			if(returnKey) {
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()){
			    	recentId=rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			try {
				if(conn!=null)
					conn.close();
				if(stmt!=null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		returnKey = false;
		return recentId;
	}
	public void updateWithObjectsorThrow(String query, Object...objects) throws MySQLIntegrityConstraintViolationException{
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = data.getConnection();
			stmt = conn.prepareStatement(query);
			if(objects!=null)
				for(int i = 0; i<objects.length; i++){
					stmt.setObject(i+1, objects[i]);
				}
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("MYSQL ERROR CODE: "+e.getErrorCode());
			if(e.getErrorCode()==1062)
				throw new MySQLIntegrityConstraintViolationException();
		}  finally {
			try {
				if(conn!=null)conn.close();
				if(stmt!=null)stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public void update(String query, Object... objects){
		ArrayList<Field> allKeyFields = getFields(objects[0].getClass());
		ArrayList<Object> objs = getObjectsForPreparedStatement(query, allKeyFields, objects);
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = data.getConnection();
			stmt = conn.prepareStatement(query);
			for(int i = 0; i<objs.size(); i++){
				stmt.setObject(i+1, objs.get(i));
			}
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			try {
				if(conn!=null)conn.close();
				if(stmt!=null)stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public <T> T select(String query, Class<T> prototype, Object... objects){
		ArrayList<Field> allSelectionFields = getFields(prototype);
		ArrayList<Field> allKeyFields = getFields(objects[0].getClass());
		ArrayList<Field> selection = new ArrayList<Field>();
		String select = "";
		Matcher m = Pattern.compile("^((?:\\b[Ss][Ee][Ll][Ee][Cc][Tt]\\b.*?\\b[Ff][Rr][Oo][Mm]\\b))").matcher(query);
		if(m.find())
			select = m.group();
		String insertPattern = "(";
		for(int i = 0; i<allSelectionFields.size(); i++){
			insertPattern+="\\b"+allSelectionFields.get(i).getName()+"\\b";
			if(i < allSelectionFields.size()-1){
				insertPattern+="|";
			} else {
				insertPattern+=")";
			}
		}
		m = Pattern.compile(insertPattern).matcher(select);
		while(m.find()){
			for(int i = 0; i<allSelectionFields.size(); i++){
				if(m.group(0).equals(allSelectionFields.get(i).getName())){
					try {
						selection.add(allSelectionFields.get(i));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if(selection.isEmpty()){
			selection = allSelectionFields;
		}
		ArrayList<Object> objs = getObjectsForPreparedStatement(query, allKeyFields, objects);
		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList<Object> result = new ArrayList<Object>();
		ResultSet res = null;
		try {
			conn = data.getConnection();
			stmt = conn.prepareStatement(query);
			for(int i = 0; i<objs.size(); i++){
				stmt.setObject(i+1, objs.get(i));
			}
			res = stmt.executeQuery();
			while(res.next()){
				Object obj = null;
				try {
					Constructor<?> ctor = null;
					if(prototype.isArray())
						ctor = prototype.getComponentType().getConstructor();
					else
						ctor = prototype.getConstructor();
					obj = ctor.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				for(int i = 0; i<selection.size(); i++){
					try {
						selection.get(i).setAccessible(true);
						selection.get(i).set(obj, res.getObject(selection.get(i).getName()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				result.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			try {
				if(conn!=null)conn.close();
				if(stmt!=null)stmt.close();
				if(res!=null)res.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(prototype.isArray()){
			if(!result.isEmpty()){
				Object[] finalRes = result.toArray(new Object[result.size()]);
				Class<? extends T[]> ogClass = (Class<? extends T[]>) prototype;
				return (T) Arrays.copyOf(finalRes, finalRes.length, ogClass);		
			} else
				return null;
		}
		if(result.isEmpty())
			return null;
		else
		return prototype.cast(result.get(0));
	}
	public <T> T selectWithObjects(String query, Class<T> prototype, Object... objs){
		ArrayList<Field> allSelectionFields = getFields(prototype);
		ArrayList<Field> selection = new ArrayList<Field>();
		String select = "";
		Matcher m = Pattern.compile("^((?:\\b[Ss][Ee][Ll][Ee][Cc][Tt]\\b.*?\\b[Ff][Rr][Oo][Mm]\\b))").matcher(query);
		if(m.find())
			select = m.group();
		String insertPattern = "(";
		for(int i = 0; i<allSelectionFields.size(); i++){
			insertPattern+="\\b"+allSelectionFields.get(i).getName()+"\\b";
			if(i < allSelectionFields.size()-1){
				insertPattern+="|";
			} else {
				insertPattern+=")";
			}
		}
		m = Pattern.compile(insertPattern).matcher(select);
		while(m.find()){
			for(int i = 0; i<allSelectionFields.size(); i++){
				if(m.group(0).equals(allSelectionFields.get(i).getName())){
					try {
						selection.add(allSelectionFields.get(i));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if(selection.isEmpty()){
			selection = allSelectionFields;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		ArrayList<Object> result = new ArrayList<Object>();
		ResultSet res = null;
		try {
			conn = data.getConnection();
			stmt = conn.prepareStatement(query);
			for(int i = 0; i<objs.length; i++){
				stmt.setObject(i+1, objs[i]);
			}
			res = stmt.executeQuery();
			while(res.next()){
				Object obj = null;
				try {
					Constructor<?> ctor = null;
					if(prototype.isArray())
						ctor = prototype.getComponentType().getConstructor();
					else
						ctor = prototype.getConstructor();
					obj = ctor.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				for(int i = 0; i<selection.size(); i++){
					try {
						selection.get(i).setAccessible(true);
						selection.get(i).set(obj, res.getObject(selection.get(i).getName()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				result.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			try {
				if(conn!=null)conn.close();
				if(stmt!=null)stmt.close();
				if(res!=null)res.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(prototype.isArray()){
			if(!result.isEmpty()){
				Object[] finalRes = result.toArray(new Object[result.size()]);
				Class<? extends T[]> ogClass = (Class<? extends T[]>) prototype;
				return (T) Arrays.copyOf(finalRes, finalRes.length, ogClass);		
			} else
				return null;
		}
		if(result.isEmpty())
			return null;
		else
			return prototype.cast(result.get(0));
	}
	public ArrayList<Object> getObjectsForPreparedStatement(String query, ArrayList<Field> allKeyFields, Object...objects){
		ArrayList<Object> objs = new ArrayList<Object>();
		String insertPattern = "((?:";
		for(int i = 0; i<allKeyFields.size(); i++){
			insertPattern+="\\b"+allKeyFields.get(i).getName()+"\\b";
			if(i < allKeyFields.size()-1){
				insertPattern+="|";
			} else {
				insertPattern+=")";
			}
		}
		insertPattern+=".{0,12}?\\?(?:(?:[\\s+]?,[\\s+]?\\?)+)?)";
		Matcher m = Pattern.compile(insertPattern).matcher(query);
		while(m.find()){
			Matcher m2 = null;
			for(int i = 0; i<allKeyFields.size(); i++){
				for(int j = 0; j<allKeyFields.size(); j++){
					if(m.group(0).contains(allKeyFields.get(j).getName()+"?")){
						query = query.replace(allKeyFields.get(j).getName()+"?", "?");
						try {
							objs.add(allKeyFields.get(j).get(objects[0]));
						} catch (Exception e) {
							e.printStackTrace();
						}
						i=allKeyFields.size();
					}
				}
				if(i<allKeyFields.size()){
					int count = m.group(0).length() - m.group(0).replace("?", "").length();
					m2 = Pattern.compile("\\b"+allKeyFields.get(i).getName()+"\\b").matcher(m.group(0));
					while(m2.find()){
						if(count>1){
							if(count == objects.length){
								for(int a = 0; a<objects.length; a++){
									try {
										objs.add(allKeyFields.get(i).get(objects[a]));
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						} else {
							try {
								objs.add(allKeyFields.get(i).get(objects[0]));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		return objs;
	}

	public ArrayList<Field> getFields(Class<?> object){
		if(object.isArray()){
			object = object.getComponentType();
		}
		Field[] fields = object.getDeclaredFields();
		ArrayList<Field> allFields = new ArrayList<Field>();
		ArrayList<Field> superFields = new ArrayList<Field>();
		Field[] superField = null;
		Class obj = object;
		while(obj.getSuperclass()!=Object.class){
			superField = obj.getSuperclass().getDeclaredFields();
			for(int i = 0; i<superField.length; i++){
				superFields.add(superField[i]);
			}
			obj = obj.getSuperclass();
		}
		for(int i = 0; i<superFields.size(); i++){
			if(!(superFields.get(i).getName().equals("complete")||superFields.get(i).getName().equals("finished")||superFields.get(i).getName().equals("passwordCheck")))
				allFields.add(superFields.get(i));
		}
		for(int i = 0; i<fields.length; i++){
			if(!(fields[i].getName().equals("complete")||fields[i].getName().equals("finished")||fields[i].getName().equals("passwordCheck")))
				allFields.add(fields[i]);
		}
		for(int i = 0; i<allFields.size(); i++){
			try {
				allFields.get(i).setAccessible(true);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		return allFields;
	}
	public void insert(String table, Object object){
		PreparedStatement stmt = null;
		Connection conn = null;
		String query = "INSERT INTO "+table+" (";
		ArrayList<Field> allFields = getFields(object.getClass());
		for(int i = 0; i<allFields.size(); i++){
			query+=allFields.get(i).getName();
			if(i==(allFields.size()-1))
				query+=") values (";
			else
				query+=", ";
		}
		for(int i = 0; i<allFields.size(); i++){
			query+="?";
			if(i==(allFields.size()-1))
				query+=")";
			else
				query+=", ";
		}
		try{
			conn = data.getConnection();
			stmt = conn.prepareStatement(query);
			for(int i = 0; i<allFields.size(); i++){
				try {
					stmt.setObject(i+1, allFields.get(i).get(object));
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			stmt.executeUpdate();
		}  catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}
