public class Hotel extends Provider{
	private int HotelId;
	public Hotel(int ProviderId, boolean loggedin, String name, String email, String phone, String password,
			String Location,int HotelId) {
		super(ProviderId, loggedin, name, email, phone, password, Location);
		this.HotelId=HotelId;
	}

	public int getHotelId() {
		return HotelId;
	}

	public void setHotelId(int hotelId) {
		this.HotelId = hotelId;
	}
     @Override
	public void donateFood(FoodItem item) {
		item.setProvider(this);
		System.out.println("Hotel: " + getName() + " donated: " + item.getName());
	}

}

