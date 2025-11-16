package backend.models;

public class Faculty {
    private int id;
    private String name;
    private String designation;
    private boolean isAvailable;

    public Faculty(int id, String name, String designation, boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.designation = designation;
        this.isAvailable = isAvailable;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDesignation() { return designation; }
    public boolean isAvailable() { return isAvailable; }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
}

