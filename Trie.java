import java.util.HashSet;
import java.util.Hashtable;

/**
 * Prefix-Trie. Supports linear time find() and insert().
 * Should support determining whether a word is a full word in the
 * Trie or a prefix.
 *
 * @author
 */
public class Trie {
    TrieNode root;          // sentinel node mapping to all starting chars
    static int depth;
    HashSet<String> words;  // contains all inserted words

    public static class TrieNode {
        private boolean endOfWord;     // indicates nodes that are end of word
        private Hashtable<Character, TrieNode> suffix;
        private int priority;          // depth priority for alphSort
        private double priority2;      // word weight, 0.0 if not endOfWord (AC)
        private double maxPriority;    // current max priority connected to node (AC)
        private String prev;           // null if not endOfWord node, else contains
                                       // word string
        public TrieNode() {
            priority = 0;
            priority2 = 0.0;
            maxPriority = 0.0;
            endOfWord = false;
            suffix = new Hashtable<>();
        }

        // Temp node used for autocomplete priority DFS
        public TrieNode(double p2, String s) {
            endOfWord = true;
            priority2 = p2;
            maxPriority = p2;
            prev = s;
        }

        /** Getters/Setters for TrieNode */
        public void changeEndOfWord() {
            endOfWord = true;
        }

        public boolean getEndOfWord() {
            return endOfWord;
        }

        public int getPriority() {
            return priority;
        }

        public String getPrev() {
            return prev;
        }

        public void setPriority(int i) {
            priority = i;
        }

        public double getPriority2() {
            return priority2;
        }

        public void setPriority2(double d) {
            priority2 = d;
        }

        public double getMaxPriority() {
            return maxPriority;
        }

        public void setMaxPriority(double d) {
            maxPriority = d;
        }
        // AC Priority Queue set from least to greatest
        // Invert max priority to order TrieNodes in PQ by
        // increasing priority
        public double getDepthPriority() {
            return -maxPriority;
        }

        public Hashtable<Character, TrieNode> getSuffix() {
            return suffix;
        }
    }

    public Trie() {
        root = new TrieNode();
        words = new HashSet<>();
    }

    /** Searches a Trie for a given string, returns true if found
     *  else false
     *
     *  Runtime: O(N) - N = length of string
     * @param s - inputted string
     * @param isFullWord - indicates if full word must be found
     * @return
     */
    public boolean find(String s, boolean isFullWord) {
        // Illegal Arguments: s is null or empty
        if (s == null) {
            throw new IllegalArgumentException("String is null.");
        }
        if (s.equals("")) {
            throw new IllegalArgumentException("String is empty.");
        }

        // Iterate through the tree checking for letters
        TrieNode currTN = root;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            // Return false when letter is not contained
            if (!currTN.getSuffix().containsKey(c)) {
                return false;
            }
            currTN = currTN.getSuffix().get(c);
        }

        // TrieNode's endOfWord must be true if isFullWord is true
        if (isFullWord) {
            return currTN.getEndOfWord();
        }
        return true;
    }

    /** Inserts a string into Trie - not used in AC/AS
     *
     *  Runtime: O(N) amortized - N = length of string
     *  @param s - inputted string
     */
    public void insert(String s) {
        // Illegal Arguments - s is null or empty
        if (s == null) {
            throw new IllegalArgumentException("String is null.");
        }
        if (s.equals("")) {
            throw new IllegalArgumentException("String is empty.");
        }

        TrieNode currTN = root;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            Hashtable<Character, TrieNode> suffix = currTN.getSuffix();
            // Move to specific character's tree node if already initiated
            // else create a new tree node for that character
            if (suffix.containsKey(c)) {
                currTN = suffix.get(c);
            } else {
                suffix.put(c, new TrieNode());
                currTN = suffix.get(c);
            }
        }
        currTN.changeEndOfWord();
    }


    /** Insert a string into Trie, used for Alphabet sort
     *  gives each node a priority value to ensure priority queue DFS works
     *  --- further down the tree = higher priority ---
     *
     *  Runtime: O(N) - N = length of string
     * @param s - inputted string
     * @param priority - hashtable linking characters and priority values
     * @param alphLen - length of given alphabet
     */
    public void asinsert(String s, Hashtable<Character, Integer> priority, int alphLen) {
        // Illegal Arguments - s is null or empty
        if (words.contains(s)) {
            return;
        }
        if (s == null) {
            throw new IllegalArgumentException("String is null.");
        }
        if (s.equals("")) {
            throw new IllegalArgumentException("String is empty.");
        }

        TrieNode currTN = root;
        depth = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            Hashtable<Character, TrieNode> suffix = currTN.getSuffix();
            // Move to specific character's tree node if already initiated
            // else create a new tree node for that character
            if (suffix.containsKey(c)) {
                currTN = suffix.get(c);
                depth++;
            } else {
                suffix.put(c, new TrieNode());
                currTN = suffix.get(c);
                currTN.setPriority(priority.get(c) - (depth * alphLen));
                depth++;
            }
        }
        currTN.changeEndOfWord();
        currTN.prev = s;
        words.add(s);
    }

    /** inserts a String with a given weight into the Trie - used for AC
     *  priority2 - value of endNode corresponding to specific string
     *         --- if !endOfNode, priority2 == 0.0
     *  maxPriority - value of highest priority string connected further down
     *  in the Trie
     *
     *  Runtime: O(N) - N = length of string
     * @param s - inputted string
     * @param weight - weight of inputted string
     */
    public void acInsert(String s, double weight) {
        if (words.contains(s)) {
            return;
        }
        if (s == null) {
            throw new IllegalArgumentException("String is null.");
        }

        TrieNode currTN = root;
        if (root.getMaxPriority() < weight) {
            root.setMaxPriority(weight);
            root.prev = s;
        }
        depth = 1;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            Hashtable<Character, TrieNode> suffix = currTN.getSuffix();
            if (suffix.containsKey(c)) {
                currTN = suffix.get(c);
                // change priority if not endOfWord
                if (currTN.getMaxPriority() < weight) {
                    currTN.setMaxPriority(weight);
                }
                depth++;
            } else {
                suffix.put(c, new TrieNode());
                currTN = suffix.get(c);
                currTN.setMaxPriority(weight);
                depth++;
            }
        }
        currTN.changeEndOfWord();
        currTN.prev = s;
        currTN.setPriority2(weight);
        words.add(s);
    }
}
