import java.util.Date;

public class PickupRequest {
	private int requestID;
	private FoodItem foodItem;
	private Date requestDate;
	private String Status;//pending,approved,rejected,completed
	public PickupRequest(int requestID, FoodItem foodItem, Date requestDate, String status) {
		this.requestID = requestID;
		this.foodItem = foodItem;
		this.requestDate = requestDate;
		this.Status = status;
	}
	public int getRequestID() {
		return requestID;
	}
	public FoodItem getFoodItem() {
		return foodItem;
	}
	public Date getRequestDate() {
		return requestDate;
	}
	public String getStatus() {
		return Status;
	}
	public void approve(){
		this.Status="approved";
		// mark the item available for manager to assign delivery
		foodItem.markAvailable();
	}
	public void reject() {
		this.Status="rejected";
	}
	public void Complete() {
		this.Status="completed";
	}

	@Override
	public String toString() {
		String itemName = (foodItem != null) ? foodItem.getName() : "Unknown";
		return "Request[" + requestID + "] " + itemName + " - " + Status;
	}
}
