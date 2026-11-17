package lab08;

import java.util.List;

/**
 * Command-line front end for {@link Permutations}.
 *
 * <p>Usage: {@code java lab08.PermutationsApp <string> [--distinct] [--iterative]}.
 * The options select the duplicate policy and the algorithm.</p>
 */
public final class PermutationsApp {

    private static final String USAGE =
            "usage: java lab08.PermutationsApp <string> [--distinct] [--iterative]";

    private PermutationsApp() {
    }

    public static void main(String[] args) {
        String input = null;
        boolean includeDuplicates = true;
        boolean iterative = false;

        for (String argument : args) {
            if (argument.equals("--distinct")) {
                includeDuplicates = false;
            } else if (argument.equals("--iterative")) {
                iterative = true;
            } else if (argument.equals("--help")) {
                System.out.println(USAGE);
                return;
            } else if (input == null) {
                input = argument;
            } else {
                System.err.println(USAGE);
                System.exit(2);
            }
        }
        if (input == null) {
            System.err.println(USAGE);
            System.exit(2);
        }

        try {
            boolean distinct = iterative || !includeDuplicates;
            List<String> permutations = iterative
                    ? Permutations.generatePermutationsIterative(input)
                    : Permutations.generatePermutations(input, includeDuplicates);
            print(input, permutations, distinct, iterative);
        } catch (IllegalArgumentException invalid) {
            System.err.println("error: " + invalid.getMessage());
            System.exit(2);
        }
    }

    private static void print(String input, List<String> permutations, boolean distinct,
            boolean iterative) {
        String algorithm = iterative ? "iterative next-permutation"
                : distinct ? "recursive, duplicates excluded" : "recursive, duplicates included";
        System.out.printf("permutations of \"%s\" (n=%d) - %s%n", input, input.length(), algorithm);
        for (int index = 0; index < permutations.size(); index++) {
            System.out.printf("%4d. %s%n", index + 1, permutations.get(index));
        }
        System.out.printf("%d permutation(s) generated%n", permutations.size());
    }
}
