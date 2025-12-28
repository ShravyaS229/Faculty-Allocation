package src.dao;
import src.DBConnection;

import src.models.Subject;
import java.sql.*;
import java.util.*;

public class SubjectDAO {

    public List<Subject> getSubjectsBySemester(String semester) {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT subject_name FROM subjects WHERE semester=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, semester);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Subject(semester, rs.getString("subject_name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
