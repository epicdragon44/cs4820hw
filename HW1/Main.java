import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * Main class for the Stable Matching problem (National Resident Matching Program)
 * Optimized in favor of the hospitals, as per the problem statement!
 * 
 * By Daniel Wei (dlw266)
 */
class Main {

    /**
     * Helper method to convert a string[] to an int[] with optional convertIndices during conversion (decrement from 1-indexed to 0-indexed)
     */
    public static int[] toIntArray(String[] stringArray, boolean convertIndices) {
        int[] intArray = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            if (convertIndices) {
                intArray[i] = Integer.parseInt(stringArray[i]) - 1;
            } else {
                intArray[i] = Integer.parseInt(stringArray[i]);
            }
        }
        return intArray;
    }

    /**
     * Polymorphic Helper method to assist with input reading, by taking in a BufferedReader instance br
     * and returning an int[] of the next line of input.
     */
    public static int[] readIntArray(BufferedReader br) throws IOException {
        return toIntArray(br.readLine().split(" "), false);
    }

    /**
     * Polymorphic Helper method to assist with input reading, by taking in a BufferedReader instance br
     * and returning an int[] of the next line of input
     * 
     * If you pass in a boolean `convertIndices`, it also converts from the 1-indexed input to the 0-indexed array we use internally!
     */
    public static int[] readIntArray(BufferedReader br, boolean convertIndices) throws IOException {
        return toIntArray(br.readLine().split(" "), true);
    }

    /**
     * Implementation heavily inspired by chapter 2.3 in the textbook and the mentors model provided in the document.
     *  Each hospital only gets one student (capacity one). To model multiple students per hospital, we simply create multiple hospitals, each with the same ID.
     */
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int[] firstLine = readIntArray(br);

        /** Number of hospitals */
        int m = firstLine[0];
        /** Number of students */
        int n = firstLine[1];

        // -- Create Initial Data Structures --

        /** 
         * Hold hospitals preferences for students as a 2D array
         * where hosPref[m][i] denotes the ith student on hospital m's preference list
         * essentially, hosPref[m] is the preference list for hospital m, ordered from most to least preferred
         */
        int[][] hosPref = new int[m][n];

        /**
         * Goal: we want to compare the rank of a hospital in a student's preference list to the rank of the hospital that student is currently matched to in constant time.
         * To do this, we create a 2D array of the ranks of each hospital in each student's preference list,
         * where ranking[w][m] is the rank of hospital m in student w's preference list
         * Thus, to decide whether hospital m or m' is a better match for student w, we can simply compare ranking[w][m] and ranking[w][m'] and take the lower one
         */
        int[][] ranking = new int[n][m];

        /**
         * Hold hospitals that are free as a LinkedList 
         *  getFirst() returns the next hospital to be matched
         *  addFirst() adds a hospital that's just been freed up
         *  removeFirst() removes a hospital that's just been matched
         */
        LinkedList<Integer> freeHos = new LinkedList<>();

        /**
         * Maintain an array of the highest ranked student to which each hospital has not yet made an offer.
         * Eg. the next student to offer to for each hospital
         * where hosPref[m][next[m]] is the next student for hospital m to offer to
         *  After each proposal, next[m] is incremented unconditionally!
         * Initially, all hospitals are at the top of their preference list (next[m] = 0)
         */
        int[] next = new int[m];

        /**
         * Maintain an array of which hospital each student is currently matched to.
         * where match[n] is the hospital that student n is currently matched to
         * -1 denotes that the student is unmatched, and all students are initially unmatched
         */
        int[] current = new int[n];
        // Initialize all students to be unmatched
        for (int i = 0; i < n; i++) {
            current[i] = -1;
        }

        // -- Populate Initial Data Structures --

        // Populate freeHos with all hospitals
        for (int hosID = 0; hosID < m; hosID++) {
            int capacity = readIntArray(br)[0];
            for (int i = 0; i < capacity; i++) {
                freeHos.addFirst(hosID);
            }
        }

        // Populate hosPref
        for (int hosID = 0; hosID < m; hosID++) {
            hosPref[hosID] = readIntArray(br, true);
        }

        // Populate ranking: for each student, populate the rank of each hospital in their preference list
        for (int stuID = 0; stuID < n; stuID++) {
            int[] stuPref = readIntArray(br, true);
            for (int i = 0; i < stuPref.length; i++) {
                ranking[stuID][stuPref[i]] = i;
            }
        }

        // -- Main Loop --

        while (!freeHos.isEmpty()) {
            // Get the next free hospital
            int hosID = freeHos.getFirst();

            // Get the next student to offer to
            int stuID = hosPref[hosID][next[hosID]];

            // Increment next[hosID] unconditionally
            next[hosID]++;

            // If the student is unmatched, match them to the hospital
            if (current[stuID] == -1) {
                current[stuID] = hosID;
                freeHos.removeFirst();
            } else {
                // If the student is matched, compare the hospital's rank in the student's preference list to the hospital they're currently matched to
                int currentMatch = current[stuID];
                if (ranking[stuID][hosID] < ranking[stuID][currentMatch]) {
                    // If the hospital is a better match, match them to the hospital and free up the current match
                    current[stuID] = hosID;
                    freeHos.removeFirst();
                    freeHos.addFirst(currentMatch);
                }
            }
        }

        // -- Output --

        // Print the final matching
        for (int stuID = 0; stuID < n; stuID++) {
            System.out.println(current[stuID]+1); // +1 to convert from 0-indexing to 1-indexing
        }

        // Close the BufferedReader

        br.close();
    }
}
