package lab08;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Two permutation generators over the characters of a string.
 *
 * <p>{@link #generatePermutations(String, boolean)} is recursive backtracking: at
 * position {@code i} it swaps every still-unused character into place, recurses on
 * the suffix, then swaps back. {@link #generatePermutationsIterative(String)} is
 * the non-recursive lexicographic generator, which sorts the characters and
 * repeatedly computes the next permutation in place.</p>
 */
public final class Permutations {

    private Permutations() {
    }

    /**
     * Generates every permutation of {@code input}, keeping repeated strings when
     * the input contains repeated characters.
     *
     * @param input
     *            nonempty string
     * @return all {@code n!} permutations, {@code n = input.length()}, in the order
     *         the backtracking search produces them
     * @throws NullPointerException
     *             if {@code input} is null
     * @throws IllegalArgumentException
     *             if {@code input} is empty
     */
    public static List<String> generatePermutations(String input) {
        return generatePermutations(input, true);
    }

    /**
     * Generates the permutations of {@code input}, optionally without duplicates.
     *
     * <p>The result always contains {@code n!} entries when duplicates are included,
     * and {@code n! / (m1! * m2! * ... * mk!)} entries when they are excluded, where
     * {@code m1 ... mk} are the multiplicities of the distinct characters.</p>
     *
     * @param input
     *            nonempty string
     * @param includeDuplicates
     *            true to keep every generated string, false to keep one copy of each
     *            distinct permutation
     * @return the permutations, all of them distinct when duplicates are excluded
     * @throws NullPointerException
     *             if {@code input} is null
     * @throws IllegalArgumentException
     *             if {@code input} is empty
     */
    public static List<String> generatePermutations(String input, boolean includeDuplicates) {
        char[] characters = charactersOf(input);
        List<String> permutations = new ArrayList<>();
        if (includeDuplicates) {
            permute(characters, 0, permutations);
        } else {
            permuteDistinct(characters, 0, permutations);
        }
        return permutations;
    }

    /**
     * Generates the distinct permutations of {@code input} without recursion.
     *
     * @param input
     *            nonempty string
     * @return the distinct permutations in lexicographic order
     * @throws NullPointerException
     *             if {@code input} is null
     * @throws IllegalArgumentException
     *             if {@code input} is empty
     */
    public static List<String> generatePermutationsIterative(String input) {
        char[] characters = charactersOf(input);
        Arrays.sort(characters);
        List<String> permutations = new ArrayList<>();
        do {
            permutations.add(new String(characters));
        } while (nextPermutation(characters));
        return permutations;
    }

    private static char[] charactersOf(String input) {
        if (input == null) {
            throw new NullPointerException("input must not be null");
        }
        if (input.isEmpty()) {
            throw new IllegalArgumentException("input must not be empty");
        }
        return input.toCharArray();
    }

    /**
     * Recursive step: chooses the character for {@code position} by swapping each
     * candidate into it, then permutes the remaining suffix.
     *
     * <p>Base case: when {@code position} has reached the end of the array the
     * current arrangement is one complete permutation and is recorded.</p>
     */
    private static void permute(char[] characters, int position, List<String> permutations) {
        if (position == characters.length) {
            permutations.add(new String(characters));
            return;
        }
        for (int candidate = position; candidate < characters.length; candidate++) {
            swap(characters, position, candidate);
            permute(characters, position + 1, permutations);
            swap(characters, position, candidate);
        }
    }

    /**
     * Variant of {@link #permute} that records the characters already tried at the
     * current position and skips a candidate that was tried before. Two indices that
     * hold the same character are interchangeable, so trying both would only repeat
     * work already done and could only produce a duplicate permutation.
     */
    private static void permuteDistinct(char[] characters, int position,
            List<String> permutations) {
        if (position == characters.length) {
            permutations.add(new String(characters));
            return;
        }
        Set<Character> placed = new HashSet<>();
        for (int candidate = position; candidate < characters.length; candidate++) {
            if (!placed.add(characters[candidate])) {
                continue;
            }
            swap(characters, position, candidate);
            permuteDistinct(characters, position + 1, permutations);
            swap(characters, position, candidate);
        }
    }

    /**
     * Rearranges {@code characters} into the next lexicographic permutation.
     *
     * @return false when the array is already in descending order, i.e. the last
     *         permutation, so the caller's loop stops
     */
    private static boolean nextPermutation(char[] characters) {
        int pivot = characters.length - 2;
        while (pivot >= 0 && characters[pivot] >= characters[pivot + 1]) {
            pivot--;
        }
        if (pivot < 0) {
            return false;
        }
        int successor = characters.length - 1;
        while (characters[successor] <= characters[pivot]) {
            successor--;
        }
        swap(characters, pivot, successor);
        for (int left = pivot + 1, right = characters.length - 1; left < right;
                left++, right--) {
            swap(characters, left, right);
        }
        return true;
    }

    private static void swap(char[] characters, int first, int second) {
        char saved = characters[first];
        characters[first] = characters[second];
        characters[second] = saved;
    }
}
