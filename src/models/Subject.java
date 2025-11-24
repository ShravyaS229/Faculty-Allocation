package src.models;

public class Subject {
    private String code;
    private String name;
    private String semester;

    public Subject(String code, String name, String semester) {
        this.code = code;
        this.name = name;
        this.semester = semester;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getSemester() { return semester; }
}