import java.util.*;

/**
 * Automated Fuel Blending Optimization using A* Search
 */
public class FuelBlendingOptimizer {

    /* -----------------------------
       STATE REPRESENTATION
       ----------------------------- */
    static class FuelState {
        int tankX;   // Fuel quantity in Tank X
        int tankY;   // Fuel quantity in Tank Y

        FuelState(int x, int y) {
            this.tankX = x;
            this.tankY = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            FuelState other = (FuelState) obj;
            return tankX == other.tankX && tankY == other.tankY;
        }

        @Override
        public int hashCode() {
            return Objects.hash(tankX, tankY);
        }
    }

    /* -----------------------------
       A* SEARCH STORAGE
       ----------------------------- */
    static Map<FuelState, FuelState> parentState = new HashMap<>();
    static Map<FuelState, Integer> costSoFar = new HashMap<>();

    /* -----------------------------
       HEURISTIC FUNCTION
       Remaining fuel mismatch
       ----------------------------- */
    static int fuelMismatch(FuelState state, int targetOctane) {
        return Math.min(
                Math.abs(state.tankX - targetOctane),
                Math.abs(state.tankY - targetOctane)
        );
    }

    /* -----------------------------
       FUEL BLENDING OPTIMIZATION
       ----------------------------- */
    static List<FuelState> optimizeBlending(
            int capacityX,
            int capacityY,
            int targetOctane) {

        PriorityQueue<FuelState> openSet = new PriorityQueue<>(
                Comparator.comparingInt(
                        s -> costSoFar.get(s) + fuelMismatch(s, targetOctane))
        );

        FuelState start = new FuelState(0, 0);

        openSet.add(start);
        parentState.put(start, null);
        costSoFar.put(start, 0);

        while (!openSet.isEmpty()) {

            FuelState current = openSet.poll();

            // Goal reached
            if (current.tankX == targetOctane ||
                current.tankY == targetOctane) {
                return reconstructSolution(current);
            }

            for (FuelState next : generateTransitions(
                    current, capacityX, capacityY)) {

                int newCost = costSoFar.get(current) + 1;

                if (!costSoFar.containsKey(next) ||
                        newCost < costSoFar.get(next)) {

                    costSoFar.put(next, newCost);
                    parentState.put(next, current);
                    openSet.add(next);
                }
            }
        }
        return null;
    }

    /* -----------------------------
       POSSIBLE FUEL OPERATIONS
       ----------------------------- */
    static List<FuelState> generateTransitions(
            FuelState s, int capX, int capY) {

        List<FuelState> next = new ArrayList<>();

        // Load fuel completely
        next.add(new FuelState(capX, s.tankY));
        next.add(new FuelState(s.tankX, capY));

        // Drain fuel
        next.add(new FuelState(0, s.tankY));
        next.add(new FuelState(s.tankX, 0));

        // Transfer fuel between tanks
        int moveXY = Math.min(s.tankX, capY - s.tankY);
        next.add(new FuelState(
                s.tankX - moveXY,
                s.tankY + moveXY));

        int moveYX = Math.min(s.tankY, capX - s.tankX);
        next.add(new FuelState(
                s.tankX + moveYX,
                s.tankY - moveYX));

        return next;
    }

    /* -----------------------------
       PATH RECONSTRUCTION
       ----------------------------- */
    static List<FuelState> reconstructSolution(FuelState goal) {
        List<FuelState> path = new ArrayList<>();
        while (goal != null) {
            path.add(goal);
            goal = parentState.get(goal);
        }
        Collections.reverse(path);
        return path;
    }

    /* -----------------------------
       MAIN DRIVER
       ----------------------------- */
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("\n⛽ Automated Fuel Blending Optimization System");

        System.out.print("Enter capacity of Fuel Tank X: ");
        int capacityX = sc.nextInt();

        System.out.print("Enter capacity of Fuel Tank Y: ");
        int capacityY = sc.nextInt();

        System.out.print("Enter required octane level: ");
        int targetOctane = sc.nextInt();

        if (targetOctane > Math.max(capacityX, capacityY)) {
            System.out.println("\n❌ Target octane level exceeds system limits.");
            return;
        }

        if (targetOctane % gcd(capacityX, capacityY) != 0) {
            System.out.println("\n❌ No feasible blending configuration exists.");
            return;
        }

        List<FuelState> solution =
                optimizeBlending(capacityX, capacityY, targetOctane);

        if (solution == null) {
            System.out.println("\n❌ Optimization failed.");
        } else {
            System.out.println("\n✅ Optimized Fuel States (Tank X , Tank Y):");
            for (FuelState s : solution) {
                System.out.println(
                        "(" + s.tankX + "L , " + s.tankY + "L)");
            }
        }
    }

    /* -----------------------------
       GCD UTILITY
       ----------------------------- */
    static int gcd(int a, int b) {
        while (b != 0) {
            int r = a % b;
            a = b;
            b = r;
        }
        return a;
    }
}
