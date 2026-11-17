package lab08;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Times the recursive and the non-recursive permutation generators on inputs of
 * increasing length.
 *
 * <p>Usage: {@code java lab08.PermutationBenchmark [maxLength]}. Every block is
 * repeated often enough to generate about one million permutations, and the median
 * of three blocks is reported as the average time of a single generation. Both
 * generators run in distinct mode, so they do the same amount of work.</p>
 */
public final class PermutationBenchmark {

    private static final int BLOCKS = 3;
    private static final long TARGET_PERMUTATIONS = 1_000_000L;

    private static long sink;

    private PermutationBenchmark() {
    }

    public static void main(String[] args) {
        int maxLength = args.length > 0 ? Integer.parseInt(args[0]) : 10;
        System.out.printf("java %s, max heap %d MiB%n", System.getProperty("java.version"),
                Runtime.getRuntime().maxMemory() / (1024 * 1024));
        System.out.printf("%-2s %-13s %-13s %-13s %-9s %s%n",
                "n", "permutations", "recursive ms", "iterative ms", "speed-up", "same set");
        for (int length = 6; length <= maxLength; length++) {
            String input = sample(length);
            int permutations = factorial(length);
            int runs = (int) Math.max(1, TARGET_PERMUTATIONS / permutations);
            double recursive = millisPerRun(input, runs, false);
            double iterative = millisPerRun(input, runs, true);
            String sameSet = length <= 8 ? (sameSet(input) ? "yes" : "no") : "not checked";
            System.out.printf("%-2d %-13d %-13.3f %-13.3f %-9s %s%n",
                    length, permutations, recursive, iterative,
                    String.format("%.1fx", recursive / iterative), sameSet);
        }
        System.out.printf("permutations produced in total: %,d%n", sink);
    }

    private static double millisPerRun(String input, int runs, boolean iterative) {
        long[] blockNanos = new long[BLOCKS];
        for (int block = 0; block < BLOCKS; block++) {
            long start = System.nanoTime();
            for (int run = 0; run < runs; run++) {
                List<String> permutations = iterative
                        ? Permutations.generatePermutationsIterative(input)
                        : Permutations.generatePermutations(input, false);
                sink += permutations.size();
            }
            blockNanos[block] = System.nanoTime() - start;
        }
        Arrays.sort(blockNanos);
        return blockNanos[BLOCKS / 2] / 1_000_000.0 / runs;
    }

    private static boolean sameSet(String input) {
        List<String> recursive = Permutations.generatePermutations(input, false);
        List<String> iterative = Permutations.generatePermutationsIterative(input);
        return recursive.size() == iterative.size()
                && new HashSet<>(recursive).equals(new HashSet<>(iterative));
    }

    private static String sample(int length) {
        StringBuilder input = new StringBuilder();
        for (int index = 0; index < length; index++) {
            input.append((char) ('a' + index));
        }
        return input.toString();
    }

    private static int factorial(int value) {
        int result = 1;
        for (int factor = 2; factor <= value; factor++) {
            result *= factor;
        }
        return result;
    }
}
