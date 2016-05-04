package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

//access to SQLite database
class Gateway {
	static Connection con;
	static Statement stmt;
	public Gateway(){}
	//try to set connection to database
	static void connect() throws SQLException, ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		con=DriverManager.getConnection("jdbc:sqlite:test.db");
		stmt=con.createStatement();
	}
	
	//creates tables
	public static void createTables() throws SQLException{
		String comm="CREATE TABLE IF NOT EXISTS COMPANY (";
		comm+="Company_ID INT, "+
				"Company_name VARCHAR(255), " +
				"Profit INT )";

		System.out.println(comm);
		stmt.executeUpdate(comm);
		
		comm="CREATE TABLE IF NOT EXISTS PRODUCT (";
		comm+="Brand_name VARCHAR(255), "+
				"Holder_ID INT, " +
				"Price INT, "+
				"Product_type VARCHAR(255), "+
				"Rating INT ) ";
		
		
		System.out.println(comm);
		stmt.executeUpdate(comm);
		
		comm="CREATE TABLE IF NOT EXISTS CUSTOMER (";
		comm+="Customer_ID INT, "+
				"Budget INT )";
				
		System.out.println(comm);
		stmt.executeUpdate(comm);
		
	}
	//drops tables
	static void dropTable(String table_name) throws SQLException{
		stmt.executeUpdate("DROP TABLE "+table_name);
		
	}
	//adds a company
	static void addCompany(int company_id, String company_name, int profit) throws SQLException {
		String comm="INSERT INTO COMPANY (Company_ID, Company_name, Profit) VALUES ("+company_id+", '"+company_name+"', "+profit+")";
		stmt.executeUpdate(comm);
	}
	//adds a product
	static void addProduct(String brand_name, int holder_id, int price, String product_type, int rating) throws SQLException {
		String comm="INSERT INTO PRODUCT (Brand_name, Holder_ID, Price, Product_type, Rating) VALUES ('"+brand_name+"', "+holder_id+", "+price+", '"+product_type+"', "+rating+")";
		stmt.executeUpdate(comm);
	}
	//adds a customer
	static void addCustomer(int customer_id, int budget) throws SQLException {
		String comm="INSERT INTO CUSTOMER VALUES ("+customer_id+", "+budget+")";
		stmt.executeUpdate(comm);
	}
	//raises or lowers a company's profit
	static void updateCompanyProfit(int id, int amount, int profit) throws SQLException{
		int sum=amount+profit;
		String comm="UPDATE COMPANY SET Profit="+sum+" WHERE Company_ID="+id;
		stmt.executeUpdate(comm);
	}
	//raises or lowers a product's price
	static void updateProductPrice(int id, int percent_slash) throws SQLException{
		ResultSet results=stmt.executeQuery("SELECT Price FROM PRODUCT WHERE Holder_ID="+id);
		
		results.next();
		int price=results.getInt(1);
		
		int new_price=price/percent_slash;
		String comm="UPDATE PRODUCT SET Price="+new_price+" WHERE Holder_ID="+id;
		stmt.executeUpdate(comm);
	}
	//raises or lowers a company's profit
	static void updateCustomerBudget(int id, int amount, int budget) throws SQLException{
		
		int difference=budget-amount;
		
		if(difference>=0){
			String comm="UPDATE CUSTOMER SET Budget="+difference+" WHERE Customer_ID="+id;
			stmt.executeUpdate(comm);
		}
		else{
			System.out.println("Budget can't be set any lower.");
		}
	}
	//selects all companies
	static Vector<Vector<Object>> selectCompanies() throws SQLException{
		Vector<Vector<Object>> companies=new Vector<Vector<Object>>();
		
		ResultSet company_results=stmt.executeQuery("SELECT * FROM COMPANY");

		while(company_results.next()){
			Vector<Object> attributes=new Vector<Object>();
			String company_name=company_results.getString("Company_name");
			int company_id=company_results.getInt("Company_ID");
			int profit=company_results.getInt("Profit");
			
			attributes.add(company_name);
			attributes.add(company_id);
			attributes.add(profit);
			
			companies.add(attributes);
		}
		company_results.close();
		return companies;
	}
	//selects all products of a product type
	static Vector<Vector<Vector<Object>>> selectProducts(Vector<String> product_types) throws SQLException{
		Vector<Vector<Vector<Object>>> products=new Vector<Vector<Vector<Object>>>();
		
		for(int i=0;i<product_types.size();++i){
			ResultSet product_results=(stmt.executeQuery("SELECT * FROM PRODUCT WHERE Product_type='"+product_types.get(i)+"'"));
			products.add(new Vector<Vector<Object>>());

			while(product_results.next()){
				//products.get(i).add(new Vector<Object>());
				Vector<Object> attributes=new Vector<Object>();
				String brand_name=product_results.getString("Brand_name");
				int product_id=product_results.getInt("Holder_ID");
				int price=product_results.getInt("Price");
				String product_type=product_results.getString("Product_type");
				int rating=product_results.getInt("Rating");
				
				attributes.add(brand_name);
				attributes.add(product_id);
				attributes.add(price);
				attributes.add(product_type);
				attributes.add(rating);
				
				products.get(i).add(attributes);
			}
			product_results.close();
		}
		return products;
	}
	//selects all customers
	static Vector<Vector<Object>> selectCustomers() throws SQLException{
		Vector<Vector<Object>> customers=new Vector<Vector<Object>>();
		
		ResultSet customer_results=stmt.executeQuery("SELECT * FROM CUSTOMER");
		
		while(customer_results.next()){
			Vector<Object> attributes=new Vector<Object>();
			int customer_id=customer_results.getInt("Customer_ID");
			int budget=customer_results.getInt("Budget");
			
			attributes.add(customer_id);
			attributes.add(budget);
			
			customers.add(attributes);
		}
		customer_results.close();
		return customers;
	}
}
