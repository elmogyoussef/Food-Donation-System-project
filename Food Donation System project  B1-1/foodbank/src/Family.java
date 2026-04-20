public class Family extends Provider{
	private String FamilyName;
	public Family(int ProviderId, boolean loggedin, String name, String email, String phone, String password,
			String Location,String FamilyName) {
		super(ProviderId, loggedin, name, email, phone, password, Location);
		this.FamilyName=FamilyName;
	}


	public String getFamilyName() {
		return FamilyName;
	}


	public void setFamilyName(String familyName) {
		this.FamilyName = familyName;
	}

    @Override
	public void donateFood(FoodItem item) {
		item.setProvider(this);
		System.out.println("Family: " + getName() + " donated: " + item.getName());
	}

}

