package resumeuploader.database;
import java.sql.*;
public class PersistFileURL {
	public boolean save(String assetKey, String url){
		   Connection conn = null;
		   PreparedStatement updateURL = null;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");
		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...");
		      //"jdbc:mysql://localhost/student_worker_new_development"
		      conn = DriverManager.getConnection(System.getenv("STUDENT_WORKER_JDBC_MYSQL"),System.getenv("STUDENT_WORKER_DB_USER"),System.getenv("STUDENT_WORKER_DB_PASS"));
		      String updateString = "update "+ "student_profiles " +"set resume_url = ? where asset_key = ?";
		      //STEP 4: Execute a query
		      System.out.println("Updating URL...");
		      updateURL = conn.prepareStatement(updateString);
		      updateURL.setString(1, url);
		      updateURL.setString(2, assetKey);
		      updateURL.executeUpdate();
		      conn.close();
		      return true;
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		      return false;
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		      return false;
		   }finally{
		      //finally block used to close resources
		      try{
		         if(updateURL!=null)
		            updateURL.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	}
	public static void main(String args[]){

	}
}
