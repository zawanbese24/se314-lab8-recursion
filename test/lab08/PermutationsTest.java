package lab08;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class PermutationsTest {

    private static void assertSameCharacters(String expected, String actual) {
        char[] expectedCharacters = expected.toCharArray();
        char[] actualCharacters = actual.toCharArray();
        java.util.Arrays.sort(expectedCharacters);
        java.util.Arrays.sort(actualCharacters);
        assertEquals(new String(expectedCharacters), new String(actualCharacters));
    }

    @Test
    public void testEmptyInputIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> Permutations.generatePermutations(""));
        assertThrows(IllegalArgumentException.class,
                () -> Permutations.generatePermutations("", false));
        assertThrows(IllegalArgumentException.class,
                () -> Permutations.generatePermutationsIterative(""));
    }

    @Test
    public void testNullInputIsRejected() {
        assertThrows(NullPointerException.class,
                () -> Permutations.generatePermutations(null));
        assertThrows(NullPointerException.class,
                () -> Permutations.generatePermutationsIterative(null));
    }

    @Test
    public void testSingleCharacterHasOnePermutation() {
        assertEquals(List.of("a"), Permutations.generatePermutations("a"));
        assertEquals(List.of("a"), Permutations.generatePermutations("a", false));
        assertEquals(List.of("a"), Permutations.generatePermutationsIterative("a"));
    }

    @Test
    public void testTwoCharactersHaveTwoPermutations() {
        List<String> permutations = Permutations.generatePermutations("ab");

        assertEquals(2, permutations.size());
        assertEquals(Set.of("ab", "ba"), new HashSet<>(permutations));
    }

    @Test
    public void testThreeDistinctCharactersHaveSixPermutations() {
        List<String> permutations = Permutations.generatePermutations("abc");

        assertEquals(6, permutations.size());
        assertEquals(Set.of("abc", "acb", "bac", "bca", "cab", "cba"),
                new HashSet<>(permutations));
    }

    @Test
    public void testEveryPermutationKeepsTheCharactersOfTheInput() {
        for (String permutation : Permutations.generatePermutations("abcd")) {
            assertEquals(4, permutation.length());
            assertSameCharacters("abcd", permutation);
        }
    }

    @Test
    public void testDuplicatesIncludedKeepsRepeatedStrings() {
        List<String> permutations = Permutations.generatePermutations("aab");

        assertEquals(6, permutations.size());
        assertEquals(2, Collections.frequency(permutations, "aab"));
        assertEquals(2, Collections.frequency(permutations, "aba"));
        assertEquals(2, Collections.frequency(permutations, "baa"));
        assertEquals(Set.of("aab", "aba", "baa"), new HashSet<>(permutations));
    }

    @Test
    public void testDuplicatesExcludedReturnsDistinctPermutations() {
        List<String> permutations = Permutations.generatePermutations("aab", false);

        assertEquals(3, permutations.size());
        assertEquals(Set.of("aab", "aba", "baa"), new HashSet<>(permutations));
    }

    @Test
    public void testDuplicatesExcludedOnDistinctInputMatchesFactorial() {
        assertEquals(24, Permutations.generatePermutations("abcd", false).size());
    }

    @Test
    public void testDuplicatesExcludedOnRepeatedInputUsesTheMultinomialCount() {
        assertEquals(6, Permutations.generatePermutations("aabb", false).size());
        assertEquals(10, Permutations.generatePermutations("aaabb", false).size());
        assertEquals(30, Permutations.generatePermutations("aabbc", false).size());
    }

    @Test
    public void testIterativeGeneratorReturnsLexicographicOrder() {
        assertEquals(List.of("abc", "acb", "bac", "bca", "cab", "cba"),
                Permutations.generatePermutationsIterative("cba"));
        assertEquals(List.of("aab", "aba", "baa"),
                Permutations.generatePermutationsIterative("aba"));
    }

    @Test
    public void testIterativeAndRecursiveAgreeOnThePermutationSet() {
        for (String input : List.of("abc", "aab", "aba", "abcd", "aabcb")) {
            Set<String> recursive = new HashSet<>(Permutations.generatePermutations(input, false));
            Set<String> iterative = new HashSet<>(Permutations.generatePermutationsIterative(input));

            assertEquals(input, iterative, recursive);
        }
    }

    @Test
    public void testEightCharactersHaveFactorialPermutations() {
        assertEquals(40320, Permutations.generatePermutations("abcdefgh", false).size());
        assertEquals(40320, Permutations.generatePermutationsIterative("abcdefgh").size());
    }

    @Test
    public void testAllPermutationsAreDistinctWhenDuplicatesAreExcluded() {
        List<String> permutations = Permutations.generatePermutations("aabcb", false);

        assertEquals(30, permutations.size());
        assertEquals(permutations.size(), new HashSet<>(permutations).size());
        assertTrue(permutations.contains("abcba"));
    }
}
