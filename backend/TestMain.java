package backend;

import java.util.*;

public class TestMain {
    public static void main(String[] args) {
        List<Faculty> faculties = new ArrayList<>();
        faculties.add(new Faculty("Alice", "Senior"));
        faculties.add(new Faculty("Bob", "Junior"));
        faculties.add(new Faculty("Charlie", "Senior"));

        List<Hall> halls = new ArrayList<>();
        halls.add(new Hall("Hall A"));
        halls.add(new Hall("Hall B"));
        halls.add(new Hall("Hall C"));

        Allocator allocator = new Allocator();
        Map<Faculty, Hall> result = allocator.allocate(faculties, halls);

        System.out.println("=== Faculty Allocation ===");
        for (Map.Entry<Faculty, Hall> entry : result.entrySet()) {
            System.out.println(entry.getKey().getName() + " (" + entry.getKey().getDesignation() + ") -> " + entry.getValue().getHallName());
        }
    }
}
