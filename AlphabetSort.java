import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * AlphabetSort takes input from stdin and prints to stdout.
 * The first line of input is the alphabet permutation.
 * The the remaining lines are the words to be sorted.
 *
 * The output should be the sorted words, each on its own line,
 * printed to std out.
 */
public class AlphabetSort {

    /**
     * Checks whether a given word or string can exist in the given alphabet
     * Returns true if characters in word exist in alphabet, false otherwise
     *
     * Runtime: O(N) - N = length of string
     * @param s    input string to check if exists in current alphabet
     * @param alph the given alphabet
     */
    public static boolean checkWordExists(String s, Hashtable<Character, Integer> alph) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!alph.containsKey(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Performs a depth-first search on the given fringe, utilizing a priority queue.
     *
     * Runtime O(N) - N = number of nodes in Trie
     * @param fringe the fringe to execute DFS on
     */
    public static void priorityDFS(PriorityQueue<Trie.TrieNode> fringe) {
        //StringBuilder sb = new StringBuilder();
        //String prefix = "";
        int keyCount = 0;

        while (!fringe.isEmpty()) {
            Trie.TrieNode curr = fringe.poll();

            if (curr.getEndOfWord()) {
                System.out.println(curr.getPrev());
                if (curr.getSuffix().isEmpty()) {
                    if (keyCount > 0) {
                        keyCount--;
                    }
                    continue;
                }
            }

            for (char c : curr.getSuffix().keySet()) {
                if (curr.getSuffix().keySet().size() > 1) {
                    keyCount = curr.getSuffix().keySet().size();
                }
                fringe.add(curr.getSuffix().get(c));
            }
        }
    }

    /**
     * Reads input from standard input and prints out the input words in
     * alphabetical order.
     *
     * Runtime: O(MN) - M = length of longest string
     *                  N = number of lines in file
     * @param args ignored
     */
    public static void main(String[] args) throws FileNotFoundException, IllegalArgumentException {
        Scanner inFile = new Scanner(System.in);
        // Check if alphabet is given
        if (!inFile.hasNextLine()) {
            throw new IllegalArgumentException("no alphabet!");
        }

        String alphabet = inFile.nextLine();
        // Set priority values for alphabet
        Hashtable<Character, Integer> alphaPerm = new Hashtable<>();
        for (int i = 0; i < alphabet.length(); i++) {
            char c = alphabet.charAt(i);
            if (alphaPerm.containsKey(c)) {
                throw new IllegalArgumentException("letter appears multiple times");
            }
            alphaPerm.put(c, i);
        }
        // Check if words are present
        if (!inFile.hasNextLine()) {
            throw new IllegalArgumentException("no words!");
        }

        Trie wordTree = new Trie();
        while (inFile.hasNextLine()) {
            String word = inFile.nextLine();
            if (!checkWordExists(word, alphaPerm)) {
                continue;
            }
            wordTree.asinsert(word, alphaPerm, alphabet.length());
        }

        // Implement a priority DFS on the Trie
        PriorityQueue<Trie.TrieNode> fringe;
        fringe = new PriorityQueue<>((o1, o2) -> o1.getPriority() - o2.getPriority());
        Trie.TrieNode curr = wordTree.root;

        for (char c : curr.getSuffix().keySet()) {
            fringe.add(curr.getSuffix().get(c));
        }
        priorityDFS(fringe);
    }
}
