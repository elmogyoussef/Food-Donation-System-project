import java.util.ArrayList;
import java.util.Date;

public class FoodItem {
	private int itemId;
	private String name;
	private int quantity;
	private Date expiryDate;
	private String status;
	private String unit;
	private Provider provider;
	private static ArrayList<FoodItem> foodItems = new ArrayList<>();
	private static int lastId = 0;

	public FoodItem(int itemId, String name, int quantity, Date expiryDate, String status, Provider provider) {
		this.itemId = itemId;
		this.name = name;
		this.quantity = quantity;
		this.expiryDate = expiryDate;
		this.status = status;
		this.provider = provider;
		this.unit = "pcs";
	}

	// New constructor with unit
	public FoodItem(int itemId, String name, int quantity, Date expiryDate, String status, Provider provider, String unit) {
		this.itemId = itemId;
		this.name = name;
		this.quantity = quantity;
		this.expiryDate = expiryDate;
		this.status = status;
		this.provider = provider;
		this.unit = unit != null ? unit : "pcs";
	}

	// Generate a unique id for new items
	public static synchronized int generateId() {
		lastId += 1;
		return lastId;
	}
	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public void setProvider(Provider provider) {
		this.provider=provider;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Provider getProvider() {
		return provider;
	}
	public void  updateStatus(String newStatus) {
		this.status = newStatus;
	}

	public boolean isExpired() {
		return expiryDate != null && expiryDate.before(new Date(System.currentTimeMillis()));
	}
	@Override
	public String toString() {
		String prov = (provider != null) ? provider.getName() : "Unknown";
		return "[" + itemId + "] " + name + " (x" + quantity + ") exp:" + (expiryDate!=null?expiryDate.toString():"N/A") + " by:" + prov + " status:" + status;
	}

	public static void addFoodItem(FoodItem item) {
		if (item != null) {
			// ensure id generator won't reuse IDs
			if (item.itemId > lastId) lastId = item.itemId;
			foodItems.add(item);
		}
	}

	public static void removeFoodItem(FoodItem item) {
		foodItems.remove(item);
	}

	public static ArrayList<FoodItem> getAllFoodItems() {
		return foodItems;
	}

	public static ArrayList<FoodItem> getAvailableFoodItems() {
		ArrayList<FoodItem> available = new ArrayList<>();
		for (FoodItem item : foodItems) {
			if (!item.isExpired() && item.quantity > 0) {
				available.add(item);
			}
		}
		return available;
	}
	public void markAvailable() {
		this.status = "available";
	}
	public void markCollected() {
		this.status = "collected";
	}
	public void markDelivered() {
		this.status = "delivered";
	}
}
