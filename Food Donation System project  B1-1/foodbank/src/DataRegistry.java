import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataRegistry implements Serializable {
    private static final long serialVersionUID = 100L;
    private static final String DATA_FILE = "food_donation_data.ser";
    
    // --- Master Lists (The Data We Need to Save) ---
    private List<Provider> providers = new ArrayList<>();
    private List<FoodItem> foodItems = new ArrayList<>();
    private List<Recipient> recipients = new ArrayList<>();
    private List<Delivery> deliveries = new ArrayList<>();
    private List<PickupRequest> pickupRequests = new ArrayList<>();
    
    // yosta5dam f kol keta
    private static DataRegistry instance;

    private DataRegistry() {
}
    public static DataRegistry getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    // --- File System Methods (Save & Load) ---

    public void save() {
        try (FileOutputStream fileOut = new FileOutputStream(DATA_FILE);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            
            objectOut.writeObject(this);
            System.out.println("\n--- DataRegistry saved successfully to " + DATA_FILE + " ---");
            
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static DataRegistry load() {
        try (FileInputStream fileIn = new FileInputStream(DATA_FILE);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            
            DataRegistry loadedRegistry = (DataRegistry) objectIn.readObject();
            System.out.println("--- DataRegistry loaded successfully from " + DATA_FILE + " ---");
            return loadedRegistry;
            
        } catch (FileNotFoundException e) {
            System.out.println("--- No existing data file found. Starting with new DataRegistry. ---");
            return new DataRegistry();
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data. Starting with new DataRegistry. Error: " + e.getMessage());
            e.printStackTrace();
            return new DataRegistry();
        }
    }

    // --- Public Accessor Methods (Add & Get) ---
    
    public void addProvider(Provider p) { providers.add(p); }
    public void addFoodItem(FoodItem item) { foodItems.add(item); }
    public void addRecipient(Recipient r) { recipients.add(r); }
    public void addPickupRequest(PickupRequest r) { pickupRequests.add(r); }
    public void addDelivery(Delivery d) { deliveries.add(d); }

    public List<Provider> getProviders() { return providers; }
    public List<FoodItem> getFoodItems() { return foodItems; }
    public List<Recipient> getRecipients() { return recipients; }
    public List<Delivery> getDeliveries() { return deliveries; }
    public List<PickupRequest> getPickupRequests() { return pickupRequests; }
    
    public void updateFoodItemIdGenerator() {
        int maxId = foodItems.stream()
                             .mapToInt(FoodItem::getItemId)
                             .max()
                             .orElse(0);
        FoodItem.setLastId(maxId);
    }
}