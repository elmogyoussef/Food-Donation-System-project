
public class Recipient {
    private int RecipientID;
    private String Name;
    private String location;
    private String Type;
	public Recipient(int recipientID, String name, String location, String type) {
		this.RecipientID = recipientID;
		this.Name = name;
		this.location = location;
		this.Type = type;
	}
	public int getRecipientID() {
		return RecipientID;
	}
	
	public String getName() {
		return Name;
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getType() {
		return Type;
	}

	@Override
	public String toString() {
		return "[" + RecipientID + "] " + Name + " (" + Type + ") - " + location;
	}
}
