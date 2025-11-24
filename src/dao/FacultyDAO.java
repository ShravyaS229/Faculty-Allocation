package src.dao;

import src.DBConnection;
import src.models.Faculty;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacultyDAO {
    public List<Faculty> getAllFaculty() {
        List<Faculty> list = new ArrayList<>();
        String sql = "SELECT * FROM faculty";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Faculty(
                        rs.getInt("faculty_id"),
                        rs.getString("name"),
                        rs.getString("designation"),
                        rs.getBoolean("is_senior")
                ));
            }
        } catch (Exception e) {
            System.out.println("Faculty Fetch Error: " + e.getMessage());
        }
        return list;
    }
}