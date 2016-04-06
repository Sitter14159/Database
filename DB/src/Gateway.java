import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;


//access to SQLite database
class Gateway {
	//use map over array or list - don't need ordering - only types and specifications
	static Connection con;
	static Statement stmt;
	public Gateway(){}
	//try to set connection to database
	static void connect() throws SQLException, ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		con=DriverManager.getConnection("jdbc:sqlite:test.db");
		stmt=con.createStatement();
	}
	
	//creates table
	public static void createTable(String table_name, String... specs) throws SQLException{
		String comm="CREATE TABLE IF NOT EXISTS EMPLOYEE (";
		comm+="Fname VARCHAR(255), "+
				"Minit VARCHAR(255) "+
				"Lname VARCHAR(255) "+
				"Ssn INT "+
				"Bdate TEXT "+
				"Address VARCHAR(255) "+
				"Sex VARCHAR(255) "+
				"Salary INT "+
				"Super_ssn INT "+
				"Dno INT )";

		System.out.println(comm);
		stmt.executeUpdate(comm);
		
		comm="CREATE TABLE IF NOT EXISTS DEPARTMENT (";
		comm+="Dname VARCHAR(255), "+
				"Dnumber INT "+
				"Msg_ssn INT "+
				"Msg_start_date TEXT )";
		
		comm="CREATE TABLE IF NOT EXISTS PROJECT (";
		comm+="Pname VARCHAR(255), "+
				"Pnumber INT "+
				"Plocation VARCHAR(255) "+
				"Dnum INT )";
				
		comm="CREATE TABLE IF NOT EXISTS DEPENDENT (";
		comm+="Essn INT, "+
				"Dependent_name VARCHAR(255) "+
				"Sex CHARACTER(20) "+
				"Bdate TEXT "+
				"Relationship VARCHAR(255) )";
		System.out.println(comm);
		stmt.executeUpdate(comm);
		
	}
	
	
	//dangerous 
	static void dropTable(String table_name) throws SQLException{
		stmt.executeUpdate("DROP TABLE "+table_name);
	}
	
	static void addCompany() throws SQLException(){
		String comm="INSERT INTO COMPANY VALUES";
	}
	
	//gets a table of data in the form of a hash map where id=spec type and other column=spec
	public static HashMap<String,String> getData(String table_name,String spec) throws SQLException{
		ResultSet results=("".equals(spec)) ? stmt.executeQuery("SELECT * FROM "+table_name) : 
			stmt.executeQuery("SELECT * FROM "+table_name+" WHERE ID='"+spec+"'");
		HashMap<String,String> map=new HashMap<String,String>();
		while(results.next()){
			String key=results.getString(0);
			String value=results.getString(1);
			map.put(key,value);
		}
		results.close();
		return map;
	}
}