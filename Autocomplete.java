import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Implements autocomplete on prefixes for a given dictionary of terms and weights.
 *
 * @author
 */
public class Autocomplete {
    /**
     * Initializes required data structures from parallel arrays.
     *
     * Runtime: O(MN) - M = max length of input string terms
     *                  N = number of items in input array
     * @param terms Array of terms.
     * @param weights Array of weights.
     */

    Trie AC = new Trie();

    public Autocomplete(String[] terms, double[] weights) {
        // Invalid argument exceptions
        if (terms == null || weights == null) {
            throw new NullPointerException("AC args are null");
        }
        if (terms.length != weights.length) {
            throw new IllegalArgumentException("term and array lengths are different");
        }

        for (int i = 0; i < terms.length; i++) {
            // Check for duplicates
            if (AC.words.contains(terms[i])) {
                throw new IllegalArgumentException("duplicate terms");
            }
            // Check for negative weights
            if (weights[i] < 0) {
                throw new IllegalArgumentException("negative weight");
            }
            // Add string + weight value to Trie
            AC.acInsert(terms[i], weights[i]);
        }
    }

    /**
     * Find the weight of a given term. If it is not in the dictionary, return 0.0
     *
     * Runtime: O(N) - N = length of string
     * @param term
     * @return weight of inputted string
     */
    public double weightOf(String term) {
        // Invalid argument exceptions
        if (!AC.words.contains(term) || term.equals("") || term == null) {
            return 0.0;
        }

        // Iterate through Trie until we reach the endNode corresponding to term
        Trie.TrieNode curr = AC.root;
        for (int i = 0; i < term.length(); i++) {
            char c = term.charAt(i);
            curr = curr.getSuffix().get(c);
        }

        return curr.getPriority2();
    }

    /**
     * Return the top match for given prefix, or null if there is no matching term.
     *
     * Runtime: O(N) - N = number of nodes
     * @param prefix Input prefix to match against.
     * @return Best (highest weight) matching string in the dictionary.
     */
    public String topMatch(String prefix) {
        // Invalid argument exceptions
        if (prefix == null) {
            throw new NullPointerException("topMatch prefix is null");
        }

        Trie.TrieNode curr = AC.root;
        // If empty prefix, return string corresponding to max weight
        if (prefix.equals("")) {
            return curr.getPrev();
        }

        // iterate until reaching node corresponding to end of prefix
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            curr = curr.getSuffix().get(c);
        }

        // iterate through each child node, taking the path where the child node's max
        // priority is equal to or greater than the current max priority
        double maxWeight = curr.getMaxPriority();
        while (curr.getPriority2() < maxWeight) {
            for (char c : curr.getSuffix().keySet()) {
                Trie.TrieNode temp = curr.getSuffix().get(c);
                if (temp.getMaxPriority() == maxWeight) {
                    curr = temp;
                    break;
                }
            }
        }
        // return string corresponding to max priority term
        return curr.getPrev();
    }

    /**
     * Returns the top k matching terms (in descending order of weight) as an iterable.
     * If there are less than k matches, return all the matching terms.
     *
     * Runtime:O(N + M + K) - N = number of nodes in Trie
     *                        M = length of prefix
     *                        K = number of terms to return (k)
     * @param prefix - inputted prefix string
     * @param k - number of matches to return
     * @return Iterable of top matching strings
     */
    public Iterable<String> topMatches(String prefix, int k) {
        // Invalid argument exceptions
        if (prefix == null) {
            throw new IllegalArgumentException("invalid prefix");
        }
        if (k < 0) {
            throw new IllegalArgumentException("k is negative");
        }

        Trie.TrieNode curr = AC.root;
        // Find node corresponding to end of prefix
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (!curr.getSuffix().containsKey(c)) {
                Iterable<String> retVal = new ArrayList<>();
                return retVal;
            }
            curr = curr.getSuffix().get(c);
        }

        // Perform Priority-Queue based DFS search on current node + children
        // ordered by maxPriority
        PriorityQueue<Trie.TrieNode> fringe;
        fringe = new PriorityQueue<>(Comparator.comparingDouble(Trie.TrieNode::getDepthPriority));
        fringe.add(curr);
        String[] dfs = priorityDFS(fringe, k);
        fringe.clear();

        // Convert string array to iterable
        int i = 0;
        for (; i < dfs.length; i++) {
            if (dfs[i] == null) {
                break;
            }
        }
        String[] retVal = new String[i];
        for (int j = 0; j < i; j++) {
            retVal[j] = dfs[j];
        }
        Iterable<String> iterRet = Arrays.asList(retVal);
        return iterRet;
    }

    /** Depth first search based on node priority - higher priority nodes will
     * be searched first
     *
     * Runtime: O(N) - N = number of nodes in Trie
     * @param fringe - inputted priority queue, sorted by inverse maxPriority
     * @param k - number of items to look for
     * @return String array of terms ordered by highest priority with respect to prefix
     */
    public String[] priorityDFS(PriorityQueue<Trie.TrieNode> fringe, int k) {
        int index = 0;
        String[] retVal = new String[k];
        // Breaks when no more strings to return or have found k items
        while (!fringe.isEmpty() && k > 0) {
            Trie.TrieNode curr = fringe.poll();

            if (curr.getEndOfWord()) {
                // adds string to retVal if the priority is the maxpriority
                if (curr.getMaxPriority() == curr.getPriority2()) {
                    retVal[index] = curr.getPrev();
                    index++;
                    k--;
                // requeue string with new maxpriority set to actual priority
                // deals with edge cases where strings with similar prefixes have
                // conflicting weights (reset children to none to prevent repeats)
                } else {
                    fringe.add(new Trie.TrieNode(curr.getPriority2(), curr.getPrev()));
                }
            }

            if (curr.getSuffix() == null || curr.getSuffix().isEmpty()) {
                continue;
            }
            // queue children nodes
            for (char c : curr.getSuffix().keySet()) {
                fringe.add(curr.getSuffix().get(c));
            }
        }
        return retVal;
    }

    /**
     * Test client. Reads the data from the file, then repeatedly reads autocomplete
     * queries from standard input and prints out the top k matching terms.
     *
     * @param args takes the name of an input file and an integer k as
     *             command-line arguments
     */
    public static void main(String[] args) {
        // initialize autocomplete data structure
        In in = new In(args[0]);
        int N = in.readInt();
        String[] terms = new String[N];
        double[] weights = new double[N];
        for (int i = 0; i < N; i++) {
            weights[i] = in.readDouble();   // read the next weight
            in.readChar();                  // scan past the tab
            terms[i] = in.readLine();       // read the next term
        }

        Autocomplete autocomplete = new Autocomplete(terms, weights);

        // process queries from standard input
        int k = Integer.parseInt(args[1]);
        while (StdIn.hasNextLine()) {
            String prefix = StdIn.readLine();
            for (String term : autocomplete.topMatches(prefix, k)) {
                StdOut.printf("%14.1f  %s\n", autocomplete.weightOf(term), term);
            }
        }
    }
}
