package src.dao;

import src.models.AllocationResult;
import java.sql.*;
import src.DBConnection;
public class AllocationDAO {

    public void saveAllocation(AllocationResult ar) {

        String sql =
        "INSERT INTO allocation_result " +
        "(exam_date, time, room_no, semester, subject, faculty_name, designation) " +
        "VALUES (?,?,?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ar.getDate());
            ps.setString(2, ar.getTime());
            ps.setInt(3, ar.getRoomNo());
            ps.setString(4, ar.getSemester());
            ps.setString(5, ar.getSubject());
            ps.setString(6, ar.getFacultyName());
            ps.setString(7, ar.getDesignation());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
