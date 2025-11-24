package src.dao;

import src.DBConnection;
import src.models.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Room(
                        rs.getInt("room_no"),
                        rs.getInt("capacity") // Fetch capacity from DB
                        // If capacity is not in DB, you could use a fixed value: 30 
                ));
            }
        } catch (Exception e) {
            System.out.println("Room Fetch Error: " + e.getMessage());
        }
        return list;
    }
}