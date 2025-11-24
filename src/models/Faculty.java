package src.models;

public class Faculty {
    private int facultyId;
    private String name;
    private String designation;
    private boolean isSenior;

    public Faculty(int facultyId, String name, String designation, boolean isSenior) {
        this.facultyId = facultyId;
        this.name = name;
        this.designation = designation;
        this.isSenior = isSenior;
    }

    public int getFacultyId() { return facultyId; }
    public String getName() { return name; }
    public String getDesignation() { return designation; }
    public boolean isSenior() { return isSenior; }
}