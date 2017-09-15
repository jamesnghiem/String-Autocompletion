import ucb.junit.textui;
import org.junit.Test;

import java.util.Iterator;
import java.util.Random;

import static org.junit.Assert.*;

/** The suite of all JUnit tests for the Autocomplete class.
 *  @author
 */

public class TestAutocomplete {
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
    /** Test that weights are properly assigned to strings */
    @Test
    public void testWeightsBasic() {
        String[] strings = {"Jet", "fuel", "can't", "melt", "steel", "beams.",
            "but", "jet", "mules", "can", "steal", "dank", "memes"};
        double[] weights = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0};

        Autocomplete test = new Autocomplete(strings, weights);
        assertTrue(test.weightOf("Jet") == 1.0);
        assertTrue(test.weightOf("fuel") == 2.0);
        assertTrue(test.weightOf("can't") == 3.0);
        assertTrue(test.weightOf("melt") == 4.0);
        assertTrue(test.weightOf("steel") == 5.0);
        assertTrue(test.weightOf("beams.") == 6.0);
        assertTrue(test.weightOf("but") == 7.0);
        assertTrue(test.weightOf("jet") == 8.0);
        assertTrue(test.weightOf("mules") == 9.0);
        assertTrue(test.weightOf("can") == 10.0);
        assertTrue(test.weightOf("steal") == 11.0);
        assertTrue(test.weightOf("dank") == 12.0);
        assertTrue(test.weightOf("memes") == 13.0);

        Iterable<String> topM = test.topMatches("", 13);
        Iterator<String> iter = topM.iterator();
        for (int i = 0; i < strings.length; i++) {
            assertEquals(iter.next(), strings[12 - i]);
        }
    }

    /** Test that weights of values not in the dictionary are 0.0 */
    @Test
    public void testWeightOfInvalidArgs() {
        String[] strings = {"Jet", "fuel", "can't", "melt", "steel", "beams.",
            "but", "jet", "mules", "can", "steal", "dank", "memes"};
        double[] weights = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0};

        Autocomplete test = new Autocomplete(strings, weights);
        assertTrue(test.weightOf(null) == 0.0);
        assertTrue(test.weightOf("") == 0.0);
        // Nani?!?!?!?! 「(°ヘ°)
        assertTrue(test.weightOf("(；´д｀)ゞ") == 0.0);
    }
    /** Test invalid arguments for constructor */
    @Test
    public void testConstructorInvalid() {
        boolean tester = true;
        // Check that null arguments throws NPE
        String[] s1 = null;
        double[] d1 = {1.0};
        try {
            Autocomplete test1 = new Autocomplete(s1, d1);
        } catch (NullPointerException e) {
            tester = false;
        }
        assertFalse(tester);

        tester = true;
        String[] s0 = {"a"};
        double[] d0 = null;
        try {
            Autocomplete test2 = new Autocomplete(s0, d0);
        } catch (NullPointerException e) {
            tester = false;
        }
        assertFalse(tester);

        // Check that unequal arrays throws IAE
        tester = true;
        String[] s2 = {"a", "b"};
        double[] d2 = {1.0};
        try {
            Autocomplete test3 = new Autocomplete(s2, d2);
        } catch (IllegalArgumentException e) {
            tester = false;
        }
        assertFalse(tester);

        // Check that duplicate strings throws IAE
        tester = true;
        String[] s3 = {"♡♡+.ﾟ(￫ε￩*)ﾟ+.ﾟ", "♡♡+.ﾟ(￫ε￩*)ﾟ+.ﾟ"};
        double[] d3 = {1.0, 3.0};
        try {
            Autocomplete test4 = new Autocomplete(s3, d3);
        } catch (IllegalArgumentException e) {
            tester = false;
        }
        assertFalse(tester);

        // Check that negative doubles throws IAE
        tester = true;
        String[] s4 = {"weeaboo", "me_irl"};
        double[] d4 = {1.0, -3.0};
        try {
            Autocomplete test5 = new Autocomplete(s4, d4);
        } catch (IllegalArgumentException e) {
            tester = false;
        }
        assertFalse(tester);


    }
    /** Check runtime to construct cities.txt */
    @Test
    public void testConstructorTime1() {
        long startTime = System.currentTimeMillis();

        In in = new In("cities.txt");
        int N = in.readInt();
        String[] terms = new String[N];
        double[] weights = new double[N];
        for (int i = 0; i < N; i++) {
            weights[i] = in.readDouble();   // read the next weight
            in.readChar();                  // scan past the tab
            terms[i] = in.readLine();       // read the next term
        }

        Autocomplete autocomplete = new Autocomplete(terms, weights);

        long endTime = System.currentTimeMillis();
        System.out.println("cities: " + (endTime - startTime) + "ms");
    }

    /** Check runtime to construct wiktionary.txt */
    @Test
    public void testConstructorTime2() {
        long startTime = System.currentTimeMillis();

        In in = new In("wiktionary.txt");
        int N = in.readInt();
        String[] terms = new String[N];
        double[] weights = new double[N];
        for (int i = 0; i < N; i++) {
            weights[i] = in.readDouble();   // read the next weight
            in.readChar();                  // scan past the tab
            terms[i] = in.readLine();       // read the next term
        }

        Autocomplete autocomplete = new Autocomplete(terms, weights);

        long endTime = System.currentTimeMillis();
        System.out.println("wiktionary: " + (endTime - startTime) + "ms");
    }

    /** Check that highest weight string returned when prefix is empty **/
    @Test
    public void testTopMatchEmpty() {
        String characters = "ABCDEFGHabcdefg 123456";
        String[] s = new String[200];
        double[] d = new double[200];
        for (int i = 0; i < 200; i++) {
            s[i] = generateString(characters, 6);
            d[i] = i + 0.0;
        }

        Autocomplete test = new Autocomplete(s, d);
        assertEquals(s[199], test.topMatch(""));
    }

    /** Test that TopMatch + TopMatches is returning appropriate ordering of Strings
     * for Basic cases
     */
    @Test
    public void testTopMatchBasic() {
        String[] s = {"a", "ab", "abed", "banana", "baddie", "bananarooski", "bed"};
        double[] d = {9.0, 50.0, 25.0, 50.0, 55.0, 54.0, 9000.0};

        Autocomplete test = new Autocomplete(s, d);

        assertEquals("bed", test.topMatch(""));
        assertEquals("ab", test.topMatch("a"));
        assertEquals("ab", test.topMatch("ab"));
        assertEquals("abed", test.topMatch("abe"));
        assertEquals("bed", test.topMatch("b"));
        assertEquals("baddie", test.topMatch("ba"));
        assertEquals("bananarooski", test.topMatch("ban"));

        Iterable<String> check = test.topMatches("", 7);
        Iterator<String> iter = check.iterator();

        // Check to see that previous topMatches should not alter Trie
        Iterable<String> check2 = test.topMatches("b", 7);
        Iterator<String> iter2 = check2.iterator();
        assertEquals("bed", iter2.next());
        assertEquals("baddie", iter2.next());
        assertEquals("bananarooski", iter2.next());
        assertEquals("banana", iter2.next());

        assertEquals("bed", iter.next());
        assertEquals("baddie", iter.next());
        assertEquals("bananarooski", iter.next());
        assertEquals("banana", iter.next());
        assertEquals("ab", iter.next());
        assertEquals("abed", iter.next());
        assertEquals("a", iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    /** Check that TopMatches is returning top 50 correctly ordered strings from a list of
     *  5000 randomly generated strings, done 200 times
     */
    public void testTopMatches2() {
        // Tests randomized searches
        String characters = "ABCDEFGHabcdefg 123456@#!^&*)_~,./?";
        String[] s = new String[5000];
        double[] d = new double[5000];
        for (int iteration = 0; iteration < 200; iteration++) {
            for (int i = 0; i < 5000; i++) {
                s[i] = generateString(characters, 25);
                d[i] = (double) i;
            }
            Autocomplete test = new Autocomplete(s, d);
            Iterable<String> iterable = test.topMatches("", 50);
            Iterator<String> iter = iterable.iterator();
            for (int i = 0; i < 50; i++) {
                assertEquals(iter.next(), s[4999 - i]);
            }
        }
    }

    /** Check that topMatches works for Strings that are overlapping */
    @Test
    public void testTopMatches3() {
        String[] s = {"P", "Pr", "Pra", "Pray", "Pray ", "Pray f", "Pray fo", "Pray for",
            "Pray for ", "Pray for H", "Pray for Ha",
            "Pray for Har", "Pray for Hara", "Pray for Haram",
            "Pray for Haramb", "Pray for Harambe",
            "Pray for Dank Memes", "Pray for me", "Pray for preys"};
        double[] d = {19.0, 18.0, 17.0, 16.0, 15.0, 14.0, 13.0, 12.0, 11.0, 10.0,
            9.0, 8.0, 7.0, 6.0, 5.0, 400.0, 3.0, 2.0, 100.0};

        Autocomplete test = new Autocomplete(s, d);
        Iterable<String> iterable = test.topMatches("Pra", 10);
        Iterator<String> iter = iterable.iterator();

        assertEquals("Pray for Harambe", iter.next());
        assertEquals("Pray for preys", iter.next());
        assertEquals("Pra", iter.next());
        assertEquals("Pray", iter.next());
        assertEquals("Pray ", iter.next());
        assertEquals("Pray f", iter.next());
        assertEquals("Pray fo", iter.next());
        assertEquals("Pray for", iter.next());
        assertEquals("Pray for ", iter.next());
        assertEquals("Pray for H", iter.next());
        assertFalse(iter.hasNext());

    }

    /** Check that topMatches returns correct values for invalid arguments */
    @Test
    public void testTopMatches4() {
        String[] s = {"ლζ*♡ε♡*ζლ", "⊂（♡⌂♡）⊃",
            "༼♥ل͜♥༽", "ლ(́◉◞౪◟◉‵ლ)"};
        double[] d = {0.0, 1.0, 2.0, 3.0};

        Autocomplete test = new Autocomplete(s, d);

        boolean testing = true;

        // Check topMatches for a null prefix
        try {
            test.topMatches(null, 5);
        } catch (IllegalArgumentException e) {
            testing = false;
        }
        assertFalse(testing);

        testing = true;
        // Check topMatches for a negative int
        try {
            test.topMatches(null, -5);
        } catch (IllegalArgumentException e) {
            testing = false;
        }
        assertFalse(testing);
    }

    /* Helper method to generate random strings  -- thanks stackoverflow */
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
        textui.runClasses(TestAutocomplete.class);
    }
}
