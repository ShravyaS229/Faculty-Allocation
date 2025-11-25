package src.models;

public class Faculty {
    private int facultyId;
    private String name;
    private String designation;
    private String email;  // Add email field

    public Faculty(int facultyId, String name, String designation, String email) {
        this.facultyId = facultyId;
        this.name = name;
        this.designation = designation;
        this.email = email;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public String getName() {
        return name;
    }

    public String getDesignation() {
        return designation;
    }

    public String getEmail() {   // <-- Add this getter
        return email;
    }
}
