package backend.models;

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
    private final Set<Integer> absentFacultyIds = new HashSet<>(); // predefine absent if needed
    private static final int CAPACITY_PER_ROOM = 30;

    @Override
    public void start(Stage stage) {
        preloadFaculty();

        TableView<ResultRow> table = new TableView<>();
        ObservableList<ResultRow> data = FXCollections.observableArrayList(runAllocationForFullDay());

        TableColumn<ResultRow, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().date));

        TableColumn<ResultRow, String> roomCol = new TableColumn<>("Room No");
        roomCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().room));

        TableColumn<ResultRow, String> semCol = new TableColumn<>("Semester");
        semCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().semester));

        TableColumn<ResultRow, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().subject));

        TableColumn<ResultRow, String> facCol = new TableColumn<>("Faculty Name");
        facCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().facultyName));

        TableColumn<ResultRow, String> desigCol = new TableColumn<>("Designation");
        desigCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().designation));

        table.getColumns().addAll(dateCol, roomCol, semCol, subjectCol, facCol, desigCol);
        table.setItems(data);

        VBox root = new VBox(10, table);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Exam Day Allocation");
        stage.show();
    }

    // ----------------- Allocation Logic -----------------
    private List<ResultRow> runAllocationForFullDay() {
        Map<String, List<Subject>> subjectsBySem = buildSubjects();

        List<Slot> slots = Arrays.asList(
                new Slot("2025-11-17", "VII", "09:00 - 10:00"),
                new Slot("2025-11-17", "V", "10:30 - 11:30"),
                new Slot("2025-11-17", "III", "11:45 - 12:45"),
                new Slot("2025-11-17", "VII", "13:00 - 14:00"),
                new Slot("2025-11-17", "V", "14:15 - 15:15"),
                new Slot("2025-11-17", "III", "15:30 - 16:30")
        );

        Map<String, Integer> studentsPerSem = Map.of("III", 400, "V", 340, "VII", 260);
        Map<String, Integer> roomsNeeded = new HashMap<>();
        studentsPerSem.forEach((sem, total) -> roomsNeeded.put(sem, ceilDiv(total, CAPACITY_PER_ROOM)));

        List<ResultRow> output = new ArrayList<>();

        // Faculty pools
        List<Faculty> juniors = facultyList.stream().filter(f -> !f.isSenior() && !absentFacultyIds.contains(f.getId())).collect(Collectors.toList());
        List<Faculty> seniors = facultyList.stream().filter(f -> f.isSenior() && !absentFacultyIds.contains(f.getId())).collect(Collectors.toList());

        int roomStartNo = 101;

        for (Slot slot : slots) {
            int roomsForSem = roomsNeeded.get(slot.semester);
            List<String> rooms = new ArrayList<>();
            for (int i = 0; i < roomsForSem; i++) {
                rooms.add(String.valueOf(roomStartNo + i));
            }
            roomStartNo += roomsForSem;

            List<Subject> semSubjects = subjectsBySem.getOrDefault(slot.semester, Collections.emptyList());
            Subject subjectForSlot = semSubjects.get(new Random().nextInt(semSubjects.size()));

            List<Faculty> available = new ArrayList<>();
            available.addAll(juniors);
            available.addAll(seniors);

            int idx = 0;
            for (String room : rooms) {
                if (idx < available.size()) {
                    Faculty assigned = available.get(idx);
                    output.add(new ResultRow(slot.date, room, slot.semester,
                            subjectForSlot.code + " - " + subjectForSlot.name,
                            assigned.getName(), assigned.getDesignation()));
                    idx++;
                } else {
                    output.add(new ResultRow(slot.date, room, slot.semester,
                            subjectForSlot.code + " - " + subjectForSlot.name,
                            "UNASSIGNED", "-"));
                }
            }
        }
        return output;
    }

    private Map<String, List<Subject>> buildSubjects() {
        Map<String, List<Subject>> m = new HashMap<>();
        m.put("III", Arrays.asList(
                new Subject("MA2001-1","Statistics and Probability Theory"),
                new Subject("IS2101-1","Computer Organization and Design"),
                new Subject("CS2001-1","Data Structures"),
                new Subject("IS1102-2","Introduction to Data Science"),
                new Subject("CS2002-1","Object Oriented Programming")
        ));
        m.put("V", Arrays.asList(
                new Subject("IS3001-1","Data Communication and Networking"),
                new Subject("IS3101-1","Operating Systems Fundamentals"),
                new Subject("IS2002-1","Machine Learning Foundations"),
                new Subject("IS2212-1","Business Intelligence and Its Applications"),
                new Subject("IS3241-1","Cloud Computing"),
                new Subject("IS2201-1","Information Storage Management"),
                new Subject("IS3242-1","Graphics and Animation"),
                new Subject("HU1010-1","Research Methodology")
        ));
        m.put("VII", Arrays.asList(
                new Subject("IS3002-1","Ethical Hacking and Network Defense"),
                new Subject("MG1002-1","Financial Management"),
                new Subject("IS4224-1","Software Defined Networks"),
                new Subject("IS2213-1","Object Oriented Modelling and Design"),
                new Subject("IS1201-1","Total Quality Management"),
                new Subject("IS2314-1","Software Architecture & Design Patterns"),
                new Subject("IS3202-1","User Interface Design"),
                new Subject("IS3334-1","Social and Web Analytics"),
                new Subject("OEC","Open Elective Course")
        ));
        return m;
    }

    private void preloadFaculty() {
        int id = 1001;
        addFaculty(id++, "Dr. Ashwini B", "Associate Professor & Head", "ISE");
        addFaculty(id++, "Dr. Karuna Pandit", "Professor", "ISE");
        addFaculty(id++, "Dr. Balasubramani R.", "Professor", "ISE");
        addFaculty(id++, "Dr. Vasudeva", "Professor", "ISE");
        addFaculty(id++, "Dr. Usha Divakarla", "Professor", "ISE");
        addFaculty(id++, "Dr. Manjula Gururaj Rao", "Professor", "ISE");
        addFaculty(id++, "Dr. Sumathi", "Professor", "ISE");
        addFaculty(id++, "Dr. Ravi B", "Associate Professor", "ISE");
        addFaculty(id++, "Dr. Naganna Chetty", "Associate Professor", "ISE");
        addFaculty(id++, "Dr. Vandana B S", "Associate Professor", "ISE");
        addFaculty(id++, "Dr. Jason Elroy Martis", "Associate Professor", "ISE");
        addFaculty(id++, "Dr. Preethi Salian K", "Associate Professor", "ISE");
        addFaculty(id++, "Dr. Bola Sunil Kamath", "Assistant Professor Gd. III", "ISE");
        addFaculty(id++, "Dr. Devidas", "Assistant Professor Gd. III", "ISE");
        addFaculty(id++, "Mr. Vasudeva Pai", "Assistant Professor Gd. III", "ISE");
        addFaculty(id++, "Mr. Deepu", "Assistant Professor Gd. III", "ISE");
        addFaculty(id++, "Mrs. Rashmi Naveen", "Assistant Professor Gd. III", "ISE");
        addFaculty(id++, "Mr. Vaikunth Pai", "Assistant Professor Gd. III", "ISE");
        addFaculty(id++, "Dr. Chinmai Shetty", "Assistant Professor Gd. III", "ISE");
        addFaculty(id++, "Mr. Krishnaraj Rao N S", "Assistant Professor Gd. III", "ISE");
        addFaculty(id++, "Dr. Santosh S", "Assistant Professor Gd. III", "ISE");
        addFaculty(id++, "Mr. Ramesh G", "Assistant Professor Gd. II", "ISE");
        addFaculty(id++, "Dr. Chaitra S N", "Assistant Professor Gd. II", "ISE");
        addFaculty(id++, "Mr. Srikanth Bhat K", "Assistant Professor Gd. II", "ISE");
        addFaculty(id++, "Ms. Alaka Ananthi", "Assistant Professor Gd. II", "ISE");
        addFaculty(id++, "Ms. Prathyakshini", "Assistant Professor Gd. II", "ISE");
        addFaculty(id++, "Ms. Pratheeksha Hegde", "Assistant Professor Gd. II", "ISE");
        addFaculty(id++, "Mr. Sharath Kumar", "Assistant Professor Gd. II", "ISE");
        addFaculty(id++, "Mr. Prashanth Kumar", "Assistant Professor Gd. II", "ISE");
        addFaculty(id++, "Ms. Anusha N", "Assistant Professor Gd. I", "ISE");
        addFaculty(id++, "Ms. Tanzila Nargis", "Assistant Professor Gd. I", "ISE");
    }

    private void addFaculty(int id, String name, String designation, String dept) {
        facultyList.add(new Faculty(id, name, designation, dept));
    }

    private static int ceilDiv(int a, int b) {
        return (a + b - 1) / b;
    }

    public static void main(String[] args) {
        launch(args);
    }

    // ------------------ Classes ------------------
    private static class Faculty {
        private final int id;
        private final String name;
        private final String designation;
        private final String dept;

        Faculty(int id, String name, String designation, String dept) {
            this.id = id; this.name = name; this.designation = designation; this.dept = dept;
        }

        int getId() { return id; }
        String getName() { return name; }
        String getDesignation() { return designation; }
        boolean isSenior() { return !designation.toLowerCase().contains("assistant"); }
    }

    private static class Subject {
        final String code, name;
        Subject(String code, String name) { this.code = code; this.name = name; }
    }

    private static class Slot {
        final String date, semester, time;
        Slot(String date, String semester, String time) { this.date = date; this.semester = semester; this.time = time; }
    }

    private static class ResultRow {
        final String date, room, semester, subject, facultyName, designation;
        ResultRow(String date, String room, String semester, String subject, String facultyName, String designation) {
            this.date = date; this.room = room; this.semester = semester;
            this.subject = subject; this.facultyName = facultyName; this.designation = designation;
        }
    }
}
