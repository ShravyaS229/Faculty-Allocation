package src.models;

public class AllocationResult {
    private String date;
    private String time;
    private int roomNo;
    private String semester;
    private String subject;
    private String facultyName;
    private String designation;

    public AllocationResult(String date, String time, int roomNo,
                            String semester, String subject,
                            String facultyName, String designation) {
        this.date = date;
        this.time = time;
        this.roomNo = roomNo;
        this.semester = semester;
        this.subject = subject;
        this.facultyName = facultyName;
        this.designation = designation;
    }

    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getRoomNo() { return roomNo; }
    public String getSemester() { return semester; }
    public String getSubject() { return subject; }
    public String getFacultyName() { return facultyName; }
    public String getDesignation() { return designation; }
}
