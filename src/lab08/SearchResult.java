package lab08;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Immutable outcome of one file search: the files found for each requested name.
 *
 * <p>A result keeps the requested names in the order they were supplied and never
 * exposes a mutable collection. A requested name that matched nothing maps to an
 * empty list rather than to {@code null}.</p>
 */
public final class SearchResult {

    private final Map<String, List<Path>> matches;

    SearchResult(Map<String, List<Path>> matches) {
        Map<String, List<Path>> frozen = new LinkedHashMap<>();
        for (Map.Entry<String, List<Path>> entry : matches.entrySet()) {
            frozen.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        this.matches = Collections.unmodifiableMap(frozen);
    }

    /**
     * Returns the files whose name matched {@code fileName}.
     *
     * @param fileName a name that was given to the search
     * @return the matching files in depth-first order, or an empty list when
     *         nothing matched (including when the name was never requested)
     */
    public List<Path> matches(String fileName) {
        List<Path> found = matches.get(fileName);
        return found == null ? List.of() : found;
    }

    /**
     * Returns how many files matched {@code fileName}.
     *
     * @param fileName a name that was given to the search
     * @return the number of matching files, 0 when none matched
     */
    public int count(String fileName) {
        return matches(fileName).size();
    }

    /** Returns how many files matched any requested name. */
    public int totalCount() {
        int total = 0;
        for (List<Path> found : matches.values()) {
            total += found.size();
        }
        return total;
    }

    /** Returns the requested names, in the order they were supplied. */
    public Set<String> searchedNames() {
        return matches.keySet();
    }
}
