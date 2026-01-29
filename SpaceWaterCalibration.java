import java.util.*;

class SpaceWaterCalibration {

    // Represents the current fluid state in containers
    static class FluidState {
        int containerX;
        int containerY;

        FluidState(int x, int y) {
            containerX = x;
            containerY = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            FluidState other = (FluidState) obj;
            return containerX == other.containerX && containerY == other.containerY;
        }

        @Override
        public int hashCode() {
            return Objects.hash(containerX, containerY);
        }
    }

    // A* search to find exact calibration
    static List<FluidState> calibrateWater(
            int capacityX, int capacityY, int targetAmount) {

        PriorityQueue<FluidState> priorityQueue =
                new PriorityQueue<>(Comparator.comparingInt(
                        s -> costSoFar.get(s) + heuristic(s, targetAmount)));

        FluidState start = new FluidState(0, 0);

        priorityQueue.add(start);
        parentMap.put(start, null);
        costSoFar.put(start, 0);

        while (!priorityQueue.isEmpty()) {

            FluidState current = priorityQueue.poll();

            // Goal condition
            if (current.containerX == targetAmount ||
                current.containerY == targetAmount) {
                return reconstructPath(current);
            }

            for (FluidState next : generateNextStates(
                    current, capacityX, capacityY)) {

                int newCost = costSoFar.get(current) + 1;

                if (!costSoFar.containsKey(next) ||
                        newCost < costSoFar.get(next)) {

                    costSoFar.put(next, newCost);
                    parentMap.put(next, current);
                    priorityQueue.add(next);
                }
            }
        }
        return null;
    }

    // Storage for A*
    static Map<FluidState, FluidState> parentMap = new HashMap<>();
    static Map<FluidState, Integer> costSoFar = new HashMap<>();

    // Heuristic: closest distance to required calibration
    static int heuristic(FluidState state, int target) {
        return Math.min(
                Math.abs(state.containerX - target),
                Math.abs(state.containerY - target)
        );
    }

    // Generate all valid fluid transfers
    static List<FluidState> generateNextStates(
            FluidState s, int capX, int capY) {

        List<FluidState> states = new ArrayList<>();

        // Fill operations
        states.add(new FluidState(capX, s.containerY));
        states.add(new FluidState(s.containerX, capY));

        // Empty operations
        states.add(new FluidState(0, s.containerY));
        states.add(new FluidState(s.containerX, 0));

        // Transfer X → Y
        int moveXY = Math.min(s.containerX, capY - s.containerY);
        states.add(new FluidState(
                s.containerX - moveXY,
                s.containerY + moveXY));

        // Transfer Y → X
        int moveYX = Math.min(s.containerY, capX - s.containerX);
        states.add(new FluidState(
                s.containerX + moveYX,
                s.containerY - moveYX));

        return states;
    }

    // Reconstruct solution path
    static List<FluidState> reconstructPath(FluidState goal) {
        List<FluidState> path = new ArrayList<>();
        while (goal != null) {
            path.add(goal);
            goal = parentMap.get(goal);
        }
        Collections.reverse(path);
        return path;
    }

    // -------------------------------
    // MAIN METHOD
    // -------------------------------
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("\n🚀 Space Mission Water Calibration 🚀");

        System.out.print("Enter capacity of Container X: ");
        int capacityX = sc.nextInt();

        System.out.print("Enter capacity of Container Y: ");
        int capacityY = sc.nextInt();

        System.out.print("Enter required calibration amount: ");
        int target = sc.nextInt();

        if (target > Math.max(capacityX, capacityY)) {
            System.out.println("\n❌ Calibration impossible: target exceeds container limits.");
            return;
        }

        List<FluidState> solution =
                calibrateWater(capacityX, capacityY, target);

        if (solution == null) {
            System.out.println("\n❌ No valid calibration sequence found.");
        } else {
            System.out.println("\n✅ Calibration Steps (X , Y):");
            for (FluidState step : solution) {
                System.out.println(
                        "(" + step.containerX + "L , " +
                              step.containerY + "L)");
            }
        }
    }
}
