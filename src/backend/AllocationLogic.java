package src.backend;

import src.dao.*;
import src.models.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AllocationLogic {

    private final SlotDAO slotDAO = new SlotDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final AllocationDAO allocationDAO = new AllocationDAO();

    // Tracks faculty assignments per day
    private final Map<String, Map<Integer, Integer>> facultyDailySlots = new HashMap<>();
    // Key: Date, Value: Map<FacultyId, slots assigned today>

    public void generateAllocation() {

        List<Slot> slots = slotDAO.getAllSlots();
        List<Room> rooms = roomDAO.getAllRooms();
        List<Faculty> allFaculty = facultyDAO.getAllFaculty();

        // Filter eligible faculty (Assistant + Associate only)
        List<Faculty> assistants = new ArrayList<>();
        List<Faculty> associates = new ArrayList<>();
        for (Faculty f : allFaculty) {
            String d = f.getDesignation();
            if (d.contains("Assistant")) assistants.add(f);
            else if (d.contains("Associate")) associates.add(f);
        }

        // Reduce room count if needed
        int totalRooms = Math.min(rooms.size(), assistants.size() + associates.size());
        rooms = rooms.subList(0, totalRooms);

        // Sort slots by date & start time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h.mm a");
        slots.sort(Comparator
                .comparing(Slot::getExamDate)
                .thenComparing(s -> LocalTime.parse(s.getTime().split(" - ")[0], formatter)));

        int roomIndex = 0;
        int assistantIndex = 0;
        int associateIndex = 0;

        System.out.println("Room | Date | Time | Subject | Faculty");
        System.out.println("---------------------------------------------------");

        for (Slot slot : slots) {
            List<Subject> subjects = subjectDAO.getSubjectsBySemester(slot.getSemester());
            if (subjects.isEmpty()) continue;

            // Ensure every room is used
            for (int i = 0; i < rooms.size(); i++) {
                Room room = rooms.get(roomIndex % rooms.size());
                roomIndex++;

                // Cycle subjects if fewer subjects than rooms
                Subject subject = subjects.get(i % subjects.size());

                Faculty assignedFaculty = null;
                String date = slot.getExamDate();

                // Assign Assistant (max 2 slots/day)
                int attempts = 0;
                while (assignedFaculty == null && attempts < assistants.size()) {
                    Faculty candidate = assistants.get(assistantIndex % assistants.size());
                    int assigned = facultyDailySlots
                            .getOrDefault(date, new HashMap<>())
                            .getOrDefault(candidate.getFacultyId(), 0);
                    if (assigned < 2) {
                        assignedFaculty = candidate;
                        assistantIndex++;
                        facultyDailySlots.computeIfAbsent(date, k -> new HashMap<>())
                                .put(candidate.getFacultyId(), assigned + 1);
                    } else {
                        assistantIndex++;
                        attempts++;
                    }
                }

                // Assign Associate (max 1 slot/day) if no Assistant available
                attempts = 0;
                while (assignedFaculty == null && attempts < associates.size()) {
                    Faculty candidate = associates.get(associateIndex % associates.size());
                    int assigned = facultyDailySlots
                            .getOrDefault(date, new HashMap<>())
                            .getOrDefault(candidate.getFacultyId(), 0);
                    if (assigned < 1) {
                        assignedFaculty = candidate;
                        associateIndex++;
                        facultyDailySlots.computeIfAbsent(date, k -> new HashMap<>())
                                .put(candidate.getFacultyId(), assigned + 1);
                    } else {
                        associateIndex++;
                        attempts++;
                    }
                }

                if (assignedFaculty == null) {
                    // Force assign anyone if still null (to avoid empty rooms)
                    if (!assistants.isEmpty()) {
                        assignedFaculty = assistants.get(assistantIndex % assistants.size());
                        assistantIndex++;
                        facultyDailySlots.computeIfAbsent(date, k -> new HashMap<>())
                                .put(assignedFaculty.getFacultyId(),
                                        facultyDailySlots.getOrDefault(date, new HashMap<>())
                                                .getOrDefault(assignedFaculty.getFacultyId(), 0) + 1);
                    } else if (!associates.isEmpty()) {
                        assignedFaculty = associates.get(associateIndex % associates.size());
                        associateIndex++;
                        facultyDailySlots.computeIfAbsent(date, k -> new HashMap<>())
                                .put(assignedFaculty.getFacultyId(),
                                        facultyDailySlots.getOrDefault(date, new HashMap<>())
                                                .getOrDefault(assignedFaculty.getFacultyId(), 0) + 1);
                    }
                }

                AllocationResult ar = new AllocationResult(
                        slot.getExamDate(),
                        slot.getTime(),
                        room.getRoomNo(),
                        slot.getSemester(),
                        subject.getName(),
                        assignedFaculty.getName(),
                        assignedFaculty.getDesignation()
                );

                allocationDAO.saveAllocation(ar);

                System.out.printf("%3d | %s | %s | %s | %s%n",
                        room.getRoomNo(),
                        slot.getExamDate(),
                        slot.getTime(),
                        subject.getName(),
                        assignedFaculty.getName());
            }
        }

        System.out.println("---------------------------------------------------");
        System.out.println("All rooms allocated with no empty halls.");
    }
}