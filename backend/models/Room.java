package backend.models;

public class Room {
    private int id;
    private String roomName;
    private int capacity;

    public Room(int id, String roomName, int capacity) {
        this.id = id;
        this.roomName = roomName;
        this.capacity = capacity;
    }

    public int getId() { return id; }
    public String getRoomName() { return roomName; }
    public int getCapacity() { return capacity; }
}

