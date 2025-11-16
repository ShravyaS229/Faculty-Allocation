package backend.models;

public class Subject {
    private int id;
    private String subjectCode;
    private String subjectName;
    private int semester;

    public Subject(int id, String subjectCode, String subjectName, int semester) {
        this.id = id;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.semester = semester;
    }

    public int getId() { return id; }
    public String getSubjectCode() { return subjectCode; }
    public String getSubjectName() { return subjectName; }
    public int getSemester() { return semester; }
}

