public class Bakery extends Provider{
	private int BakeryID;
	public Bakery(int ProviderId, boolean loggedin, String name, String email, String phone, String password,
			String Location,int BakeryID) {
		super(ProviderId, loggedin, name, email, phone, password, Location);
		this.BakeryID=BakeryID;
	}


	public int getBakeryID() {
		return BakeryID;
	}


	public void setBakeryID(int BakeryID) {
		this.BakeryID = BakeryID;
	}

    @Override
	public void donateFood(FoodItem item) {
		item.setProvider(this);
		System.out.println("Bakery: " + getName() + " donated: " + item.getName());
	}

}

