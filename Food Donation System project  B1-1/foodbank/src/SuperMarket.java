

public class SuperMarket extends Provider{
	private int SuperMarketID;
	public SuperMarket(int ProviderId, boolean loggedin, String name, String email, String phone, String password,
			String Location,int SuperMarketID) {
		super(ProviderId, loggedin, name, email, phone, password, Location);
		this.SuperMarketID=SuperMarketID;
	}

	public int getSuperMarketID() {
		return SuperMarketID;
	}
	public void setSuperMarketID(int superMarketID) {
		SuperMarketID = superMarketID;
	}
	@Override
	public void donateFood(FoodItem item) {
		item.setProvider(this);
		System.out.println("SuperMarket: " + getName() + " donated: " + item.getName());
	}

}


