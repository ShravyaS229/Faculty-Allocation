package src.backend;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

public class AdminPage extends Application {

    private final List<Faculty> facultyList = new ArrayList<>();
    private final Set<Integer> absentFacultyIds = new HashSet<>();
    private static final int CAPACITY_PER_ROOM = 30;

    @Override
    public void start(Stage stage) {
        preloadFaculty();

        TableView<ResultRow> table = new TableView<>();
        ObservableList<ResultRow> data = FXCollections.observableArrayList(runAllocationForFullDay());

        TableColumn<ResultRow, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().date));

        TableColumn<ResultRow, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().time));

        TableColumn<ResultRow, String> roomCol = new TableColumn<>("Room No");
        roomCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().room));

        TableColumn<ResultRow, String> semCol = new TableColumn<>("Semester");
        semCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().semester));

        TableColumn<ResultRow, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().subject));

        TableColumn<ResultRow, String> facCol = new TableColumn<>("Faculty Name");
        facCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().facultyName));

        TableColumn<ResultRow, String> idCol = new TableColumn<>("Faculty ID");
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().facultyId));

        TableColumn<ResultRow, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().email));

        TableColumn<ResultRow, String> desigCol = new TableColumn<>("Designation");
        desigCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().designation));

        table.getColumns().addAll(dateCol, timeCol, roomCol, semCol, subjectCol, facCol, idCol, emailCol, desigCol);
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox root = new VBox(10, table);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 1200, 600);
        stage.setScene(scene);
        stage.setTitle("Exam Day Allocation");
        stage.show();
    }

    private List<ResultRow> runAllocationForFullDay() {

        List<Slot> slots = Arrays.asList(
            // VII Semester
            new Slot("2025-11-17", "VII", "09:15 - 10:15",
                    Collections.singletonList(new Subject("IS3002-1", "Ethical Hacking and Network Defense"))),
            new Slot("2025-11-17", "VII", "13:00 - 14:00",
                    Collections.singletonList(new Subject("MG1002-1","Financial Management"))),
            new Slot("2025-11-18", "VII", "09:15 - 10:15",
                    Arrays.asList(
                            new Subject("IS4224-1", "Software Defined Networks"),
                            new Subject("IS2213-1", "Object Oriented Modelling and Design"),
                            new Subject("IS1201-1", "Total Quality Management for Sustainable Growth")
                    )),
            new Slot("2025-11-18", "VII", "13:00 - 14:00",
                    Arrays.asList(
                            new Subject("IS2314-1", "Software Architecture and Design Patterns"),
                            new Subject("IS2302-1", "User Interface Design"),
                            new Subject("IS3334-1", "Social and Web Analytics")
                    )),
            new Slot("2025-11-19", "VII", "09:15 - 10:15",
                    Collections.singletonList(new Subject("OEC", "Open Elective Course"))),
            new Slot("2025-11-19", "VII", "13:00 - 14:00", Collections.emptyList()),

            // V Semester
            new Slot("2025-11-17", "V", "10:30 - 11:30",
                    Collections.singletonList(new Subject("IS3001-1","Data Communication and Networking"))),
            new Slot("2025-11-17", "V", "14:10 - 15:10",
                    Collections.singletonList(new Subject("IS3101-1","Operating Systems Fundamentals"))),
            new Slot("2025-11-18", "V", "10:30 - 11:30",
                    Collections.singletonList(new Subject("IS2002-1","Machine Learning Foundations"))),
            new Slot("2025-11-18", "V", "14:10 - 15:10",
                    Arrays.asList(
                            new Subject("IS2212-1","Business Intelligence and its Applications"),
                            new Subject("IS3241-1","Cloud Computing"),
                            new Subject("IS2201-1","Information Storage Management"),
                            new Subject("IS3242-1","Graphics and Animation")
                    )),
            new Slot("2025-11-19", "V", "10:30 - 11:30",
                    Collections.singletonList(new Subject("HU1010-1","Research Methodology"))),
            new Slot("2025-11-19", "V", "14:10 - 15:10", Collections.emptyList()),

            // III Semester
            new Slot("2025-11-17", "III", "11:45 - 12:45",
                    Collections.singletonList(new Subject("MA2001-1", "Statistics and Probability Theory"))),
            new Slot("2025-11-17", "III", "15:20 - 16:20",
                    Collections.singletonList(new Subject("IS2101-1", "Computer Organization and Design"))),
            new Slot("2025-11-18", "III", "11:45 - 12:45",
                    Collections.singletonList(new Subject("CS2001-1", "Data Structures"))),
            new Slot("2025-11-18", "III", "15:20 - 16:20",
                    Collections.singletonList(new Subject("IS1102-2", "Introduction to Data Science"))),
            new Slot("2025-11-19", "III", "11:45 - 12:45",
                    Collections.singletonList(new Subject("CS2002-1", "Object Oriented Programming"))),
            new Slot("2025-11-19", "III", "15:20 - 16:20", Collections.emptyList())
        );

        Map<String, Integer> studentsPerSem = Map.of("III", 360, "V", 340, "VII", 260);
        Map<String, Integer> roomsNeeded = new HashMap<>();
        studentsPerSem.forEach((sem, total) -> roomsNeeded.put(sem, ceilDiv(total, CAPACITY_PER_ROOM)));

        List<ResultRow> output = new ArrayList<>();

        // Split faculty into juniors and seniors, exclude absent
        List<Faculty> juniors = facultyList.stream()
                .filter(f -> !f.isSenior() && !absentFacultyIds.contains(f.getId()))
                .collect(Collectors.toList());
        List<Faculty> seniors = facultyList.stream()
                .filter(Faculty::isSenior)
                .filter(f -> !absentFacultyIds.contains(f.getId()))
                .collect(Collectors.toList());

        List<Faculty> assignmentList = new ArrayList<>();
        assignmentList.addAll(juniors);
        assignmentList.addAll(seniors);

        Map<String, Set<Integer>> assignedPerDay = new HashMap<>();
        int roomStartNo = 101;

        for (Slot slot : slots) {
            if (slot.subjects.isEmpty()) continue;

            int roomsForSem = roomsNeeded.getOrDefault(slot.semester, 0);
            if (roomsForSem == 0) roomsForSem = 1;

            List<String> rooms = new ArrayList<>();
            for (int i = 0; i < roomsForSem; i++) rooms.add(String.valueOf(roomStartNo + i));
            roomStartNo += roomsForSem;

            assignedPerDay.putIfAbsent(slot.date, new HashSet<>());
            Set<Integer> todayAssigned = assignedPerDay.get(slot.date);

            int idx = 0;
            for (String room : rooms) {
                String subjectText = slot.subjects.stream()
                        .map(s -> s.code + " - " + s.name)
                        .collect(Collectors.joining(", "));

                Faculty assigned = null;
                for (int f = 0; f < assignmentList.size(); f++) {
                    Faculty candidate = assignmentList.get((idx + f) % assignmentList.size());
                    if (!todayAssigned.contains(candidate.getId())) {
                        assigned = candidate;
                        todayAssigned.add(candidate.getId());
                        break;
                    }
                }

                if (assigned == null) { // extra slot allowed
                    assigned = assignmentList.get(idx % assignmentList.size());
                    todayAssigned.add(assigned.getId());
                }

                output.add(new ResultRow(slot.date, slot.time, room, slot.semester,
                        subjectText, assigned.getName(), assigned.getId()+"",
                        assigned.getEmail(), assigned.getDesignation()));
                idx++;
            }
        }

        return output;
    }

    private void preloadFaculty() {
        int id = 1001;
        addFaculty(id++, "Dr. Ashwini B", "Associate Professor & Head", "ISE", "ashwini@college.edu");
        addFaculty(id++, "Dr. Karuna Pandit", "Professor", "ISE", "karuna@college.edu");
        addFaculty(id++, "Dr. Balasubramani R.", "Professor", "ISE", "balasubramani@college.edu");
        addFaculty(id++, "Dr. Vasudeva", "Professor", "ISE", "vasudeva@college.edu");
        addFaculty(id++, "Dr. Usha Divakarla", "Professor", "ISE", "usha@college.edu");
        addFaculty(id++, "Dr. Manjula Gururaj Rao", "Professor", "ISE", "manjula@college.edu");
        addFaculty(id++, "Dr. Sumathi", "Professor", "ISE", "sumathi@college.edu");
        addFaculty(id++, "Dr. Ravi B", "Associate Professor", "ISE", "ravi@college.edu");
        addFaculty(id++, "Dr. Naganna Chetty", "Associate Professor", "ISE", "naganna@college.edu");
        addFaculty(id++, "Dr. Vandana B S", "Associate Professor", "ISE", "vandana@college.edu");
        addFaculty(id++, "Dr. Jason Elroy Martis", "Associate Professor", "ISE", "jason@college.edu");
        addFaculty(id++, "Dr. Preethi Salian K", "Associate Professor", "ISE", "preethi@college.edu");
        addFaculty(id++, "Dr. Bola Sunil Kamath", "Assistant Professor Gd. III", "ISE", "bola@college.edu");
        addFaculty(id++, "Dr. Devidas", "Assistant Professor Gd. III", "ISE", "devidas@college.edu");
        addFaculty(id++, "Mr. Vasudeva Pai", "Assistant Professor Gd. III", "ISE", "vasudeva.pai@college.edu");
        addFaculty(id++, "Mr. Deepu", "Assistant Professor Gd. III", "ISE", "deepu@college.edu");
        addFaculty(id++, "Mrs. Rashmi Naveen", "Assistant Professor Gd. III", "ISE", "rashmi@college.edu");
        addFaculty(id++, "Mr. Vaikunth Pai", "Assistant Professor Gd. III", "ISE", "vaikunth@college.edu");
        addFaculty(id++, "Dr. Chinmai Shetty", "Assistant Professor Gd. III", "ISE", "chinmai@college.edu");
        addFaculty(id++, "Mr. Krishnaraj Rao N S", "Assistant Professor Gd. III", "ISE", "krishnaraj@college.edu");
        addFaculty(id++, "Dr. Santosh S", "Assistant Professor Gd. III", "ISE", "santosh@college.edu");
        addFaculty(id++, "Mr. Ramesh G", "Assistant Professor Gd. II", "ISE", "ramesh@college.edu");
        addFaculty(id++, "Dr. Chaitra S N", "Assistant Professor Gd. II", "ISE", "chaitra@college.edu");
        addFaculty(id++, "Mr. Srikanth Bhat K", "Assistant Professor Gd. II", "ISE", "srikanth@college.edu");
        addFaculty(id++, "Ms. Alaka Ananthi", "Assistant Professor Gd. II", "ISE", "alaka@college.edu");
        addFaculty(id++, "Ms. Prathyakshini", "Assistant Professor Gd. II", "ISE", "prathyakshini@college.edu");
        addFaculty(id++, "Ms. Pratheeksha Hegde", "Assistant Professor Gd. II", "ISE", "pratheeksha@college.edu");
        addFaculty(id++, "Mr. Sharath Kumar", "Assistant Professor Gd. II", "ISE", "sharath@college.edu");
        addFaculty(id++, "Mr. Prashanth Kumar", "Assistant Professor Gd. II", "ISE", "prashanth@college.edu");
        addFaculty(id++, "Ms. Anusha N", "Assistant Professor Gd. I", "ISE", "anusha@college.edu");
        addFaculty(id++, "Ms. Tanzila Nargis", "Assistant Professor Gd. I", "ISE", "tanzila@college.edu");
    }

    private void addFaculty(int id, String name, String designation, String dept, String email) {
        facultyList.add(new Faculty(id, name, designation, dept, email));
    }

    private static int ceilDiv(int a, int b) {
        return (a + b - 1) / b;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class Faculty {
        private final int id;
        private final String name;
        private final String designation;
        private final String dept;
        private final String email;

        Faculty(int id, String name, String designation, String dept, String email) {
            this.id = id; this.name = name; this.designation = designation; this.dept = dept; this.email = email;
        }

        int getId() { return id; }
        String getName() { return name; }
        String getDesignation() { return designation; }
        String getEmail() { return email; }
        boolean isSenior() { return !designation.toLowerCase().contains("assistant"); }
    }

    private static class Subject {
        final String code, name;
        Subject(String code, String name) { this.code = code; this.name = name; }
    }

    private static class Slot {
        final String date, semester, time;
        final List<Subject> subjects;
        Slot(String date, String semester, String time, List<Subject> subjects) {
            this.date = date; this.semester = semester; this.time = time; this.subjects = subjects;
        }
    }

    private static class ResultRow {
        final String date, time, room, semester, subject;
        final String facultyName, facultyId, email, designation;

        ResultRow(String date, String time, String room, String semester, String subject,
                  String facultyName, String facultyId, String email, String designation) {
            this.date = date; this.time = time; this.room = room; this.semester = semester;
            this.subject = subject; this.facultyName = facultyName; this.facultyId = facultyId;
            this.email = email; this.designation = designation;
        }
    }
}