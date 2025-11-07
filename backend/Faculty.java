package backend;

public class Faculty {
    private String name;
    private String designation;

    public Faculty(String name, String designation) {
        this.name = name;
        this.designation = designation;
    }

    public String getName() {
        return name;
    }

    public String getDesignation() {
        return designation;
    }
}