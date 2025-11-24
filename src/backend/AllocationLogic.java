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
    private final Random random = new Random();

    // --- Mock Absence Data (Replace with a database fetch in production) ---
    // Key: Faculty ID, Value: Set of Dates Absent
    private final Map<Integer, Set<String>> absentFacultyByDate = initializeAbsenceData(); 

    // Map to track daily duties for each faculty (Key: Date, Value: Map<Faculty ID, List of Slot Times>)
    private final Map<String, Map<Integer, List<LocalTime>>> facultyDailySchedule = new HashMap<>();

    /** Initializes temporary mock absence data for testing constraints. */
    private Map<Integer, Set<String>> initializeAbsenceData() {
        Map<Integer, Set<String>> absenceData = new HashMap<>();
        // Example: Faculty ID 1001 is absent on 2025-09-15
        absenceData.put(1001, new HashSet<>(Arrays.asList("2025-09-15")));
        // Example: Faculty ID 1005 is absent on 2025-09-16
        absenceData.put(1005, new HashSet<>(Arrays.asList("2025-09-16")));
        return absenceData;
    }

    public void generateAllocation() {
        List<Slot> allSlots = slotDAO.getAllSlots();
        List<Room> allRooms = roomDAO.getAllRooms();
        List<Faculty> allFaculty = facultyDAO.getAllFaculty();
        
        System.out.println("Starting exam allocation with complex constraints...");
        System.out.println("Total Slots: " + allSlots.size() + ", Total Rooms: " + allRooms.size() + ", Total Faculty: " + allFaculty.size());
        
        // 1. Sort slots by date and time to process chronologically
        allSlots.sort(Comparator
            .comparing(Slot::getExamDate)
            .thenComparing(slot -> LocalTime.parse(slot.getTime().split(" - ")[0], DateTimeFormatter.ofPattern("h.mm a")))
        );

        for (Slot slot : allSlots) {
            
            String currentDate = slot.getExamDate();
            LocalTime currentStartTime = LocalTime.parse(slot.getTime().split(" - ")[0], DateTimeFormatter.ofPattern("h.mm a"));
            
            // Ensure schedule map exists for the current date
            facultyDailySchedule.computeIfAbsent(currentDate, k -> new HashMap<>());

            List<Subject> subjects = subjectDAO.getSubjectsBySemester(slot.getSemester());
            if (subjects.isEmpty()) continue;

            int subjectIndex = 0;
            
            for (Room room : allRooms) {
                if (subjectIndex >= subjects.size()) break; 
                
                Subject subject = subjects.get(subjectIndex);
                
                // --- CORE LOGIC CALL: Find the best faculty based on all rules ---
                Faculty invigilator = findAvailableFaculty(allFaculty, currentDate, currentStartTime);

                if (invigilator != null) {
                    
                    // Update daily schedule tracking
                    facultyDailySchedule.get(currentDate)
                        .computeIfAbsent(invigilator.getFacultyId(), k -> new ArrayList<>())
                        .add(currentStartTime);

                    AllocationResult result = new AllocationResult(
                        currentDate,
                        slot.getTime(),
                        room.getRoomNo(),
                        slot.getSemester(),
                        subject.getName() + " (" + subject.getCode() + ")", 
                        invigilator.getName(),
                        invigilator.getDesignation()
                    );
                    
                    allocationDAO.saveAllocation(result);
                    
                    System.out.printf("Allocated: %s | Sem: %s | Time: %s | Room: %d | Subject: %s | Invigilator: %s%n", 
                                        currentDate, slot.getSemester(), slot.getTime(), room.getRoomNo(), subject.getName(), invigilator.getName());
                    
                    subjectIndex++;
                } else {
                    System.err.println("CRITICAL: No eligible faculty available for allocation on " + currentDate + " at " + slot.getTime() + ". Proceeding to next slot.");
                    break; 
                }
            }
        }
        
        System.out.println("\nAllocation complete. Check the 'allocation_result' table.");
    }

    private Faculty findAvailableFaculty(List<Faculty> allFaculty, String currentDate, LocalTime currentStartTime) {
        
        List<Faculty> availableFaculty = new ArrayList<>();
        
        for (Faculty faculty : allFaculty) {
            
            String designation = faculty.getDesignation();

            // --- P3 EXCLUSION RULE: EXCLUDE ALL PROFESSORS AND HODS ---
            if (designation.contains("Professor") && !designation.contains("Associate Professor") && !designation.contains("Assistant Professor")) {
                 // Excludes General Professors, H.Q. Professors, etc.
                 // This must be run BEFORE the P1/P2 checks below.
                 continue;
            }
            if (designation.contains("HOD")) {
                 // Excludes anyone explicitly marked as HOD.
                 continue;
            }

            // Rule: Check Absence Status (Admin Marked Absent)
            if (absentFacultyByDate.containsKey(faculty.getFacultyId()) && 
                absentFacultyByDate.get(faculty.getFacultyId()).contains(currentDate)) {
                
                // If you want to show this in the main output, you can move the print statement here:
                // System.out.println("SKIPPING: " + faculty.getName() + " is marked ABSENT on " + currentDate);
                continue;
            }

            // Get the faculty's schedule for the current day
            List<LocalTime> dailySlots = facultyDailySchedule.getOrDefault(currentDate, new HashMap<>())
                                                            .getOrDefault(faculty.getFacultyId(), Collections.emptyList());

            // Rule: Max 2 slots per day
            if (dailySlots.size() >= 2) {
                continue;
            }

            // Rule: Must NOT be back-to-back (assumes 1 hour slot duration)
            if (dailySlots.stream().anyMatch(
                previousTime -> currentStartTime.minusHours(1).equals(previousTime)
            )) {
                continue;
            }

            availableFaculty.add(faculty);
        }

        // 2. Sort available faculty based on hierarchy (P1: Assistant, P2: Associate) and then by least duty count
        availableFaculty.sort(Comparator
            .comparingInt((Faculty f) -> getPriority(f.getDesignation())) // Primary sort: P1 (1), P2 (2)
            .thenComparingInt(f -> facultyDailySchedule.getOrDefault(currentDate, new HashMap<>()).getOrDefault(f.getFacultyId(), Collections.emptyList()).size()) // Secondary sort: Least busy today
            .thenComparing(f -> random.nextInt()) // Tertiary sort: Random tie-breaker
        );
        
        if (availableFaculty.isEmpty()) {
            return null;
        }

        // 3. Return the best available faculty
        return availableFaculty.get(0);
    }
    
    /** Assigns priority rank: 1 (highest) for Assistant, 2 for Associate. */
    private int getPriority(String designation) {
        // P1
        if (designation.contains("Assistant Professor")) {
            return 1;
        }
        // P2
        if (designation.contains("Associate Professor")) {
            return 2;
        }
        // Should only be reached if a staff member slipped past the exclusion filter, giving them low priority.
        return 99; 
    }
}