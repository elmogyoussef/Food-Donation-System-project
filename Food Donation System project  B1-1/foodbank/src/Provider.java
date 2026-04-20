
public abstract class Provider {
	private int ProviderId;
	private String name;
	private String email;
	private String phone;
	private String password;
	private String Location;
	private boolean loggedin;

	public Provider(int ProviderId,boolean loggedin,String name, String email, String phone, String password,String Location) {
		this.ProviderId = ProviderId;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.password = password;
		this.Location=Location;
		this.loggedin = loggedin;
	}
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		this.Location = location;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getProviderId() {
		return ProviderId;
	}
	public void setUserId(int ProviderId) {
		this.ProviderId = ProviderId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean login(String passinput) {
		if(loggedin) {
			return true; 
		} else {
			if(email != null && password != null && password.equals(passinput)) {
				loggedin=true;
				return true;
			}
			return false;
		}
	}
	public void logout() {
		loggedin=false;
	}
	public abstract void donateFood(FoodItem item);
	public  PickupRequest RequestPickUp (FoodItem item) {
		int requestID=item.getItemId();
		PickupRequest request= new PickupRequest(requestID,item,new java.util.Date(),"pending");
		System.out.println(name + " requested pickup for item: " + item.getName());
		return request;
	}


}

