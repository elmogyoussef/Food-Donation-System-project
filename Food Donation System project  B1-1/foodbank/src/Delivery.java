import java.util.Date;

public class Delivery {
	private int DeliveryID;
	private FoodItem fooditem;
    private Recipient recipient;
	private String DriverName;
	private Date DeliveryTime;
	private String Status;

	public Delivery(int DeliveryID , FoodItem fooditem,Recipient recipient,String DriverName,Date DeliveryTime,String Status) {
		this.DeliveryID=DeliveryID;
	    this.recipient=recipient;
		this.fooditem=fooditem;
		this.DriverName=DriverName;
		this.DeliveryTime=DeliveryTime;
		this.Status=Status;

	}

	public Recipient getRecipient() {
		return recipient;
	}

	public int getDeliveryID() {
		return DeliveryID;
	}
	public FoodItem getFooditem() {
		return fooditem;
	}
	
	public String getDriverName() {
		return DriverName;
	}
	
	public Date getDeliveryTime() {
		return DeliveryTime;
	}
	
	public String getStatus() {
		return Status;
	}
	
	public void startDelivery() {
		fooditem.updateStatus("OUT FOR DELIVERY");
			Status = "OUT FOR DELIVERY";
			DeliveryTime = new Date();
			System.out.println("Food is out for delivery");
		}
		
	public void confirmDelivery() {
		fooditem.updateStatus("delivered");
		this.Status = "DELIVERED";
		this.DeliveryTime = new Date();
		fooditem.markDelivered();
		System.out.println("Order is delivered");
	}

	@Override
	public String toString() {
		return "Delivery["+DeliveryID+"] " + (fooditem!=null?fooditem.getName():"Unknown") + " → " + (recipient!=null?recipient.getName():"Unknown") + " driver:" + DriverName + " status:" + Status;
	}
	

}
