package src.dao;

import src.models.Slot;
import java.sql.*;
import java.util.*;
import src.DBConnection;
public class SlotDAO {

    public List<Slot> getAllSlots() {
        List<Slot> list = new ArrayList<>();

        // ✅ correct table name: slots
        String sql = "SELECT exam_date, semester, time FROM slots";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Slot(
                        rs.getDate("exam_date").toString(),
                        rs.getString("semester"),
                        rs.getString("time")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
