package lab08;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Command-line front end for {@link FileSearch}.
 *
 * <p>Usage: {@code java lab08.FileSearchApp <directory> <file> [<file> ...]
 * [--ignore-case]}. Matching paths are printed relative to the searched directory.
 * The exit status is 0 when every requested name was found, 1 when at least one
 * was not found, and 2 for a usage or input error.</p>
 */
public final class FileSearchApp {

    private static final String USAGE =
            "usage: java lab08.FileSearchApp <directory> <file> [<file> ...] [--ignore-case]";

    private FileSearchApp() {
    }

    public static void main(String[] args) {
        String directoryArgument = null;
        List<String> names = new ArrayList<>();
        boolean caseSensitive = true;

        for (String argument : args) {
            if (argument.equals("--ignore-case")) {
                caseSensitive = false;
            } else if (argument.equals("--help")) {
                System.out.println(USAGE);
                return;
            } else if (directoryArgument == null) {
                directoryArgument = argument;
            } else {
                names.add(argument);
            }
        }
        if (directoryArgument == null || names.isEmpty()) {
            System.err.println(USAGE);
            System.exit(2);
        }

        try {
            Path root = Path.of(directoryArgument);
            SearchResult result = FileSearch.search(root, names, caseSensitive);
            boolean everythingFound = report(result, root.toAbsolutePath().normalize(),
                    caseSensitive);
            if (!everythingFound) {
                System.exit(1);
            }
        } catch (InvalidPathException invalid) {
            System.err.println("error: invalid path: " + invalid.getInput());
            System.exit(2);
        } catch (IllegalArgumentException invalid) {
            System.err.println("error: " + invalid.getMessage());
            System.exit(2);
        } catch (IOException failure) {
            System.err.println("error: cannot read " + directoryArgument + ": " + failure);
            System.exit(2);
        }
    }

    private static boolean report(SearchResult result, Path root, boolean caseSensitive) {
        System.out.printf("searching %s (%s) for %d file name(s)%n",
                root.getFileName(), caseSensitive ? "case-sensitive" : "case-insensitive",
                result.searchedNames().size());
        boolean everythingFound = true;
        for (String name : result.searchedNames()) {
            List<Path> found = result.matches(name);
            if (found.isEmpty()) {
                everythingFound = false;
                System.out.printf("NOT FOUND  %s%n", name);
            }
            for (Path path : found) {
                System.out.printf("FOUND      %s  ->  %s%n", name, root.relativize(path));
            }
            System.out.printf("count      %s = %d%n", name, result.count(name));
        }
        System.out.printf("%d match(es) in total%n", result.totalCount());
        return everythingFound;
    }
}
