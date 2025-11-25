package src.models;

public class Slot {
    private final int slotId;
    private final String examDate;   // Format: "YYYY-MM-DD"
    private final String semester;   // "III", "V", "VII"
    private final String time;       // Format: "hh.mm am/pm - hh.mm am/pm"

    public Slot(int slotId, String examDate, String semester, String time) {
        this.slotId = slotId;
        this.examDate = examDate;
        this.semester = semester;
        this.time = time;
    }

    public int getSlotId() {
        return slotId;
    }

    public String getExamDate() {
        return examDate;
    }

    public String getSemester() {
        return semester;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Slot{" +
                "slotId=" + slotId +
                ", examDate='" + examDate + '\'' +
                ", semester='" + semester + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
