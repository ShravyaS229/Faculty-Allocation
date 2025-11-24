package src.dao;

import src.DBConnection;
import src.models.Subject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {
    public List<Subject> getSubjectsBySemester(String sem) {
        List<Subject> list = new ArrayList<>();
        // In a real scenario, you might want to join with slots to only get subjects 
        // scheduled for that day/time, but here we assume all subjects in the semester 
        // need to be allocated rooms/invigilators across the rooms available in that slot.
        String sql = "SELECT * FROM subjects WHERE semester = ?";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, sem);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Subject(
                        rs.getString("subject_code"),
                        rs.getString("subject_name"),
                        rs.getString("semester")
                ));
            }
        } catch (Exception e) {
            System.out.println("Subject Fetch Error: " + e.getMessage());
        }
        return list;
    }
}