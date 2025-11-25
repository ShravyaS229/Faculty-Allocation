package src.dao;

import src.DBConnection;
import src.models.AllocationResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AllocationDAO {

    // Save allocation to DB
    public boolean saveAllocation(AllocationResult a) {
        String sql = "INSERT INTO allocation_result(exam_date, time, room_no, semester, subject, faculty_name, designation) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) {
                System.err.println("Connection failed. Cannot save allocation.");
                return false;
            }

            ps.setString(1, a.getDate());
            ps.setString(2, a.getTime());
            ps.setInt(3, a.getRoom());
            ps.setString(4, a.getSemester());
            ps.setString(5, a.getSubject());
            ps.setString(6, a.getFacultyName());
            ps.setString(7, a.getDesignation());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("Allocation Save Error: " + e.getMessage());
            return false;
        }
    }

    // Fetch all allocations from DB
    public List<AllocationResult> getAllAllocations() {
        List<AllocationResult> list = new ArrayList<>();
        String sql = "SELECT * FROM allocation_result";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AllocationResult ar = new AllocationResult(
                        rs.getString("exam_date"),
                        rs.getString("time"),
                        rs.getInt("room_no"),
                        rs.getString("semester"),
                        rs.getString("subject"),
                        rs.getString("faculty_name"),
                        rs.getString("designation")
                );
                list.add(ar);
            }

        } catch (Exception e) {
            System.err.println("Fetch Allocations Error: " + e.getMessage());
        }

        return list;
    }
}
