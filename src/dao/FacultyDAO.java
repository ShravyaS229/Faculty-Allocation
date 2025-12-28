package src.dao;

import src.models.Faculty;
import java.sql.*;
import java.util.*;
import src.DBConnection;
public class FacultyDAO {

    public List<Faculty> getAllFaculty() {
        List<Faculty> list = new ArrayList<>();
        String sql = "SELECT faculty_id, name, designation FROM faculty";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Faculty(
                        rs.getInt("faculty_id"),
                        rs.getString("name"),
                        rs.getString("designation")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
