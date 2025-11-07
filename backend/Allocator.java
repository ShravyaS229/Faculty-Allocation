package backend;

import java.util.*;

public class Allocator {
    public Map<Faculty, Hall> allocate(List<Faculty> facultyList, List<Hall> hallList) {
        Map<Faculty, Hall> allocation = new LinkedHashMap<>();

        Collections.sort(facultyList, Comparator.comparing(Faculty::getDesignation));

        for (int i = 0; i < facultyList.size() && i < hallList.size(); i++) {
            allocation.put(facultyList.get(i), hallList.get(i));
        }

        return allocation;
    }
}
