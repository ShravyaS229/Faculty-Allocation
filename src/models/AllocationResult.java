package src.models;

public class AllocationResult {
    private String examDate;
    private String time;
    private int roomNo;
    private String semester;
    private String subject;
    private String facultyName;
    private String designation;

    public AllocationResult(String examDate, String time, int roomNo, String semester,
                            String subject, String facultyName, String designation) {
        this.examDate = examDate;
        this.time = time;
        this.roomNo = roomNo;
        this.semester = semester;
        this.subject = subject;
        this.facultyName = facultyName;
        this.designation = designation;
    }

    public String getExamDate() { return examDate; }
    public String getTime() { return time; }
    public int getRoomNo() { return roomNo; }
    public String getSemester() { return semester; }
    public String getSubject() { return subject; }
    public String getFacultyName() { return facultyName; }
    public String getDesignation() { return designation; }
}