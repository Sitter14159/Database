package db;

import db.Gateway;

import java.sql.SQLException;
import java.util.Vector;

public class Example {
	private Vector<Vector<Object>> companies=new Vector<Vector<Object>>();
	private Vector<Vector<Vector<Object>>> products=new Vector<Vector<Vector<Object>>>();
	private Vector<Vector<Object>> customers=new Vector<Vector<Object>>();
	public Example() throws SQLException{
		companies=Gateway.selectCompanies();
		customers=Gateway.selectCustomers();
	}
	//good economy, increase budget limit, decrease prices, increase quantity
	public void goodEconomy() throws SQLException{
		//do queries to update budget, price and quantity
		//update prices
		//update budget
		//for each customer
		//	for each product set, rank them according to customer info and have the customer pick from each set
		//get the products for each company
		
		Vector<String> product_types=new Vector<String>();
		product_types.add("ToiletPaper");
		product_types.add("Cereal");
		product_types.add("Vacuum");
		product_types.add("MobilePhone");
		product_types.add("TV");
		//products are grouped by product type, so the customer can pick one of each type
		products=Gateway.selectProducts(product_types);
		
		//first slash the price of each product
		int percent_slash=2;
		for(int j=0;j<products.size();++j){
			//for every set of products in a product type
			for(int k=0;k<products.get(j).size();++k){
				Vector<Object> size=products.get(j).get(k);
				int holder_id=(int)size.get(1);
				Gateway.updateProductPrice(holder_id, percent_slash);
			}
		}
		
		for(int i=0;i<customers.size();++i){
			int customer_id=(int)customers.get(i).get(0);
			//raise the budget of this customer
			int customer_budget=(int)customers.get(i).get(1);
			Gateway.updateCustomerBudget(customer_id, 30, customer_budget);
		}
		shop();
	}
	
	public void shop() throws SQLException{
		for(int i=0;i<customers.size();++i){
			int customer_id=(int)customers.get(i).get(0);
			int customer_budget=(int)customers.get(i).get(1);
			for(int j=0;j<products.size();++j){
				//for every set of products in a product type
				Vector<Integer> top_rank_product=new Vector<Integer>();
				for(int k=0;k<products.get(j).size();++k){
					//for every product in a set
					//judge their ranking and tally them up to make the best choice
					//vector first entry is holder id, second is price, third is rating and fourth is final ranking
					//initialize the top rank product
					top_rank_product.add(0,0);
					top_rank_product.add(1,100000000);
					top_rank_product.add(2,0);
					
					int holder_id=(int)products.get(j).get(k).get(1);
					int new_price=(int)products.get(j).get(k).get(2);
					if(new_price<=customer_budget){
						int rating=(int)products.get(j).get(k).get(4);
						if(rating>top_rank_product.get(1)&&new_price<top_rank_product.get(1)){
							//replace other product with new
							top_rank_product.add(0, holder_id);
							top_rank_product.add(1, new_price);
							top_rank_product.add(2, rating);
						}
					}
				}
				buyProduct(top_rank_product,customer_id,customer_budget);
			}
		}
	}

	
	private void buyProduct(Vector<Integer> top_rank_product, int customer_id, int budget) throws SQLException{
		int company_id=top_rank_product.get(0);
		int price=top_rank_product.get(1); //add to company profit (minus taxes)
		int neg_price=-price; //subtract from customer budget
		int profit=0;
		for(int l=0;l<companies.size();++l){
			if((int)companies.get(l).get(2)==top_rank_product.get(0)){
				profit=(int) companies.get(l).get(2);
			}
		}
		Gateway.updateCustomerBudget(company_id,neg_price,budget);
		Gateway.updateCompanyProfit(company_id,price,profit);
	}
	//bad economy, decrease budget limit, increase prices, decrease quantity
	public void badEconomy() throws SQLException{
		//do queries to update budget, price and quantity
				//update prices
				//update budget
				//for each customer
				//	for each product set, rank them according to customer info and have the customer pick from each set
				//get the products for each company
				
				Vector<String> product_types=new Vector<String>();
				product_types.add("ToiletPaper");
				product_types.add("Cereal");
				product_types.add("Vacuum");
				product_types.add("MobilePhone");
				product_types.add("TV");
				//products are grouped by product type, so the customer can pick one of each type
				products=Gateway.selectProducts(product_types);
				
				//first slash the price of each product
				int percent_slash=1/2;
				for(int j=0;j<products.size();++j){
					//for every set of products in a product type
					for(int k=0;k<products.get(j).size();++k){
						Vector<Object> size=products.get(j).get(k);
						int holder_id=(int)size.get(1);
						Gateway.updateProductPrice(holder_id, percent_slash);
					}
				}
				
				for(int i=0;i<customers.size();++i){
					int customer_id=(int)customers.get(i).get(0);
					//raise the budget of this customer
					int customer_budget=(int)customers.get(i).get(1);
					Gateway.updateCustomerBudget(customer_id, -30, customer_budget);
				}
			shop();
	}
	
	public static void main(String[] args) {
		try{
			Gateway.connect();
			Gateway.dropTable("COMPANY");
			Gateway.dropTable("PRODUCT");
			Gateway.dropTable("CUSTOMER");
			Gateway.createTables();
			
			Gateway.addCompany(112234, "Wally-World", 5);
			Gateway.addCompany(123435, "Price Chomper", 0);
			Gateway.addCompany(452429, "ArrowMart", 0);
			Gateway.addCompany(887478, "Belongs-to-Us", 0);
			Gateway.addCompany(127476, "Penny Store", 0);
			
			//Gateway.updateCompanyProfit(112234, 25);
			
			Gateway.addCustomer(128947284, 1230);
			Gateway.addCustomer(234234098, 2399);
			Gateway.addCustomer(989234882, 2312);
			Gateway.addCustomer(348578757, 8786);
			Gateway.addCustomer(328472483, 3245);
			
			Gateway.addProduct("Shakin", 112234, 13, "ToiletPaper", 5);
			Gateway.addProduct("Popmallows", 112234, 4, "Cereal", 4);
			Gateway.addProduct("Tornado", 112234, 24, "Vacuum", 3);
			Gateway.addProduct("Raspberry", 112234, 400, "MobilePhone", 3);
			Gateway.addProduct("HDDelux", 112234, 1880, "TV", 4);
			
			Gateway.addProduct("ComfortWipe", 123435, 10, "ToiletPaper", 2);
			Gateway.addProduct("ChocoPuffs", 123435, 4, "Cereal", 4);
			Gateway.addProduct("Cleanerr", 123435, 45, "Vacuum", 4);
			Gateway.addProduct("FlipMaster", 123435, 560, "MobilePhone", 3);
			Gateway.addProduct("Sungsam", 123435, 877, "TV", 5);
			
			Gateway.addProduct("Bountiful", 452429, 14, "ToiletPaper", 3);
			Gateway.addProduct("Pranx", 452429, 9, "Cereal", 4);
			Gateway.addProduct("Sweeptastic", 452429, 56, "Vacuum", 2);
			Gateway.addProduct("On-the-Go", 452429, 456, "MobilePhone", 5);
			Gateway.addProduct("ScreenCenter", 452429, 2000, "TV", 4);
			
			Gateway.addProduct("CarePaper", 887478, 16, "ToiletPaper", 4);
			Gateway.addProduct("ToastSquares", 887478, 3, "Cereal", 4);
			Gateway.addProduct("Whirlwind", 887478, 34, "Vacuum", 5);
			Gateway.addProduct("uChat", 887478, 569, "MobilePhone", 3);
			Gateway.addProduct("ColorMaster", 887478, 2999, "TV", 4);
			
			Gateway.addProduct("OnaCloud", 127476, 16, "ToiletPaper", 5);
			Gateway.addProduct("BreakfastOats", 127476, 1, "Cereal", 4);
			Gateway.addProduct("FloorHero", 127476, 33, "Vacuum", 3);
			Gateway.addProduct("iTalk", 127476, 670, "MobilePhone", 3);
			Gateway.addProduct("VisualMight", 127476, 949, "TV", 2);
				
			
			
			Example example=new Example();
			example.goodEconomy();
			
			Vector<Vector<Object>> companies=Gateway.selectCompanies();
			for(int i=0;i<companies.size();++i){
				for(int j=0;j<companies.get(i).size();++j){
					System.out.println(companies.get(i).get(j));
				}
			}
			
			Vector<Vector<Object>> customers=Gateway.selectCustomers();
			for(int i=0;i<customers.size();++i){
				for(int j=0;j<customers.get(i).size();++j){
					System.out.println(customers.get(i).get(j));
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}

/*
 QUESTIONS:
 What is the most popular product?
 What is the most popular company?
 Does the most popular product always belong to the most popular company? Why or why not?
 Does the lack of quantity turn customers away from that product? Does it make them turn away from the company?
 Which types of products were most popular for each company?
 Which company did better in the good economy? In the bad one?
 Did one company stand on top in both good and bad economies? If so, which one?
 */
