import org.junit.Test;
import ucb.junit.textui;

import java.util.Hashtable;
import java.util.Random;

import static org.junit.Assert.*;

/** The suite of all JUnit tests for the Trie class.
 *  @author
 */
public class TestTrie {

    /*********************************************************************
     This is a magic cat
     /\
     /  \
     .∧＿∧
     ( ･ω･｡)つ━☆・*。
     ⊂　 ノ 　　　・゜+.
     しーＪ　　　°。+ *´¨)
     　　　.· ´¸.·*´¨) ¸.·*¨) 　　　　　　　　　　
     (¸.·´ (¸.·'* ☆

     Copy and paste into code to get full test points
     *********************************************************************/

    /** Illegal Argument Checks */

    /**
     * null argument check tests
     */
    @Test
    public void findNullArguments1() {
        // true means test passed
        Trie test = new Trie();
        test.insert("james");
        test.insert("jamesis");
        test.insert("jamesisa");
        test.insert("jamesisabutt");

        try {
            test.find(null, true);
            fail();
        } catch (IllegalArgumentException e) {
            return;
        }
    }

    @Test
    public void findNullArguments2() {
        // true means test passed
        Trie test = new Trie();
        test.insert("james");
        test.insert("jamesis");
        test.insert("jamesisa");
        test.insert("jamesisabutt");

        try {
            test.find(null, false);
            fail();
        } catch (IllegalArgumentException e) {
            return;
        }
    }

    @Test
    public void insertNullArguments() {
        Trie test = new Trie();
        try {
            test.insert(null);
            fail();
        } catch (IllegalArgumentException e) {
            return;
        }
    }

    /**
     * tests for empty string input in find() method
     */
    @Test
    public void findEmptyArguments1() {
        // true means test passed
        Trie test = new Trie();
        test.insert("james");
        test.insert("jamesis");
        test.insert("jamesisa");
        test.insert("jamesisabutt");

        try {
            test.find("", false);
            fail();
        } catch (IllegalArgumentException e) {
            return;
        }
    }

    @Test
    public void findEmptyArguments2() {
        // true means test passed
        Trie test = new Trie();
        test.insert("james");
        test.insert("jamesis");
        test.insert("jamesisa");
        test.insert("jamesisabutt");

        try {
            test.find("", true);
            fail();
        } catch (IllegalArgumentException e) {
            return;
        }
    }

    /**
     * test to see if exception thrown when inserting empty string into trie
     */
    @Test
    public void insertEmptyArguments() {
        Trie test = new Trie();
        try {
            test.insert("");
            fail();
        } catch (IllegalArgumentException e) {
            return;
        }
    }

    /**
     * tests to make sure find works properly
     * returns true if isFullWord is true and word exists, false otherwise
     * returns true if isFullWord is false and prefix exists, false otherwise
     */
    @Test
    public void findPartialAndComplete() {
        Trie test = new Trie();
        test.insert("james");
        test.insert("evan");
        test.insert("obamacare");

        assertEquals(true, test.find("james", true));
        assertEquals(true, test.find("james", false));
        assertEquals(true, test.find("evan", true));
        assertEquals(true, test.find("evan", false));
        assertEquals(true, test.find("obamacare", true));
        assertEquals(true, test.find("obamacare", false));

        assertEquals(true, test.find("ja", false));
        assertEquals(false, test.find("ja", true));
        assertEquals(true, test.find("eva", false));
        assertEquals(false, test.find("eva", true));
        assertEquals(true, test.find("obama", false));
        assertEquals(false, test.find("obama", true));
    }

    @Test
    public void findPartialAndCompleteInComplete() {
        Trie test = new Trie();
        test.insert("Bush");
        test.insert("Bush did");
        test.insert("Bush did ");
        test.insert("Bush did 9/11");

        // Check that concatenated phrases added separately are still
        // identified as separate
        assertEquals(true, test.find("Bush", false));
        assertEquals(true, test.find("Bush", true));
        assertEquals(true, test.find("Bush did", false));
        assertEquals(true, test.find("Bush did", true));
        assertEquals(true, test.find("Bush did 9/11", false));
        assertEquals(true, test.find("Bush did 9/11", true));

        assertEquals(false, test.find("9/11", false));
        assertEquals(false, test.find("did", false));
    }

    /**
     * test to see if asinsert inserts elements into trie correctly
     * asinsert is to insert elements into trie specifically used for AlphabetSort
     */
    @Test
    public void testASInsert() {
        Trie test = new Trie();
        Hashtable<Character, Integer> alph = new Hashtable<>();
        alph.put('!', 0);
        alph.put('@', 1);
        alph.put('#', 2);
        alph.put('$', 3);
        alph.put('%', 4);

        test.asinsert("%$#@!", alph, alph.size());
        test.asinsert("###", alph, alph.size());
        test.asinsert("@$!!!!", alph, alph.size());

        assertEquals(true, test.find("%$#@!", true));
        assertEquals(true, test.find("%$#@!", false));
        assertEquals(true, test.find("###", true));
        assertEquals(true, test.find("###", false));
        assertEquals(true, test.find("@$!!!!", true));
        assertEquals(true, test.find("@$!!!!", false));

        assertEquals(true, test.find("%$", false));
        assertEquals(false, test.find("%$", true));
        assertEquals(true, test.find("#", false));
        assertEquals(false, test.find("#", true));
        assertEquals(true, test.find("@$!", false));
        assertEquals(false, test.find("@$!", true));
    }

    /**
     * test to see if acInsert inserts elements into trie correctly
     * acInsert is to insert elements into trie specifically used for Autocomplete
     */
    @Test
    public void testACInsert() {
        Trie test = new Trie();
        String[] dict = new String[10];
        // inserting random string into trie
        for (int i = 0; i < 10; i++) {
            String temp = generateString("covfefe!", 69 + 420);
            dict[i] = temp;
            test.acInsert(temp, (double) i);
        }

        assertEquals(true, test.find(dict[0], true));
        assertEquals(true, test.find(dict[0], false));
        assertEquals(true, test.find(dict[5], true));
        assertEquals(true, test.find(dict[5], false));

        assertEquals(true, test.find(dict[7].substring(0, 3), false));
        assertEquals(false, test.find(dict[7].substring(0, 3), true));
        assertEquals(true, test.find(dict[3].substring(0, 3), false));
        assertEquals(false, test.find(dict[3].substring(0, 3), true));
    }

    /* helper method for creating random strings - thanks StackOverflow */
    public String generateString(String characters, int length) {
        char[] text = new char[length];
        Random rng = new Random();
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

    /** Run the JUnit tests above. */
    public static void main(String[] ignored) {
        textui.runClasses(TestTrie.class);
    }
}
