package src.dao;

import src.models.Slot;
import src.dao.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import src.DBConnection;
public class SlotDAO {

    private final Connection conn;

    public SlotDAO() {
        conn = DBConnection.getConnection(); // Make sure this returns a valid connection
    }

    /**
     * Fetch all slots for the given exam dates
     * Sorted by examDate and start time
     */
    public List<Slot> getAllSlots() {
        List<Slot> slots = new ArrayList<>();

        // Only fetch slots between 3rd, 4th, 5th Nov 2025 (change as per your DB)
        String sql = "SELECT slot_id, exam_date, semester, time " +
                     "FROM slots " +
                     "WHERE exam_date BETWEEN '2025-11-03' AND '2025-11-05' " +
                     "ORDER BY exam_date, STR_TO_DATE(SUBSTRING_INDEX(time, ' - ', 1), '%h.%i %p')";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int slotId = rs.getInt("slot_id");
                String examDate = rs.getString("exam_date");
                String semester = rs.getString("semester");
                String time = rs.getString("time");

                Slot slot = new Slot(slotId, examDate, semester, time);
                slots.add(slot);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return slots;
    }

    /**
     * Fetch slots for a specific semester
     */
    public List<Slot> getSlotsBySemester(String semester) {
        List<Slot> slots = new ArrayList<>();
        String sql = "SELECT slot_id, exam_date, semester, time " +
                     "FROM slots " +
                     "WHERE semester = ? AND exam_date BETWEEN '2025-11-03' AND '2025-11-05' " +
                     "ORDER BY exam_date, STR_TO_DATE(SUBSTRING_INDEX(time, ' - ', 1), '%h.%i %p')";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, semester);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int slotId = rs.getInt("slot_id");
                    String examDate = rs.getString("exam_date");
                    String sem = rs.getString("semester");
                    String time = rs.getString("time");

                    slots.add(new Slot(slotId, examDate, sem, time));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return slots;
    }
}
