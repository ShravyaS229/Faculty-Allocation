package src.dao;

import src.DBConnection;
import src.models.AllocationResult;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AllocationDAO {
    public boolean saveAllocation(AllocationResult a) {
        // FIX: Changed table name from 'allocations' to 'allocation_result'
        String sql = "INSERT INTO allocation_result(exam_date, time, room_no, semester, subject, faculty_name, designation) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) {
                System.err.println("Connection failed. Cannot save allocation.");
                return false;
            }
            
            ps.setString(1, a.getExamDate());
            ps.setString(2, a.getTime());
            ps.setInt(3, a.getRoomNo());
            ps.setString(4, a.getSemester());
            ps.setString(5, a.getSubject());
            ps.setString(6, a.getFacultyName());
            ps.setString(7, a.getDesignation());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            // This line now reports the correct error, which should go away after the fix.
            System.err.println("Allocation Save Error: " + e.getMessage());
            return false;
        }
    }
}