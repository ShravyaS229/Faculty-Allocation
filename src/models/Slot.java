package src.models;

public class Slot {
    private int slotId;
    private String examDate;
    private String semester;
    private String time;

    public Slot(int slotId, String examDate, String semester, String time) {
        this.slotId = slotId;
        this.examDate = examDate;
        this.semester = semester;
        this.time = time;
    }

    public int getSlotId() { return slotId; }
    public String getExamDate() { return examDate; }
    public String getSemester() { return semester; }
    public String getTime() { return time; }
}