
public class Restaurant extends Provider{
	private int RestaurantID;
	public Restaurant(int ProviderId, boolean loggedin, String name, String email, String phone, String password,
			String Location,int RestaurantID) {
		super(ProviderId, loggedin, name, email, phone, password, Location);
		this.RestaurantID=RestaurantID;
	}

	public int getRestaurantID() {
		return RestaurantID;
	}

	public void setRestaurantID(int restaurantID) {
		this.RestaurantID = restaurantID;
	}
    @Override
	public void donateFood(FoodItem item) {
		item.setProvider(this);
		// Do not add to global registry until manager approves pickup request
		System.out.println("Restaurant: " + getName() + " donated: " + item.getName());

		
	}

}

