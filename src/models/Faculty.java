package src.models;

public class Faculty {
    private int facultyId;
    private String name;
    private String designation;

    public Faculty(int facultyId, String name, String designation) {
        this.facultyId = facultyId;
        this.name = name;
        this.designation = designation;
    }

    public int getFacultyId() { return facultyId; }
    public String getName() { return name; }
    public String getDesignation() { return designation; }
}
