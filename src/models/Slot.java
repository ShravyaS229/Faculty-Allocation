package src.models;

public class Slot {
    private String examDate;
    private String semester;
    private String time;

    public Slot(String examDate, String semester, String time) {
        this.examDate = examDate;
        this.semester = semester;
        this.time = time;
    }

    public String getExamDate() { return examDate; }
    public String getSemester() { return semester; }
    public String getTime() { return time; }
}
