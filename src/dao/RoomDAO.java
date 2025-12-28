package src.dao;

import src.models.Room;
import java.sql.*;
import java.util.*;
import src.DBConnection;
public class RoomDAO {

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();

        // ✅ correct table name: rooms
        String sql = "SELECT room_no FROM rooms";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Room(rs.getInt("room_no")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
