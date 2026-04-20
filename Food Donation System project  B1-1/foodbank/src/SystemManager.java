import java.util.ArrayList;
import java.util.List;

public class SystemManager {

	private ArrayList<FoodItem> foodItems;
	private ArrayList<PickupRequest> pickupRequests;
	private ArrayList<Delivery> deliveries;
	private ArrayList<Recipient> recipients;
    private List<PickupRequestListener> listeners;
    private ArrayList<Provider> providers;


	public SystemManager() {
		foodItems = new ArrayList<>();
		pickupRequests = new ArrayList<>();
		deliveries = new ArrayList<>();
		recipients = new ArrayList<>();
		listeners = new ArrayList<>();
		providers = new ArrayList<>();
	}

	public void addProvider(Provider p) {
		if (p != null && !providers.contains(p)) providers.add(p);
	}

	public Provider authenticateProvider(String email, String password) {
		if (email == null || password == null) return null;
		for (Provider p : providers) {
			if (p.getEmail() != null && p.getEmail().equals(email) && p.login(password)) {
				return p;
			}
		}
		return null;
	}

	public void addPickupRequest(PickupRequest request) {
		pickupRequests.add(request);
		notifyPickupRequestListeners();
	}

	public void addPickupRequestListener(PickupRequestListener l) {
		if (l != null && !listeners.contains(l)) listeners.add(l);
	}
	public void removePickupRequestListener(PickupRequestListener l) {
		listeners.remove(l);
	}

	private void notifyPickupRequestListeners() {
		for (PickupRequestListener l : new ArrayList<>(listeners)) {
			try { l.onPickupRequestsChanged(); } catch (Exception ex) { }
		}
	}
	public void reviewPickupRequest(PickupRequest request,boolean approve) {
		if(approve) {
			request.approve();
			// Add to global registry so manager can see available items
			FoodItem.addFoodItem(request.getFoodItem());
			System.out.println("Pickup request approved for item: " + request.getFoodItem().getName());
			notifyPickupRequestListeners();
		} else {
			request.reject();
			System.out.println("Pickup request rejected for item: " + request.getFoodItem().getName());
			notifyPickupRequestListeners();
		}
	}

	public void confirmDelivery(Delivery d) {
		if (d == null) return;
		d.confirmDelivery();
		System.out.println("Order is delivered: DeliveryID=" + d.getDeliveryID());
		notifyPickupRequestListeners();
	}

	public Delivery assignDelivery(FoodItem fooditem, Recipient recipient, String driverName){
		int DeliveryID=deliveries.size()+1;
		java.util.Date now = new java.util.Date();
		String driver = (driverName == null || driverName.trim().isEmpty()) ? ("Driver-" + DeliveryID) : driverName;
		Delivery delivery = new Delivery(DeliveryID, fooditem, recipient, driver, now, "IN_TRANSIT");
		// update food item status when assigned
		fooditem.updateStatus("OUT FOR DELIVERY");
		deliveries.add(delivery);
		System.out.println("Delivery assigned: " + fooditem.getName() + " → " + recipient.getName() + " by " + driver);
		notifyPickupRequestListeners();
		return delivery;
	}
	public void addRecipient(Recipient recipient) {
		recipients.add(recipient);
	}
	public ArrayList<PickupRequest> getPickupRequest(){
		return pickupRequests;
	}
	public ArrayList<Delivery> getDeliveries(){
		return deliveries;
	}
    public ArrayList<Recipient> getRecipients(){
    	return recipients;
    }

	public ArrayList<FoodItem> getFoodItems() {
		// prefer the global food registry maintained by FoodItem class to avoid duplication
		return FoodItem.getAllFoodItems();
	}



}