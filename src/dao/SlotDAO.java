package src.dao;

import src.DBConnection;
import src.models.Slot;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SlotDAO {
    public List<Slot> getAllSlots() {
        List<Slot> list = new ArrayList<>();
        String sql = "SELECT * FROM slots ORDER BY exam_date, time, semester";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Slot(
                        rs.getInt("slot_id"),
                        rs.getString("exam_date"),
                        rs.getString("semester"),
                        rs.getString("time")
                ));
            }
        } catch (Exception e) {
            System.out.println("Slot Fetch Error: " + e.getMessage());
        }
        return list;
    }
}