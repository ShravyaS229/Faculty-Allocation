package src.models;

public class Room {
    private int roomNo;
    private int capacity;

    public Room(int roomNo, int capacity) {
        this.roomNo = roomNo;
        this.capacity = capacity;
    }

    public int getRoomNo() { return roomNo; }
    public int getCapacity() { return capacity; }
}