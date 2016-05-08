package db;

import db.Gateway;

import java.sql.SQLException;
import java.util.Set;
import java.util.Vector;

public class Main {
	private Vector<Vector<Object>> companies=new Vector<Vector<Object>>();
	private Vector<Vector<Vector<Object>>> products=new Vector<Vector<Vector<Object>>>();
	private Vector<Vector<Object>> customers=new Vector<Vector<Object>>();
	public Main() throws SQLException{
		companies=Gateway.selectCompanies();
		customers=Gateway.selectCustomers();
	}
	//good economy, increase budget limit, decrease prices, increase quantity
	public void goodEconomy() throws SQLException{
		
		//get a set of all product types of products in the database
		Set<String> product_types=Gateway.getProductTypes();
		
		//products are grouped by product type, so the customer can pick one of each type
		products=Gateway.selectProducts(product_types);
		
		//first lower the price of each product
		int percent_slash=-1;
		//update the price of each product
		for(int j=0;j<products.size();++j){
			//for every set of products in a product type, update
			for(int k=0;k<products.get(j).size();++k){
				Vector<Object> product=products.get(j).get(k);
				product.set(2, (int)product.get(2)+percent_slash);
			}
		}
		//for each customer, raise the budget
		int budget_raise=1;
		for(int i=0;i<customers.size();++i){
			customers.get(i).set(1, (int)customers.get(i).get(1)+budget_raise);
		}
		//now go shopping
		shop();
	}
	
	public void shop() throws SQLException{
		for(int i=0;i<customers.size();++i){
			int customer_id=(int)customers.get(i).get(0);
			String brand_name="";
			for(int j=0;j<products.size();++j){
				//for every set of products in a product type
				//initialize the top rank product
				//top rank product is a vector with a holder id, price, rating, customer id and customer budget
				Vector<Integer> top_rank_product=new Vector<Integer>();
				top_rank_product.add(0,0); //holder id
				//only the price needs to be set high
				top_rank_product.add(1,100000000); //price
				top_rank_product.add(2,0); //rating
				top_rank_product.add(3,customer_id);
				boolean can_buy=false;
				for(int k=0;k<products.get(j).size();++k){
					//for every product in a set
					//tally each ranking of each product to make the best choice
					int price=(int)products.get(j).get(k).get(2);
					int customer_budget=(int)customers.get(i).get(1);
					top_rank_product.add(4,customer_budget);
					brand_name=(String) products.get(j).get(k).get(0);
					//can the customer buy the product?
					if(price<=customer_budget){
						can_buy=true;
						int holder_id=(int)products.get(j).get(k).get(1);
						int rating=(int)products.get(j).get(k).get(5);
						//is the product's rating better than the current top pick or is its price lower than the top pick?
						if(rating>=top_rank_product.get(2)&&price<=top_rank_product.get(1)){
							//replace top product with new top product
							brand_name=(String) products.get(j).get(k).get(0);
							top_rank_product.set(0, holder_id);
							top_rank_product.set(1, price);
							top_rank_product.set(2, rating);
						}
					}
				}
				//is the customer able to buy a product of this type?
				if(can_buy){
					buyProduct(top_rank_product);
					Gateway.updateProductStock(brand_name);
				}
			}
		}
		//finally, update the expenditure of the customers and the profit of the companies
		Gateway.updateCustomerBudget(customers);
		Gateway.updateCompanyProfit(companies);
	}

	
	private void buyProduct(Vector<Integer> top_rank_product) throws SQLException{
		int profit=top_rank_product.get(1); //add to company profit (taxes not considered)
		int payment=-profit; //subtract from customer budget (taxes not considered)
		//seek out the customer that will be paying for this product
		for(int l=0;l<customers.size();++l){
			//compare the ids
			int customer_id=(int)customers.get(l).get(0);
			int top_rank_id=top_rank_product.get(3);
			if(customer_id==top_rank_id){
				int p=(int)customers.get(l).get(1);
				customers.get(l).set(1, (int)customers.get(l).get(1)+payment);
				int q=(int)customers.get(l).get(1);
				System.out.println(customers.get(l).get(1));
			}
		}
		//seek out the company that will be profiting from the customer
		for(int l=0;l<companies.size();++l){
			//compare the ids
			int company_id=(int)companies.get(l).get(1);
			int top_rank_id=top_rank_product.get(0);
			if(company_id==top_rank_id){
				int cur_profit=(int) companies.get(l).get(2);
				companies.get(l).set(2, profit+cur_profit);
			}
		}
	}
	//bad economy, decrease budget limit, increase prices
	public void badEconomy() throws SQLException{
		Set<String> product_types=Gateway.getProductTypes();
		products=Gateway.selectProducts(product_types);	
		int percent_slash=-1;
		for(int j=0;j<products.size();++j){
			for(int k=0;k<products.get(j).size();++k){
				Vector<Object> product=products.get(j).get(k);
				product.set(2, (int)product.get(2)+percent_slash);
			}
		}
		int budget_raise=1;
		for(int i=0;i<customers.size();++i){
			customers.get(i).set(1, (int)customers.get(i).get(1)+budget_raise);
		}
		shop();
	}
	
	public static void main(String[] args) {
		try{
			Gateway.connect();
			/*Gateway.dropTable("COMPANY");
			Gateway.dropTable("PRODUCT");
			Gateway.dropTable("CUSTOMER");*/
			Gateway.createTables();
			
			Gateway.addCompany(112234, "Wally-World", 5);
			Gateway.addCompany(123435, "Price Chomper", 0);
			Gateway.addCompany(452429, "ArrowMart", 0);
			Gateway.addCompany(887478, "Belongs-to-Us", 0);
			Gateway.addCompany(127476, "Penny Store", 0);
			
			Gateway.addCustomer(128947284, 1230);
			Gateway.addCustomer(234234098, 2399);
			Gateway.addCustomer(989234882, 2312);
			Gateway.addCustomer(348578757, 8786);
			Gateway.addCustomer(328472483, 3245);
			
			Gateway.addProduct("Shakin", 112234, 13, "ToiletPaper", 300, 5);
			Gateway.addProduct("Popmallows", 112234, 4, "Cereal", 300, 4);
			Gateway.addProduct("Tornado", 112234, 24, "Vacuum", 300, 3);
			Gateway.addProduct("Raspberry", 112234, 400, "MobilePhone", 300, 3);
			Gateway.addProduct("HDDelux", 112234, 1880, "TV", 300, 4);
			
			Gateway.addProduct("ComfortWipe", 123435, 10, "ToiletPaper", 300, 2);
			Gateway.addProduct("ChocoPuffs", 123435, 4, "Cereal", 300, 4);
			Gateway.addProduct("Cleanerr", 123435, 45, "Vacuum", 300, 4);
			Gateway.addProduct("FlipMaster", 123435, 560, "MobilePhone", 300, 3);
			Gateway.addProduct("Sungsam", 123435, 877, "TV", 300, 5);
			
			Gateway.addProduct("Bountiful", 452429, 14, "ToiletPaper", 300, 3);
			Gateway.addProduct("Pranx", 452429, 9, "Cereal", 300, 4);
			Gateway.addProduct("Sweeptastic", 452429, 56, "Vacuum", 300, 2);
			Gateway.addProduct("On-the-Go", 452429, 456, "MobilePhone", 300, 5);
			Gateway.addProduct("ScreenCenter", 452429, 2000, "TV", 300, 4);
			
			Gateway.addProduct("CarePaper", 887478, 3, "ToiletPaper", 300, 4);
			Gateway.addProduct("ToastSquares", 887478, 3, "Cereal", 300, 4);
			Gateway.addProduct("Whirlwind", 887478, 3, "Vacuum", 300, 5);
			Gateway.addProduct("uChat", 887478, 3, "MobilePhone", 300, 3);
			Gateway.addProduct("ColorMaster", 887478, 3, "TV", 300, 4);
			
			Gateway.addProduct("OnaCloud", 127476, 16, "ToiletPaper", 300, 5);
			Gateway.addProduct("BreakfastOats", 127476, 1, "Cereal", 300, 4);
			Gateway.addProduct("FloorHero", 127476, 33, "Vacuum", 300, 3);
			Gateway.addProduct("iTalk", 127476, 670, "MobilePhone", 300, 3);
			Gateway.addProduct("VisualMight", 127476, 949, "TV", 300, 2);
				
			Main example=new Main();
			example.goodEconomy();
			example.badEconomy();
			
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
 Given the products started with the same stock, which types of products were most popular for each company?
 Which company did better in the good economy? In the bad one?
 Did one company stand on top in both good and bad economies? If so, which one?
 */
