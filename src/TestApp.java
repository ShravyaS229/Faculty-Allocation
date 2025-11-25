package src;

import src.dao.FacultyDAO;
import src.models.Faculty;
import src.backend.AllocationLogic; 
import java.sql.Connection; // <-- ADD THIS IMPORT

public class TestApp {
    public static void main(String[] args) {
        
        // --- 1. Test DB Connection (Run TestDB logic inline) ---
        Connection con = DBConnection.getConnection();
        if (con != null) {
            System.out.println("Connection Successful! ✅");
        } else {
            System.out.println("Connection Failed! ❌");
        }
        System.out.println("----------------------------------------");
        
        // --- 2. Test Faculty Fetch (Original TestApp logic) ---
        FacultyDAO dao = new FacultyDAO();
        System.out.println("Fetching all faculty...");
        for (Faculty f : dao.getAllFaculty()) {
            System.out.println(f.getFacultyId() + " - " + f.getName());
        }
        System.out.println("----------------------------------------");
        
        // --- 3. Run Allocation Logic ---
        System.out.println("Running Allocation Logic...");
        AllocationLogic allocator = new AllocationLogic();
        allocator.generateAllocation();
    }
}
