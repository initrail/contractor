package SQLManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class ProjectSQLManager extends SQLManager {
    public final static String COLUMN_1="exteriorProjectFlag";
    public final static String COLUMN_2="interiorProjectFlag";
    public final static String COLUMN_3="fullName";
    public final static String COLUMN_4="phoneNumber";
    public final static String COLUMN_5="address";
    public final static String COLUMN_6="aptNumber";
    public final static String COLUMN_7="lockBoxCode";
    public final static String COLUMN_8="budget";
    public final static String COLUMN_9="dateOfCompletion";
    public final static String COLUMN_10="projectDescription";
    public final static String COLUMN_11="creator";
    public final static String COLUMN_12="id";
	public ProjectSQLManager(){
		super();
	}
	public int getLatestProjectId(String user){
		int highest = 0;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet res = null;
		try{
			conn = data.getConnection();
			String sqlStatement = "SELECT MAX(id) FROM projects WHERE creator = ?";
			stmt = conn.prepareStatement(sqlStatement);
			stmt.setString(1, user);
			res = stmt.executeQuery();
			while(res.next()){
				highest = res.getInt("max(id)");
			}
		} catch (SQLException e){
			System.out.println("From ProjectSQLManager.getLatestProjectId()");
			e.printStackTrace();
		} finally {
				try {
					if(conn!=null)conn.close();
					if(stmt!=null)stmt.close();
					if(res!=null)res.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return highest;
	}
}
