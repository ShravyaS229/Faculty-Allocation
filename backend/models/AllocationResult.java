package backend.models;

public class AllocationResult {
    private Faculty faculty;
    private Room room;
    private Slot slot;
    private Subject subject;

    public AllocationResult(Faculty faculty, Room room, Slot slot, Subject subject) {
        this.faculty = faculty;
        this.room = room;
        this.slot = slot;
        this.subject = subject;
    }

    public Faculty getFaculty() { return faculty; }
    public Room getRoom() { return room; }
    public Slot getSlot() { return slot; }
    public Subject getSubject() { return subject; }
}

