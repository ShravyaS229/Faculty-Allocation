package backend.models;

public class Slot {
    private int id;
    private String slotName;   // Example: "SLOT 1"
    private String timeRange;  // Example: "9:00 AM - 10:00 AM"

    public Slot(int id, String slotName, String timeRange) {
        this.id = id;
        this.slotName = slotName;
        this.timeRange = timeRange;
    }

    public int getId() { return id; }
    public String getSlotName() { return slotName; }
    public String getTimeRange() { return timeRange; }
}

