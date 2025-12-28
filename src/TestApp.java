package src;

import src.backend.AllocationLogic;

public class TestApp {
    public static void main(String[] args) {

        System.out.println("=== Faculty Allocation Started ===");

        AllocationLogic logic = new AllocationLogic();
        logic.generateAllocation();

        System.out.println("=== Faculty Allocation Completed ===");
    }
}
