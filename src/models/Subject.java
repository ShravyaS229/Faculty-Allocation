package src.models;

public class Subject {
    private String semester;
    private String name;

    public Subject(String semester, String name) {
        this.semester = semester;
        this.name = name;
    }

    public String getSemester() { return semester; }
    public String getName() { return name; }
}
