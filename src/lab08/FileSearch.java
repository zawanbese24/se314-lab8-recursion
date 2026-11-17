package lab08;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Recursive search for files inside a directory tree.
 *
 * <p>A search is described by a root directory, a nonempty list of file names, and
 * a case-sensitivity flag. It visits every directory below the root depth-first and
 * reports each regular file whose name equals one of the requested names.</p>
 *
 * <p>Symbolic links are never followed, so a link that points back to an ancestor
 * cannot make the search loop forever. A subdirectory that cannot be listed (for
 * example because the user lacks read permission) is skipped and the search
 * continues in its siblings; the root itself must be readable.</p>
 */
public final class FileSearch {

    private FileSearch() {
    }

    /**
     * Searches {@code root} and all of its subdirectories for the given file names.
     *
     * @param root
     *            existing directory to search; must be readable
     * @param fileNames
     *            one or more file names to look for; no entry may be null or blank
     * @param caseSensitive
     *            true if "Notes.txt" and "notes.txt" are considered different names
     * @return a result holding, for every requested name, the matching files in
     *         depth-first order (each list empty when nothing matched)
     * @throws NullPointerException
     *             if {@code root} or {@code fileNames} is null
     * @throws IllegalArgumentException
     *             if {@code fileNames} is empty or contains a null or blank name,
     *             or if {@code root} is not an existing directory
     * @throws IOException
     *             if the root directory cannot be listed
     */
    public static SearchResult search(Path root, List<String> fileNames, boolean caseSensitive)
            throws IOException {
        if (root == null) {
            throw new NullPointerException("root must not be null");
        }
        if (fileNames == null) {
            throw new NullPointerException("fileNames must not be null");
        }
        if (fileNames.isEmpty()) {
            throw new IllegalArgumentException("at least one file name is required");
        }
        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("not an existing directory: " + root);
        }

        Map<String, List<Path>> matches = new LinkedHashMap<>();
        for (String fileName : fileNames) {
            if (fileName == null || fileName.isBlank()) {
                throw new IllegalArgumentException("file names must not be blank");
            }
            matches.put(fileName, new ArrayList<>());
        }

        collect(root.toAbsolutePath().normalize(), matches, caseSensitive);
        return new SearchResult(matches);
    }

    /**
     * Recursive step: handles one directory by looking at each of its entries.
     *
     * <p>Base case: a directory that cannot be listed, or that contains no
     * subdirectory and no matching file, contributes nothing and the call returns.
     * Recursive step: every real subdirectory is searched with the same method.</p>
     */
    private static void collect(Path directory, Map<String, List<Path>> matches,
            boolean caseSensitive) throws IOException {
        for (Path entry : listSorted(directory)) {
            if (Files.isDirectory(entry, LinkOption.NOFOLLOW_LINKS)) {
                try {
                    collect(entry, matches, caseSensitive);
                } catch (IOException unreadable) {
                    continue;
                }
            } else if (Files.isRegularFile(entry, LinkOption.NOFOLLOW_LINKS)) {
                record(entry, matches, caseSensitive);
            }
        }
    }

    /**
     * Base-case helper: lists a directory in a stable order so that two runs over
     * the same tree report the same files in the same sequence.
     */
    private static List<Path> listSorted(Path directory) throws IOException {
        List<Path> entries = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                entries.add(entry);
            }
        }
        Collections.sort(entries);
        return entries;
    }

    private static void record(Path file, Map<String, List<Path>> matches,
            boolean caseSensitive) {
        String actualName = file.getFileName().toString();
        for (Map.Entry<String, List<Path>> requested : matches.entrySet()) {
            if (sameName(actualName, requested.getKey(), caseSensitive)) {
                requested.getValue().add(file);
            }
        }
    }

    private static boolean sameName(String actualName, String requestedName,
            boolean caseSensitive) {
        if (caseSensitive) {
            return actualName.equals(requestedName);
        }
        return actualName.toLowerCase(Locale.ROOT).equals(requestedName.toLowerCase(Locale.ROOT));
    }
}
