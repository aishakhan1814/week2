import java.util.*;

public class WaterJugAStar {

    // Represents a state (Jug A, Jug B)
    static class State {
        int a, b;

        State(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            State state = (State) obj;
            return a == state.a && b == state.b;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }

    // A* Search implementation
    static List<State> waterJugAStar(int capA, int capB, int goal) {

        State start = new State(0, 0);

        // Priority Queue → (f = g + h)
        PriorityQueue<State> pq = new PriorityQueue<>(
                Comparator.comparingInt(
                        s -> cost.get(s) + heuristic(s, goal))
        );

        pq.add(start);
        parent.put(start, null);
        cost.put(start, 0);

        while (!pq.isEmpty()) {

            State current = pq.poll();
            int x = current.a;
            int y = current.b;

            // Goal Test
            if (x == goal || y == goal) {
                return reconstructPath(current);
            }

            // Generate next states
            List<State> nextStates = List.of(
                    new State(capA, y),                        // Fill A
                    new State(x, capB),                        // Fill B
                    new State(0, y),                           // Empty A
                    new State(x, 0),                           // Empty B
                    new State(x - Math.min(x, capB - y),
                              y + Math.min(x, capB - y)),     // Pour A → B
                    new State(x + Math.min(y, capA - x),
                              y - Math.min(y, capA - x))      // Pour B → A
            );

            for (State next : nextStates) {
                int newCost = cost.get(current) + 1;

                if (!cost.containsKey(next) || newCost < cost.get(next)) {
                    cost.put(next, newCost);
                    parent.put(next, current);
                    pq.add(next);
                }
            }
        }
        return null;
    }

    // Heuristic function
    static int heuristic(State s, int goal) {
        return Math.min(Math.abs(s.a - goal), Math.abs(s.b - goal));
    }

    // Reconstruct solution path
    static List<State> reconstructPath(State goalState) {
        List<State> path = new ArrayList<>();
        while (goalState != null) {
            path.add(goalState);
            goalState = parent.get(goalState);
        }
        Collections.reverse(path);
        return path;
    }

    static Map<State, State> parent = new HashMap<>();
    static Map<State, Integer> cost = new HashMap<>();

    // -------------------------------
    // MAIN METHOD
    // -------------------------------
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("\n--- Water Jug Problem using A* Search ---");

        System.out.print("Enter capacity of Jug A: ");
        int capA = sc.nextInt();

        System.out.print("Enter capacity of Jug B: ");
        int capB = sc.nextInt();

        System.out.print("Enter target amount: ");
        int goal = sc.nextInt();

        // Input Validation
        if (goal > Math.max(capA, capB)) {
            System.out.println("\n❌ Goal cannot be greater than both jug capacities.");
            return;
        }

        if (goal % gcd(capA, capB) != 0) {
            System.out.println("\n❌ No solution exists (goal not divisible by GCD).");
            return;
        }

        // Solve
        List<State> solution = waterJugAStar(capA, capB, goal);

        if (solution != null) {
            System.out.println("\n✅ Solution Path (Jug A, Jug B):");
            for (State s : solution) {
                System.out.println("(" + s.a + ", " + s.b + ")");
            }
        } else {
            System.out.println("\n❌ No solution found.");
        }
    }

    // GCD function
    static int gcd(int a, int b) {
        while (b != 0) {
            int temp = a % b;
            a = b;
            b = temp;
        }
        return a;
    }
}
